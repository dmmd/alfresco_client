package com.xenosync.afclient

class Ticket(){

	import java.io.File
	import org.apache.commons.io.FileUtils
	import com.xenosync.afclient.Credentials
	import com.xenosync.afclient.Request
	
	val creds : Credentials = new Credentials()
	var ticket : String = readTicketFile()
	
	def readTicketFile() : String = {
		val ticketFile : File = new File(creds.ticket)
		validateTicket(FileUtils.readLines(ticketFile, "UTF-8").iterator().next())
	}
	
	def validateTicket(currentTicket : String): String = {
		val request = creds.api + "login/ticket/" + currentTicket
		if(("TICKET_.{40}".r findAllIn new Request(request).getResponse()).mkString == currentTicket)
			currentTicket
		else
			getNewTicket()
	}
	
	def getNewTicket() : String = {
		val request = ("login?u=" + creds.u + "&pw=" + creds.pw)
		val ticketString : String = ("TICKET_.{40}".r findAllIn new Request(request).getResponse()).mkString
		val f : File = new File("ticket")
		writeTicketFile(ticketString)
		ticketString
	}
		
	def writeTicketFile(string : String) = {
		val ticketFile : File = new File(creds.ticket)
		FileUtils.writeStringToFile(ticketFile, string, "UTF-8", false)
	}
	
	override def toString() : String = ticket
}