package priv.ethanzhang.migration.example.generic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Test;
import priv.ethanzhang.migration.core.utils.GenericUtil;

public class GenericTest {

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
