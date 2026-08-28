package dain.web.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dain.web.service.util.PropUtil;

@Controller
public class NiceService {

	@RequestMapping("api/nice/ci")
	public ResponseEntity getTokenVerify(HttpServletRequest request) {
		PropUtil proUtil = new PropUtil();
		Properties properties = proUtil.Load("/nice.properties");
		String CLIENT_IP = properties.getProperty("CLIENT_IP");
		String apiUrl = properties.getProperty("URL");
		String clientId = properties.getProperty("CLIENT_ID");
		String clientSecret = properties.getProperty("CLIENT_SECRET");
		
		String juminId = request.getParameter("id");

		// Authorization 헤더 생성
		String raw = clientId + ":" + clientSecret;
		// Base64 인코딩
		String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
		// Authorization 헤더
		String authHeader = "Basic " + encoded;

		String reqDtim = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String targetCi = "Not Found";
		HttpStatus status = HttpStatus.NO_CONTENT;
		System.out.println(juminId);
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", authHeader);
			conn.setRequestProperty("ProductID", properties.getProperty("PRODUCT_ID"));
			conn.setDoOutput(true);

			String reqNo = UUID.randomUUID().toString().replace("-", "").substring(0, 30);
			String stieCode = properties.getProperty("SITE_CODE");

			// 🔹 요청 데이터 (JSON 문자열)
			String reqData = "{" + "\"site_code\":\"" + stieCode + "\"," + "\"info_req_type\":\"1\","
					+ "\"jumin_id\":\"" + juminId + "\"," + "\"req_no\":\"" + reqNo + "\"," + "\"req_dtim\":\""
					+ reqDtim + "\"," + "\"client_ip\":\"" + CLIENT_IP + "\"" + "}";

			// 🔹 대칭키 (등록할 때 쓴 값 그대로)
			String key = properties.getProperty("KEY"); // 32byte
			String iv = properties.getProperty("IV"); // 16byte
			String hmacKey = properties.getProperty("HMAC_KEY");

			String symkeyVersion = properties.getProperty("SYMKEY_VERSION");

			// 🔹 AES Key 생성
			SecretKeySpec secureKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

			// 🔹 AES 암호화
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

			byte[] encrypted = c.doFinal(reqData.getBytes(StandardCharsets.UTF_8));

			// 🔹 Base64 인코딩
			String reqDataEnc = Base64.getEncoder().encodeToString(encrypted);

			// System.out.println("enc_data: " + reqDataEnc);

			// HMAC 대상 문자열
			String originText = reqDataEnc;

			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

			mac.init(keySpec);

			byte[] hmacBytes = mac.doFinal(originText.getBytes(StandardCharsets.UTF_8));
			String integrityValue = Base64.getEncoder().encodeToString(hmacBytes);

			String jsonInput = "{" + "\"dataHeader\":{" + "\"CNTY_CD\":\"ko\","
					+ "\"TRAN_ID\":\"123456789012345678901234\"" + "}," + "\"dataBody\":{" + "\"symkey_version\":\""
					+ symkeyVersion + "\"," + "\"enc_data\":\"" + reqDataEnc + "\"," + "\"integrity_value\":\""
					+ integrityValue + "\"" + "}" + "}";
			// System.out.println(reqData);
			// System.out.println(jsonInput);

			ObjectMapper reQeustObjectMapper = new ObjectMapper();

			JsonNode requestNode = reQeustObjectMapper.readTree(jsonInput.toString());

			String reCntyCd = requestNode.get("dataHeader").get("CNTY_CD").asText();

			String version = requestNode.get("dataBody").get("symkey_version").asText();

			String reEncData = requestNode.get("dataBody").get("enc_data").asText();

			System.out.println("=== Nice API CI 송신 결과 ===");
			System.out.println("CNYC CD: " + reCntyCd);
			System.out.println("CLIENT IP: " + CLIENT_IP);
			System.out.println("ENC DATA: " + reEncData);
			System.out.println("SYMKEY VERSION:" + version);
			System.out.println("");

			try (OutputStream os = conn.getOutputStream()) {
				os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			// 응답
			int responseCode = conn.getResponseCode();

			BufferedReader br = (responseCode == 200)
					? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
					: new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

			StringBuilder response = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			br.close();

			// System.out.println("Response Code: " + responseCode);
			// System.out.println("Response: " + response.toString());

			ParsedData parsedData = NiceParser.parse(response.toString());

			String result = process(parsedData.encData, parsedData.integrityValue, hmacKey, iv, key);

			ObjectMapper objectMapper = new ObjectMapper();

			JsonNode jsonNode = objectMapper.readTree(response.toString());

			String cntyCd = jsonNode.get("dataHeader").get("CNTY_CD").asText();

			String rsltMsg = jsonNode.get("dataHeader").get("GW_RSLT_MSG").asText();

			String encData = jsonNode.get("dataBody").get("enc_data").asText();

			System.out.println("=== Nice API CI 수신 결과 ===");
			System.out.println("CNYC CD: " + cntyCd);
			System.out.println("GW RSLT MSG: " + rsltMsg);
			System.out.println("ENC DATA: " + encData);

			ObjectMapper mapper = new ObjectMapper();

			JsonNode node = mapper.readTree(result);

			if (node.get("ci1") != null) {
				targetCi = node.get("ci1").asText();
				status = HttpStatus.OK;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<Object>(targetCi, status);
	}

	public static class NiceParser {
		public static ParsedData parse(String json) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);

			JsonNode header = root.get("dataHeader");
			JsonNode body = root.get("dataBody");

			String encData = body.get("enc_data").asText();
			String integrityValue = body.get("integrity_value").asText();
			// String symkeyVersion = body.get("symkey_version").asText();

			String tranId = header.get("TRAN_ID").asText();

			return new ParsedData(encData, integrityValue, tranId);
		}
	}

	public static class ParsedData {
		public String encData;
		public String integrityValue;
		public String symkeyVersion;
		public String tranId;

		public ParsedData(String encData, String integrityValue, String tranId) {
			this.encData = encData;
			this.integrityValue = integrityValue;
			this.tranId = tranId;
		}
	}

	// 1. HMAC 생성
	public static String makeHmac(String data, String key) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		mac.init(secretKey);

		byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(rawHmac);
	}

	// 2. AES 복호화
	public static String decrypt(String encData, String key, String iv) throws Exception {

		byte[] keyBytes = key.getBytes("UTF-8");
		byte[] ivBytes = iv.getBytes("UTF-8");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		byte[] decoded = Base64.getDecoder().decode(encData);
		byte[] decrypted = cipher.doFinal(decoded);

		return new String(decrypted, "UTF-8");
	}

	// 3. 전체 처리
	public static String process(String encData, String integrityValue, String hmacKey, String iv, String key)
			throws Exception {

		// Step 1. 무결성 체크
		String calculatedHmac = makeHmac(encData, hmacKey);

		if (!calculatedHmac.equals(integrityValue)) {
			throw new RuntimeException("무결성 검증 실패 (위변조 의심)");
		}

		// Step 2. 복호화
		String resultJson = decrypt(encData, key, iv);

		return resultJson;
	}
}
