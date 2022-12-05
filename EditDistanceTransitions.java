package editdistance;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import static java.util.stream.Stream.*;
import static java.util.stream.Collectors.*;
import editdistance.*;

public class EditDistanceTransitions
{
    private record Transition(int lenStr1, int lenStr2) {
        public String toString() {
            return String.format("(%d, %d)", lenStr1, lenStr2);
        }
    }

    private record TwinTrans(Transition trans1, Transition trans2) {
        public String toString() {
            return String.format("\n{%s, %s}", trans1, trans2);
        }
    }

    private enum Action {ADD_CHAR, DEL_CHAR, REPLACE_CHAR, NONE}

    private String string1;
    private String string2;
    private EditDistance editdistanceObj;
    private Map<EditDistance.StringLengthsAsKey, Integer> matrixMap;
    private List<Transition> transitions;
    private String[] arrayString1;

    private interface TailCall<T>
    {
        public abstract TailCall<T> apply();
        public default boolean isComplete() { return false; }
        public default T data() { return null; }
        public default T invoke() {
            return iterate(this, TailCall::apply)
                .filter(TailCall::isComplete)
                .map(TailCall::data)
                .findFirst()
                .get();
        }
    }

    EditDistanceTransitions(final EditDistance editdistanceObj,
            final String string1, final String string2) {
        Map<EditDistance.StringLengthsAsKey, Integer> matrixMap =
                editdistanceObj.buildMatrix(string1, string2);
        
        //EditDistanceUtility.printMatrix(matrixMap, string1, string2);

        System.out.printf("\n%d character transitions are required to convert string \"%s\" into string \"%s\"\n",
            matrixMap.get(new EditDistance.StringLengthsAsKey(string1.length(), string2.length())),
            string1, string2);

        initEditDistanceTransitions(editdistanceObj, matrixMap, string1, string2);
        traceTransitions();
        printTheTransitionsList();
        printTransitions();
    }

    private void initEditDistanceTransitions(final EditDistance editdistanceObj,
            final Map<EditDistance.StringLengthsAsKey, Integer> matrixMap,
            final String string1, final String string2) {
        this.editdistanceObj = editdistanceObj;
        this.matrixMap = matrixMap;
        this.string1 = string1;
        this.string2 = string2;
        transitions = new ArrayList<>();
    }

    private void traceTransitions() {
        final Transition zeroethTransition =
            traceNextTransition(new Transition(string1.length(), string2.length()))
            .invoke();
        transitions.add(zeroethTransition);
    }

    private TailCall<Transition> traceNextTransition(final Transition trans) {
        if (isZeroethTransition(trans))
            return () -> endTracing(trans);
        else
            return () -> doTracing(trans);
    }

    private boolean isZeroethTransition(final Transition trans) {
        return (trans.lenStr1() == 0) && (trans.lenStr2() == 0);
    }

    private <T> TailCall<T> endTracing(final T t) {
        return new TailCall<T>() {
            public TailCall<T> apply() { return null; }
            public boolean isComplete() { return true; }
            public T data() { return t; }
        };
    }

    private class CheckEndCharacterClass 
    {
        private Supplier<Boolean> sameEndCharacterFunc;
        private boolean isFunctionExecuted;
        private boolean booleanValue;
        CheckEndCharacterClass(final Supplier<Boolean> supplierFunction) {
            sameEndCharacterFunc = supplierFunction;
            isFunctionExecuted = false;
        }
        boolean call() {
            if (isFunctionExecuted == false) {
                booleanValue = sameEndCharacterFunc.get();
                isFunctionExecuted = true;
            }
            return booleanValue;
        }
    }

    private TailCall<Transition> doTracing(final Transition trans) {
        final int lenStr1 = trans.lenStr1();
        final int lenStr2 = trans.lenStr2();

        final int addCharValue = lenStr2 == 0 ? 1 << 30 : editdistanceObj.computeEditDistanceWhenCharacterAdded(lenStr1, lenStr2);
        final int delCharValue = lenStr1 == 0 ? 1 << 30 : editdistanceObj.computeEditDistanceWhenCharacterDeleted(lenStr1, lenStr2);

        int transitionMinValue = -1;
        Transition newTrans = null;

        if (addCharValue < delCharValue) {
            transitionMinValue = addCharValue;
            newTrans = new Transition(lenStr1, lenStr2 - 1);
        } else {
            transitionMinValue = delCharValue;
            newTrans = new Transition(lenStr1 - 1, lenStr2);
        }

        final int replaceCharValue = (lenStr1 == 0) || (lenStr2 == 0)
            ? 1 << 30
            : editdistanceObj.computeEditDistanceWhenCharacterReplaced(lenStr1, lenStr2);

        boolean replaceChar = true;
        if (replaceCharValue < transitionMinValue)
            newTrans = new Transition(lenStr1 - 1, lenStr2 - 1);
        else
            replaceChar = false;

        final CheckEndCharacterClass checkEndCharacterObj = new CheckEndCharacterClass(
            () -> editdistanceObj.doesBothStringsEndWithSameCharacter(lenStr1, lenStr2));

        if ((lenStr1 == 0) || (lenStr2 == 0) || !checkEndCharacterObj.call()
                || (checkEndCharacterObj.call() && (replaceChar == false)))
            transitions.add(trans);

        return traceNextTransition(newTrans);
    }

