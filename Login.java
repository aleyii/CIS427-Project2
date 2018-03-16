import java.util.*;

public class Login extends SThread { //SThread Class, which handle the server's messages
									 //*NOTE: Need to create SThread class for this to work 
	Scanner sc = new Scanner(System.in);
	if(!sc.hasNext("[LOGIN]")) { //Check to see if string user enters matches correct format: "Login followed by space, followed by user ID followed by space
    		System.out.println("SUCCESS: Checking Username and Password");
    	sc.next();
	}

	else {
		System.out.println("ERROR: Invalid Expression Try Again!"); //Error handling if user enters wrong format 
	}
	
	public void RegexMatcher() {  //Create Regex method and use Regex to validate username and password 
	
	String username= "[PASS IN THE USERNAME TO BE MATCHED]";
	String password = "[PASS IN THE PASSWORD TO BE MATCHED]";
	String pattern = "[ENTER USERNAME/PASSWORD MATCHING COMBO]";
	
	boolean isMatch = pattern.matches(pattern, username, password);
      	System.out.println("200 ok");
	} 

} 

}
