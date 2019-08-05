package com.group.cll.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.cll.model.Account;
import com.group.cll.service.AccountServiceImpl;

import net.sf.json.JSONObject;

@RestController
public class AccountController {
	
	@Autowired
	private AccountServiceImpl accountService;
	
	@RequestMapping(path = { "/queryAccount"})
	public String queryAccount(Account account) throws IOException {
		return JSONObject.fromObject(accountService.queryAccount(account)).toString();
	}
	
	@RequestMapping(path = { "/saveOrUpdateAccount"})
	public void saveOrUpdateAccount(Account account) throws IOException {
		accountService.saveOrUpdateAccount(account);
	}
	
	@RequestMapping(path = { "/deleteAccount"})
	public void deleteAccount(Account account) throws IOException {
		accountService.deleteAccount(account);
	}
}
