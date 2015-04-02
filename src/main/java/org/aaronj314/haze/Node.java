package org.aaronj314.haze;

import java.util.UUID;

public class Node {
	String uuid;
	String hostIp;
	int port = 5050;
	
	
	public Node() {
		uuid = UUID.randomUUID().toString();
	}
	
	public Node(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "UUID="+uuid+":hostIp="+hostIp+":port="+port;
	}
}
