package com.group.cll.util;

import java.io.IOException;

import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TextFileReaderTest {

	private String filePath = "./777.yedgw.com_exchange_record.txt";
	
	@Test
	public void testReadJson() throws IOException {
		JSONObject jsonObject = TextFileReader.readJson(filePath, 0);
		long position = jsonObject.getLong("position");
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		
		System.out.println(position +"----"+ jsonArray);
	}
}
