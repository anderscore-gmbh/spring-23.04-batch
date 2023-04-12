package infra;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableBatchProcessing
public class PersonJsonJacksonJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job personJacksonJob(@Qualifier("jackson") JsonItemReader<Person> reader) {
        Step step = stepBuilderFactory.get("personJacksonStep")
                .<Person, Person>chunk(1)
                .reader(reader)
                .processor(validator())
                .writer(writer())
                .build();
        return jobBuilderFactory.get("personJacksonJob").start(step).build();
    }

    // tag::reader[]
    @Bean
    @Qualifier("jackson")
    @StepScope
    JsonItemReader<Person> personJacksonReader(@Value("#{jobParameters[file]}") Resource source) {
        return new JsonItemReaderBuilder<Person>()
                .name("personJacksonReader")
                .jsonObjectReader(jacksonJsonObjectReader())
                .resource(source)
                .build();
    }

    @Bean
    JacksonJsonObjectReader<Person> jacksonJsonObjectReader() {
    	JacksonJsonObjectReader<Person> objectReader = new JacksonJsonObjectReader<Person>(Person.class);
        objectReader.setMapper(objectMapper());
        return objectReader;
    }

    @Bean
    ObjectMapper objectMapper() {
    	ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
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
