package com.xenosync.afclient

import org.apache.http.HttpEntity
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import scala.io.Source

class Request(request : String){
		
	val creds : Credentials = new Credentials()
	val client = new DefaultHttpClient();
	def getResponse() : String = httpRequest(request)
	
	def httpRequest(request : String) : String = {
		val targetHost = new HttpHost(creds.url, creds.port, "http");
		client.getCredentialsProvider().setCredentials(
			new AuthScope(targetHost.getHostName(), targetHost.getPort()),
			new UsernamePasswordCredentials(creds.u, creds.pw)
		)
		val authCache = new BasicAuthCache();
		val basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		val localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);  
		val get = new HttpGet(request);
		val response = client.execute(targetHost, get, localcontext);
		val entity = response.getEntity
		val output = Source.fromInputStream(entity.getContent()).getLines.mkString
		EntityUtils.consume(entity);
		get.releaseConnection();
		output
	}
}