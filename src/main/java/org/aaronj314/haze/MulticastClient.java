package org.aaronj314.haze;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MulticastClient implements Runnable {
	Main main;
	MulticastSocket receiver;

	// DatagramChannel dmChannel;

	MulticastClient(Main main) {
		this.main = main;
	}

	public void run() {

		try {
			// Enumeration<NetworkInterface> en = NetworkInterface
			// .getNetworkInterfaces();
			// NetworkInterface ni = null;
			// // Retrieve Network Interface
			// while (en.hasMoreElements()) {
			// ni = en.nextElement();
			// System.out.println("Network Interface Name: " + ni.getName());
			// break;
			// }
			//
			// MembershipKey key = null;
			// dmChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			//
			// NetworkInterface interf = NetworkInterface.getByName("en4");
			// dmChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			// dmChannel.bind(new InetSocketAddress(main.port));
			// dmChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF,
			// interf);
			//
			// InetAddress group = InetAddress.getByName(main.group);
			// key = dmChannel.join(group, interf);

			// Thread t = new Thread() {
			// @Override
			// public void run() {
			// try {
			// InetSocketAddress group = new InetSocketAddress(main.group,
			// main.port);
			//
			// while(true) {
			// ByteBuffer buffer = ByteBuffer.wrap(main.uuid.getBytes());
			// System.out.println("sending="+main.uuid);
			// main.channel.send(buffer, group);
			// Thread.sleep(2000L);
			// }
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
			// }
			// };
			// t.start();

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

			// receiver = new MulticastSocket(main.port);
			// receiver.joinGroup(InetAddress.getByName(main.group));
			// receiver.setSoTimeout(5000);
			// while (true) {
			// if(main.nodes.size() >= main.clusterSize) {
			// List<String> uuids = new ArrayList<String>(main.nodes.values());
			// sortList(uuids);
			//
			//
			// if(uuids.get(0).equals(main.uuid)) {
			// System.out.println("We are started!");
			// main.isStarted = true;
			// return;
			// }
			//
			//
			//
			// }
			//
			// byte buf[] = new byte[1024];
			// DatagramPacket pack = new DatagramPacket(buf, buf.length);
			//
			// try {
			// receiver.receive(pack);
			// String data = new String(pack.getData(), 0, pack.getLength());
			//
			// if(data.equals("ALL_STARTED")) {
			// main.isStarted = true;
			// return;
			// } else if (!data.equals(main.uuid)) {
			// //System.out.println("Received data from: "
			// // + pack.getAddress().toString() + ":" + pack.getPort()
			// // + " with length: " + pack.getLength());
			// //System.out.println("uuid=" + uuid + "::" + main.uuid);
			//
			// String u = main.nodes.get(data);
			// if(u == null) {
			// main.nodes.put(data,data);
			// }
			//
			// //System.out.println("Nodes::"+main.nodes.size());
			// }
			// //System.out.write(pack.getData(), 0, pack.getLength());
			// //System.out.println("Master=" + main.isMainNode);
			// //System.out.println();
			// } catch (SocketTimeoutException ste) {}
			// }

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// try {
			// //receiver.leaveGroup(InetAddress.getByName(main.group));
			// //receiver.close();
			// } catch (UnknownHostException e) {
			//
			// e.printStackTrace();
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			// }
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
