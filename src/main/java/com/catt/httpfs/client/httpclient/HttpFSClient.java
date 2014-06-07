package com.catt.httpfs.client.httpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.catt.httpfs.client.utils.HttpFSConf;

public class HttpFSClient {
	private final static Log log = LogFactory.getLog(HttpFSClient.class);
	private final static String CHARSET = "UTF8";

	private Cookie[] cookies;
	private boolean isInitCookie = false;

	public void initCookie() {
		String url = HttpFSUtils.createURL("",
				"op=gethomedirectory&user.name=hdfs");
		System.out.println(url);
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			getCookie(client);
		} catch (Exception e) {
			log.error("init cookie value fail", e);
		}
		this.isInitCookie = true;
		method.releaseConnection();
	}

	public String get(String path, String params) {
		return this.request(path, params, GetMethod.class);
	}

	public String get(String path, String params, boolean isGenFile) {
		return this.request(path, params, GetMethod.class, isGenFile, null);
	}

	public String delete(String path, String params) {
		return this.request(path, params, DeleteMethod.class);
	}

	public String put(String path, String params) {
		return this.request(path, params, PutMethod.class);
	}

	public String post(String path, String params) {
		return this.request(path, params, PostMethod.class);
	}

	public String upload(String path, String params, String fileName) {
		return this.request(path, params, fileName);
	}

	private String request(String path, String params, Class clz) {
		return this.request(path, params, clz, false, null);
	}

	private String request(String path, String params, String fileName) {
		return this.request(path, params, null, false, fileName);
	}

	private String request(String path, String params, Class clz,
			boolean isGenFile, String fileName) {
		if (this.isInitCookie == false) {
			return "please init cookie first";
		}

		HttpClient client = new HttpClient();
		// 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		client.getState().addCookies(cookies);
		StringBuffer sb = new StringBuffer();
		int status = -1;

		HttpMethod method = getMethod(path, params, clz, fileName);
		try {
			status = client.executeMethod(method);
			if (isGenFile == false) {
				this.dealResponse(method.getResponseBodyAsStream(), sb);
			} else {
				this.dealResponse(method.getResponseBodyAsStream(), path);
			}
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		method.releaseConnection();
		log.info(method.getStatusLine() + "," + sb.toString());
		return sb.toString();
	}

	private HttpMethod getMethod(String path, String params, Class clz,
			String fileName) {
		HttpMethod method = null;
		String url = HttpFSUtils.createURL(path, params);
		try {
			if (null == clz) {
				method = getMethod(path, params, fileName);
			} else {
				method = (HttpMethod) clz.getConstructor(String.class)
						.newInstance(url);
			}
			method.setRequestHeader("content-type", "application/octet-stream");
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private HttpMethod getMethod(String path, String params, String fileName) {
		String url = HttpFSUtils.createURL(path, params);
		PutMethod method = new PutMethod(url);
		method.setRequestHeader("content-type", "application/octet-stream");
		try {
			// 设置上传文件
			File targetFile = new File(fileName);
			Part[] parts = { new FilePart(targetFile.getName(), targetFile) };
			method.setRequestEntity(new MultipartRequestEntity(parts, method
					.getParams()));
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private void getCookie(HttpClient client) {
		CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
		this.cookies = cookiespec.match(HttpFSConf.getHOST(), HttpFSConf
				.getPORT(), "/", false, client.getState().getCookies());
	}

	/**
	 * deal HttpResponse content
	 * 
	 * @param conn
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	private String dealResponse(InputStream in, StringBuffer sb) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			log.error("deal response content fail", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("close BufferedReader error", e);
				}
			}
		}
		return sb.toString();
	}

	private void dealResponse(InputStream in, String path) {
		int pos = path.lastIndexOf("/");
		String fileName = path.substring(pos + 1);
		BufferedReader reader = null;
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
			reader = new BufferedReader(new InputStreamReader(in, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				out.append(line + "\n");
			}
		} catch (IOException e) {
			log.error("deal response content fail", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("close BufferedReader error", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					log.error("close PrintWriter error", e);
				}
			}
		}
	}
}