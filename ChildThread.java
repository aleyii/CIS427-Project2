/* 
 * ChildThread.java
 */


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class ChildThread extends Thread 
{
    static  Vector<ChildThread> handlers = new Vector<ChildThread>(20);
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    public static ArrayList<ArrayList<String>> contacts = new ArrayList<ArrayList<String>>(); // Holds Data from file
	public static ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
	public static String contact = "contacts.txt", user ="users.txt";

	private boolean isLogged=false;
	
    public ChildThread(Socket socket) throws IOException 
    {
	this.socket = socket;
	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	out = new PrintWriter( new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() 
    {
		String line;
		synchronized(handlers) 
		{
		    // add the new client in Vector class
		    handlers.addElement(this);
		}
	
		try 
		{
		    while ((line = in.readLine()) != null) 
		    {
			System.out.println(line);
		
			// Broadcast it to everyone!  You will change this.  
			// Most commands do not need to broadcast
				for(int i = 0; i < handlers.size(); i++) 
				{	
				    synchronized(handlers) 
				    {
				    	ChildThread handler = (ChildThread)handlers.elementAt(i);
						if (handler != this) 
						{
						    handler.out.println(line);
						    handler.out.flush();
						}
				    }
				}
		    }
		} 
		catch(IOException ioe) 
		{
		    ioe.printStackTrace();
		} 
		finally 
		{
		    try 
		    {
				in.close();
				out.close();
				socket.close();
		    } 
		    catch(IOException ioe) 
		    {} 
		    finally
		    {
		    	synchronized(handlers) 
		    	{
		    		handlers.removeElement(this);
		    	}
		    }
		}
    }
    

	//PRE:command called, matches format
	//POST: Splits string into 4 parts,if string less than 4 parts, print error 301
	//		check if number contacts is maxed out, if not generate id based on highest id present
	//		create new row and  add ID, First name, last name. Check format of the phone number, 
	//		if matches expression, then we add phone number and contact is inserted
	// 		else, error 301 printed and row is removed. 
	static void add(String line,ArrayList<ArrayList<String>> contacts,PrintStream os, int max) {
	  String[] parts = line.split(" ");
	  if(contacts.size()<max&& parts.length==4) {
		  contacts.add(new ArrayList<String>());
		  int spot=contacts.size()-1; 
		  int id = 0;
		 if(contacts.size()-1==0)
			 id=1001;
		 else
			 id=Integer.parseInt(contacts.get(spot-1).get(0))+1;
		  
		  contacts.get(spot).add(Integer.toString(id));
		  contacts.get(spot).add(parts[1]); // ADD FNAME 
		  contacts.get(spot).add(parts[2]); // ADD LNAME
			
		  if(parts[3].matches("(\\d-)?(\\d{3}-)?\\d{3}-\\d{4}")) { // check if phone follows format
			  contacts.get(spot).add(parts[3]);  // if follows format, insert into array
			  os.println("ADD=200 OK=The new Record ID is " + Integer.toString(id)); // = is key to seperate string on client side
			}
		  else {
			  os.println("301 Message Format Error ");		
			  contacts.remove(spot);
			}
		}
	  else if(parts.length<4)
		  os.println("301 Message Format Error");
		
	  else
		  os.println("Can not insert data, file is full");
	} 
	
	//PRE: command called, Index is in database, or not found
	//POST: if found delete row, else output error, output error if format for command doesn't
	// match program requirements
	static void delete(ArrayList<ArrayList<String>> contacts, String line,PrintStream os) {
	
		String[] parts = line.split(" "); 
		Boolean found = false;
		if (parts.length<2)
			os.println("301 Message Format Error");
		else {
			
			for(int i=0;i<contacts.size();i++) {
				if(contacts.get(i).get(0).equals(parts[1])) {
					contacts.remove(i);
					found =true;
					break;
				}
			}
		if (found)
			os.println("200 OK");
		else
			os.println("403 Record ID does not exist");
		}
	}
	
	//PRE:command called
	//POST: concatenate data together with equal sign after each row
	// 	output to client data and 200 OK
	static void list(ArrayList<ArrayList<String>> contacts,PrintStream os){	
		String output="";
		for(int i=0;i< contacts.size();i++) {
			for(int j=0;j<contacts.get(i).size();j++) {			
				output+=contacts.get(i).get(j);
				output+="\t";
			}
			output+="="; // inserted to split string on client side
		}
		os.println("LIST=200 OK=The List of records in the book="+output);
	} 
    
    
    
}

