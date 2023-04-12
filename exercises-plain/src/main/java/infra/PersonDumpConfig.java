package infra;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.format.support.DefaultFormattingConversionService;

/**
 * Run with {@link org.springframework.batch.core.launch.support.CommandLineJobRunner} using parameters:
 * <i>infra.PersonDumpConfig personDumpJob file=classpath:/Persons.csv</i>.
 */
@Configuration
@EnableBatchProcessing
public class PersonDumpConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job personDumpJob(FlatFileItemReader<Person> personCsvReader) {
        Step personDumpStep = stepBuilderFactory.get("personDumpStep")
                .<Person, Person>chunk(1)
                .reader(personCsvReader)
                .processor(validator())
                .writer(systemOutWriter())
                .build();
        return jobBuilderFactory.get("personDumpJob").start(personDumpStep).build();
    }

    // tag::code[]
    @Bean
    @JobScope
    FlatFileItemReader<Person> personCsvReader(
            @Value("#{jobParameters[file]}") Resource source) {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personCsvReader") // arbitrary name
                .saveState(false) // don't save progress in ExecutionContext
                .resource(source) // read from this Resource
                .delimited() // expect a delimited (CSV) file
                .delimiter(";") // use ';' as delimiter instead of ','
                .quoteCharacter('\"') // remove quotation from content
                .names(new String[] { "lastName", "firstName", "birthday" }) // map column to names
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
                    {
                        setTargetType(Person.class); // initialize mapper
                        DefaultFormattingConversionService cv = new DefaultFormattingConversionService();
                        setConversionService(cv); // convert date/time values
                    }
                })
                .linesToSkip(1) // skip first (header) row
                .build(); // create the reader
    }
    // end::code[]

    @Bean
    BeanValidatingItemProcessor<Person> validator() {
        BeanValidatingItemProcessor<Person> validator = new BeanValidatingItemProcessor<>();
        return validator;
    }

    @Bean
    SystemOutWriter<Person> systemOutWriter() {
        return new SystemOutWriter<Person>();
    }
}
