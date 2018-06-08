package com.rengu.machinereadingcomprehension.Utils;

import org.python.util.PythonInterpreter;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

public class PythonUtils {

    public static void excuScript() throws FileNotFoundException {
        PythonInterpreter interpreter = new PythonInterpreter();
        String filePath = ResourceUtils.getFile("classpath:Main.py").getAbsolutePath();
        interpreter.execfile(filePath);
    }
}
