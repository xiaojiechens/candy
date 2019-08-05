package com.group.cll.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

/**
 * 利用HttpClient进行post请求的工具类
 * 
 * @ClassName: HttpClientUtil
 * @Description: TODO
 * @author Devin <xxx>
 * @date 2017年2月7日 下午1:43:38
 * 
 */

public class HttpsClientUtil {

	public static void main(String[] args) {
		String url = "https://888.eislkni654eisoj.com/app/WebService/JSON/display.php/ForwardGameH5By5?key=057435f564896bec80cd19a7c749a9445e16b&website=gpk88&username=l4008wygqsm&uppername=dl4008&gametype=5902&lang=zh-cn";
		Header[] headers = HttpClientUtil.doGetAndGetHeader(url, new HashMap<String,String>(), "utf-8");

		String httpOrgCreateTestRtn = HttpClientUtil.doGet(url, new HashMap<String,String>(), "utf-8");
		
		for(Header header : headers) {
			System.out.println(header.toString());
		}
		
		System.out.println(httpOrgCreateTestRtn);
	}

	public static String doPost(String url, String jsonstr, String charset) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = buildSSLCloseableHttpClient();
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(jsonstr);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String doGet(String url, Map<String, String> headers, String charset) {
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try {
			httpClient = buildSSLCloseableHttpClient();
			httpGet = new HttpGet(url);
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpGet.addHeader(header.getKey(), header.getValue());
			}
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static Header[] doGetAndGetHeader(String url, Map<String, String> headers, String charset) {
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		try {
			httpClient = buildSSLCloseableHttpClient();
			httpGet = new HttpGet(url);
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpGet.addHeader(header.getKey(), header.getValue());
			}
			HttpResponse response = httpClient.execute(httpGet);
			return response.getAllHeaders();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static CloseableHttpClient buildSSLCloseableHttpClient() throws Exception {
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// 信任所有
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		// ALLOW_ALL_HOSTNAME_VERIFIER:这个主机名验证器基本上是关闭主机名验证的,实现的是一个空操作，并且不会抛出javax.net.ssl.SSLException异常。
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();

	}
}
