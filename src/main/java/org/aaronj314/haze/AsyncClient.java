package org.aaronj314.haze;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AsyncClient implements Runnable,CompletionHandler<Integer, ByteBuffer> {
	volatile boolean completed = false;
	volatile boolean error = false;
	NodeCluster nodeCluster;
	public AsyncClient(NodeCluster nodeCluster) {
		this.nodeCluster = nodeCluster;
	}

	public boolean pingNode(InetSocketAddress nodeAddr, String msg) {
		completed = false;
		error = false;
		ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(1024);
		ByteBuffer sendingBuffer = ByteBuffer.wrap(msg.getBytes());

		try {
			//System.out.println("sending messing to node:"+msg);
			writeToChannel(nodeAddr, receivingBuffer, sendingBuffer);
		} catch (Exception ex) {
			//System.out.println("Error reading node:"+msg);
			error = true;
		}
		return error;
	}
	
	private void writeToChannel(InetSocketAddress nodeAddr, ByteBuffer receivingBuffer, ByteBuffer sendingBuffer) throws Exception {
		final AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
		if (asynchronousSocketChannel.isOpen()) {
			//String ip = main.networkInter.getInetAddresses().nextElement().getHostAddress();
			Void connect = asynchronousSocketChannel.connect(nodeAddr).get();
			if (connect == null) {
				//System.out.println("Local address: "+ asynchronousSocketChannel.getLocalAddress());
				asynchronousSocketChannel.write(sendingBuffer).get();
				asynchronousSocketChannel.read(receivingBuffer, receivingBuffer, this);
				
				while (!completed) {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}
					//System.out.println("Waiting for response from the server");
				}

			} else {
				System.out.println("The connection cannot be established!");
			}
		} else {
			System.out.println("The asynchronous socket channel cannot be opened!");
		}
	}
	
	

	@Override
	public void run() {

	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		buffer.flip();
		String msgReceived = Charset.defaultCharset().decode(buffer).toString();
		String[] data = msgReceived.split("\\|");
		 
		 if(data[0].equals("ADD_NODE_ACK")) {
        	 handleAddNodeSyn(data);
         } 
		
		completed = true;
		
	}
	
	

	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		
		//System.out.println("Error:"+exc.getMessage());
		completed = true;
		error = true;
	}
	
	private void handleAddNodeSyn(String[] data) {
		Node u = nodeCluster.nodes.get(data[1]);
		if (u == null) {
			
			
				Node n = new Node(data[1]);
				n.hostIp = data[2];
				n.port = Integer.valueOf(data[3]);
				nodeCluster.nodes.put(data[1], n);
			
				System.out.println("THIS NODE:"+nodeCluster.localNode);
				System.out.println("THIS NODE TS:"+nodeCluster.lastupdated);
				System.out.println("ADDED THIS NODE TO LIST:"+n);
				System.out.println("NODE LIST="+nodeCluster.nodes);
	
		}
	}

}
