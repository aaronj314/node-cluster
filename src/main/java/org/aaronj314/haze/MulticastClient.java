package org.aaronj314.haze;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MulticastClient implements Runnable {
	NodeCluster nodeCluster;
	DatagramChannel channel;
	MulticastSocket receiver;
	AsyncClient asyncClient;
	
	MulticastClient(NodeCluster nodeCluster, DatagramChannel channel) {
		this.nodeCluster = nodeCluster;
		this.channel = channel;
		asyncClient = new AsyncClient(nodeCluster);
	}

	public void run() {
		try {
			while (true) {
				
				try {
					//ADD_NODE|UUID|HOST|PORT|TIMESTAMP
					String[] data = readStringFromChannel(channel).split("\\|");
					
					long ts = Long.valueOf(data[4]);
					if (data[0].equals(MulticastServer.ADD_NODE) && !data[1].equals(nodeCluster.localNode.uuid) && ts > nodeCluster.lastupdated) {
						//System.out.println("data="+data[2]+":"+data[3]+":"+data[1]+":"+data[0]+"||");
						//String[] addr = data[2].split(":");
					
								
						InetSocketAddress addr = new InetSocketAddress(data[2], Integer.valueOf(Integer.valueOf(data[3])));
							asyncClient.pingNode(addr,"ADD_NODE_SYN|"+nodeCluster.localNode.uuid
									+"|"+nodeCluster.clusterIP+"|"+nodeCluster.cPort+"|"+nodeCluster.lastupdated);
								
		
						

						
					}
					
				} catch (SocketTimeoutException ste) {
				}
			}
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	private String readStringFromChannel(DatagramChannel c) throws IOException {
		ByteBuffer in = ByteBuffer.allocate(1024);
		c.receive(in);
		in.flip();
		int limits = in.limit();
		byte bytes[] = new byte[in.limit()];
		in.get(bytes, 0, limits);
		String data = new String(bytes);
		return data;
	}
}
