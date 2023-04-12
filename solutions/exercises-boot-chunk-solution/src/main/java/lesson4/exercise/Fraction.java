package lesson4.exercise;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Fraction {
    private final int numerator;
    private final int denominator;

    public Fraction(@JsonProperty("numerator") int numerator,
                    @JsonProperty("denominator") int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Fraction createRandomFraction() {
        return createRandomFraction(-10, 10);
    }

    public static Fraction createPositiveRandomFraction() {
        return createRandomFraction(1, 10);
    }

    public static Fraction createRandomFraction(int min, int max) {
        return new Fraction(randomInt(min, max), randomInt(min, max));
    }

    private static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    public String toString() {
        return String.format("(%d/%d)", numerator, denominator);
    }

    /**
     * @return fraction as BigDecimal rounded to 3 decimal digits
     * @throws ArithmeticException if denominator is 0
     */
    public BigDecimal toBigDecimal() {
        BigDecimal num = BigDecimal.valueOf(numerator);
        BigDecimal denom = BigDecimal.valueOf(denominator);
        BigDecimal fraction = num.divide(denom, 3, RoundingMode.HALF_UP);
        return fraction;
    }

    public String toDescription() {
        return String.format("%3d/%-3d = %7.3f", numerator, denominator, toBigDecimal());
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }
}
