package org.aaronj314.haze;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Enumeration;
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
	volatile boolean isSyncNode;
	DatagramChannel channel;
	MembershipKey key;
	NetworkInterface networkInter;

	public Main(int size) throws Exception {
		port = 5000;
		isStarted = false;
		isSyncNode = false;
		group = "225.254.254.5";
		uuid = UUID.randomUUID().toString();
		nodes = new ConcurrentHashMap<String, String>();
		nodes.put(uuid, uuid);
		clusterSize = size;

		networkInter = getNetworkInterface();
		channel = openChannel();

		InetAddress groupAddr = InetAddress.getByName(group);
		key = channel.join(groupAddr, networkInter);

	}

	public static void main(String[] args) throws Exception {
		Main main = new Main(10);

	

		Thread tClient = new Thread(new MulticastClient(main));
		Thread tServer = new Thread(new MulticastServer(main));

		tClient.setName("multicast client");
		tServer.setName("multicast server");

		tClient.start();
		tServer.start();
	
		
		while (true) {
			if (main.isStarted) {
				System.out.println(main.uuid + "::node ready");
				return;
			}

			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
			}
		}

	}

	private DatagramChannel openChannel() throws IOException {
		DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET);

		NetworkInterface interf = NetworkInterface.getByName("en4");
		dc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		dc.bind(new InetSocketAddress(port));
		dc.setOption(StandardSocketOptions.IP_MULTICAST_IF, interf);
		return dc;
	}

	private static NetworkInterface getNetworkInterface() throws SocketException {
		Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces();
		NetworkInterface ni = null;

		while (en.hasMoreElements()) {
			ni = en.nextElement();
			System.out.println("Network Interface Name: " + ni.getName());
			break;
		}
		return ni;
	}

}
