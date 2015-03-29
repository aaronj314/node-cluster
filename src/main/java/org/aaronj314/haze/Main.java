package org.aaronj314.haze;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
	int port;
	String group;
	Map<String, String> nodes;
	String uuid;
	int clusterSize;
	volatile boolean isStarted;
	
	public Main(int size) {
		port = 5000;
		group = "225.254.254.5";
		uuid = UUID.randomUUID().toString();
		nodes = new ConcurrentHashMap<String, String>();
		nodes.put(uuid, uuid);
		clusterSize = size;
		
	}
	
	public static void main(String[] args) {
		
		
		Main main = new Main(10);
		
		
		MulticastClient client = new MulticastClient(main);
		MulticastServer server = new MulticastServer(main);
		
		Thread tClient = new Thread(client);
		Thread tServer = new Thread(server);
		
		
		tClient.setName("multicast client");
		tClient.start();
		
		tServer.setName("multicast server");
		tServer.start();
		
		
		

	}

}
