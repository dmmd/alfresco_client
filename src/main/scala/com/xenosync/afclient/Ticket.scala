package com.xenosync.afclient

import com.typesafe.config._
import java.io.File
import org.apache.commons.io.FileUtils

class Ticket(){
			
	val creds = ConfigFactory.load()
	var ticket : String = readTicketFile()
	
	def readTicketFile() : String = {
		val ticketFile : File = new File(creds.getString("afc.ticket"))
		validateTicket(FileUtils.readLines(ticketFile, "UTF-8").iterator().next())
	}
	
	def validateTicket(currentTicket : String): String = {
		val request = creds.getString("afc.api") + "login/ticket/" + currentTicket
		if(("TICKET_.{40}".r findAllIn new Request(request).getResponse()).mkString == currentTicket)
			currentTicket
		else
			getNewTicket()
	}
	
	def getNewTicket() : String = {
		val request = (creds.getString("afc.api") + "login?u=" + creds.getString("afc.u") + "&pw=" + creds.getString("afc.pw"))
		val ticketString : String = ("TICKET_.{40}".r findAllIn new Request(request).getResponse()).mkString
		val f : File = new File("ticket")
		writeTicketFile(ticketString)
		ticketString
	}
		
	def writeTicketFile(string : String) = {
		val ticketFile : File = new File(creds.getString("afc.ticket"))
		FileUtils.writeStringToFile(ticketFile, string, "UTF-8", false)
	}
	
	override def toString() : String = ticket
}