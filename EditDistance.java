package editdistance;

import java.util.*;
import java.util.stream.*;

public class EditDistance
{
    public static record StringLengthsAsKey(int lenStr1, int lenStr2) {}

    private String string1;
    private String string2;
    private Map<StringLengthsAsKey, Integer> matrixMap;

    private void compute(final String inputStr1, final String inputStr2) {
        buildMatrix(inputStr1, inputStr2);
    }

    public Map<StringLengthsAsKey, Integer> buildMatrix(final String inputStr1, final String inputStr2) {
        string1 = inputStr1;
        string2 = inputStr2;
        createMatrixAsAMap();
        computeMatrixData();
        return matrixMap;
    }

    private void createMatrixAsAMap() {
        matrixMap = new HashMap<>();
    }

    private void computeMatrixData() {
        IntStream.rangeClosed(0, string1.length())
            .forEach(lenString1 -> IntStream.rangeClosed(0, string2.length())
                .mapToObj(lenString2 -> new StringLengthsAsKey(lenString1, lenString2))
                .forEach(strLensAsKey -> matrixMap.compute(strLensAsKey, this::computeMatrixCellValue)));
    }

    private int computeMatrixCellValue(final StringLengthsAsKey strLensAsKey, final Integer ignorableMatrixCellValue) {
        final int lenStr1 = strLensAsKey.lenStr1();
        final int lenStr2 = strLensAsKey.lenStr2();

        if (lenStr1 == 0)
            return lenStr2;

        if (lenStr2 == 0)
            return lenStr1;

        return computeCellValAsMinFrom3Transitions(lenStr1, lenStr2);
    }

    private int computeCellValAsMinFrom3Transitions(final int lenStr1, final int lenStr2) {
        final int addCharValue = computeEditDistanceWhenCharacterAdded(lenStr1, lenStr2);
        final int delCharValue = computeEditDistanceWhenCharacterDeleted(lenStr1, lenStr2);
        int cellValue = Math.min(addCharValue, delCharValue);
        final int replaceCharValue = computeEditDistanceWhenCharacterReplaced(lenStr1, lenStr2);
        return Math.min(cellValue, replaceCharValue);
    }

    public int computeEditDistanceWhenCharacterAdded(final int lenStr1, final int lenStr2) {
        final StringLengthsAsKey strLensAsKey = new StringLengthsAsKey(lenStr1, lenStr2 - 1);
        return 1 + matrixMap.get(strLensAsKey);
    }

    public int computeEditDistanceWhenCharacterDeleted(final int lenStr1, final int lenStr2) {
        final StringLengthsAsKey strLensAsKey = new StringLengthsAsKey(lenStr1 - 1, lenStr2);
        return 1 + matrixMap.get(strLensAsKey);
    }

    public int computeEditDistanceWhenCharacterReplaced(final int lenStr1, final int lenStr2) {
        final StringLengthsAsKey strLensAsKey = new StringLengthsAsKey(lenStr1 - 1, lenStr2 - 1);
        int replaceCharValue = matrixMap.get(strLensAsKey);
        if (doesBothStringsEndWithSameCharacter(lenStr1, lenStr2))
            return replaceCharValue;
        else 
            return 1 + replaceCharValue;
    }

    public boolean doesBothStringsEndWithSameCharacter(final int lenStr1, final int lenStr2) {
        return string1.charAt(lenStr1 - 1) == string2.charAt(lenStr2 - 1);
    }

    public static void main(String[] args) {
        EditDistance editdistanceObj = new EditDistance();

        editdistanceObj.compute("pqqrst", "qqttps");
    }
}