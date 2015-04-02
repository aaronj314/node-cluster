package org.aaronj314.haze;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MulticastServer implements Runnable {
	static final String ADD_NODE = "ADD_NODE_SYNC";
	NodeCluster nodeCluster;
	DatagramChannel channel;
	public MulticastServer(NodeCluster nodeCluster, DatagramChannel channel) {
		this.nodeCluster = nodeCluster;
		this.channel = channel;
	}
	
	public void run() {
		
		try {
			InetSocketAddress group = new InetSocketAddress(nodeCluster.mcGroup, nodeCluster.mcPort);
			NetworkInterface ni = channel.getOption(StandardSocketOptions.IP_MULTICAST_IF);
			String msg = ADD_NODE+"|"+nodeCluster.localNode.uuid+"|"+ni.getInetAddresses().nextElement().getHostAddress()
					+"|"+nodeCluster.localNode.port+"|"+nodeCluster.lastupdated;
			//while(true) {
				//System.out.println("out messsage from MC server:"+msg);
				ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
				channel.send(buffer, group);
				Thread.sleep(2000);
				
			//}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		
	}

}
