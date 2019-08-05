package com.group.cll.util;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TextFileReader {
	
	/**
	 * 
	 * @param filePath
	 * @param startPosition
	 * @return {"position": 100, data:[{},{}]}
	 * @throws IOException
	 */
	public static JSONObject readJson(String filePath, long startPosition) throws IOException {
		
		JSONObject returnValue = new JSONObject();
	
		RandomAccessFile raf = new RandomAccessFile(filePath,"r");
		raf.seek(startPosition);
		
		JSONArray data = new JSONArray();
		
		JSONObject jsonObject = null;
		while((jsonObject = TextFileReader.readLine(raf)) != null) {
			
			if(jsonObject.getString("data") == null){
				break;
			}
			try {
				data.add(JSONObject.fromObject(jsonObject.getString("data")));
			} catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
			startPosition = startPosition + jsonObject.getInt("length");
		}
		raf.close();
		
		returnValue.put("position", startPosition);
		returnValue.put("data", data);
		
		return returnValue;
	}

	/**
	 * 读一行数据，并返回当前数据的长度
	 * @param raf
	 * @return
	 * @throws IOException
	 */
	public final static JSONObject readLine(RandomAccessFile raf) throws IOException {
		
		JSONObject returnValue = new JSONObject();
		
        StringBuffer input = new StringBuffer();
        int c = -1;
        int length = 0;
        boolean eol = false;

        while (!eol) {
            switch (c = raf.read()) {
            case -1:
            case '\n':
            	length ++;
                eol = true;
                break;
            case '\r':
            	length ++;
                eol = true;
                long cur = raf.getFilePointer();
                if ((raf.read()) != '\n') {
                	raf.seek(cur);
                } else {
                	length ++;
                }
                break;
            default:
            	length ++;
                input.append((char)c);
                break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        
        returnValue.put("length", length);
        returnValue.put("data", input.toString());
        
        return returnValue;
    }
	
}
