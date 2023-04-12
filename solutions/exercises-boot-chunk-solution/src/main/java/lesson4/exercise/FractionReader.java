package lesson4.exercise;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@JobScope
public class FractionReader implements ItemReader<Fraction> {
	
	@Value("#{jobParameters[count]?:5}")
	private int count;

	@Override
	public Fraction read() {
		if (count <= 0) {
			return null;
		}
		count--;
		return Fraction.createRandomFraction();
	}
}
