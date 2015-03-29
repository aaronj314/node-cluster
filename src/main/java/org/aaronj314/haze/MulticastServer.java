package org.aaronj314.haze;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MulticastServer implements Runnable {
	Main main;
	
	public MulticastServer(Main main) {
		this.main = main;
	}
	
	public void run() {
		
		try {
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
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		
	}

}
