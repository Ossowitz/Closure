package com.jkrotal.closure

import groovy.transform.CompileStatic

import java.util.function.Function
import java.util.stream.Stream

class ClosureRunner {

    static void main(String[] args) {
//        Function<Integer, Integer> func = value -> value + value
//        int result = func.apply(5)
//        println result

        Closure closure = {
            println it
            it + it
        }
        def result = closure(5)
        println result

//        Stream.of(1, 2, 3, 4, 5)
////                .map(func)
//                .map(closure)
//                .map(String::valueOf)
//                .forEach(System.out::println)
    }
}