package com.aihuishou.pipeline.example.generic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Test;
import com.aihuishou.core.utils.GenericUtil;

import java.util.function.Consumer;

public class GenericTest {

    @Test
    public void testConsume() {
    }

    private <T> void consume(Consumer<T> consumer) {



    }

    @Test
    public void test() {

        Child child = new Child();

        System.out.println(child.getGenericType());
    }

    @Data
    private static class Parent<T> {

        private T data;

        public Class<T> getGenericType() {
            return GenericUtil.getSuperclassGenericType(this.getClass(), 0);
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Child extends Parent<Long> {}

}
