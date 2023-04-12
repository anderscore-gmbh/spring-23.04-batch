package chunk.exercise;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@JobScope
public class RandomFractionReader implements ItemReader<Fraction> {
	@Value("#{jobParameters[count]?:10}")
	private int count;
	private int counter;

	@Override
	public Fraction read() {
		if (counter >= count) {
			return null;
		}
		counter++;
		return Fraction.createRandomFraction();
	}
}
