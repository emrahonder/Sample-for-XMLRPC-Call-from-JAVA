package com.nioya.samplexmlrpc;

public class Test {

	public static void main(String[] args) {
		SendXMLRPC sendXMLRPC = new SendXMLRPC("http://YOURDOMAIN.YOURTLD:PORT/SMT");
		sendXMLRPC.send();

	}

}
