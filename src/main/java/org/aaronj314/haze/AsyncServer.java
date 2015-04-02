package org.aaronj314.haze;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class AsyncServer implements Runnable {
	volatile boolean isStarted = false;
	
	NodeCluster nodeCluster;
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	
	public AsyncServer(NodeCluster nodeCluster) {
		this.nodeCluster = nodeCluster;
	}
	
	@Override
	public void run() {
		//create asynchronous server-socket channel bound to the default group
        try {
        	asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            if (asynchronousServerSocketChannel.isOpen()) {
            	
            	synchronized (this) {
            		bind(asynchronousServerSocketChannel);
					notifyAll();
				}
 
                //display a waiting message 
                System.out.println("Waiting for connections ...");
                isStarted = true;
                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel.accept();
                    try {
                    	AsynchronousSocketChannel channel = asynchronousSocketChannelFuture.get();
                        //System.out.println("Incoming connection from: " + asynchronousSocketChannel.getRemoteAddress());
                        ByteBuffer incomingBuffer = ByteBuffer.allocateDirect(1024);
                        //receiving data
                        org.aaronj314.haze.ServerCompletionHandler handler = new org.aaronj314.haze.ServerCompletionHandler(channel, nodeCluster);
                        channel.read(incomingBuffer, incomingBuffer, handler);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		
	}
	
	private void bind(AsynchronousServerSocketChannel channel) throws Exception {
		channel.bind(new InetSocketAddress(nodeCluster.localNode.hostIp, nodeCluster.localNode.port));
		System.out.println("Node Server listening on "+nodeCluster.clusterIP+":"+nodeCluster.localNode.port);
	}
}
