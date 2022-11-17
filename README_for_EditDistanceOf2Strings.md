# for_Venkat_Subramaniam

Hi Venkat,

I'm giving details about the problem and solution below. I request you to please read the java functional style code in EditDistanceOf2Strings.java, and kindly suggest corrections/improvements/mistakes/etc. I tried a lot to make as much improvement as I could, keeping in mind the pointers you gave in your lectures.

Thanks,
Madhukiran

----------------------

Problem :
Given 2 strings, find the min # of characters to be changed to convert 1st string into 2nd string. This concept is called "Edit Distance"

Solution -- Theory :
A string can be modified either by adding a character (to it's end) or deleting a character (from it's end) or replacing a character (at it's end).
f(x,y) = min( 1 + f(x+1, y), 1 + f(x-1, y), 1 + f(x-1, y-1) )
x & y represent the sizes of the 2 strings at that point in the algorithm

The 1 in each OP is the cost of the character transition.

But, "add character" is looking ahead, whereas the other 2 OPs are looking backwards. Looking ahead is troublesome as f(x+1, y) isn't known at the time of computing f(x,y). Hence replace "add char" to 1st string with "delete char" from 2nd string.
f(x,y) = min( 1 + f(x, y-1), 1 + f(x-1, y), 1 + f(x-1, y-1) )

How are the 2 OPs the same?
Suppose abc and xyz are the 2 strings. We add z to 1st string, and they become abcz and xyz. The next OP has to be "replace char" as the 2 end characters are the same, which also means there is nothing to replace. So the strings become abc and xy. Thus, effectively, "add char" to 1st string became "del char" from 2nd string.


Solution -- Implementation :
3 steps
#1: Build memo table using Dynamic Programming technique
#2: Find all character transitions
#3: Print character transitions


#1: Build memo table
-- Create table of size 1+A x 1+B where A & B are the lengths of the 2 strings. String length ranges from 0 to A and 0 to B respectively for the 2 strings.

-- 0-th column (x,0) for x in [0,A] can be filled with x. Similarly 0-th row (0,y) for y in [0,B] can be filled with y -- because when 1 string is empty, edit distance would be the size of the other string.

-- Now for x & y in the range [1,A] and [1,B] respectively, f(x,y) = min( <as mentioned above in Theory> )
With only 1 change. If the 2 strings have the same character at indices x and y, the cost of "replace char" is 0. 
and then the min() function would include f(x-1, y-1) for the "replace char" situation -- i.e. without the extra +1 cost

-- After the entire memo table is filled, f(A,B) represents the edit distance


#2: Find the character transitions
-- Start from indices pair (A,B). Add at the front of a deque. Stop when we reach (0,0)

-- We do same actions as in previous section, except that the # of slots to process are O(A+B) as we move only from (A,B) to (0,0)

-- When we are at (x,y), we move to (x-1,y-1) or (x-1,y) or (x,y-1), depending on which among the 3 OPs was cheaper. And, we also take into consideration the situation of not having to put an extra cost for character replacement (as I wrote above).

-- Add the new indices pair at the front of Deque, and proceed ahead with this algo

-- (0,0) becomes the last pair added to Deque's front


#3: Print the character transitions
-- Print 1st string. When we print 1st string again, the comparison tells us about the character transition.

-- Start with indices pair (0,0) at the start of Deque, and stop at pair (A,B)

-- Process 2 pairs at a time -- (x1,y1) and (x2,y2)

-- If the transition represents character deletion :
delete the character at position x2 in 1st string, and print 1st string, along with the tag "delete"

-- If the transition represents character addition :
Fetch the character at position y2 in 2nd string, and add at position x2 in 1st string, and print 1st string, along with the tag "add"

-- If the transition represents character replacement :
Fetch the character at position y2 in 2nd string, and replace the character at position x2 in 1st string, and print 1st string, along with the tag "replace"


Example :
Input: A=pqqrst, B=qqttps

Output :
(from memo table -- f(A,B))
5 is the edit distance from string "pqqrst" to string "qqttps".

(from "find transitions" OP)
The indices pairs representing the transitions :
[(0, 0), (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6)]

(from "print transitions" OP)
The transitions...
pqqrst
replace:  qqqrst
replace:  qqtrst
replace:  qqttst
replace:  qqttpt
replace:  qqttps
