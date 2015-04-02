package org.aaronj314.haze;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ServerCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	AsynchronousSocketChannel channel;
	NodeCluster nodeCluster;
	
	public ServerCompletionHandler(AsynchronousSocketChannel channel, NodeCluster nodeCluster) {
		this.channel = channel;
		this.nodeCluster = nodeCluster;
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		 buffer.flip();
         String msgReceived = Charset.defaultCharset().decode(buffer).toString();
         String[] data = msgReceived.split("\\|");
        
         if(data[0].equals("ADD_NODE_SYN")) {
        	 //System.out.println(msgReceived);
        	 handleAddNodeSyn(data);
        	 writeToChannel("ADD_NODE_ACK|"+nodeCluster.localNode.uuid+"|"+nodeCluster.localNode.hostIp
        			 +"|"+nodeCluster.localNode.port+"|"+nodeCluster.lastupdated);
        	 nodeCluster.isSynced = true;
         } else if(data[0].equals("SYN")) {
        	long ts = Long.valueOf(data[2]);
        	if(ts > nodeCluster.lastupdated) {
        		nodeCluster.lastupdated = ts;
        	}
            writeToChannel("ACK|"+nodeCluster.localNode.uuid);
         } else if (data[0].equals("SYN_START")) {
        	 long ts = Long.valueOf(data[2]);
         	if(ts > nodeCluster.lastupdated) {
         		nodeCluster.lastupdated = ts;
         		System.out.println("'Node set to started state by node:"+data[1]);
         		 nodeCluster.isStarted = true;
         	}
        	
        	 writeToChannel("ACK_START|"+nodeCluster.localNode.uuid);
         }
		
	}

	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		 
		System.out.println(exc);
	}
	
	private void handleAddNodeSyn(String[] data) {
		Node u = nodeCluster.nodes.get(data[1]);
		if (u == null) {
			
			
				Node n = new Node(data[1]);
				n.hostIp = data[2];
				n.port = Integer.valueOf(data[3]);
				nodeCluster.lastupdated = Long.valueOf(data[4]);
				nodeCluster.nodes.put(data[1], n);
			
				//System.out.println("THIS NODE:"+nodeCluster.localNode);
				//System.out.println("THIS NODE TS:"+nodeCluster.lastupdated);
				System.out.println("ADDED NODE::"+n+":TO NODE::"+nodeCluster.localNode);
				//System.out.println("NODE LIST="+nodeCluster.nodes);
	
		}
	}
	
	private void writeToChannel(String s) {
		ByteBuffer outgoingBuffer = ByteBuffer.wrap(s.getBytes());
        try {
       	 channel.write(outgoingBuffer).get();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
