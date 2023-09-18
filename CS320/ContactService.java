package testing;
import java.util.ArrayList;

public class ContactService {
   public static Contact addContact (String contactID, String firstName, String lastName, String phone, String address) {
	   var contact = new Contact(contactID, firstName, lastName, phone, address);
	   return contact;
   } 
   public static Contact deleteContact (String contactID) {
	   var allContacts = Contact.getAllContacts();
	   for (Contact contact : allContacts) {
   	      if (contact.getContactID().equals(contactID)) {
             allContacts.remove(contact);
		     break;
	    } 
	   }
	   return null;
   }
   public static void updateFirstName (String contactID, String newFirstName) {
	   var allContacts = Contact.getAllContacts();
	   for (Contact contact : allContacts) {
   	      if (contact.getContactID().equals(contactID)) {
             contact.setFirstName(newFirstName);
		     break;
	    } 
	   }
   }
   public static void updateLastName (String contactID, String newLastName) {
	   var allContacts = Contact.getAllContacts();
	   for (Contact contact : allContacts) {
   	      if (contact.getContactID().equals(contactID)) {
             contact.setLastName(newLastName);
		     break;
	    } 
	   }
   }
   public static void updatePhone (String contactID, String newPhone) {
	   var allContacts = Contact.getAllContacts();
	   for (Contact contact : allContacts) {
   	      if (contact.getContactID().equals(contactID)) {
             contact.setPhone(newPhone);
		     break;
	    } 
	   }
   }
   public static void updateAddress (String contactID, String newAddress) {
	   var allContacts = Contact.getAllContacts();
	   for (Contact contact : allContacts) {
   	      if (contact.getContactID().equals(contactID)) {
             contact.setAddress(newAddress);
		     break;
	    } 
	   }
   }
   
}
