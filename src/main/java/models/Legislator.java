package models;

public class Legislator extends Recipient {

	public Legislator(String recipientEmail, String firstName, String lastName) {
		super(recipientEmail);
		setFirstName(firstName);
		setLastName(lastName);
	}

	public String email;
	public String firstName;
	public String lastName;
	public String title;
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}