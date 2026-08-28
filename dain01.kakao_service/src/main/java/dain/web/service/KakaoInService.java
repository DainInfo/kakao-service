package dain.web.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import api.KtAPI;
import dain.web.Util;
import dain.web.service.util.PropUtil;

@Controller
public class KakaoInService {

	PropUtil proUtil = new PropUtil();
	Properties properties = proUtil.Load("/header.properties");

	String targetAuthorization = "KakaoAK 46b050f23d6d7202d8ac97584978a7bc";
	String authorization = "KakaoAK 3f4785dfe31d5b9d0d82fe7b68f315ed";
	// operation
	String settleId = "d40d16f8-5cb0-4771-8ef4-cf97157181af";
	// dev
	// String settleId = "ff3266ca-d4b6-4091-b659-8dd4c783d77b";

	@RequestMapping("/api/token")
	public ResponseEntity getTokenVerify(HttpServletRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;

		try {
			String envId = request.getParameter("envelopeId");
			String token = request.getParameter("token");

			String host = "https://private-edoc-gw.kakao.com/v1/envelopes/";
			String urlPath = host + envId + "/tokens/" + token + "/verify";

			URL url = new URL(urlPath);

			HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

			// Header Setting
			httpsConn.setRequestProperty("Target-Authorization", targetAuthorization);
			httpsConn.setRequestProperty("Authorization", authorization);
			httpsConn.setRequestProperty("settle-id", settleId);
			httpsConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			httpsConn.setRequestMethod("GET");
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);

			// Caches setting
			httpsConn.setUseCaches(false);
			httpsConn.setReadTimeout(3000);
			httpsConn.setConnectTimeout(3000);

			int responseCode = httpsConn.getResponseCode();

			// SSL setting
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, null, null); // No validation for now
			httpsConn.setSSLSocketFactory(context.getSocketFactory());

			// Connect to host
			httpsConn.connect();
			httpsConn.setInstanceFollowRedirects(true);

			// Print response from host
			if (responseCode == HttpsURLConnection.HTTP_OK) { // 정상 호출 200
				status = HttpStatus.OK;
			} else {
				ErrorMessage(urlPath, responseCode, httpsConn.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(status);
	}

	@RequestMapping("/api/envelopes/read")
	public ResponseEntity<Object> setEnvelopeStatus(HttpServletRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;

		String envId = request.getParameter("envelopeId");
		String urlPath = "https://private-edoc-gw.kakao.com/v1/envelopes/" + envId + "/read";

		try {

			URL url = new URL(urlPath);
			HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

			// Header Setting
			httpsConn.setRequestProperty("Target-Authorization", targetAuthorization);
			httpsConn.setRequestProperty("Authorization", authorization);
			httpsConn.setRequestProperty("settle-id", settleId);
			httpsConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			httpsConn.setRequestMethod("POST");
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);

			// Caches setting
			httpsConn.setUseCaches(false);
			httpsConn.setReadTimeout(3000);
			httpsConn.setConnectTimeout(3000);

			int responseCode = httpsConn.getResponseCode();

			// SSL setting
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, null, null); // No validation for now
			httpsConn.setSSLSocketFactory(context.getSocketFactory());

			// Connect to host
			httpsConn.connect();
			httpsConn.setInstanceFollowRedirects(true);

			InputStream in = null;

			// Print response from host
			if (responseCode == HttpsURLConnection.HTTP_NO_CONTENT) { // 정상 호출
																		// 200
				status = HttpStatus.OK;
				in = httpsConn.getInputStream();
			} else { // 에러 발생
				in = httpsConn.getErrorStream();
				ErrorMessage(urlPath, responseCode, httpsConn.getResponseMessage());
			}

			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				System.out.printf("응답 결과: " + "%s\n", line);
			}

