import java.util.*;
import static java.util.stream.Stream.*;
import static java.util.stream.Collectors.*;

// Final all numbers in a given range [snr, lnr],
// with the sum of digits in every number of a given value "esd"
// (using recursion to solve the problem
// and using java functional programming style)
public class NumbersOfSameDigitsSum
{
    // these 3 i/p values are effectively final, during the life of every test
    private int esd; // expected sum of digits
    private int snr; // smallest number in the range
    private int lnr; // largest number in the range
    private List<Long> optimalNums; // the numbers found for each test

    NumbersOfSameDigitsSum() {
        Scanner sc = new Scanner(System.in);
        iterate(1, t -> t + 1) // t: test
        .limit(sc.nextInt()) // # of tests
        .forEach(t -> solveProblem(sc));
    }

    private void solveProblem(Scanner sc) {
        esd = sc.nextInt();
        snr = sc.nextInt();
        lnr = sc.nextInt();
        optimalNums = new ArrayList<>();
        // nml: number max length
        final int nml = 1 + (int) Math.floor(Math.log10(lnr));
        // the recursive function
        findNums(nml, esd, 0L); // in: incomplete number
        prnOutput();
    }

    // nl: number length | sd: sum of digits | in: incomplete number
    private void findNums(final int nl, final int sd, final long in) {
        if (nl == 1) processFinalDigit(sd, in);
        else processOtherDigits(nl, sd, in);
    }

    // sd: sum of digits | in: incomplete number
    private void processFinalDigit(final int sd, final long in) {
        of(sd)
        .filter(d -> d <= 9) // d: digit
        .filter(d -> d >= 0)
        .map(d -> in + d)
        .filter(fn -> fn <= lnr) // fn: found number
        .filter(fn -> fn >= snr)
        .forEach(optimalNums::add);
        return;
    }

    // nl: number length | sd: sum of digits | in: incomplete number
    private void processOtherDigits(final int nl, final int sd, final long in) {
        // factor=1000 when nl=4 -- to fix the next digit in
        // appropriate position in potential final number
        final int factor = (int) Math.pow(10, nl - 1);
        iterate(0, d -> d + 1) // d: digit
        .limit(10)
        .forEach(d -> of(in + d * factor)
            .filter(nin -> nin <= lnr) // nin: new incomplete number
            .filter(nin -> (nin + factor - 1) >= snr)
            .forEach(nin -> findNums(nl - 1, sd - d, nin)));
    }

    private void prnOutput() {
        System.out.printf("\nThere are %d numbers in the range [%d, %d], with %d as the sum of digits.\n",
            optimalNums.size(), snr, lnr, esd);
        System.out.printf("The numbers : %s\n", optimalNums);
    }

    public static void main(String[] args) {
        new NumbersOfSameDigitsSum();
    }
}