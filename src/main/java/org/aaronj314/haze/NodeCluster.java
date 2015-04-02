package org.aaronj314.haze;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeCluster {
	volatile boolean isStarted;
	volatile long lastupdated;
	volatile boolean isSynced;
	String mcGroup;
	int mcPort;
	int mcTTL;
	String clusterIP;
	int cPort;
	Map<String, Node> nodes;
	NetworkInterface nInterface;
	DatagramChannel dChannel;
	MembershipKey key;
	Node localNode;
	Thread mcServer;
	Thread mcClient;
	Thread nodeServer;

	
	public NodeCluster() {
		nodes = new ConcurrentHashMap<String, Node>();
		lastupdated = System.nanoTime();
	}
	
	protected int size() {
		return nodes.size();
	}
	
	protected List<String> getNodeUUIDs() {
		List l = new ArrayList<String>(nodes.keySet());
		  l.add(localNode.uuid);
		return l;
	}
	
	public void start() throws Exception {
		localNode = new Node();
		localNode.hostIp = clusterIP;
		localNode.port = cPort;
		AsyncServer asyncServer = new AsyncServer(this);
		nodeServer = new Thread(asyncServer);
		nodeServer.setName("node server");
		nodeServer.start();
		
		//wait for node sever to bind
		synchronized (asyncServer) {
			asyncServer.wait();
		}
		
		dChannel= openDatagramChannel();
		InetAddress groupAddr = InetAddress.getByName(mcGroup);
		key = dChannel.join(groupAddr, nInterface);
		mcServer = new Thread(new MulticastServer(this, dChannel));
		mcServer.setName("multicast server");
		mcServer.start();
		
		Thread.sleep(2000);
		
		mcClient = new Thread(new MulticastClient(this, dChannel));
		mcClient.setName("multicast client");
		mcClient.start();
	}
	
	public void addNode(Node node) {
		nodes.put(node.uuid.toString(), node);
	}
	
	
	private DatagramChannel openDatagramChannel() throws IOException {
		DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET);

		dc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		dc.bind(new InetSocketAddress(mcPort));
		dc.setOption(StandardSocketOptions.IP_MULTICAST_IF, nInterface);
		dc.setOption(StandardSocketOptions.IP_MULTICAST_TTL, mcTTL);
		return dc;
	}
}
