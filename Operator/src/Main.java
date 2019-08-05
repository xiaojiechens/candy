package com.sc;  
  
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader;  
  
import javax.script.Invocable;  
import javax.script.ScriptEngine;  
import javax.script.ScriptEngineManager;  
  
public class JsTest {  
      
    public static void main(String[] args) throws Exception {   
        testJSFile();   
    }   
    
    private static void testJSFile() throws Exception {   
        ScriptEngineManager mgr = new ScriptEngineManager();   
        ScriptEngine engine = mgr.getEngineByName(”javascript”);   
        engine.eval(readJSFile());   
        Invocable inv = (Invocable) engine;   
        Object res = (Object) inv.invokeFunction(”convert”, new String[] { “5050412”, “D” });   
        System.out.println(”res:” + res);   
    }   
    
    private static String readJSFile() throws Exception {   
        StringBuffer script = new StringBuffer();   
        File file = new File(“E:\\workspace\\test4\\WebRoot\\test.js”);   
        FileReader filereader = new FileReader(file);   
        BufferedReader bufferreader = new BufferedReader(filereader);   
        String tempString = null;   
        while ((tempString = bufferreader.readLine()) != null) {   
            script.append(tempString).append(”\n”);   
        }   
        bufferreader.close();   
        filereader.close();   
        return script.toString();   
    }  
}  