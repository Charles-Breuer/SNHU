package testing;

import java.util.ArrayList;

public class Contact {
  
   private static ArrayList<Contact> allContacts = new ArrayList<Contact>();
   
   private String contactID = "";
   private String firstName = "";
   private String lastName = "";
   private String phone = "";
   private String address = "";
   
public Contact(String contactID, String firstName, String lastName, String phone, String address) {
	if (contactID == null || contactID.length() > 10) {
		throw new IllegalArgumentException("Invalid contactID");
	}
	for (Contact contact : allContacts) {
		if (contactID.equals(contact.getContactID())) {
			throw new IllegalArgumentException("contactID already taken");
		}
	}
	if (firstName == null || firstName.length() > 10) {
		throw new IllegalArgumentException("Invalid first name");
	}
	if (lastName == null || lastName.length() > 10) {
		throw new IllegalArgumentException("Invalid last name");
	}
	if (phone == null || phone.length() != 10) {
		throw new IllegalArgumentException("Invalid phone");
	}
	if (address == null || address.length() > 30) {
		throw new IllegalArgumentException("Invalid address");
	}
	this.contactID = contactID;
	this.firstName = firstName;
	this.lastName = lastName;
	this.phone = phone;
	this.address = address;
	allContacts.add(this);
}

public static ArrayList<Contact> getAllContacts () {
	return allContacts;
}
public String getContactID() {
	return contactID;
}

public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	if (firstName == null || firstName.length() > 10) {
		throw new IllegalArgumentException("Invalid first name");
	}
	this.firstName = firstName;
}

public String getLastName() {
	return lastName;
}

public void setLastName(String lastName) {
	if (lastName == null || lastName.length() > 10) {
		throw new IllegalArgumentException("Invalid last name");
	}
	this.lastName = lastName;
}

public String getPhone() {
	return phone;
}

public void setPhone(String phone) {
	if (phone == null || phone.length() != 10) {
		throw new IllegalArgumentException("Invalid phone");
	}
	this.phone = phone;
}

public String getAddress() {
	return address;
}

public void setAddress(String address) {
	if (address == null || address.length() > 30) {
		throw new IllegalArgumentException("Invalid address");
	}
	this.address = address;
}
   
   
}
