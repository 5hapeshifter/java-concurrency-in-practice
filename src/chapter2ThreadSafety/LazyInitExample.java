package chapter2ThreadSafety;

import java.util.ArrayList;
import java.util.List;

public class LazyInitExample {

    private List<String> items = null;

    public List<String> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            System.out.printf("Items initialized by thread: %s items hash: %s\n", Thread.currentThread().getName(), items.hashCode());
        }
        return items;
    }

}
