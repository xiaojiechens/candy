package com.group.cll.init;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.group.cll.constant.Constant;
import com.group.cll.model.Account;
import com.group.cll.model.Activity;

import net.sf.json.JSONObject;

@Component
public class ConfigLoader implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
		// 加载账号文件
		Properties accountProperties = new Properties();
		accountProperties.load(new FileInputStream("account.properties"));
		
		Map<String, Account> accounts = new HashMap<>();
		for(Map.Entry<Object, Object> AccountObject : accountProperties.entrySet()) {
			
			Account account = (Account) JSONObject.toBean(JSONObject.fromObject(AccountObject.getValue()), Account.class);
			
			try{
				Properties activityProperties = new Properties();
				activityProperties.load(this.getClass().getClassLoader().getResourceAsStream(account.getWebSiteName()+"_activity.properties"));
				
				Map<String, Activity> activities = new TreeMap<String, Activity>();
				for(Map.Entry<Object, Object> activityObject : activityProperties.entrySet()) {
					String uid = (String) activityObject.getKey();
					JSONObject activityJSONObject = JSONObject.fromObject(activityObject.getValue());
					
					Activity activity = new Activity();
					activity.setUid(uid);
					activity.setName(activityJSONObject.getString("name"));
					activity.setUrl(activityJSONObject.getString("url"));
					activity.setContent(activityJSONObject.getString("content"));
					activity.setRuleParams(JSONObject.fromObject(activityJSONObject.getString("ruleParams")));
					activities.put(uid, activity);
				}
				account.setActivities(activities);
			}catch(Exception e) {
			}
			accounts.put((String) AccountObject.getKey(), account);
		}
		Constant.accounts = accounts;
    }
}
