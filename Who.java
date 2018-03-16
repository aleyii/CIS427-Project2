
public class Who extends ChildThread { //Inherit functions from 'ChildThread' class
	String name;
	int IPaddress;
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		if(!sc.hasNext("[WHO]")) {
			System.out.println("200 ok");
			System.out.println("The list of the active users:");
		}
		
		else {
			System.out.println("ERROR: Invalid Expression Try Again!"); //Error handling if user enters wrong format 
		}
	}
	
	public void displayName() {  //Need to add another constructor in 'CHILD THREADS' class to pull name from
		//Once constructor is added call constructor to print names
		
	}
		
		public void displayIP() {  
			System.out.println(Vector<ChildThread> handlers);  //Print all clients added to handler from Vector class
	}
		
		
	}

	 

		
