package parallel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Run with -Djava.lang.invoke.stringConcat=MH_SB_SIZED, see for more options.
 */
public class StringPerformanceTest {
    private static final int COUNT = 100_000;

    @Test
    void testStringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < COUNT; i++) {
            sb.append(i % 10);
        }
        assertThat(sb.toString()).hasSize(COUNT);
    }

    @Test
    void testString() {
        String s = "";
        for (int i = 0; i < COUNT; i++) {
            s += i % 10;
        }
        assertThat(s).hasSize(COUNT);
    }
}