    private void printTheTransitionsList() {
        System.out.println("\nThe Transitions List :");
        System.out.println(transitions);
    }

    private void printTransitions() {
        Iterator<Transition> iter = transitions.stream().iterator();
        List<TwinTrans> transitionPairs = transitions.stream()
                .skip(1)
                .map(trans -> new TwinTrans(trans, iter.next()))
                .collect(toList());
        //System.out.printf("\nThe List of Transition Pairs :\n %s\n", transitionPairs);

        System.out.printf("\nString \"%s\" underwent below transitions to become \"%s\"\n", string1, string2);

        arrayString1 = " ".concat(string1).split("");

        IntStream.rangeClosed(1, transitionPairs.size())
            .map(index -> transitionPairs.size() - index)
            .mapToObj(reverseIdx -> transitionPairs.get(reverseIdx))
            .map(this::computeNextTransitionOfString1)
            .forEach(System.out::println);
    }

    private String computeNextTransitionOfString1(final TwinTrans transitionPair) {
        final Transition trans1 = transitionPair.trans1();
        final Transition trans2 = transitionPair.trans2();

        final int charsShiftStr1 = trans2.lenStr1() - trans1.lenStr1();
        final int charsShiftStr2 = trans2.lenStr2() - trans1.lenStr2();

        final int minCharsShift = Math.min(charsShiftStr1, charsShiftStr2);

        final int relativeCharsShiftStr1 = charsShiftStr1 - minCharsShift;
        final int relativeCharsShiftStr2 = charsShiftStr2 - minCharsShift;

        final Action action = getAction(relativeCharsShiftStr1 - relativeCharsShiftStr2);

        applyActionToString1Array(trans2, action);

        return String.format("%17s", getActionInText(action)) + ": " + String.join("", arrayString1);
    }

    private Action getAction(final int relativeCharsShiftState) {
        return switch(relativeCharsShiftState) {
            case 0 -> Action.REPLACE_CHAR;
            case 1 -> Action.DEL_CHAR;
            case -1 -> Action.ADD_CHAR;
            default -> Action.NONE;
        };
    }

    private void applyActionToString1Array(final Transition transition, final Action action) {
        switch(action) {
            case ADD_CHAR :
                final char charToAdd = string2.charAt(transition.lenStr2() - 1);
                arrayString1[transition.lenStr1()] += charToAdd;
                break;

            case DEL_CHAR :
                int idxStr1 = transition.lenStr1();
                arrayString1[idxStr1] = arrayString1[idxStr1].substring(1);
                break;

            case REPLACE_CHAR :
                final char charToReplaceWith = string2.charAt(transition.lenStr2() - 1);
                idxStr1 = transition.lenStr1();
                arrayString1[idxStr1] = charToReplaceWith + arrayString1[idxStr1].substring(1);
                break;

            default :
                break;
        };
    }

    private String getActionInText(final Action action) {
        return switch(action) {
            case ADD_CHAR -> "add character";
            case DEL_CHAR -> "delete character";
            case REPLACE_CHAR -> "replace character";
            default -> "ignore!!!";
        };
    }

    public static void main(String[] args) {
        EditDistance editdistanceObj = new EditDistance();

        new EditDistanceTransitions(editdistanceObj, "pqqrst", "qqttps");
        new EditDistanceTransitions(editdistanceObj, "qqttps", "pqqrst");
        new EditDistanceTransitions(editdistanceObj, "attivilli", "kuchibhotla");
        new EditDistanceTransitions(editdistanceObj, "kuchibhotla", "attivilli");
        new EditDistanceTransitions(editdistanceObj, "hello", "there");
        new EditDistanceTransitions(editdistanceObj, "there", "hello");
        new EditDistanceTransitions(editdistanceObj, "money", "monkey");
        new EditDistanceTransitions(editdistanceObj, "monkey", "money");
        new EditDistanceTransitions(editdistanceObj, "skip", "purge");
        new EditDistanceTransitions(editdistanceObj, "purge", "skip");
        new EditDistanceTransitions(editdistanceObj, "bounce", "ounce");
        new EditDistanceTransitions(editdistanceObj, "ounce", "bounce");
        new EditDistanceTransitions(editdistanceObj, "movva", "gutta");
        new EditDistanceTransitions(editdistanceObj, "gutta", "movva");
        new EditDistanceTransitions(editdistanceObj, "abcd", "abab");
        new EditDistanceTransitions(editdistanceObj, "abab", "abcd");
        new EditDistanceTransitions(editdistanceObj, "abcd", "aabb");
        new EditDistanceTransitions(editdistanceObj, "aabb", "abcd");
    }
}
