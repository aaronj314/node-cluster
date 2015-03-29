package org.aaronj314.haze;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MulticastClient implements Runnable {
	Main main;
	MulticastSocket receiver;

	MulticastClient(Main main) {
		this.main = main;
	}

	public void run() {
		
		try {
			receiver = new MulticastSocket(main.port);
			receiver.joinGroup(InetAddress.getByName(main.group));
			receiver.setSoTimeout(5000);
			while (true) {
				if(main.nodes.size() >= main.clusterSize) {
					List<String> uuids = new ArrayList<String>(main.nodes.values());
					sortList(uuids);
					
					
						if(uuids.get(0).equals(main.uuid)) {
							System.out.println("We are started!");
							main.isStarted = true;
							return;
						}
					
					
					
				}
				
				byte buf[] = new byte[1024];
				DatagramPacket pack = new DatagramPacket(buf, buf.length);
				
				try {
					receiver.receive(pack);
					String data = new String(pack.getData(), 0, pack.getLength());
					
					if(data.equals("ALL_STARTED")) {
						main.isStarted = true;
						return;
					} else if (!data.equals(main.uuid)) {
						//System.out.println("Received data from: "
						//		+ pack.getAddress().toString() + ":" + pack.getPort()
						//		+ " with length: " + pack.getLength());
						//System.out.println("uuid=" + uuid + "::" + main.uuid);
						
						String u = main.nodes.get(data);
						if(u == null) {
							main.nodes.put(data,data);
						}
						
						//System.out.println("Nodes::"+main.nodes.size());
					} 
					//System.out.write(pack.getData(), 0, pack.getLength());
					//System.out.println("Master=" + main.isMainNode);
					//System.out.println();
				} catch (SocketTimeoutException ste) {}
			}

		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			try {
				receiver.leaveGroup(InetAddress.getByName(main.group));
				receiver.close();
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}
	
	private static void sortList(List<String> aItems){
	    Collections.sort(aItems, String.CASE_INSENSITIVE_ORDER);
	  }
}
