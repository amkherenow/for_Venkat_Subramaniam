import java.util.*;
import static java.util.stream.Stream.*;
import static java.util.stream.Collectors.*;

// Find all numbers in a given range, 
// such that the sum of digits in every number equals a given value
// (used recursion to solve the problem
// and using java functional programming style)
public class NumbersOfSameDigitsSum
{
    // these 3 i/p values are effectively final, during the life of every test
    private int expectedSumOfDigits;
    private long smallestNumInRange; 
    private long largestNumInRange; 
    private List<Long> optimalNums; // the numbers found during each test

    NumbersOfSameDigitsSum() {
        Scanner scan = new Scanner(System.in);
        iterate(1, test -> test + 1) 
        .limit(scan.nextInt()) 
        .forEach(test -> solveProblem(scan));
    }

    private void solveProblem(Scanner scan) {
        expectedSumOfDigits = scan.nextInt();
        smallestNumInRange = scan.nextLong();
        largestNumInRange = scan.nextLong();
        optimalNums = new ArrayList<>();
        // numMaxLen: number max length
        final int numMaxLen = 1 + (int) Math.floor(Math.log10(largestNumInRange));
        // the recursive function
        findNums(numMaxLen, expectedSumOfDigits, 0L); // in: incomplete number
        prnOutput();
    }

    private void findNums(final int numLen, final int sumOfDigits, final long incompleteNum) {
        if (numLen == 1) processFinalDigit(sumOfDigits, incompleteNum);
        else processOtherDigits(numLen, sumOfDigits, incompleteNum);
    }

    private void processFinalDigit(final int sumOfDigits, final long incompleteNum) {
        of(sumOfDigits)
        .filter(digit -> digit <= 9)
        .filter(digit -> digit >= 0)
        .map(digit -> incompleteNum + digit)
        .filter(completeNum -> completeNum <= largestNumInRange) 
        .filter(completeNum -> completeNum >= smallestNumInRange)
        .forEach(optimalNums::add);
        return;
    }

    private void processOtherDigits(final int numLen, final int sumOfDigits, final long incompleteNum) {
        // factor=1000 when numLen = 4 -- to fix the next digit in
        // appropriate position in potential final number
        final int factor = (int) Math.pow(10, numLen - 1);
        iterate(0, digit -> digit + 1)
        .limit(10)
        .forEach(digit -> of(incompleteNum + digit * factor)
            .filter(newIncompleteNum -> newIncompleteNum <= largestNumInRange) 
            .filter(newIncompleteNum -> (newIncompleteNum + factor - 1) >= smallestNumInRange)
            .forEach(newIncompleteNum -> findNums(numLen - 1, sumOfDigits - digit, newIncompleteNum)));
    }

    private void prnOutput() {
        System.out.printf("\nThere are %d numbers in the range [%d, %d], with %d as the sum of digits.\n",
            optimalNums.size(), smallestNumInRange, largestNumInRange, expectedSumOfDigits);
        System.out.printf("The numbers : %s\n", optimalNums);
    }

    public static void main(String[] args) {
        new NumbersOfSameDigitsSum();
    }
}