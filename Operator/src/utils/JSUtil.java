package utils;  
  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;  
  
public class JSUtil {  
      
    public static void main(String[] args) throws Exception {   
    	System.out.println(executeFunction("JavaScript.js", "convert", "5050412", "D"));   
    	System.out.println(executeFunction("JavaScript2.js", "encryptedString", 
    			"{\"e\":{\"digits\":[1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false},\"d\":{\"digits\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false},\"m\":{\"digits\":[29191,56232,51305,54136,41993,2962,9932,275,10290,64092,59599,9566,39344,10059,42911,27335,4108,51115,46130,8852,28290,24194,12741,28413,6664,38700,49931,39242,6083,11511,7884,6288,28633,20943,57446,18434,53415,45732,82,53798,64848,28223,64942,12243,29168,34345,12263,47681,17447,21635,41296,34033,37,11620,14616,38771,6194,60946,17029,56027,62795,42927,17791,53656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false},\"digitSize\":128,\"chunkSize\":117,\"radix\":16,\"barrett\":{\"modulus\":{\"digits\":[29191,56232,51305,54136,41993,2962,9932,275,10290,64092,59599,9566,39344,10059,42911,27335,4108,51115,46130,8852,28290,24194,12741,28413,6664,38700,49931,39242,6083,11511,7884,6288,28633,20943,57446,18434,53415,45732,82,53798,64848,28223,64942,12243,29168,34345,12263,47681,17447,21635,41296,34033,37,11620,14616,38771,6194,60946,17029,56027,62795,42927,17791,53656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false},\"k\":64,\"mu\":{\"digits\":[3335,59036,40980,38600,38260,12899,6776,7520,3148,37707,20039,30199,40408,37089,27121,13543,51209,59743,52040,61151,17645,21738,46179,27525,26694,28413,37764,36311,5478,47023,36739,29566,34316,45554,31711,2273,10086,35678,14852,33382,11028,45227,3008,42369,13936,16574,41970,6271,22104,23438,24908,36689,41188,47346,7966,44963,11331,8399,37082,43050,61423,8978,62347,14509,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false},\"bkplus1\":{\"digits\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"isNeg\":false}}}", 
    			"3A%22111111%22%2C%22password%22%3A%22222222%22%2C%22checkCode%22%3A%223333%22%2C%22"));   
    }   
    
    private static Object executeFunction(String JSFilePath, String functionName, Object... params) throws Exception {   
        ScriptEngineManager mgr = new ScriptEngineManager();   
        ScriptEngine engine = mgr.getEngineByName("javascript");   
        engine.eval(readJSFile(JSFilePath));
        
        Invocable inv = (Invocable) engine;   
        Object res = inv.invokeFunction(functionName, params);
        
        return res;
    }   
    
    private static String readJSFile(String JSFilePath) throws Exception {
    	String filePath = JSUtil.class.getClassLoader().getResource(JSFilePath).getPath();
    	
        StringBuffer script = new StringBuffer();   
        File file = new File(filePath);   
        FileReader filereader = new FileReader(file);   
        BufferedReader bufferreader = new BufferedReader(filereader);   
        String tempString = null;   
        while ((tempString = bufferreader.readLine()) != null) {   
            script.append(tempString).append("\n");   
        }   
        bufferreader.close();   
        filereader.close();   
        return script.toString();   
    }  
}  