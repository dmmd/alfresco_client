package com.xenosync.afclient

object AFClient{
	def main(args: Array[String]) = {
		println("Alfresco Scala Client")
		println(new Ticket(new Credentials()).ticket)
	}
}