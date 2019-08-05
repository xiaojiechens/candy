package com.group.cll.model;

import net.sf.json.JSONObject;

public class Activity implements Comparable<Activity>{

	private String uid;
	private String name;
	private String url;
	private String content;
	private JSONObject ruleParams;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public JSONObject getRuleParams() {
		return ruleParams;
	}
	public void setRuleParams(JSONObject ruleParams) {
		this.ruleParams = ruleParams;
	}
	public int compareTo(Activity activity) {
		 return this.getName().compareTo(activity.getName());
	}
	
}
