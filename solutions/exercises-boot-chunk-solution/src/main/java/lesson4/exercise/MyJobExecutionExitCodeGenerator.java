package lesson4.exercise;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyJobExecutionExitCodeGenerator implements ApplicationListener<JobExecutionEvent>, ExitCodeGenerator {

	private final List<JobExecution> executions = new ArrayList<>();

	@Override
	public void onApplicationEvent(JobExecutionEvent event) {
		this.executions.add(event.getJobExecution());
	}

	@Override
	public int getExitCode() {
		for (JobExecution execution : this.executions) {
			if (DumpInvalidFractionsTasklet.WARNING.getExitCode().equals(execution.getExitStatus().getExitCode())) {
				return 99;
			}
			if (execution.getStatus().ordinal() > 0) {
				return execution.getStatus().ordinal();
			}
		}
		return 0;
	}

}
