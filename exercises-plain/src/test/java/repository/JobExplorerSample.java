package repository;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import hello.HelloConfig;

@SpringJUnitConfig(HelloConfig.class)
public class JobExplorerSample {

    @Autowired
    private JobExplorer jobExplorer;

    @Test
    void testDumpJobExecutionAndStepExecutions() {
    }
}
