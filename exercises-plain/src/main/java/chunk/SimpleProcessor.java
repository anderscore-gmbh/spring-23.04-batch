package chunk;

import org.springframework.batch.item.ItemProcessor;

//tag::code[]
public class SimpleProcessor implements ItemProcessor<Integer, String> {

    @Override
    public String process(Integer item) {
        return String.format("Item %03d", item);
    }

}
//end::code[]