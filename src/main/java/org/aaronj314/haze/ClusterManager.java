package org.aaronj314.haze;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClusterManager {

	NodeCluster nodeCluster;
	int startLimit;
	
	Thread monitor;
	
	public NodeCluster startNodeCluster(String mcGroup, int mcPort, String clusterIP, int cPort) throws Exception {
		nodeCluster = new NodeCluster();
		nodeCluster.mcGroup  = mcGroup;
		nodeCluster.mcPort = mcPort;
		nodeCluster.mcTTL = 1;
		nodeCluster.clusterIP = clusterIP;
		nodeCluster.cPort = cPort;
		nodeCluster.nInterface = getNetworkInterface(clusterIP);
		
		nodeCluster.start();
		
		startMonitors();
		
		return nodeCluster;
	}
	
	private void startMonitors() {
		monitor = new Thread("node_list_monitor") {
			
			public void run() {
				while(true) {
					if(!nodeCluster.isStarted) {
						updateNodeList();
						checkStartLimit();
					} else {
						updateStartNodeList();
					}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
				}
			}
		};
		monitor.start();
	}
	
	private void checkStartLimit() {
		if (nodeCluster.size() >= startLimit) {
			List<String> uuids =nodeCluster.getNodeUUIDs();
			Collections.sort(uuids, String.CASE_INSENSITIVE_ORDER);
			//System.out.println("checking started:"+nodeCluster.size()+"::"+startLimit);
			if (uuids.get(0).equals(nodeCluster.localNode.uuid) && !nodeCluster.isStarted) {
				nodeCluster.isStarted = true;
				nodeCluster.lastupdated = System.nanoTime();
				System.out.println("********We are started!***********::clusterSize="+(nodeCluster.size() +1));
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void updateStartNodeList() {
		Iterator it = nodeCluster.nodes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
				Node n = ((Node) pair.getValue());
				InetSocketAddress addr = new InetSocketAddress(n.hostIp, Integer.valueOf(n.port));
				boolean error = new AsyncClient(nodeCluster).pingNode(addr, "SYN_START|"+n.uuid+"|"+nodeCluster.lastupdated);
				if (error) {
					it.remove();
					System.out.println("Dead node detected removed: " + pair.getKey());

					if(nodeCluster.isStarted) {
						nodeCluster.isStarted = false;
						nodeCluster.lastupdated = System.nanoTime();
						System.out.println("cluster state set to 'not started':"+(nodeCluster.size()+1));
					}
					return;
				}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void updateNodeList() {
		Iterator it = nodeCluster.nodes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
				Node n = ((Node) pair.getValue());
				//System.out.println(pair.getKey() + " = " + pair.getValue());
				InetSocketAddress addr = new InetSocketAddress(n.hostIp, Integer.valueOf(n.port));
				boolean error = new AsyncClient(nodeCluster).pingNode(addr, "SYN|"+n.uuid+"|"+nodeCluster.lastupdated);
				if (error) {
					it.remove();
					System.out.println("Dead node detected removed: " + pair.getKey());
					if(nodeCluster.isStarted) {
						nodeCluster.isStarted = false;
						nodeCluster.lastupdated = System.nanoTime();
						
					}
				}
		}
	}
	
	private static NetworkInterface getNetworkInterface(String clusterIp) throws Exception {
		//Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		NetworkInterface ni = null;
		ni = NetworkInterface.getByInetAddress(InetAddress.getByName(clusterIp));
//		while (en.hasMoreElements()) {
//			ni = en.nextElement();
//			System.out.println("Network Interface Name: " + ni.getName());
//			if (ni.getName().startsWith("vmnet8")) {
//				System.out.println("IP="+ ni.getInetAddresses().nextElement().getHostAddress());
//				break;
//			}
//		}
		return ni;
	}
}
