package chunk;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;

//tag::code[]
public class SimpleWriter implements ItemWriter<String> {

    @Override
    public void write(List<? extends String> items) {
        System.out.println(items.stream().collect(Collectors.joining(", ")));
    }

}
//end::code[]