package com.catt.httpfs.client.utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpFSConf {
	private final static Log log = LogFactory.getLog(HttpFSConf.class);

	private static String HOST;
	private static int PORT = 14000;

	public static String getHOST() {
		return HOST;
	}

	public static void setHOST(String hOST) {
		HOST = hOST;
	}

	public static int getPORT() {
		return PORT;
	}

	public static void setPORT(int pORT) {
		PORT = pORT;
	}

	static {
		Properties prop = new Properties();
		InputStream in;
		try {
			in = HttpFSConf.class.getResourceAsStream("/httpfs.properties");
			prop.load(in);
			in.close();
		} catch (Exception e) {
			log.error("load httpfs.properties fail", e);
		}

		Set keyValue = prop.keySet();
		for (Iterator it = keyValue.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if ("host".equals(key)) {
				HttpFSConf.setHOST(prop.getProperty(key));
			} else if ("port".equals(key)) {
				HttpFSConf.setPORT(Integer.parseInt(prop.getProperty(key,
						"14000")));
			}
		}
	}
	
	public static void main(String[] args) {
		log.info(HttpFSConf.getHOST());
		log.info(HttpFSConf.getPORT());
	}

}
