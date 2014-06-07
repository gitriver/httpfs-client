package com.catt.httpfs.client.httpclient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.catt.httpfs.client.utils.HttpFSConf;

public class HttpFSUtils {
	private final static Log log = LogFactory.getLog(HttpFSUtils.class);
	private static final String SERVICE_PATH = "/webhdfs/v1";
	private static final String SCHEME = "http";

	public static String createURL(String path, String params) {
		StringBuilder sb = new StringBuilder();
		sb.append(SCHEME).append("://").append(HttpFSConf.getHOST())
				.append(":").append(HttpFSConf.getPORT()).append(SERVICE_PATH)
				.append(path).append("?").append(params);
		return sb.toString();
	}

	public static void validateResponse(InputStream in, int respCode,
			int expectedCode) {
		if (respCode != expectedCode) {
			JSONObject json = (JSONObject) jsonParse(in);
			json = (JSONObject) json.get("RemoteException");
			String message = (String) json.get("message");
			String exception = (String) json.get("exception");
			String className = (String) json.get("javaClassName");
		}
	}

	public static void parseResult(String str) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			json = (JSONObject) json.get("FileStatuses");
			JSONArray jsonArray = (JSONArray) json.get("FileStatus");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = (JSONObject) jsonArray.get(i);
				Set set = obj.keySet();
				for (Iterator it = set.iterator(); it.hasNext();) {
					String key = (String) it.next();
					log.info(key + "==>" + obj.get(key));
				}
			}
		} catch (Exception e) {
			log.error("jsonParse fail", e);
		}
	}

	public static Object jsonParse(InputStream in) {
		try {
			JSONParser parser = new JSONParser();
			return parser.parse(new InputStreamReader(in));
		} catch (Exception e) {
			log.error("jsonParse fail", e);
		}
		return null;
	}
}