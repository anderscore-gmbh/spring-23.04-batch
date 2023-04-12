package infra.exercise;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import infra.exercise.jaxb.StepExecutionElement;
import repository.DataSourceConfig;

@Configuration
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class ExportStepExecutionsJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    RunIdIncrementer incementer() {
        return new RunIdIncrementer();
    }

    @Bean
    Job exportStepExecutionsJob() {
        return jobBuilderFactory.get("exportStepExecutionsJob")
                .start(exportStepExecutionsStep())
                .incrementer(incementer())
                .build();
    }

    @Bean
    Step exportStepExecutionsStep() {
        return stepBuilderFactory.get("exportStepExecutionsStep")
                .<StepExecutionData, StepExecutionData>chunk(1)
                .reader(stepExecutionReader())
                .processor(filterProcessor())
                .writer(stepExecutionCombinedWriter())
                .build();
    }

    @Bean
    JdbcCursorItemReader<StepExecutionData> stepExecutionReader() {
        return new JdbcCursorItemReaderBuilder<StepExecutionData>()
                .name("stepExecutionReader")
                .dataSource(dataSource)
                .sql("select * from BATCH_STEP_EXECUTION order by STEP_EXECUTION_ID")
                .beanRowMapper(StepExecutionData.class)
                .build();
    }

    @Bean
    CompositeItemWriter<StepExecutionData> stepExecutionCombinedWriter() {
        return new CompositeItemWriterBuilder<StepExecutionData>()
                .delegates(stepExecutionCsvWriter(), stepExecutionJsonWriter())
                .build();
    }

    @Bean
    FlatFileItemWriter<StepExecutionData> stepExecutionCsvWriter() {
        return new FlatFileItemWriterBuilder<StepExecutionData>()
                .name("stepExecutionCsvWriter")
                .resource(new FileSystemResource("target/stepExecutions.csv"))
                .delimited()
                .delimiter(";")
                .names("stepExecutionId,stepName,status,readCount,writeCount,startTime,endTime,exitCode".split(","))
                .build();
    }
    
    @Bean
    BeanValidatingItemProcessor<StepExecutionData> filterProcessor() {
    	BeanValidatingItemProcessor<StepExecutionData> processor = new BeanValidatingItemProcessor<StepExecutionData>();
    	processor.setFilter(true);
    	return processor;
    }

    @Bean
    JsonFileItemWriter<StepExecutionData> stepExecutionJsonWriter() {
        return new JsonFileItemWriterBuilder<StepExecutionData>()
                .name("personJsonWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("target/stepExecutions.json"))
                .build();
    }

    @Bean
    Job exportStepExecutionsXmlJob() {
        return jobBuilderFactory.get("exportStepExecutionsXmlJob")
                .start(exportStepExecutionsXmlStep())
                .incrementer(incementer())
                .build();
    }

    @Bean
    Step exportStepExecutionsXmlStep() {
        return stepBuilderFactory.get("exportStepExecutionsXmlStep")
                .<StepExecutionData, StepExecutionElement>chunk(1)
                .reader(stepExecutionReader())
                .processor((ItemProcessor<StepExecutionData, StepExecutionElement>) this::convertToElement)
                .writer(stepExecutionXmlWriter())
                .build();
    }

    private StepExecutionElement convertToElement(StepExecutionData data) throws DatatypeConfigurationException {
        StepExecutionElement elem = new StepExecutionElement();
        elem.setStepExecutionId(data.getStepExecutionId());
        elem.setStepName(data.getStepName());
        elem.setStatus(String.valueOf(data.getStatus()));
        elem.setReadCount(data.getReadCount());
        elem.setWriteCount(data.getWriteCount());
        elem.setStartTime(asCalendar(data.getStartTime()));
        elem.setEndTime(asCalendar(data.getEndTime()));
        elem.setExitCode(data.getExitCode());
        return elem;
    }

    private GregorianCalendar asCalendar(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

    @Bean
    StaxEventItemWriter<StepExecutionElement> stepExecutionXmlWriter() {
        return new StaxEventItemWriterBuilder<StepExecutionElement>()
                .name("stepExecutionXmlWriter")
                .marshaller(jaxb2Marshaller())
                .resource(new FileSystemResource("target/stepExecutions.xml"))
                .rootTagName("stepExecutions") // root-tag of the file
                .build();
    }

    @Bean
    Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(StepExecutionElement.class);
        return marshaller;
    }
}
