package org.aaronj314.haze;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
			while (true) {
				if (main.nodes.size() >= main.clusterSize) {
					List<String> uuids = new ArrayList<String>(
							main.nodes.values());
					sortList(uuids);

					if (uuids.get(0).equals(main.uuid)) {
						main.isSyncNode = true;
						main.isStarted = true;
						System.out.println("We are started!");

						return;
					}
				}
				try {
					String data = readStringFromChannel(main.channel);

					if (data.equals("ALL_STARTED")) {
						main.isStarted = true;
						return;
					} else if (!data.equals(main.uuid)) {

						String u = main.nodes.get(data);
						if (u == null) {
							main.nodes.put(data, data);
						}
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

	private static void sortList(List<String> aItems) {
		Collections.sort(aItems, String.CASE_INSENSITIVE_ORDER);
	}
}
