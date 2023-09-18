// Charles Breuer
#include <iostream>
#include <iomanip>
#include <stdexcept>
#include <thread>
#include "Header.h"
using namespace std;

// Global Variables I couldn't figure out a solution for multithreading without using these!
int userInput = 0;
int secondsClock = 0;
int minutesClock = 0;
int hours24Clock = 0;
int hours12Clock = 12;
char amOrPm = 'A';

// Function that is used to update the clock every second
void updateSecondsAuto() {
	// While loop keeps the clock updating until user exits the program (userInput == 4)
	while (userInput != 4) {
		secondsClock++; // Updates the seconds
		clearScreen(); // Clears the screen
		clockUpdate(); // Ensures clock is formatted correctly
		displayTime(); // Displays the clock
		displayMenu(); // Displays the menu
		std::this_thread::sleep_for(1000ms); // Allows thread to wait 1 second before continuing loop
	}
}
// This function allows users to input options from the displayMenu
void userInputFunction() {
	while (userInput != 4) {
		try { // Try/Catch provides input validation for entries other than 1,2,3, or 4.
			cin >> userInput;
			if ((userInput != 1) && (userInput != 2) && (userInput != 3) && (userInput != 4)) {
				throw runtime_error("Invalid Input");
			}
			switch (userInput) {
			case 1: // Increments hours
				hours24Clock++;
				clockUpdate();
				break;
			case 2: // Increments minutes
				minutesClock++;
				clockUpdate();
				break;
			case 3: // Increments seconds
				secondsClock++;
				clockUpdate();
				break;
			case 4: // Allows while loop to exit
				break;
			default: // Unexpected Behavior
				cout << "Something went wrong!" << endl;
				break;
			}
		}
		catch (runtime_error& excpt) {
			cin.clear(); //clear bad input flag
			cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); //discard input
		}
	}
}
// This function clears the screen for Windows, Mac, and Linex
void clearScreen() {
	// Decision branching checks what type of Operating System.
#ifdef _WIN32
	system("cls"); // This is the clear screen for windows
#else
	system("clear"); // This is the clear screen for mac/linex
#endif
}
// Displays the user input menu
void displayMenu() {
	cout << setfill('*') << setw(26) << "" << endl;
	cout << "* " << "1 - Add One Hour" << "       *" << endl;
	cout << "* " << "2 - Add One Minute" << "     *" << endl;
	cout << "* " << "3 - Add One Second" << "     *" << endl;
	cout << "* " << "4 - Exit Program" << "       *" << endl;
	cout << setfill('*') << setw(26) << "" << endl;
}
// Displays the current time using current time variables
void displayTime() {
	cout << setfill('*') << setw(26) << "" << setfill(' ') << setw(4) << "" << setfill('*') << setw(26) << "" << endl;
	cout << "*" << "      " << "12-Hour Clock" << "     " << "*" << "    " << "*" << "      " << "24-Hour Clock" << "     " << "*" << endl;
	cout << "*" << "      ";
	cout << setfill('0') << right << setw(2) << hours12Clock << ":" << setw(2) << minutesClock << ":" << setw(2) << secondsClock << " " << amOrPm << "M";
	cout << "       " << "*" << "    " << "*" << "        ";
	cout << setfill('0') << right << setw(2) << hours24Clock << ":" << setw(2) << minutesClock << ":" << setw(2) << secondsClock;;
	cout << "        " << "*" << endl;
	cout << setfill('*') << setw(26) << "" << setfill(' ') << setw(4) << "" << setfill('*') << setw(26) << "" << endl;
}
// Updates the values for the 12 and 24 hour clocks
void clockUpdate() {
	const int maximumSeconds = 60;
	const int maximumMinutes = 60;
	const int maximum24Hours = 24;
	const int resetVariable = 0;

	if (secondsClock >= maximumSeconds) { // Updates seconds and minutes if seconds reach 60
		secondsClock = resetVariable;
		minutesClock++;
	}
	if (minutesClock >= maximumMinutes) { // Updates minutes and hours if minutes reach 60
		minutesClock = resetVariable;
		hours24Clock++;
	}
	if (hours24Clock >= maximum24Hours) { // Updates minutes, hours, and seconds if hours reach 24
		hours24Clock = resetVariable;
		minutesClock = resetVariable;
		secondsClock = resetVariable;
	}

	if (hours24Clock >= 13) { // Updates 12 hours clock for the afternoon
		hours12Clock = hours24Clock - 12;
		amOrPm = 'P';
	}
	else if (hours24Clock == 0) { // Updates 12 hour clock for correct 12AM
		hours12Clock = hours24Clock + 12;
		amOrPm = 'A';
	}
	else { // Updates 12 hour clock for the morning, including noon.
		hours12Clock = hours24Clock;
		if (hours24Clock == 12) {
			amOrPm = 'P';
		}
		else {
			amOrPm = 'A';
		}
	}
}
