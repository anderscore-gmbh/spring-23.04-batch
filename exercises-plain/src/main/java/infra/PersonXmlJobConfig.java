package infra;

import java.time.LocalDate;
import java.util.Calendar;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import infra.persons.jaxb.PersonJaxb;
import infra.persons.jaxb.Persons;

@Configuration
@EnableBatchProcessing
public class PersonXmlJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job personXmlJob(ItemReader<PersonJaxb> reader) {
        Step personXmlStep = stepBuilderFactory.get("personXmlStep")
                .<PersonJaxb, Person>chunk(1)
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .build();
        return jobBuilderFactory.get("personXmlJob").start(personXmlStep).build();
    }

    // tag::reader[]
    @Bean
    @JobScope
    StaxEventItemReader<PersonJaxb> personXmlReader(
            @Value("#{jobParameters[file]}") Resource source) {
        return new StaxEventItemReaderBuilder<PersonJaxb>()
                .name("personXmlReader")
                .resource(source)
                .addFragmentRootElements("person")
                .unmarshaller(getUnmarshaller())
                .build();
    }
    // end::reader[]

    // tag::jaxb[]
    @Bean
    Unmarshaller getUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Persons.class.getPackage().getName());
        marshaller.setMappedClass(PersonJaxb.class);
        return marshaller;
    }
    // end::jaxb[]

    // tag::compose[]
    @Bean
    ItemProcessor<PersonJaxb, Person> processor() {
        return new CompositeItemProcessorBuilder<PersonJaxb, Person>()
                .delegates((ItemProcessor<PersonJaxb, Person>) this::map, validator())
                .build();
    }
    // end::compose[]

    private Person map(PersonJaxb jaxb) {
        Person person = new Person();
        person.setFirstName(jaxb.getFirstName());
        person.setLastName(jaxb.getLastName());
        person.setBirthday(toLocalDate(jaxb.getBirthday()));
        return person;
    }

    private LocalDate toLocalDate(Calendar cal) {
    	return cal.toInstant().atZone(cal.getTimeZone().toZoneId()).toLocalDate();
    }

    // tag::validator[]
    @Bean
    BeanValidatingItemProcessor<Person> validator() {
        BeanValidatingItemProcessor<Person> validator = new BeanValidatingItemProcessor<>();
        validator.setFilter(true);
        return validator;
    }
    // end::validator[]

    @Bean
    SystemOutWriter<Person> writer() {
        return new SystemOutWriter<Person>();
    }
}
