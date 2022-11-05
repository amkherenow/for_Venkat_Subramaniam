import static java.util.stream.Stream.iterate;
import static java.util.stream.Collectors.toCollection;
import java.util.*;
public class AccumulateNumbers
{
    public static void main(String[] args) {
        new AccumulateNumbers();
    }
    private ArrayList<ArrayList<Integer>> list_of_lists;
    private ArrayList<Integer> single_list;
    AccumulateNumbers() {
        init_list_of_lists_of_numbers();
        System.out.println(list_of_lists);
        single_list = new ArrayList<>();
        accumulate_the_numbers();
        System.out.println(single_list);
    }
    private void init_list_of_lists_of_numbers() {
        list_of_lists = iterate(0, outer_index -> outer_index + 1)
                    .limit(5)
                    .map(outer_index
                            -> iterate(0, inner_index -> inner_index + 1)
                                .map(value -> outer_index)
                                .limit(outer_index)
                                .collect(toCollection(ArrayList::new))
                        )
                    .collect(toCollection(ArrayList::new));
    }
    private void accumulate_the_numbers() {
        iterate(0, outer_index -> outer_index + 1)
        .limit(5)
        .forEach(AccumulateNumbers::process_inner_list);
        /*
        .forEach(outer_index
                    -> list_of_lists.get(outer_index).stream()
                                                        .forEach(single_list::add)
                );
        */
    }
    private void process_inner_list(int outer_index) {
        list_of_lists.get(outer_index).stream()
                                .forEach(single_list::add);
    }
}