package org.apache.hadoop.fs.http.client;

import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.catt.httpfs.client.utils.HttpFSConf;

public class Demo {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://localhost.localdomain:8020/");

		Path path = new Path("/test2/test3");
		FileSystem fs = getHttpFileSystem();
		fs.mkdirs(path);
		fs.close();
		fs = FileSystem.get(conf);
		System.out.println(fs.exists(path));
		fs.close();
	}

	protected static FileSystem getHttpFileSystem() throws Exception {
		Configuration conf = new Configuration();
		conf.set("fs.http.impl", HttpFSFileSystem.class.getName());
		String url = "http://" + HttpFSConf.getHOST() + ":"
				+ HttpFSConf.getPORT();
		return FileSystem.get(new URL(url).toURI(), conf);
	}
}
