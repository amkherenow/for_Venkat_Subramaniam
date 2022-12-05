# for_Venkat_Subramaniam

Hi Venkat,

This is extension to README_for_EditDistanceOf2Strings.md

I tried to improve my previous code (EditDistanceOf2Strings.java) -- minimized mutability, minimized loops, kept functions as pure as feasible, etc

New implementation (i.e. Dynamic Programming implementation of the problem solution in Java functional programming style) is of 3 files :
(1) EditDistance.java
Build the matrix that represents the dynamic programming table. Final cell value gives the min # of character changed required i.e. edit distance to convert 1st string into 2nd string.

(2) EditDistanceUtility.java
This is not so important. This is to print the matrix built in EditDistance.java

(3) EditDistanceTransitions.java
I traced the transitions from matrix's final cell until 1st cell (or zeroeth cell, as it represents (0,0) as the lenghts of the 2 input strings). And then printed the character transitions that 1st string goes through to eventually convert into the 2nd string.

Below are the most important changes, to adhere to functional programming style (and go away from imperative style) :
_________________________________________
(1) EditDistance.java :
-- record StringLengthsAsKey(int lenStr1, int lenStr2)
represents the key into the matrix, where key is built from the lenghts of the 2 input strings (which increases from 0 till input string actual length)

-- Map<K,V> has compute(K, Function<K, V, R>) method
to allow implicit memoization, which can be used to implement dynamic programming technique. map.compute() calls the functional-interface-object.apply(key, map.get(key)). I've ignored the value (is null anyway, in our case), and implemented computation of the matrix cell here. The return value R is used by map.compute() to run map.put(K, R) into the matrix

-- Thus mutation is still done, but is implicit mutation, via a feature provided by Java

_________________________________________
(2) EditDistanceTransitions.java :
-- Tracing the transitions from matrix final cell until first cell involves no Collection, and so there is nothing to convert into a stream. Imperative style can use an infinite while loop, from which we break upon reaching zeroeth cell of matrix (0,0). I implemented that using an infinite stream, which always contains only a single element, and that element produces the next element -- basically, I used your TailCall technique to trace the character transitions.

-- The TailCall.invoke() method returns the zeroeth transition, which marks the end of the infinite stream, and then I add this to the list of transitions.

-- While in the infinite stream, I made an additional change (from previous program) : I save only transitions that involve character changes, i.e. if both strings have same final character (at same stage during the algorithm) and string1 undergoes "replace character" action, I don't save this transition as there is no actual character change. Instead, if final characters are same, but string1 undergoes "add character" or "delete character" action, I save this transition.

-- Printing the transitions involve printing string1 (1st input string) with some character change. So, a pair of adjacent transitions are required to compute the next character transition. So, I used streams pipeline to transform list of transitions into a list of transition-pairs.

-- Now, for each transition-pair, I computed the actual character transition to produce the next version of 1st string (in it's path to eventually become the 2nd string).

-- For example, if (1, 0) and (4, 2) is a transition pair, it means few in-between transitions aren't noted as they don't involve an actual character change. The numbers tell us that the 2 strings of intermediate lengths 4 and 2 got transitioned into strings of intermediate lengths 1 and 0 respectively. This means, 1st string changed by 3 characters, and 2nd string changed by 2 characters. Relatively, this means, 1st string changed by 1 character vs no changes to 2nd string. This represents the "delete character" situation, where a character is removed from 3rd position of 1st string (4 is string length, so 4 - 1 = 3 is character position). All this work is done to determine the next character transition.


_________________________________
Sample output :

% java editdistance.EditDistanceTransitions     

5 character transitions are required to convert string "pqqrst" into string "qqttps"

The Transitions List :
[(6, 6), (4, 5), (4, 4), (4, 3), (1, 0), (0, 0)]

String "pqqrst" underwent below transitions to become "qqttps"
 delete character:  qqrst
replace character:  qqtst
    add character:  qqttst
    add character:  qqttpst
 delete character:  qqttps

