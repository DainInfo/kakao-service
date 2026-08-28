package dain.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.simple.JSONObject;

public class ServiceUtil {

	private JSONObject envelopes;
	private JSONObject envelopesStatus;
	private HttpsURLConnection httpsConn;

	public void setSimpleEnvelopes(String id, String ip, String ci) {
		ProUtil proUtil = new ProUtil();
		Properties properties = proUtil.Load("/header.properties");

		HashMap<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("title", "운행제한단속시스템 로그인 안내");
		bodyMap.put("guide", "사용자: " + id + "\n접속 IP:" + ip + "정상적인 로그인이 맞는지 확인해주세요.");
		bodyMap.put("payload", "페이로드");

		bodyMap.put("ci", ci);
		envelopes = new JSONObject(bodyMap);
	}

	public JSONObject getEnvelopes() {
		return envelopes;
	}

	public JSONObject getEnvelopesStatus() {
		return envelopesStatus;
	}

	public void setHttpsURL() {
		ProUtil proUtil = new ProUtil();
		Properties properties = proUtil.Load("header.properties");
		HttpsURLConnection httpsConn;
		
		try {
			URL url = new URL(properties.getProperty("url"));
			httpsConn = (HttpsURLConnection) url.openConnection();
			InputStream in = null;
			// Set Hostname verification
			httpsConn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					// Ignore host name verification. It always returns true.
					return true;
				}
			});

			// Header Setting
			httpsConn.setRequestProperty("Target-Authorization", properties.getProperty("targetAuthorization"));
			httpsConn.setRequestProperty("Authorization", properties.getProperty("authorization"));
			httpsConn.setRequestProperty("settle-id", properties.getProperty("settleId"));
			httpsConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			httpsConn.setRequestMethod(properties.getProperty("method"));
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);

			// Caches setting
			httpsConn.setUseCaches(false);
			httpsConn.setReadTimeout(3000);
			httpsConn.setConnectTimeout(3000);
			
			this.httpsConn = httpsConn;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HttpsURLConnection getHttpsURL() {
		return httpsConn;
	}
}
