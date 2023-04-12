package infra;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.json.GsonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@Configuration
@EnableBatchProcessing
public class PersonJsonGsonJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job personGsonJob(@Qualifier("gson") JsonItemReader<Person> reader) {
        Step step = stepBuilderFactory.get("personGsonStep")
                .<Person, Person>chunk(1)
                .reader(reader)
                .processor(validator())
                .writer(writer())
                .build();
        return jobBuilderFactory.get("personGsonJob").start(step).build();
    }

    // tag::reader[]
    @Bean
    @Qualifier("gson")
    @StepScope
    JsonItemReader<Person> personGsonReader(@Value("#{jobParameters[file]}") Resource source) {
        return new JsonItemReaderBuilder<Person>()
                .name("personGsonReader")
                .jsonObjectReader(gsonJsonObjectReader())
                .resource(source)
                .build();
    }

    @Bean
    GsonJsonObjectReader<Person> gsonJsonObjectReader() {
    	GsonJsonObjectReader<Person> objectReader = new GsonJsonObjectReader<Person>(Person.class);
        objectReader.setMapper(gson());
        return objectReader;
    }

    @Bean
    Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter().nullSafe())
                .create();
    }

    // see https://stackoverflow.com/questions/39192945/serialize-java-8-localdate-as-yyyy-mm-dd-with-gson/39193077#39193077
    static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(DateTimeFormatter.ISO_LOCAL_DATE.format(value));
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString());
        }
    }
    // end::reader[]

    @Bean
    BeanValidatingItemProcessor<Person> validator() {
        BeanValidatingItemProcessor<Person> validator = new BeanValidatingItemProcessor<>();
        validator.setFilter(true);
        return validator;
    }

    @Bean
    SystemOutWriter<Person> writer() {
        return new SystemOutWriter<Person>();
    }
}
