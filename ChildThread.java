/* 
 * ChildThread.java
 */


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class ChildThread extends Thread 
{
    static  Vector<ChildThread> handlers = new Vector<ChildThread>(20);
    public static ArrayList<ArrayList<String>> contacts = new ArrayList<ArrayList<String>>(); 
	public static ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public String userL="";
    boolean isLogged= false;

    public ChildThread(Socket socket) throws IOException 
    {
    	this.socket = socket;
    	in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
    	out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
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
		    	String[] parts = line.split(" ");
		    	
		    	 if (parts[0].equals("ADD")) 
					add(parts,contacts,isLogged);
		    	 else if (parts[0].equals("DELETE")) 
					delete(contacts, parts,isLogged);
		    	 else if (parts[0].equals("LIST")) 
					list (contacts);
		    	 else if(parts[0].equals("LOGIN"))
					isLogged =login(line,isLogged, users);	
		    	 else if(parts[0].equals("LOOK"))
					look(parts,contacts);
		    	 else if(parts[0].equals("WHO"))
						who();
		    	 else if (parts[0].equals("LOGOUT")||parts[0].equals("QUIT")) {
					if(isLogged)
						isLogged=false;
					userL="";
					this.out.println(line+"=200 OK");
					this.out.flush();	
				 }	
		    	 else if(parts[0].equals("SHUTDOWN")){
		    		 System.out.println(userL);
		    		 if(userL.equals("root")&&isLogged==true) {
						ChildThread handler;
						
						for(int i = 0; i < handlers.size();i++) 
						{	
							synchronized(handlers) 
						    {
								handler = (ChildThread)handlers.elementAt(i);
								if (handler != this) 
								{
								    handler.out.println(line+ "=210 Server is about to shutdown");
								    handler.out.flush();
								   
								}
						    }
						}
						this.out.println(line+"=200 OK");
						this.out.flush();
						writeFile(contacts);
						System.exit(0);
					 }
					 else {
						this.out.println("=402 User not allowed to execute command");
						this.out.flush();
					 }
		    	 }
		    	 else {
						this.out.println("=300 Invalid Command");
						this.out.flush();
		    	 }
			
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
		catch(Exception ex) 
		{
		    ex.printStackTrace();
		    
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
    
    // READ DONE
	private static Scanner x;
	static void readFile(ArrayList<ArrayList<String>> contacts,String file)
	{
		try{
			x = new Scanner (new File(file));
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
			contacts.add(c1);
		}
		x.close();
	}
    
	//WRITE DONE
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
    
    //ADD DONE
    void add(String [] parts,ArrayList<ArrayList<String>> contacts,Boolean logged) {
		 if(!logged)
		 {
			 this.out.println("ADD=401 You are not currently logged in, login first");
			 this.out.flush();
		 }
		 else {
			  if(parts.length==4) {
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
					  this.out.println("ADD=200 OK=The new Record ID is " + Integer.toString(id)); // = is key to seperate string on client side
					  this.out.flush();
				  }
				  else {
					  this.out.println("ADD=301 Message Format Error ");		
					  this.out.flush();
					  contacts.remove(spot);
					}
				}
			  else if(parts.length<4) {
				  this.out.println("ADD=301 Message Format Error");
				  this.out.flush();
			  }
				
			  else {
				  this.out.println("ADD=Can not insert data, file is full");
				  this.out.flush();
				  
			  }
		  }
		} 
    
    //DELETE DONE
 	void delete(ArrayList<ArrayList<String>> contacts, String [] parts,Boolean logged) {
 		if(!logged)
 		 {
 			 this.out.println("DELETE=401 You are not currently logged in, login first");
 			 this.out.flush();
 		 }
 		 else {
 				Boolean found = false;
 				if (parts.length<2||parts.length>2) {
 					this.out.println("DELETE=301 Message Format Error");
 					this.out.flush(); 
 				}
 				else {
 					
 					for(int i=0;i<contacts.size();i++) {
 						if(contacts.get(i).get(0).equals(parts[1])) {
 							contacts.remove(i);
 							found =true;
 							break;
 						}
 					}
 					if (found) {
 						this.out.println("DELETE=200 OK");
 						this.out.flush();
 					}
 					else {
 						this.out.println("DELETE=403 Record ID does not exist");
 						this.out.flush();
 					}
 				}
 		 }
 		
 	}

 	// LIST DONE
	 void list(ArrayList<ArrayList<String>> contacts){	
		String output="";
		for(int i=0;i< contacts.size();i++) {
			for(int j=0;j<contacts.get(i).size();j++) {			
				output+=contacts.get(i).get(j);
				output+="\t";
			}
			output+="="; // inserted to split string on client side
		}
			this.out.println("LIST=200 OK=The List of records in the book="+output);
			this.out.flush();
	}

	//LOGIN DONE
	 Boolean login(String  line,Boolean isLogged,ArrayList<ArrayList<String>> users){	
		 String[] parts = line.split(" ");
		 int i;
		for(i=0;i<users.size();i++){
			if(users.get(i).get(0).equals(parts[1])&&users.get(i).get(1).equals(parts[2])){
				isLogged=true;
				userL+=parts[1];
				this.out.println("LOGIN=200 OK");
				this.out.flush();
				return true;
			}	
		}
		if(i==users.size())
		{
			this.out.println("LOGIN=410 Wong UserID or Password");
			this.out.flush();
			return false;
		}
		return false;
	}

	 //LOOK DONE
	 void look(String  [] parts,ArrayList<ArrayList<String>> users){
	    	int index = Integer.parseInt(parts[1]); // 
	    	//ArrayList<String> looking= new ArrayList<String>();
	    	int i,usersFound=0;
	    	String looking="=";
	    	for(i=0;i<users.size();i++)
	    	{
	    		if(users.get(i).get(index).equals(parts[2])){
	    			String foundUser="";
	    			for(int j=0;j<users.get(i).size();j++){
	    				foundUser+=users.get(i).get(j);
	    				foundUser+=" ";
	    			}
	    			looking+=foundUser;
	    			looking+="=";
	    			usersFound++;
	    		}
	    	}
	    	if(usersFound==0){
	    		this.out.println("LOOK=404 Your search did not match any records");
	    		this.out.flush();
	    	}
	
	    	else{
	    			this.out.println("LOOK=Found "+usersFound+" Match"+looking);
	    			this.out.flush();
	    	}
    }
	 // WHO DONE
	 void who() {
		 ArrayList<String> whos= new ArrayList<String>();
		 for(int i=0;i<handlers.size();i++)
		 {
			 String temp="";
			 if(handlers.get(i).isLogged) {
				 temp += handlers.get(i).userL+ " ";
				 temp+=handlers.get(i).socket.getInetAddress();
				 temp= temp.replace('/', ' ');
				
				 whos.add(temp);
			 }
			 
		 }
		 String send="WHO=The list of active users:=";
		 for(int i=0;i<whos.size();i++)
			 send+=whos.get(i)+"=";
		 this.out.println(send);
		 this.out.flush();
	 }
}

//Vector<ChildThread> handlers