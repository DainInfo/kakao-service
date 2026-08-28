package api;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.DateUtil;
import util.HttpUtil;

public class KtAPI
{
	private static Logger logger = Logger.getLogger(KtAPI.class);

	private static final String service_cd = ApiUtil.getProperty("kt.service_cd");
	private static final String service_key = ApiUtil.getProperty("kt.service_key");
	private static final String accessToken = "Bearer "+ ApiUtil.getProperty("kt.accessToken");
	private static final String url_base = ApiUtil.getProperty("kt.service_url");
	private static final String uri_request = "req";
	private static final String uri_result  = "rsp";
	private static final String uri_token   = "token/status";
	private static final String uri_agrees  = "agrees";
	private static final String uri_status  = "status";

	private static final String RES_OK = "0000";

	private static final String BIZ_CD = ApiUtil.getProperty("kt.biz_cd");

	private static URL ProxyURL;
	private static HttpHost Proxy;

	static
	{
		try
		{	
			String sProxy = ApiUtil.getProperty("kt.proxy");
			
			if( sProxy != null )
			{
				ProxyURL = new URL(sProxy);
				Proxy = new HttpHost(ProxyURL.getHost(), ProxyURL.getPort(), ProxyURL.getProtocol());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static LinkedHashMap<String, Object> getCommonMsg()
	{
		LinkedHashMap<String, Object> msg = new LinkedHashMap<String, Object>();
		msg.put("service_cd", service_cd);
		msg.put("service_key", service_key);
		return msg;
	}

	public static synchronized String getSendKey()
	{
		return DateUtil.getToday("yyyyMMddHHmmssSSSSSS");
	}

/*
	// for test
	public static boolean request(String msg_cd, String ci, String uid, String name, String phone, String msg)
	{
		return request(, msg_cd, ci, uid, name, phone, msg);
	}

	public static boolean request(String sendKey, String msg_cd, String ci, String uid, String name, String phone, String msg)
	{
		String sender	= ApiUtil.getProperty("kt."+ msg_cd +".sender");
		String title	= ApiUtil.getProperty("kt."+ msg_cd +".title");
		String url		= ApiUtil.getProperty("kt."+ msg_cd +".url");
		String expire	= ApiUtil.getProperty("kt."+ msg_cd +".expire");

		return request(sendKey, msg_cd, ci, uid, name, phone, sender, title, msg, url, expire);
	}
*/
	
	public static boolean request(
		String sendKey,
		String msg_cd,
		String ci,
		String uid,
		String name,
		String receiver,
		String sender,
		String title,
		String msg,
		String url,
		String expire
	)
	{
//		if( title == null || msg == null || url == null ) return false;

		msg = msg.replaceAll("\r\n", "<BR>");
		msg = msg.replaceAll("\r", "<BR>");
		msg = msg.replaceAll("\n", "<BR>");

		if( url != null )
		url = url.replaceAll("\\$seq\\$", sendKey);

		// NULL시  KT에서 최장 90 일 보관
		if( expire == null ) expire = "";
		else if( expire.length() > 0 )
		{
			if( expire.length() != 8 )	// 정해진 날짜가 아니면
			{
				// 날짜 계산
				expire = DateUtil.offset("yyyyMMdd", Integer.parseInt(expire));
			}
			expire += "235959";
		}

		String birth = uid.substring(0,6);
		String gender = uid.substring(6,7);

		LinkedHashMap<String, Object> packet = getCommonMsg();
		packet.put("req_type", "1");	// 발송요청구분(1:즉시, 2:배치_비승인, 3:배치_승인)

		LinkedHashMap<String, Object> hdr = new LinkedHashMap<String, Object>();
		hdr.put("biz_cd", BIZ_CD);	// 기관코드(5)
		hdr.put("msg_cd", msg_cd);	// 문서코드(5)
		hdr.put("make_dt", DateUtil.getToday("yyyyMMddHHmmss"));	// 생성일시(yyyymmddhhmiss, 14)
		hdr.put("send_seq", "0000");	// 발송회차(4, 0001~9999), 즉시발송은 무조건 0000

		ArrayList<Object> reqs = new ArrayList<Object>();

		LinkedHashMap<String, Object> req = new LinkedHashMap<String, Object>();
		req.put("src_key", sendKey);	// 관리키
		req.put("src_seq", "0000");	// 한건에 여러명세서 데이타일경우 0001로 증가(기본 0000)

		req.put("sci", ci);	// 아이핀CI값
		req.put("d_birth", birth);	// 생년월일
		req.put("jumin_first_no", gender);	// 성별구분
		req.put("cn_person", name);	// 성명
		req.put("rcv_tel_no", receiver);	// 고객휴대폰번호(수신전화번호)
		req.put("snd_tel_no", sender);	// 발신번호(서비스기관)(발송전화번호)

		req.put("mms_title", title);	// MMS제목
		req.put("mms_dtl_cnts", msg);	// MMS상세내용

		req.put("ex_time", expire);	// 처리마감시간
		req.put("url", url);	// 연결URL
		req.put("doc_hash", "");	// 문서해시(SHA-256 ?)
		req.put("rcv_type", "");	// 수신자유형구분(1:개인,2:법인) NULL이면 개인 처리

		req.put("m_type", "");	// 문서종류(sms,lms,mms)
		req.put("c_zip", "");	// 우편번호
		req.put("addr", "");	// 주소
		req.put("rcv_pwd_use_yn", "");	// MMS수신비밀번호사용여부
		req.put("send_tel", "");	// 기관발송번호

		reqs.add(req);
		hdr.put("data_cnt", reqs.size());			// 회차전체데이터건수

		packet.put("hdr", hdr);
		packet.put("reqs", reqs);

		JSONObject res = doPost(uri_request, packet);
		if( res == null ) return false;

		String resCode = (String) res.get("code");

		JSONArray resDatas = (JSONArray) res.get("results");
		JSONObject data;
		if( resDatas != null )
		{
			for( int i=0; i<resDatas.size(); i++ )
			{
				data = (JSONObject) resDatas.get(i);
				System.out.println(data);
			}
		}
		System.out.println(resCode);
		return RES_OK.equals(resCode);
	}

	public static JSONArray result(ArrayList<String> src_keys)
	{
		LinkedHashMap<String, Object> msg = getCommonMsg();
		msg.put("src_keys", src_keys);

		JSONObject res = doPost(uri_result, msg);
		if( res == null ) return null;

		String resCode = (String) res.get("code");
		if( !RES_OK.equals(resCode) ) return null;

		JSONArray resDatas = (JSONArray) res.get("rsps");
		JSONObject data;
		if( resDatas != null )
		{
			for( int i=0; i<resDatas.size(); i++ )
			{
				data = (JSONObject) resDatas.get(i);
				System.out.println(data);
			}
		}

		return resDatas;
	}

	public static boolean token(String src_key, String token)
	{
		LinkedHashMap<String, Object> msg = getCommonMsg();
		msg.put("src_key", src_key);
		msg.put("token", token);

		JSONObject res = doPost(uri_token, msg);
		if( res == null ) return false;

		String resCode = (String) res.get("code");
		return RES_OK.equals(resCode);
	}

	public static JSONArray agrees() throws Exception
	{
		LinkedHashMap<String, Object> msg = getCommonMsg();

		JSONObject res = doPost(uri_agrees, msg);
		if( res == null ) return null;

		String resCode = (String) res.get("code");
		if( !RES_OK.equals(resCode) && !"0001".equals(resCode) ) return null;

		JSONArray resDatas = (JSONArray) res.get("agrees");
		JSONObject data;
		if( resDatas != null )
		{
			for( int i=0; i<resDatas.size(); i++ )
			{
				data = (JSONObject) resDatas.get(i);
				System.out.println(data);
			}
		}
		return resDatas;
		// 추가데이타 있을 시
//		if( "0001".equals(resCode) ) return agrees();
//		return (RES_OK.equals(resCode) || "0001".equals(resCode));
	}

	public static boolean status() throws Exception
	{
		JSONObject res = doGet(uri_status, null );
		String resCode = (String) res.get("code");
		return RES_OK.equals(resCode);
	}

	private static JSONObject doGet(String uri, LinkedHashMap<String, Object> msg)
	{
		String body = (msg == null ? "" : new JSONObject(msg).toString());
		System.out.println( uri +" >>> "+ body );
/*
		URIBuilder url = new URIBuilder(url_base + uri);
		Iterator iter = msg.entrySet().iterator();
		Map.Entry entry;
		while( iter.hasNext() )
		{
			entry = (Map.Entry) iter.next();
			url.addParameter((String)entry.getKey(), (String)entry.getValue());
		}
		body = url.toString();
*/
//		logger.debug( URLEncoder.encode(body,"UTF-8") );

		try {
			RequestConfig config = RequestConfig.custom().setProxy(Proxy).build();

			HttpGet get = new HttpGet(url_base + uri +"?"+ URLEncoder.encode(body,"UTF-8"));
			get.setConfig(config);
			get.addHeader("Authorization", accessToken);
			get.addHeader("Content-Type", "application/json;charset=UTF-8");

			body = HttpUtil.doRequest(get);
			System.out.println( uri +" <<< "+ body );
			if( body != null )
				return (JSONObject) new JSONParser().parse(body);
		} catch(Exception e) { logger.error(e,e); }

		return null;
	}

	private static JSONObject doPost(String uri, LinkedHashMap<String, Object> msg)
	{
		String body = (msg == null ? "" : new JSONObject(msg).toString());
		System.out.println( uri +" >>> "+ body );

		RequestConfig config = RequestConfig.custom().setProxy(Proxy).build();

		HttpPost post = new HttpPost(url_base + uri);
		post.setConfig(config);
		post.addHeader("Authorization", accessToken);
		post.addHeader("Content-Type", "application/json;charset=UTF-8");

		HttpEntity entity = new ByteArrayEntity(body.getBytes());
		post.setEntity(entity);

		body = HttpUtil.doRequest(post);
		System.out.println( uri +" <<< "+ body );
		if( body != null )
		{
			try {
				return (JSONObject) new JSONParser().parse(body);
			} catch(Exception e) { logger.error(e,e); }
		}

		return null;
	}

}
