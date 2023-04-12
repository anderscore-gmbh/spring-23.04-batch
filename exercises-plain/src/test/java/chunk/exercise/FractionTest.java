package chunk.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FractionTest {

    @ParameterizedTest
    @CsvSource({ "  1,  1,'  1/1   =   1,000'",
            "-10,-10,'-10/-10 =   1,000'",
            "-10,  1,'-10/1   = -10,000'",
            "  2,  3,'  2/3   =   0,667'",
            "  4,  3,'  4/3   =   1,333'",
            "  1,  2,'  1/2   =   0,500'" })
    void testFormatting(int numerator, int denominator, String expectedResult) {
    	Fraction fraction = new Fraction(numerator, denominator);
        assertThat(fraction.toDescription()).isEqualTo(expectedResult);
    }

    @Test
    void testDivisionByZero() {
    	Fraction fraction = new Fraction(1, 0);
        assertThrows(ArithmeticException.class, () -> fraction.toDescription());
    }

    @Test
    void testRange() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 10000; i++) {
        	Fraction fraction = Fraction.createRandomFraction();
            min = Math.min(min, fraction.getNumerator());
            min = Math.min(min, fraction.getDenominator());
            max = Math.max(max, fraction.getNumerator());
            max = Math.max(max, fraction.getDenominator());
        }
        assertThat(min).as("min").isEqualTo(-10);
        assertThat(max).as("max").isEqualTo(10);
    }
}
