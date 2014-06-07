package com.catt.httpfs.client.httpclient;

public class Demo {
	public static void main(String[] args) {
		HttpFSClient client = new HttpFSClient();
		client.initCookie();

		// 获取当前用户的目录
		client.get("", "op=gethomedirectory");

//		 // 上传文件
//		 client.put("/test", "op=CREATE&buffersize=1000");
//		 client.upload("/test/pom.xml", "op=CREATE&buffersize=1000&data=true",
//		 "pom.xml");
//		
//		 // 删除文件
//		 client.delete("/test2/demo.xml", "op=DELETE");
//		
//		 // 创建目录
//		 client.put("/test2/test9", "op=MKDIRS");
//		
//		 // 读取文件
//		 client.get("/test/data.txt",
//		 "op=OPEN&buffersize=10000&data=true",true);

		// 获取文件列表信息
		String result = client.get("/test", "op=LISTSTATUS");
		System.out.println(result);
		// 处理返回信息
		HttpFSUtils.parseResult(result);
	}
}
