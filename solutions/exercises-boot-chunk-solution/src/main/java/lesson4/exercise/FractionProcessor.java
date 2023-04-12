package lesson4.exercise;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FractionProcessor implements ItemProcessor<Fraction, String> {
	private static final Logger logger = LoggerFactory.getLogger(FractionProcessor.class);
	private static final List<Fraction> invalidFractions = new ArrayList<>();

	@Override
	public String process(Fraction item) {
		return item.toDescription();
	}
	
	@BeforeStep
	public void init(StepExecution stepExecution) {
		stepExecution.getExecutionContext().put("invalidFractions", invalidFractions);
	}

	@OnProcessError
	public void onError(Fraction item, Exception ex) {
		logger.info("processing failed for {} with {}", item, ex.toString());
		invalidFractions.add(item);
	}
}
