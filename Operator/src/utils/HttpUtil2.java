package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class HttpUtil2 {

	public static void main(String[] args) {
		exeHttpsRequest("https://777.eislkni654eisoj.com/ipl/app/member/game/Game.php?HTML5=Y&Client=2&GameType=5902&lang=zh-cn&HALLID=3819831&VerID=b5964ae20c515a9f9c055fbada7afdf4", 
				"", 
				"");
	}
	
    public static String exeHttpsRequest(String URL, String message, String token) {
        String body = "";
        CloseableHttpClient client = null;
        try {
            // 閲囩敤缁曡繃楠岃瘉鐨勬柟寮忓鐞唄ttps璇锋眰
            SSLContext sslcontext = createIgnoreVerifySSL();
            // 璁剧疆鍗忚http鍜宧ttps瀵瑰簲鐨勫鐞唖ocket閾炬帴宸ュ巶鐨勫璞�
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext,
                            SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            // 鍒涘缓鑷畾涔夌殑httpclient瀵硅薄
            client = HttpClients.custom().setConnectionManager(connManager).build();
            // CloseableHttpClient client = HttpClients.createDefault();

            // 鍒涘缓post鏂瑰紡璇锋眰瀵硅薄
            HttpGet httpPost = new HttpGet(URL);

            // 鎸囧畾鎶ユ枃澶碈ontent-type銆乁ser-Agent
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpPost.setHeader("Cache-Control", "max-age=0");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Cookie", "T0_IPL_AVRbbbbbbbbbbbbbbbb=GKKKIKLAKMFEHBIINBEIKFJGPLBLKAMBEDDMAIJJPCBDJBHNFOGMHLHKNEMIGINJMFDDIBGMKGDDLIJNDDNKEAIKPPFAJAOPLBGPKBPDNMCHNJACPPMJEMGDHCPOKMDG; SESSION_ID=ca6bad60440feeedba7283ac42a91619862e88eb; lang=zh-cn; langx=zh-cn; _ga=GA1.2.135084307.1560586065; _gid=GA1.2.1039823905.1560586065");
			httpPost.setHeader("Host", "777.eislkni654eisoj.com");
			httpPost.setHeader("Upgrade-Insecure-Requests", "1");
			httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            
//            httpPost.setEntity(stringEntity);
            // 鎵ц璇锋眰鎿嶄綔锛屽苟鎷垮埌缁撴灉锛堝悓姝ラ樆濉烇級
            CloseableHttpResponse response = client.execute(httpPost);

            // 鑾峰彇缁撴灉瀹炰綋
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 鎸夋寚瀹氱紪鐮佽浆鎹㈢粨鏋滃疄浣撲负String绫诲瀷
                body = EntityUtils.toString(entity, "UTF-8");
                
                System.out.println(body);
            }

            EntityUtils.consume(entity);
            // 閲婃斁閾炬帴
            response.close();
            // System.out.println("body:" + body);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	if(client != null) {
        		try {
        			client.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            }
        }
        return body;

    }

    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 瀹炵幇涓�涓猉509TrustManager鎺ュ彛锛岀敤浜庣粫杩囬獙璇侊紝涓嶇敤淇敼閲岄潰鐨勬柟娉�
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static String sendSSLPostRequest(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            // URLEncoder.encode(param,"UTF-8");

            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Encoding", "utf-8");
            conn.setRequestProperty("Charsert", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (conn instanceof HttpsURLConnection) {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
                ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));

            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("POST 璇锋眰寮傚父锛侊紒锛�" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }
}