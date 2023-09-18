package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContactServiceTest {

	@Test
	void testAddContact() {
		Contact testContact = ContactService.addContact("223abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		assertTrue(testContact.getContactID().equals("223abc"));
		assertTrue(testContact.getFirstName().equals("Carl"));
		assertTrue(testContact.getLastName().equals("Johnson"));
		assertTrue(testContact.getPhone().equals("5552223333"));
		assertTrue(testContact.getAddress().equals("123 Alberts Street."));
	}
	@Test
	void testAddUniqueContact() {
		Contact testContact = ContactService.addContact("123abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			ContactService.addContact("123abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		});
	}
	@Test
	void testDeleteContact() {
		int currentNumberOfContacts = Contact.getAllContacts().size();
		Contact testContact = new Contact("323abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		assertTrue(currentNumberOfContacts + 1 == Contact.getAllContacts().size()); // Tests if number of contacts were raised by 1
		currentNumberOfContacts = Contact.getAllContacts().size();
		testContact = ContactService.deleteContact("323abc"); // Deletes contact
		assertTrue(currentNumberOfContacts - 1 == Contact.getAllContacts().size()); // Tests if number of contacts was lowered by 1 (removed from list of all contacts)
		assertTrue (testContact == null); // Tests if reference was assigned to null
		
	}
   @Test 
   void testUpdateFirstName() {
	   Contact testContact = new Contact("423abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
	   ContactService.updateFirstName("423abc", "Markus");
	   assertTrue(testContact.getFirstName().equals("Markus"));
   }
   @Test 
   void testUpdateLastName() {
	   Contact testContact = new Contact("523abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
	   ContactService.updateLastName("523abc", "Breuer");
	   assertTrue(testContact.getLastName().equals("Breuer"));
   }
   @Test 
   void testUpdatePhone() {
	   Contact testContact = new Contact("623abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
	   ContactService.updatePhone("623abc", "7778889999");
	   assertTrue(testContact.getPhone().equals("7778889999"));
   }
   @Test 
   void testUpdateAddress() {
	   Contact testContact = new Contact("723abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
	   ContactService.updateAddress("723abc", "5739 Samantha Ave");
	   assertTrue(testContact.getAddress().equals("5739 Samantha Ave"));
   }
}
