package ops.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

// tag::code[]
@Configuration
public class QuartzConfig {
    @Value("${ops.quartz.cron:0 0/5 * * * ?}")
    private String cronExpression;

    @Bean
    JobDetail helloQuartzJobDetail() {
        return JobBuilder.newJob(HelloQuartzJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    CronTriggerFactoryBean cronTrigger() {
        CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setCronExpression(cronExpression);
        cronTrigger.setJobDetail(helloQuartzJobDetail());
        return cronTrigger;
    }
}
// end::code[]