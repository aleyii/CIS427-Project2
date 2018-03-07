/*
 * Server.java
 */

import java.io.*;
import java.net.*;
import java.util.*;
//PROJECT 2 THREAD
public class MultiThreadServer {

    public static final int SERVER_PORT = 5432;

    public static void main(String args[]) 
    {
		ServerSocket myServerice = null;
		Socket serviceSocket = null;
	
		
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
    
    
    //PRE: NONE
    //Post: Attempt open file, if successful, for each line, create row for array list, split the line at each space
    //		add each piece of info into that row, add row to the 2d array, close file
    private static Scanner x;
	static void readFile(ArrayList<ArrayList<String>> data,String fileName)
	{
		try{
			x = new Scanner (new File(fileName));
		}
		catch(Exception e){
			
			System.out.println("File does not exists");
		}
		while(x.hasNextLine())
		{
			ArrayList<String> c1= new ArrayList<String>();
			String [] parts = x.nextLine().split(" "); // using next line to read entire line instead each of bit of data
			for (int i=0;i <parts.length;i++)
				c1.add(parts[i]);	
			data.add(c1);
		}
		x.close();
	}
	
	

	//PRE: command called, Data present in database
	//POST: Write each line to selected file
	static void writeFile(ArrayList<ArrayList<String>> contacts) throws IOException {
		PrintWriter ons = new PrintWriter("contacts.txt"); 
	    
	    for(int i=0;i<contacts.size();i++) {
	    		for(int j=0;j<contacts.get(i).size();j++){
	    			ons.write(contacts.get(i).get(j)+" ");
	    		}
	    		ons.write("\n");
	    }
	    ons.close();
	}
}
