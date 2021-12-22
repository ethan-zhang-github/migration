package com.aihuishou.pipeline.core.utils;

import java.util.function.BinaryOperator;

public class Functions {

    public static <T> BinaryOperator<T> firstOneBinaryOperator() {
        return (a, b) -> a;
    }

}
