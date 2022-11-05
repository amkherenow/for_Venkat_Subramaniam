import javafx.util.Pair;
import java.util.*;
import static java.util.stream.Stream.iterate;
import static java.util.stream.Collectors.toList;
public class DoubleTheListOfNumbers
{
    public static void main(String[] args) {
        new DoubleTheListOfNumbers();
    }
    private List<Integer> list;
    DoubleTheListOfNumbers() {
        init_the_list();
        System.out.println(list);
        double_the_list_numbers();
        System.out.println(list);
    }
    private void init_the_list() {
        list = iterate(0, index -> index + 1)
                .limit(5)
                .collect(toList());
    }
    private void double_the_list_numbers() {
        /*
        list = list.stream()
                    .map(value -> value << 1)
                    .collect(toList());
        */
        iterate(0, index -> index + 1)
        .limit(5)
        .map(index -> new Pair<Integer,Integer>(index, list.get(index) << 1))
        .forEach(pair -> list.set(pair.getKey(), pair.getValue()));
    } 
}