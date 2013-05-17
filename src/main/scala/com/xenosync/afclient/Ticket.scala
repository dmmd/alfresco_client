package com.xenosync.afclient

class Ticket(val credentials : Credentials){
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
	import java.io.File
	import org.apache.commons.io.FileUtils
	
	val client = new DefaultHttpClient();
	val creds : Credentials = credentials
	var ticket : String = readTicketFile()
	
	def readTicketFile() : String = {
		val ticketFile : File = new File(creds.ticket)
		validateTicket(FileUtils.readLines(ticketFile, "UTF-8").iterator().next())
	}
	
	def validateTicket(currentTicket : String): String = {
		val request = creds.api + "login/ticket/" + currentTicket
		if(("TICKET_.{40}".r findAllIn httpRequest(request)).mkString == currentTicket)
			currentTicket
		else
			getNewTicket()
	}
	
	def getNewTicket() : String = {
		val request = ("login?u=" + creds.u + "&pw=" + creds.pw)
		val ticketString : String = ("TICKET_.{40}".r findAllIn httpRequest(request)).mkString
		val f : File = new File("ticket")
		writeTicketFile(ticketString)
		ticketString
	}
		
	def writeTicketFile(string : String) = {
		val ticketFile : File = new File(creds.ticket)
		FileUtils.writeStringToFile(ticketFile, string, "UTF-8", false)
	}

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