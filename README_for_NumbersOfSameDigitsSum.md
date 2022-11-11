# for_Venkat_Subramaniam

___________________________________
Program :
NumbersOfSameDigitsSum.java

___________________________________
Problem :
Find all numbers in an input range of numbers, such that the sum of digits in each number equals an input value

___________________________________
Solution :
f(n, s) = Σf(n - 1, s - d) ∀d in [0,9]

f(n, s): get the list of all numbers of lengths in the range [0,n], and sum of digits of each number 's'

f(n-1, s-d): get all numbers in range [0,n-1], and sum of digits = s - d,
  where d is a digit in the range [0,9]

___________________________________
Analysis :
DP algo runs in O(nlogn) but recursive algo can be run in O(n) time. So, wrote the recursive algo in java functional programming style

___________________________________
Implementation :
Recursive function findNums(nl, sd, in) :
nl: number length (same as n above)
sd: sum of digits (same as s above)
in: incomplete number

Example :
get numbers in range [642,953] for sum of digits = 23
max number length = 3

findNums(3, 23, 0) :
for digits in range [0,9] call findNums(2, 23 - d, d * 100)

for every digit considered, insert into the left most position of the current incomplete number

for d = 5 when nl=3
findNums(2, 18, 500)
call findNums(1, 18-d, 500 + d*10)

for d=3 when nl=2
findNums(1, 15, 530)
15 isn't a 1 digit number. So, we reject this number

If we're in findNums(1, 9, 860)
the final complete number would be 869
add 869 to the optimal list of numbers

during every run of findNums() :
if 5400 is the incomplete number
reject if it's > upper limit of numbers range
or reject if 5499 is < lower limit of numbers range
