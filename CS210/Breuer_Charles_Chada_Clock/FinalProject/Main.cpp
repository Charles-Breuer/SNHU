// Charles Breuer
#include <iostream>
#include <iomanip>
#include <stdexcept>
#include <thread>
#include "Header.h"
using namespace std;



// The main function!
int main() {
	std::thread worker1(updateSecondsAuto); // This starts the thread that updates ('ticks') the clock
	userInputFunction(); // This allows userinput to be taken
	worker1.join(); // This allows the thread to terminate before ending the program
	return 0;
}
