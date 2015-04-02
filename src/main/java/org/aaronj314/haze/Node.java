package org.aaronj314.haze;

import java.util.UUID;

public class Node {
	String uuid;
	String hostIp;
	int port = 5050;
	NodeCluster nodeCluster;
	
	
	public Node(NodeCluster nodeCluster) {
		uuid = UUID.randomUUID().toString();
		this.nodeCluster = nodeCluster;
	}
	
	public Node(NodeCluster nodeCluster, String uuid) {
		this.uuid = uuid;
		this.nodeCluster = nodeCluster;
	}
	
	@Override
	public String toString() {
		return "UUID="+uuid+":hostIp="+hostIp+":port="+port+":timestamp="+nodeCluster.lastupdated;
	}
}
