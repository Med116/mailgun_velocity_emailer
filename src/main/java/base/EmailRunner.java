package base;

import clients.marty.EmailMarty;

public class EmailRunner {

	public static void main(String[] args){
		
		Email email = new EmailMarty("Beer Distribution News", "email_marty");
		email.setWait(20);
		email.sendEmail();
		
	
	}
}
