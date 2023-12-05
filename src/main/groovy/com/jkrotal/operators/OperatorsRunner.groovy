package com.jkrotal.operators

assert 1 + 2 == 3
assert 4 - 3 == 1
assert 3 * 5 == 15
assert 3 / 2 == 1.5
assert 3.intdiv(2) == 1
assert 10 % 3 == 1
assert 2**3 == 8

int i = (int) (3 / 2)
def result = (3 / 2) as Integer // def asType(clazz)

println result <= 29

println 103 <=> 130