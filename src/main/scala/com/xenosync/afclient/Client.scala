package com.xenosync.afclient

object AFClient{
	import com.xenosync.afclient.Credentials
	def main(args: Array[String]) = {
		println("Alfresco Scala Client")
		val t: Ticket = new Ticket(new Credentials())
	}
}