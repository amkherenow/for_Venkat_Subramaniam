package editdistance;

import java.util.*;
import java.util.stream.*;
import editdistance.*;

public class EditDistanceUtility
{
    private String string1;
    private String string2;
    private Map<EditDistance.StringLengthsAsKey, Integer> matrixMap;

    EditDistanceUtility() {}

    EditDistanceUtility(final EditDistance editdistanceObj,
            final String string1, final String string2) {
        final Map<EditDistance.StringLengthsAsKey, Integer> matrixMap =
            editdistanceObj.buildMatrix(string1, string2);
        initEditDistanceUtility(matrixMap, string1, string2);
        printMatrix();
    }
    
    private void initEditDistanceUtility(final Map<EditDistance.StringLengthsAsKey, Integer> matrixMap,
            final String string1, final String string2) {
        this.string1 = string1;
        this.string2 = string2;
        this.matrixMap = matrixMap;
    }

    public static void printMatrix(final Map<EditDistance.StringLengthsAsKey, Integer> matrixMap,
            final String string1, final String string2) {
        EditDistanceUtility editdistanceUtil = new EditDistanceUtility();
        editdistanceUtil.initEditDistanceUtility(matrixMap, string1, string2);
        editdistanceUtil.printMatrix();
    }

    private void printMatrix() {
        System.out.println("\nDEBUG :: Dynamic Programming memo table :");
        printString2();
        printString2Lengths();
        printLine();
        IntStream.rangeClosed(0, string1.length())
            .map(this::printString1CharacterAndLength)
            .forEach(lenStr1 -> IntStream.rangeClosed(0, string2.length())
                .mapToObj(lenStr2 -> new EditDistance.StringLengthsAsKey(lenStr1, lenStr2))
                .forEach(this::printMatrixCell));
        System.out.println("\n");
    }

    private void printString2() {
        System.out.printf("%4c|", ' ');
        IntStream.rangeClosed(0, string2.length())
            .forEach(this::printString2Character);
        System.out.println();
    }

    private void printString2Character(final int lenStr2) {
        System.out.printf("%5c", lenStr2 == 0 ? ' ' : string2.charAt(lenStr2 - 1));
    }

    private void printString2Lengths() {
        System.out.printf("%4c|", ' ');
        IntStream.rangeClosed(0, string2.length())
            .forEach(this::printString2Length);
        System.out.println();
    }

    private void printString2Length(final int lenStr2) {
        System.out.printf("%5d", lenStr2);
    }

    private void printLine() {
        System.out.printf("%4s|", "————");
        IntStream.rangeClosed(0, string2.length())
            .forEach(this::printDashes);
    }

    private void printDashes(final int lenStr2) {
        System.out.printf("%5s", "–––––");
    }

    private final int printString1CharacterAndLength(final int lenStr1) {
        System.out.printf("\n%c %d |",
            lenStr1 == 0 ? ' ' : string1.charAt(lenStr1 - 1),
            lenStr1);
        return lenStr1;
    }

    private void printMatrixCell(final EditDistance.StringLengthsAsKey strLensAsKey) {
        System.out.printf("%5d", matrixMap.get(strLensAsKey));
    }

    public static void main(String[] args) {
        final EditDistance editdistanceObj = new EditDistance();

        new EditDistanceUtility(editdistanceObj, "pqqrst", "qqttps");
    }
}