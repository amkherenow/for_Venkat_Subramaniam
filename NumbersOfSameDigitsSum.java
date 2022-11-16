import java.util.*;
import java.util.stream.*;
import static java.util.stream.Stream.*;
import static java.util.stream.Collectors.*;

// Find all numbers in a given range, 
// such that the sum of digits in every number equals a given value
// (used recursion to solve the problem
// and using java functional programming style)
public class NumbersOfSameDigitsSum
{
    NumbersOfSameDigitsSum() {
        final Scanner read_ip = new Scanner(System.in);
        final int numberOfTests = read_ip.nextInt();
        IntStream.rangeClosed(1, numberOfTests) 
            .forEach(test -> solveProblem(read_ip));
    }

    private void solveProblem(Scanner read_ip) {
        final int expectedSumOfDigits = read_ip.nextInt();
        final long smallestNumInRange = read_ip.nextLong();
        final long largestNumInRange = read_ip.nextLong();
        // to store the numbers found during each test
        final List<Long> optimalNums = new ArrayList<>();
        final int numMaxLen = 1 + (int) Math.floor(Math.log10(largestNumInRange));
        // the recursive function
        findNums(expectedSumOfDigits, smallestNumInRange, largestNumInRange, optimalNums, numMaxLen, expectedSumOfDigits, 0L);
        prnOutput(expectedSumOfDigits, smallestNumInRange, largestNumInRange, optimalNums);
    }

    private void findNums(final int expectedSumOfDigits, final long smallestNumInRange, final long largestNumInRange,
                            final List<Long> optimalNums, final int numLen, final int sumOfDigits, final long incompleteNum) {
        if (numLen == 1) processFinalDigit(expectedSumOfDigits, smallestNumInRange, largestNumInRange, optimalNums,
                                            sumOfDigits, incompleteNum);
        else processOtherDigits(expectedSumOfDigits, smallestNumInRange, largestNumInRange, optimalNums, numLen,
                                sumOfDigits, incompleteNum);
    }

    private void processFinalDigit(final int expectedSumOfDigits, final long smallestNumInRange, final long largestNumInRange,
                                    final List<Long> optimalNums, final int sumOfDigits, final long incompleteNum) {
        of(sumOfDigits)
        .filter(digit -> digit <= 9)
        .filter(digit -> digit >= 0)
        .map(digit -> incompleteNum + digit)
        .filter(completeNum -> completeNum <= largestNumInRange) 
        .filter(completeNum -> completeNum >= smallestNumInRange)
        .forEach(optimalNums::add);
    }

    private void processOtherDigits(final int expectedSumOfDigits, final long smallestNumInRange,
                                    final long largestNumInRange, final List<Long> optimalNums,
                                    final int numLen, final int sumOfDigits, final long incompleteNum) {
        // factor=1000 when numLen = 4 -- to fix the next digit in
        // appropriate position in potential final number
        final int factor = (int) Math.pow(10, numLen - 1);
        IntStream.range(0, 10)
        .forEach(digit -> of(incompleteNum + digit * factor)
            .filter(newIncompleteNum -> newIncompleteNum <= largestNumInRange) 
            .filter(newIncompleteNum -> (newIncompleteNum + factor - 1) >= smallestNumInRange)
            .forEach(newIncompleteNum -> findNums(expectedSumOfDigits, smallestNumInRange, largestNumInRange,
                                                    optimalNums, numLen - 1, sumOfDigits - digit, newIncompleteNum)));
    }

    private void prnOutput(final int expectedSumOfDigits, final long smallestNumInRange,
                            final long largestNumInRange, final List<Long> optimalNums) {
        System.out.printf("\nThere are %d numbers in the range [%d, %d], with %d as the sum of digits.\n",
            optimalNums.size(), smallestNumInRange, largestNumInRange, expectedSumOfDigits);
        System.out.printf("The numbers : %s\n", optimalNums);
    }

    public static void main(String[] args) {
        new NumbersOfSameDigitsSum();
    }
}
