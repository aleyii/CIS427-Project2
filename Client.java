/* 
 * Client.java
 */

import java.io.*;
import java.net.*;

public class Client 
{
    public static final int SERVER_PORT = 3246;
    public static boolean closeClient;
    public static String userInput = null;
	public static Socket clientSocket = null;
	public static BufferedReader stdInput = null;

    public static void main(String[] args) 
    {
	PrintStream os = null;
	BufferedReader is = null;
	closeClient=true;

	//Check the number of command line parameters
	if (args.length < 1)
	{
	    System.out.println("Usage: client <Server IP Address>");
	    System.exit(1);
	}

	// Try to open a socket on SERVER_PORT
	// Try to open input and output streams
	try 
	{
	    clientSocket = new Socket(args[0], SERVER_PORT);
	    os = new PrintStream(clientSocket.getOutputStream(),true);
	    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    stdInput = new BufferedReader(new InputStreamReader(System.in));
	} 
	catch (UnknownHostException e) 
	{
	    System.err.println("Don't know about host: hostname");
	} 
	catch (IOException e) 
	{
	    System.err.println("Couldn't get I/O for the connection to: hostname");
	}

	// If everything has been initialized then we want to write some data
	// to the socket we have opened a connection to on port 25

	if (clientSocket != null && os != null) 
	{
	    try 
	    {
		//Start a child thread to handle the server's messages
		SThread sThread = new SThread(is); // handles message to client
		sThread.start();
		PThread pThread = new PThread(os); // handles message to server
		pThread.start();

		//handle the user input
		sThread.join();
		pThread.join();
		// close the input and output stream
		// close the socket
	    }
	    catch (Exception e)
	    {}
	    try 
	    {
	    		is.close();
			os.close();
			clientSocket.close();
	    }
	    catch (Exception e)
	    {}
	}
    }           
}
/*
 * PThread Class, handles sending messages to server
 */
class PThread extends Thread
{
	PrintStream os = null;
	PThread(PrintStream os)
	{
		this.os = os;
	}
	public void run()
	{
		try {
		while (Client.closeClient && (Client.userInput = Client.stdInput.readLine())!= null)
		{
		    os.println(Client.userInput);
		    if (Client.userInput.equals("QUIT")||Client.userInput.equals("SHUTDOWN"))
		    {
		    	 break;
		    }
		}
		}
		catch (IOException e)
		{
		}
	}
}
/*
 * SThread Class, which handle the server's messages
 */
class SThread extends Thread 
{
    String serverInput = null;
    BufferedReader is = null;
    /**
     * Constructor
     */
    SThread(BufferedReader is)
    {
    	this.is = is;
    }

    /*
     * Child thread of execution, handle the server's messages
     */
    public void run ()
    {
	try 
	{

	    while (Client.closeClient && (serverInput = is.readLine())!= null)
	    {
	    	 		String [] output = serverInput.split("=");
	    	 		
	    	 		
			    for ( int i=0;i<output.length;i++)
			    {
			    		System.out.println(output[i]);
			    }
			    
			    if(output[0].equals("QUIT"))
			    		break;
			    
			    else if(output[0].equals("SHUTDOWN")) {
			    	Client.closeClient=false;
			    	System.exit(0);
			    }
	    }
	} 
	catch (IOException e) 
	{
		e.printStackTrace();
	}
    }           
}
