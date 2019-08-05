package com.group.cll.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.group.cll.constant.Constant;
import com.group.cll.model.Account;
import com.group.cll.util.DateUtil;

@PropertySource(value = {"classpath:application.properties"})
@Component 
public class FileLoader {
	
	/** 日志文件路径 */
	@Value("${logDir}")
    private String logDir;
	
	/**
	 * 每秒钟扫描一次文件目录，将指定文件的内容读入内存
	 */
	@Scheduled(cron = "*/1 * * * * ?")// 每一秒钟执行一次
	public void loadFiles() throws IOException {
		
		// 读日志文件
		for(File subDir : new File(logDir).listFiles()) {
			if(subDir.isDirectory() && subDir.getName().endsWith(DateUtil.getToday("M-d"))) {
				
				for(Map.Entry<String, Account> accountMap : Constant.accounts.entrySet()) {
					Account account = accountMap.getValue();
					
					String webSiteName = account.getWebSiteName();
					
//					// 读形如"777.pzazq.com_exchange_record.txt"文件名的文件
//					BufferedReader br = null;
//					String line = null;
//					br = new BufferedReader(new InputStreamReader(new FileInputStream(subDir.getAbsolutePath()+"/"+webSiteName+"_exchange_record.txt")));
//					JSONArray jsonArray = new JSONArray();
//					while((line = br.readLine()) != null) {
//						jsonArray.add(JSONObject.fromObject(line));
//					}
//					account.setExchangeRecords(jsonArray);
//					br.close();
//					
//					// 读形如"777.ksgav.com_eliminate_record.txt"文件名的文件
//					br = new BufferedReader(new InputStreamReader(new FileInputStream(subDir.getAbsolutePath()+"/"+webSiteName+"_eliminate_record.txt")));
//					jsonArray = new JSONArray();
//					while((line = br.readLine()) != null) {
//						jsonArray.add(JSONObject.fromObject(line));
//					}
//					account.setEliminateRecords(jsonArray);
//					br.close();
//					
//					// 读形如"777.ksgav.com_wagers_record.txt"文件名的文件
//					br = new BufferedReader(new InputStreamReader(new FileInputStream(subDir.getAbsolutePath()+"/"+webSiteName+"_wagers_record.txt")));
//					jsonArray = new JSONArray();
//					while((line = br.readLine()) != null) {
//						jsonArray.add(JSONObject.fromObject(line));
//					}
//					account.setWagersRecords(jsonArray);
//					br.close();
				}
			}
		}
	}
}
