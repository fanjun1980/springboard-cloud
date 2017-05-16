package io.springboard.framework.utils.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * HttpClient的工具类
 * 
 * @author fanjun
 * 
 */
public class HttpClientUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 50;
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
	private static final int DEFAULT_CONN_TIMEOUT_MILLISECONDS = 5 * 1000;
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 160 * 1000;
	private static String DEFAULT_CHARSETNAME = "UTF-8";

	private HttpClient httpClient;
	private PoolingClientConnectionManager cm;
	private TrustManager easyTrustManager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	public HttpClientUtil() {
		init();
	}
	
	public HttpClientUtil(String charsetName){
		DEFAULT_CHARSETNAME = charsetName;
		init();
	}
	
	public void init(){
		try {
			// 初始化ConnectionManager
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
//			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, socketFactory));

			cm = new PoolingClientConnectionManager(schemeRegistry);
			cm.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
			cm.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

			// 初始化httpClient
			httpClient = new DefaultHttpClient(cm);
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, DEFAULT_CHARSETNAME);
			setConnectTimeout(DEFAULT_CONN_TIMEOUT_MILLISECONDS);
			setReadTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用get方法调用url,以文本形式返回http content
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String doGetRequest(String url) throws Exception {
		URI uri = new URI(url);
		return doGetRequest(uri, null, null);
	}

	public String doGetRequest(URI uri, Header[] heads, HttpParams params) throws Exception {
		HttpUriRequest httpUriRequest = new HttpGet(uri);
		String result = "";

		try {
			HttpResponse response = doRequest(httpUriRequest, heads, params);
			result = EntityUtils.toString(response.getEntity(),DEFAULT_CHARSETNAME);
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new HttpException(e.getMessage());
		}

		return result;
	}

	/**
	 * 使用post方法调用url
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String doPostRequest(String url) throws Exception {
		URI uri = new URI(url);
		return doPostRequest(uri, null, null);
	}

	public String doPostRequest(URI uri, Header[] heads, HttpParams params) throws Exception {
		HttpUriRequest httpUriRequest = new HttpPost(uri);
		String result = "";

		try {
			HttpResponse response = doRequest(httpUriRequest, heads, params);
			result = EntityUtils.toString(response.getEntity(),DEFAULT_CHARSETNAME);
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new HttpException(e.getMessage());
		}

		return result;
	}
	
	public String doPostRequest(URI uri, Header[] heads, HttpParams params, HttpEntity entity) throws Exception {
		HttpUriRequest httpUriRequest = new HttpPost(uri);
		String result = "";

		try {
			if(entity != null) ((HttpPost)httpUriRequest).setEntity(entity);
			HttpResponse response = doRequest(httpUriRequest, heads, params);
			result = EntityUtils.toString(response.getEntity(),DEFAULT_CHARSETNAME);
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new HttpException(e.getMessage());
		}

		return result;
	}

	/**
	 * 通过request访问url，直接返回response。可以出入header和params
	 * 
	 * @param httpUriRequest
	 * @param heads
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse doRequest(HttpUriRequest httpUriRequest, Header[] headers, HttpParams params)
			throws ClientProtocolException, IOException {
		if (!StringUtils.isEmpty(headers)) {
			httpUriRequest.setHeaders(headers);
		}
		if (params != null) {
			httpUriRequest.setParams(params);
		}

		HttpResponse response = httpClient.execute(httpUriRequest);
		validateResponse(response);
		return response;
	}
	
	/**
	 * 获取HttpClient的params
	 * @return
	 */
	public HttpParams getHttpParams(){
		return httpClient.getParams();
	}
	
	/**
	 * 创建新的HttpHeader
	 * @param name
	 * @param value
	 * @return
	 */
	public Header createHeader(String name, String value){
		return new BasicHeader(name, value);
	}
	
	/**
	 * 创建Form Entity，用于post提交表单
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public HttpEntity createFormEntity(Map<String, String> values) throws Exception{
		if(values == null) return null;
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		for(Entry<String, String> v : values.entrySet()){
			nvps.add(new BasicNameValuePair(v.getKey(), v.getValue()));
		}
		return new UrlEncodedFormEntity(nvps, DEFAULT_CHARSETNAME);
	}

	/**
	 * 创建File Entity，用于post上传文件
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public HttpEntity createFileEntity(File file) throws Exception{
		return new FileEntity(file);
	}

	
	/**
	 * 关闭ConnectionManager，释放资源
	 */
	public void shutdown() {
		httpClient.getConnectionManager().shutdown();
	}

	protected void finalize() throws Throwable {
		shutdown();
	}

	/**
	 * 检验返回的response状态，若为错误则抛异常
	 * 
	 * @param response
	 * @throws IOException
	 */
	protected void validateResponse(HttpResponse response) throws IOException {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() >= 300) {
			throw new NoHttpResponseException("Did not receive successful HTTP response: status code = "
					+ status.getStatusCode() + ", status message = [" + status.getReasonPhrase() + "]");
		}
	}

	//==========================================================================================
	public void setConnectTimeout(int timeout) {
		if (httpClient == null)
			return;
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
	}

	public void setReadTimeout(int timeout) {
		if (httpClient == null)
			return;
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
	}

}
