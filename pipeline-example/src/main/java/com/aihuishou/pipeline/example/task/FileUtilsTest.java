package com.aihuishou.pipeline.example.task;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileUtilsTest {

    @Test
    public void test() throws IOException {
        File target = new File("src/main/resources/demo3.txt");
        FileUtils.writeLines(target, Arrays.asList("123", "456"), true);
        FileUtils.writeLines(target, Arrays.asList("789", "321"), true);
    }

}
