httpfs-client util
	httpfs-client is a client tool of HttpFS server which provides a REST HTTP gateway to HDFS with full
filesystem read & write capabilities.
	
	httpfs-client read & write hdfs filesystem with the webhdfs REST HTTP API
	
	说明：
	包com.catt.httpfs.client.httpclient是采用commons-httpclient.jar,基于http请求实现的，没有使用到hadoop相关的jar
	包org.apache.hadoop.fs.http.client根据httpfs项目的源代码，根据需要修改了一下，使用了hadoop相关的jar
	
example
	please read com.catt.httpfs.client.httpclient.Demo and org.apache.hadoop.fs.http.client.Demo

How to build:

  Clone this Git repository. Checkout the cdh3u5 branch.

  Run 'mvn package -Pdist'.

  The resulting TARBALL will under the 'target/' directory.
  
 Reference
 	http://cloudera.github.com/httpfs/
 	
 Contact me
 	http://my.oschina.net/cloudcoder
 	欢迎各位多提宝贵意见，并贡献你的力量

