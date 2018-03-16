
public class Logout extends SThread {  //SThread Class, which handle the server's messages
	
	 Scanner sc = new Scanner(System.in);
	if(!sc.hasNext("[LOGOUT]")) { //Check to see if string user enters matches correct format: "LOGOUT"
		thread.stopRunning(); //Call thread.stopRunning method to terminate thread 
   		System.out.println("200 ok");
	} 
	
}

}



