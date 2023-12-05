package com.jkrotal.operators

import groovy.transform.CompileStatic

import java.sql.Date as SqlDate

class VariablesRunner {

    /**
     * byte, short, int, long, BigInteger
     * float, double, BigDecimal
     * char
     * boolean
     */
    @CompileStatic
    static void main(String[] args) {
        int value1 = 5;
        def value2 = 5;
        def value3 = 36G
        def value4 = 3.3G
//        new SqlDate(100L)
    }
}