package com.xenosync.afclient

object AFClient{
	def main(args: Array[String]) = {
		println("Alfresco Scala Client")
		val ticket : Ticket = new Ticket()
		println(ticket)
	}
}