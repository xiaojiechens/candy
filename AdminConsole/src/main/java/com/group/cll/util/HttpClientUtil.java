package com.group.cll.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	public static void main(String[] args){ 
		String url = "http://41186868.com:8888/Account/LoginToSupplier?supplierType=1&gId=4271&cId=2&externalType=undefined";

		Header[] headers = HttpClientUtil.doGetAndGetHeader(url, new HashMap<String,String>(), "utf-8");

		String httpOrgCreateTestRtn = HttpClientUtil.doGet(url, new HashMap<String,String>(), "utf-8");
		
		for(Header header : headers) {
			System.out.println(header.toString());
		}
		
		System.out.println(httpOrgCreateTestRtn);
	}
  public static String doPost(String url,String jsonstr,String charset){
    HttpClient httpClient = null;
    HttpPost httpPost = null;
    String result = null;
    try{
	  httpClient = HttpClients.createDefault();
      httpPost = new HttpPost(url);
      httpPost.addHeader("Content-Type", "application/json");
      StringEntity se = new StringEntity(jsonstr);
      se.setContentType("text/json");
      se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
      httpPost.setEntity(se);
      HttpResponse response = httpClient.execute(httpPost);
      if(response != null){
        HttpEntity resEntity = response.getEntity();
        if(resEntity != null){
          result = EntityUtils.toString(resEntity,charset);
        }
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return result;
  }
  
  public static String doGet(String url,Map<String, String> headers,String charset){
	  HttpClient httpClient = null;
	  HttpGet httpGet = null;
	  String result = null;
	  try{
		  httpClient = HttpClients.createDefault();
		  httpGet = new HttpGet(url);
		  RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		  httpGet.setConfig(requestConfig);
		  
		  for(Map.Entry<String, String> header : headers.entrySet()) {
			  httpGet.addHeader(header.getKey(), header.getValue());
		  }
		  
		  HttpResponse response = httpClient.execute(httpGet);
		  if(response != null){
			  HttpEntity resEntity = response.getEntity();
			  if(resEntity != null){
				  result = EntityUtils.toString(resEntity,charset);
			  }
		  }
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return result;
  }
  
  public static Header[] doGetAndGetHeader(String url,Map<String, String> headers,String charset){
	  HttpClient httpClient = null;
	  HttpGet httpGet = null;
	  try{
		  httpClient = HttpClients.createDefault();
		  httpGet = new HttpGet(url);
		  RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		  httpGet.setConfig(requestConfig);
		  
		  for(Map.Entry<String, String> header : headers.entrySet()) {
			  httpGet.addHeader(header.getKey(), header.getValue());
		  }
		  
		  HttpResponse response = httpClient.execute(httpGet);
		  	
		  return response.getAllHeaders();
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return null;
  }
}