			reader.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return new ResponseEntity<Object>(status);
	}

	@RequestMapping("/api/envelopes/status")
	public ResponseEntity<Object> getEnvelopeStatus(HttpServletRequest request) {

		HttpStatus status = null;
		String envId = request.getParameter("envelopeId");
		String responseBody = null;

		try {
			URL url = new URL("https://private-edoc-gw.kakao.com/v1/envelopes/status");
			InputStream in = null;
			HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

			// Header Setting
			httpsConn.setRequestProperty("Target-Authorization", targetAuthorization);
			httpsConn.setRequestProperty("Authorization", authorization);
			httpsConn.setRequestProperty("settle-id", settleId);
			httpsConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			httpsConn.setRequestMethod("GET");
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);

			JSONObject body = new JSONObject();

			ArrayList<String> envelopesIds = new ArrayList<String>();
			envelopesIds.add(envId);

			HashMap<String, Object> content = new HashMap<>();
			content.put("envelopeIds", envelopesIds);

			body = new JSONObject(content);

			try (OutputStream os = httpsConn.getOutputStream()) {
				byte[] input = body.toString().getBytes("utf-8"); // JSON 문자열을
																	// 바이트 배열로
																	// 변환
				os.write(input, 0, input.length); // 변환된 바이트 배열을 출력 스트림을 통해 전송
			}

			int responseCode = httpsConn.getResponseCode();

			// SSL setting
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, null, null); // No validation for now
			httpsConn.setSSLSocketFactory(context.getSocketFactory());

			// Connect to host
			httpsConn.connect();
			httpsConn.setInstanceFollowRedirects(true);

			// Print response from host
			if (responseCode == HttpsURLConnection.HTTP_OK) { // 정상 호출 200
				in = httpsConn.getInputStream();
				status = HttpStatus.OK;
			} else { // 에러 발생
				in = httpsConn.getErrorStream();
				ErrorMessage(url.toString() + "/" + envId, responseCode, httpsConn.getResponseMessage());
			}

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(in, "UTF-8"));
			responseBody = jsonObject.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Object>(responseBody, status);
	}

	@RequestMapping("/api/notifications/kakao/login-success")
	public ResponseEntity<Object> sendMessage(HttpServletRequest request) {

		ServiceUtil apiUtil = new ServiceUtil();
		String id = null;
		String ip = null;
		String ci = null;

		String message = null;
		HttpStatus status = HttpStatus.NOT_FOUND;

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser
					.parse(new InputStreamReader(request.getInputStream(), "UTF-8"));

			id = jsonObject.get("id").toString();
			ip = jsonObject.get("ip").toString();
			ci = jsonObject.get("ci").toString();

			if (ci != null) {
//				InputStream in = null;
				apiUtil.setHttpsURL();
				apiUtil.setSimpleEnvelopes(id, ip, ci);
				HttpsURLConnection httpsConn = apiUtil.getHttpsURL();
				JSONObject body = new JSONObject(apiUtil.getEnvelopes());

				try (OutputStream os = httpsConn.getOutputStream()) {
					byte[] input = body.toString().getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				int responseCode = httpsConn.getResponseCode();
				System.out.println("응답코드 : " + responseCode);
				System.out.println("응답메시지 : " + httpsConn.getResponseMessage());

				// SSL setting
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, null, null); // No validation for now
				httpsConn.setSSLSocketFactory(context.getSocketFactory());

				// Connect to host
				httpsConn.connect();
				httpsConn.setInstanceFollowRedirects(true);

				// Print response from host
//				if (responseCode == HttpsURLConnection.HTTP_OK) { // 정상 호출 200
//					in = httpsConn.getInputStream();
//				} else { // 에러 발생
//					in = httpsConn.getErrorStream();
//				}

//				JSONParser responseJsonParser = new JSONParser();
				// JSONObject responseJsonObject = (JSONObject)
				// responseJsonParser.parse(new InputStreamReader(in, "UTF-8"));

				message = httpsConn.getResponseMessage();
				if (responseCode == 200) {
					status = HttpStatus.OK;
				} else {
					status = HttpStatus.NOT_FOUND;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return new ResponseEntity<Object>(message, status);
	}

	@RequestMapping("/api/kt/request")
	public ResponseEntity<Object> sendKtMessage(HttpServletRequest request) {

		String message = null;
		HttpStatus status = HttpStatus.NOT_FOUND;

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser
					.parse(new InputStreamReader(request.getInputStream(), "UTF-8"));

			String sendKey = jsonObject.get("sendKey").toString();
			String docId = jsonObject.get("docId").toString();
			String ci = jsonObject.get("ci").toString();
			String uid = jsonObject.get("uid").toString();
			String name = jsonObject.get("name").toString();
			String phone = jsonObject.get("phone").toString();
			String sender = jsonObject.get("sender").toString();
			String title = jsonObject.get("title").toString();
			String msg = jsonObject.get("msg").toString();
			String url = jsonObject.get("url").toString();
			String expire = jsonObject.get("expire").toString();

			if (KtAPI.request(sendKey, docId, ci, uid, name, phone, sender, title, msg, url, expire)) {
				status = HttpStatus.OK;
				System.out.println(Util.Date());
				System.out.println("문서 번호:" + sendKey);
				System.out.println("CI:" + ci);
				System.out.println("제목:" + title);
				System.out.println("메시지:" + msg);
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new ResponseEntity<Object>(message, status);
	}

	@ResponseBody
	@RequestMapping("/api/kt/result")
	public ResponseEntity<String> sendKtResult(HttpServletRequest request) {
		
		ArrayList<String> src_keys = null;
		HttpStatus status = HttpStatus.NOT_FOUND;
		 JSONArray resDatas = null;

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser
					.parse(new InputStreamReader(request.getInputStream(), "UTF-8"));

			JSONArray jsonArray = (JSONArray) jsonObject.get("srcKey");
			src_keys = new ArrayList<String>();

			if (jsonArray != null) {

				for (int i = 0; i < jsonArray.size(); i++) {
					src_keys.add((String) jsonArray.get(i));
				}
			}

			 resDatas = KtAPI.result(src_keys);
			 if (resDatas != null) {
				 status = HttpStatus.OK;
			 }

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new ResponseEntity<String>(resDatas.toString(), status);
	}
	
	@ResponseBody
	@RequestMapping("/api/kt/agree")
	public ResponseEntity<String> sendKtAgree(HttpServletRequest request) {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		JSONArray resDatas = null;
		String agreeResult = "no agrees data" ;
		try {
		
			 resDatas = KtAPI.agrees();
			 if (resDatas != null) {
				 status = HttpStatus.OK;
				 agreeResult = resDatas.toString();
			 }else{
				 status = HttpStatus.OK;
			 }

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new ResponseEntity<String>(agreeResult, status);
	}

	private void ErrorMessage(String urlPath, int responseCode, String responseMessage) {
		System.out.println(Util.Date());
		System.out.println("호출 정보" + urlPath);
		System.out.println("응답코드 : " + responseCode);
		System.out.println("응답메세지 : " + responseMessage);
	}
}
