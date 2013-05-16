package com.xenosync.afclient

class Ticket(val credentials : Credentials){
	import org.apache.http.HttpEntity
	import org.apache.http.HttpResponse
	import org.apache.http.client.methods.HttpGet
	import org.apache.http.impl.client.DefaultHttpClient
	import scala.io.Source
	import java.io.File
	import org.apache.commons.io.FileUtils
	
	val creds : Credentials = credentials
	var ticket : String = readTicketFile()
	println(ticket)
	
	def readTicketFile() : String = {
		val ticketFile : File = new File(creds.ticket)
		validateTicket(FileUtils.readLines(ticketFile, "UTF-8").iterator().next())
	}
	
	def writeTicketFile(string : String) = {
		val ticketFile : File = new File(creds.ticket)
		FileUtils.writeStringToFile(ticketFile, string, "UTF-8", false)
	}
	
	def validateTicket(currentTicket : String) : String = {
		val client = new DefaultHttpClient();
		//this is broken
		val get = new HttpGet(creds.server + "login/ticket/" + currentTicket)
		val response = client.execute(get)
		val entity = response.getEntity
		val rawTicket = Source.fromInputStream(entity.getContent()).getLines.mkString
		if(("TICKET_.{40}".r findAllIn rawTicket).mkString == currentTicket)
			currentTicket
		else
			getNewTicket()
	}
	
	def getNewTicket() : String = {
		val client = new DefaultHttpClient();
		val get = new HttpGet(creds.server + "login?u=" + creds.u + "&pw=" + creds.pw)
		val response = client.execute(get)
		val entity = response.getEntity
		val rawTicket = Source.fromInputStream(entity.getContent()).getLines.mkString
		val ticketString : String = ("TICKET_.{40}".r findAllIn rawTicket).mkString
		val f : File = new File("ticket")
		writeTicketFile(ticketString)
		ticketString
	}
}