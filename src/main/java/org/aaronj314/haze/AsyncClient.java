package org.aaronj314.haze;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class AsyncClient {

	static boolean completed = false;
    public static void main(String[] args)
    {
         
        final int SERVER_PORT = 9001;
        final String SERVER_IP = "127.0.0.1";
        
        ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(1024);
        ByteBuffer sendingBuffer = ByteBuffer.wrap("Hello".getBytes());
                
        //create asynchronous socket channel
        try (final AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open())
        {
            if (asynchronousSocketChannel.isOpen())
            {
                //connect this channel's socket
                Void connect = asynchronousSocketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT)).get();
                if (connect == null)
                {
                    System.out.println("Local address: " + asynchronousSocketChannel.getLocalAddress());
                     
                    //sending data
                    asynchronousSocketChannel.write(sendingBuffer).get();
                    asynchronousSocketChannel.read(receivingBuffer, receivingBuffer,  new CompletionHandler<Integer, ByteBuffer>()
                    {
                        public void completed(Integer result, ByteBuffer buffer)
                        {
                            buffer.flip();
                            String msgReceived = Charset.defaultCharset().decode(buffer).toString();
                            System.out.println("Msg received from server : " + msgReceived);
                            completed = true;
                        }
                        public void failed(Throwable exc, ByteBuffer buffer)
                        {
                            completed = false;
                            throw new UnsupportedOperationException("read failed!");
                        }                                             
                  });
                   
                  while(!completed)
                  {
                      try
                      {
                          Thread.sleep(1000);
                      }
                      catch(Exception e){}
                      System.out.println("Waiting for response from the server");
                  } 
                   
                }
                else
                {
                    System.out.println("The connection cannot be established!");
                }
            }
            else
            {
                System.out.println("The asynchronous socket channel cannot be opened!");
            }
        }
        catch (IOException | InterruptedException | ExecutionException ex)
        {
            System.err.println(ex);
        }
       
    }

}
