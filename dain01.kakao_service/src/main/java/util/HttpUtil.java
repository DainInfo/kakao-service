package util;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtil
{
	private static Logger logger = Logger.getLogger(HttpUtil.class);

	private static RequestConfig reqConfig
		= RequestConfig.custom()
		.setSocketTimeout(5000)
		.setConnectTimeout(5000)
		.setConnectionRequestTimeout(5000)
		.build();

	public static String doPost(String url, String body)
	{
//		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return doPost(httpClient, url, body);
	}

	public static String doPost(CloseableHttpClient httpClient, String url, String body)
	{
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(reqConfig);
			httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
			httpPost.setHeader("Connection", "close");
			httpPost.setEntity(new StringEntity(body));
//			httpPost.getRequestLine();
			return doRequest(httpClient, httpPost);
		} catch(Exception e) { logger.error(e,e); }
		return null;
	}

	public static String doRequest(CloseableHttpClient httpClient, HttpUriRequest request)
	{
		long sTime = System.currentTimeMillis();
		CloseableHttpResponse httpResponse = null;
		try
		{
			httpResponse = httpClient.execute(request);
			logger.info("===============================");
			logger.info(httpResponse);
			HttpEntity entity = httpResponse.getEntity();
			logger.info("===============================");
			logger.info(entity);
			String response = EntityUtils.toString(entity);

			StatusLine statusLine = httpResponse.getStatusLine();

			if( statusLine.getStatusCode() == HttpStatus.SC_OK )
			{
//				ResponseHandler<String> handler = new BasicResponseHandler();
//				body = handler.handleResponse(httpResponse);
				return response;
			}
			else
			{
				logger.error( statusLine );
				logger.error( response );
			}
		}
		catch(java.net.SocketTimeoutException e)
		{
			logger.error(e);
		}
		catch(Exception e)
		{
			logger.error(e,e);
		}
		finally
		{
			if( httpResponse != null ) try { httpResponse.close(); } catch(Exception e) { logger.error(e,e); }
			if( httpClient != null ) try { httpClient.close(); } catch(Exception e) { logger.error(e,e); }
			logger.info("Elapsed: "+ (System.currentTimeMillis()-sTime));
		}
		return null;
	}

	public static String doRequest(HttpUriRequest request)
	{
		CloseableHttpClient httpClient = null;
		try
		{
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					return false;
				}
			}).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" },
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier()
				);

			httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			return doRequest(httpClient, request);
		}
		catch(Exception e)
		{
			logger.error(e,e);
		}
		finally
		{
			if( httpClient != null ) try { httpClient.close(); } catch(Exception e) { logger.error(e,e); }
		}
		return null;
	}

}
