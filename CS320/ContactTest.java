package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContactTest {

	@Test
	void testContact() {
		Contact newContact = new Contact("123abc", "Carl", "Johnson", "5552223333", "123 Alberts Street."); //Typical Case
		assertTrue(newContact.getContactID().equals("123abc"));
		assertTrue(newContact.getFirstName().equals("Carl"));
		assertTrue(newContact.getLastName().equals("Johnson"));
		assertTrue(newContact.getPhone().equals("5552223333"));
		assertTrue(newContact.getAddress().equals("123 Alberts Street."));
		
		Contact newContact2 = new Contact("123456789", "Carl6789", "Johnson89", "5552223333", "123 Alberts Street.2123456789"); // Border case
		assertTrue(newContact2.getContactID().equals("123456789"));
		assertTrue(newContact2.getFirstName().equals("Carl6789"));
		assertTrue(newContact2.getLastName().equals("Johnson89"));
		assertTrue(newContact2.getPhone().equals("5552223333"));
		assertTrue(newContact2.getAddress().equals("123 Alberts Street.2123456789"));
		
	}
	@Test
	void testContactIDTooLong () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("1234567891011", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border Case
			new Contact("12345678911", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		});
	}
	@Test
	void testFirstNameTooLong () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("223abc", "CarlmarkoniusPhlangus", "Johnson", "5552223333", "123 Alberts Street."); 
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border Case
			new Contact("323abc", "Carlmarkuss", "Johnson", "5552223333", "123 Alberts Street."); 
		});
	}
	@Test
	void testLastNameTooLong () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("423abc", "Carl", "Johnsonianmarkonus", "5552223333", "123 Alberts Street.");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border case
			new Contact("523abc", "Carl", "Johnsoniann", "5552223333", "123 Alberts Street.");  
		});
	}
    @Test
	void testPhoneIncorrectLength () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("623abc", "Carl", "Johnson", "555222333344", "123 Alberts Street."); 
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border case
			new Contact("723abc", "Carl", "Johnson", "555222333", "123 Alberts Street.");  
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border case
			new Contact("823abc", "Carl", "Johnson", "55522233334", "123 Alberts Street.");  
		});
	}
	@Test
	void testAddressTooLong () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("923abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.21234567891011121314");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Border Case
			new Contact("1023abc", "Carl", "Johnson", "5552223333", "1234567890123456789012345678901");
		});
	}
	@Test
	void testAddressNotNull () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("1123abc", "Carl", "Johnson", "5552223333", null);
		});
	}
	@Test
	void testPhoneNotNull () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("1223abc", "Carl", "Johnson", null, "123 Alberts Street.");
		});
	}
	@Test
	void testLastNameNotNull () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("1323abc", "Carl", null, "5552223333", "123 Alberts Street.");
		});
	}
	@Test
	void testFirstNameNotNull () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("1423abc", null, "Johnson", "5552223333", "123 Alberts Street.");
		});
	}
	@Test
	void testContactIDNotNull () {
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact(null, "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		});
	}
	@Test
	void testUniqueID () {
		Contact contact1 = new Contact("abc", "Carll", "Markus", "2223334444", "1234 Alberts Street." );
		Assertions.assertThrows(IllegalArgumentException.class, () -> { // Typical Case
			new Contact("abc", "Carl", "Johnson", "5552223333", "123 Alberts Street.");
		});
	}

}
