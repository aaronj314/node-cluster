package org.aaronj314.haze;

import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Main {
	private Options options;
	int clusterSize;
	volatile boolean isStarted;
	volatile boolean isSyncNode;

	public Main() {
		options = new Options();
		addCliOptions();

	}
	
	public void init(String[] args) throws Exception {
		CommandLineParser parser = new DefaultParser();
		CommandLine cm = parser.parse(options, args);
		
		if(!cm.hasOption("n")) {
			help();
			return;
		}
		
		Iterator<Option> ops = cm.iterator();
		
		int n = 0;
		String m = "225.254.254.5";
		int mp = 5000;
		String c = "192.168.81.1";
		int cp = 5050;
		while(ops.hasNext()) {
			Option o = ops.next();
			if(o.getOpt().equals("n")) {
				n = Integer.valueOf(o.getValue());
				n--;
			}
			
			if(o.getOpt().equals("m")) {
				m = o.getValue();
			} 
			if(o.getOpt().equals("mp")) {
				mp = Integer.valueOf(o.getValue());
			}
			
			if(o.getOpt().equals("c")) {
				c = o.getValue();
			} 

			if(o.getOpt().equals("cp")) {
				cp = Integer.valueOf(o.getValue());
			} 
			
		}
		
		ClusterManager cMgr = new ClusterManager();
		cMgr.startLimit = n;
		NodeCluster nodeCluster = cMgr.startNodeCluster(m, mp, c, cp);
		
		while(!nodeCluster.isLocalStarted) {
			Thread.sleep(1000);
		}
		
		System.out.println("[NODE READY]Local Node UUID:" + nodeCluster.localNode.uuid);
	}


	public static void main(String[] args) throws Exception {

		Main main = new Main();
		main.init(args);
		
	}
	
	private void addCliOptions() {
		options.addOption("h", "help", true, "Help");
		options.addOption("n", "numNodes", true, "Number of nodes in the cluster");
		options.addOption("m", "multicast-addr", true, "Multicast address used for auto discovery - default 225.254.254.5");
		options.addOption("mp", "multicast-port", true, "Multicast port used for auto discovery - default 5000");
		options.addOption("c", "cluster-addr", true, "Cluster addres used for cluster commuication - default 192.168.81.1");
		options.addOption("cp", "cluster-port", true, "Cluster port used for cluster communication - default 5050");
	}

	private void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("Main", options);
		System.exit(0);
	}

}
