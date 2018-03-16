/*
 * Server.java

 */

// Current Version

import java.io.*;
import java.net.*;
import java.util.*;

public class MultiThreadServer {

    public static final int SERVER_PORT = 3246;

    public static void main(String args[]) 
    {
    	ServerSocket myServerice = null;
		Socket serviceSocket = null;
		//ChildThread cThread;
		ChildThread.readFile(ChildThread.contacts, ChildThread.contact);
		ChildThread.readFile(ChildThread.users, ChildThread.user);
		
		// Try to open a server socket 
		try {
		    myServerice = new ServerSocket(SERVER_PORT);
		    System.out.println("System Connected");
		}
		catch (IOException e) {
		    System.out.println(e);
		}   
	
		// Create a socket object from the ServerSocket to listen and accept connections.
		while (true)
		{
		    try 
		    {
			// Received a connection
				serviceSocket = myServerice.accept();
				System.out.println("MultiThreadServer: new connection from " + serviceSocket.getInetAddress());
		
				// Create and start the client handler thread
				 ChildThread cThread = new ChildThread(serviceSocket);
				cThread.start();
		    }   
		    catch (IOException e) 
		    {
		    		System.out.println(e);
		    }
		}
    }
}
