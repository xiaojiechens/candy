package com.group.cll.task;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.group.cll.constant.Constant;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！ 
@SpringBootTest
@WebAppConfiguration // 由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
public class FileLoaderTest {

	@Autowired
	private FileLoader fileLoader;
	
	@Test
	public void testImportDataToDB() throws IOException {
//		fileLoader.importDataToDB();
	}
	
	@Test
	public void testLoadFiles() throws IOException {
		fileLoader.loadFiles();
		
		for(Object object : Constant.accounts.get(0).getExchangeRecords()) {
			
			JSONObject exchangeRecord = (JSONObject)object;
			
			System.out.println(exchangeRecord.getInt("bet_number"));
		}
		
	}

}
