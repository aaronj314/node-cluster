package org.aaronj314.haze;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

public class MulticastServer implements Runnable {
	Main main;
	//int port = 5000;
	//String group = "225.254.254.5";
	int ttl = 1;
	//MulticastSocket sender;
	//DatagramChannel dmChannel;
	
	public MulticastServer(Main main) {
		this.main = main;
	}
	
	public void run() {
		
		try {
//			Enumeration<NetworkInterface> en = NetworkInterface
//					.getNetworkInterfaces();
//			NetworkInterface ni = null;
//			// Retrieve Network Interface
//			while (en.hasMoreElements()) {
//				ni = en.nextElement();
//				System.out.println("Network Interface Name: " + ni.getName());
//				break;
//			}
			
//			dmChannel = DatagramChannel.open(StandardProtocolFamily.INET);
//			DatagramChannel dgChannel = DatagramChannel.open(StandardProtocolFamily.INET);
//
//			dgChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//			dgChannel.bind(null);
//			dgChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
//			
//			//InetAddress address = InetAddress.getByName(main.group);
			InetSocketAddress group = new InetSocketAddress(main.group, main.port);
	
			while(true) {
				ByteBuffer buffer = ByteBuffer.wrap(main.uuid.getBytes());

				if(main.isSyncNode) {
					ByteBuffer allStartedMsg = ByteBuffer.wrap("ALL_STARTED".getBytes());
					main.channel.send(allStartedMsg, group);
					
					return;
				} else if(main.isStarted) {
					return;
				}

				main.channel.send(buffer, group);
				Thread.sleep(200);
			}
//			sender = new MulticastSocket();
//	
//			byte buf[]  = main.uuid.toString().getBytes();
//			
//			DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
//			ttl = sender.getTimeToLive(); 
//			sender.setTimeToLive(ttl);
//			while(true) {
//				
//				if(main.isStarted) {
//					byte end[] = "ALL_STARTED".getBytes();
//					DatagramPacket donePack = new DatagramPacket(end, end.length, InetAddress.getByName(group), port);
//					sender.send(donePack);
//					return;
//				}
//				
//				sender.send(pack);
//				Thread.sleep(2000L);
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//sender.close();
		}
		
		
	}

}
