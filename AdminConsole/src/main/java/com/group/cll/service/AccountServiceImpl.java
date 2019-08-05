package com.group.cll.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.group.cll.constant.Constant;
import com.group.cll.model.Account;

@Service
public class AccountServiceImpl {
	
	String filePath = "account.properties";
	
	/**
	 * 查询账户信息。
	 * @param account
	 * @return 
	 * @throws IOException 
	 */
	public Account queryAccount(Account account) throws IOException {
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		
		return Constant.accounts.get(accountNoAndDomain);
	}
	
	/**
	 * 保存账户信息到内存，并写入文件。
	 * @param account
	 * @throws IOException 
	 */
	public void saveOrUpdateAccount(Account account) throws IOException {
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		Constant.accounts.put(accountNoAndDomain, account);
		
		Properties accountProperties = readPropertiesFromFile(filePath);
		accountProperties.put(accountNoAndDomain, account.getJSONObjectString());
		writePropertiesToFile(accountProperties, filePath);
	}

	/**
	 * 删除账户信息到内存，并写入文件。
	 * @param account
	 * @throws IOException 
	 */
	public void deleteAccount(Account account) throws IOException {
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		Constant.accounts.remove(accountNoAndDomain);
		
		Properties accountProperties = readPropertiesFromFile(filePath);
		accountProperties.remove(accountNoAndDomain);
		writePropertiesToFile(accountProperties, filePath);
	}
	
	public Properties readPropertiesFromFile(String filePath) throws IOException {
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(filePath);
		properties.load(in);
		in.close();
		return properties;
	}
	
	public void writePropertiesToFile(Properties properties, String filePath) throws IOException {
		FileOutputStream out = new FileOutputStream(filePath);
		properties.store(out, filePath);
		out.close();
	}
	
}
