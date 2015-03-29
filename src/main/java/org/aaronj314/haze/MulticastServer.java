package org.aaronj314.haze;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastServer implements Runnable {
	Main main;
	int port = 5000;
	String group = "225.254.254.5";
	int ttl = 1;
	MulticastSocket sender;
	
	public MulticastServer(Main main) {
		this.main = main;
	}
	
	public void run() {
		
		try {
			sender = new MulticastSocket();
	
			byte buf[]  = main.uuid.toString().getBytes();
			
			DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
			ttl = sender.getTimeToLive(); 
			sender.setTimeToLive(ttl);
			while(true) {
				
				if(main.isStarted) {
					byte end[] = "ALL_STARTED".getBytes();
					DatagramPacket donePack = new DatagramPacket(end, end.length, InetAddress.getByName(group), port);
					sender.send(donePack);
					return;
				}
				
				sender.send(pack);
				Thread.sleep(2000L);
			}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} finally {
			sender.close();
		}
		
		
	}

}
