#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <math.h>
#include <stdexcept>
using namespace std;

class Course {
private:
    // Class Fields
    string courseNumber; // Store course number
    string courseTitle; // Sotre course Title
    vector<string> prerequisites = {}; // Vector holds list of prerequisites (courseNumbers)
    int key; // Key is used for sorting alphanumeric courseNumber's

    // Algorithm used to calculate a unique key for each alphanumeric courseNumber
    int calcKey() {
        // Local Variables
       int newKey = 0; // New key being created
       int stringLength = courseNumber.size(); // Length of courseNumber
       const int CHARACTER_NUMBER = 4; // Defines length of courseNumber alphabetical section
       // Calculates unique number from first four characters of courseNumber and adds it to newKey
       for (int i = 0; i < CHARACTER_NUMBER; ++i) {
           newKey += (((int)courseNumber.at(i) - 65) * pow(26, (CHARACTER_NUMBER - i)));
        }
       // Calculates unique number from last 3 digits of courseNumber and adds it to newKey
       for (int j = 4; j < stringLength; ++j) {
           newKey += (((int)courseNumber.at(j) - 48) * pow(10, (stringLength - j)));
        }
       return newKey;
    }
public:
    // Default Constructor
    Course() {
        courseNumber = "";
        courseTitle = "";
        key = UINT_MAX;
    }
    // Overloaded constructor. Regular method for creating a Course object.
    Course(string courseNumber, string courseTitle, vector<string> prerequisites) {
        this->courseNumber = courseNumber;
        this->courseTitle = courseTitle;
        this->prerequisites = prerequisites;
        key = calcKey();
    }
    // Getter and Setter methods for accessing fields in Course object
    int getKey() {
        return key;
    }
    string getCourseNumber() {
        return courseNumber;
    }
    void setCourseNumber(string courseNumber) {
        this->courseNumber = courseNumber;
        key = calcKey();
    }
    string getCourseTitle() {
        return courseTitle;
    }
    void setCourseTitle(string courseTitle) {
        this->courseTitle = courseTitle;
    }
    vector<string> getPrerequisites() {
        return prerequisites;
    }
    void appendPrerequisite(string prerequisite) {
        prerequisites.push_back(prerequisite);
    }

};

class BinarySearchTree {
private:
    // Defines data structure that is managed by the BinarySearchTree. Is a container for the course.
    struct Node {
        // Structure Fields
        Course course; // Internal course object
        int key; // Key that was defined in course object
        Node* left; // Pointer to left node from (this node)
        Node* right; // Pointer to right node from (this node)
        // Default Constructor
        Node() {
            course = Course();
            key = UINT_MAX;
            left = nullptr;
            right = nullptr;
        }
        // Overloaded Constructor
        Node(Course course) {
            this->course = course;
            key = course.getKey();
            left = nullptr;
            right = nullptr;
            
        }
    };
    // Class Fields
    Node* root; // Root defines entry point into BinarySearchTree

    // Internal Method Declarations
    void addNode(Node* node, Course course);
    void inOrder(Node* node);
    void deleteNode(Node* currentNode);

public:
    // External Method Declarations
    BinarySearchTree();
    virtual ~BinarySearchTree();
    void InOrder();
    void Insert(Course course);
    Course Search(int key);

};
//Default Constructor
BinarySearchTree::BinarySearchTree() {
    root = nullptr;
}
// Destructor, Calls deleteNode to recursively free all memory for BinarySearchTree
BinarySearchTree::~BinarySearchTree() {
    Node* currentNode = root;
    deleteNode(currentNode);
}

void BinarySearchTree::deleteNode(Node* currentNode) {
    if (currentNode == nullptr) {
        return;
    }
    Node* leftNode = currentNode->left; // Defines left and right nodes
    Node* rightNode = currentNode->right;
    deleteNode(leftNode); // Navigates through binary tree from left to right and deletes leaves
    deleteNode(rightNode);
    if (currentNode != root) {
        delete currentNode;
    }
}

void BinarySearchTree::InOrder() {
    Node* node = root;
    cout << "Here is the schedule: " << endl << endl;
    inOrder(node);
}

void BinarySearchTree::inOrder(Node* node) {
    if (node != nullptr) { // Recursion ends when a nullptr is reached
        // Local Variables
        Node* leftNode = node->left;
        Node* rightNode = node->right;
        // Recursive Calls
        inOrder(leftNode);
        cout << node->course.getCourseNumber() << ", " << node->course.getCourseTitle() << endl;
        inOrder(rightNode);
    }
}

void BinarySearchTree::Insert(Course course) {
    Node* newNode = new Node(course);
    if (root == nullptr) { // Adds course to root
        root = newNode;
    }
    else { // Traverses tree to add course
        addNode(newNode, course);
    }
}
Course BinarySearchTree::Search(int key) {
    // Local Variables
    Node* currentNode = root; // Node pointer that traverses tree
    Course emptycourse = Course(); // Empty course that is used if no match is found

    while (currentNode != nullptr) { // Iteration ends if no match is found
        if (key == currentNode->key) { // Match is found
            return currentNode->course;
        }
        else if (key < currentNode->key) { // Search continues to the left node
            currentNode = currentNode->left;
        }
        else { // Search continues to the right node
            currentNode = currentNode->right;
        }
    }
    return emptycourse; // Search was unsuccessful
}

void BinarySearchTree::addNode(Node* node, Course course) {
    // Local Variables
    node->course = course;
    Node* currentNode = root;
    Node* parentNode = nullptr;
    int direction = -1;
    int key = course.getKey();

    while (currentNode != nullptr) { // Iteration ends when a location is found to place node
        if (key < currentNode->key) { // Search currentNode's left pointer 
            parentNode = currentNode;
            currentNode = currentNode->left;
            direction = 0;
        }
        else { // Search currentNode's right pointer
            parentNode = currentNode;
            currentNode = currentNode->right;
            direction = 1;
        }
    }
    if (direction == 0) { // Adds node to (left pointer) of previous node
        parentNode->left = node;
    }
    else if (direction == 1) { // Adds node to (right pointer) of previous node
        parentNode->right = node;
    }
}
// Algorithm used to process a string (lowerCaseString) and makes all lowercase characters uppercase
string to_upper(string lowerCaseString) {
    // Iterates through every character in the string, checks if the character is a lower case letter, and changes it to uppercase
    for (int i = 0; i < lowerCaseString.size(); i++) {
        if (lowerCaseString.at(i) >= 97 && lowerCaseString.at(i) <= 122) {
            lowerCaseString.at(i) = lowerCaseString.at(i) - 32;
        }
    }
    return lowerCaseString;
}

// Algorithm used to calculate a unique key for alphanumeric newString
// Function used in search function to match the correct key in the BinarySearchTree
int calcKey(string newString) {
    // Local Variables
    int newKey = 0; // New key being created
    int stringLength = newString.size(); // Length of newString
    const int CHARACTER_NUMBER = 4; // Defines length of newString alphabetical section
    // Calculates unique number from first four characters of newString and adds it to newKey
    for (int i = 0; i < CHARACTER_NUMBER; ++i) {
        newKey += (((int)newString.at(i) - 65) * pow(26, (CHARACTER_NUMBER - i)));
    }
    // Calculates unique number from last 3 digits of newString and adds it to newKey
    for (int j = 4; j < stringLength; ++j) {
        newKey += (((int)newString.at(j) - 48) * pow(10, (stringLength - j)));
    }
    return newKey;
}

// Searches the given BinarySearchTree (courseBST) for the user's input (searchQuery) and outputs course information for a successful search
void search(BinarySearchTree* courseBST) {
    // Local Variables
    string searchQuery;
    Course tempCourse;
    
    // Receive input into searchQuery for later use.
    // searchQuery is processed to ensure all letters are uppercase
    cout << "Please input the course number to find!" << endl;
    cin >> searchQuery;
    searchQuery = to_upper(searchQuery);
    // Call to courseBST's search method which either outputs the correct course, or a null course
    tempCourse = courseBST->Search(calcKey(searchQuery));
    // Informs user the search was unsuccessful if a null course was returned
    if (tempCourse.getKey() == UINT_MAX) {
        cout << endl << "Course was not found!" << endl;
        return;
    }
    // Output's courseNumber, courseTitle, and Prerequisites that were found in the search
    cout << endl << tempCourse.getCourseNumber() << ", " << tempCourse.getCourseTitle() << endl;
    cout << "Prerequisites: ";
    if (tempCourse.getPrerequisites().size() == 0) { // Returns none if no prerequisites are present in tempCourse
        cout << "None" << endl;
        return;
    }
    for (int i = 0; i < tempCourse.getPrerequisites().size(); i++) { // For loop iterates through entire prerequisites vector
        if (i == tempCourse.getPrerequisites().size() - 1) {
            cout << tempCourse.getPrerequisites().at(i) << endl;
        }
        else {
            cout << tempCourse.getPrerequisites().at(i) << ", ";
        }
    }
}

// Parses input line (inputString) recursively and enters substrings as fields into the newCourse
void parseInput(string inputString, char delimiter, int recursionIndex, Course* newCourse) {
    // Local Variables
    int currentIndex = 0; // Current Location in the inputString
    string outputString = ""; // Substring to be added to field inside newCourse
    char currentChar = inputString.at(currentIndex); // Current character being parsed
    // While loop iterates until the delimiter is reached, where the outputString is submitted as a field in newCourse
    while (currentChar != delimiter) {
        // Add currentChar to outputString
        outputString.push_back(currentChar);
        // If statement is entered when the end fo the inputString is reached
        if (currentIndex >= inputString.length() - 1) {
            // Decision branching decides whether the outputString is added as a courseNumber, courseTitle, or prerequisite in newCourse
            // This is based on the current recursion depth
            if (recursionIndex == 0) {
                newCourse->setCourseNumber(outputString);
            }
            else if (recursionIndex == 1) {
                newCourse->setCourseTitle(outputString);
            }
            else {
                newCourse->appendPrerequisite(outputString);
            }
            return;
        }
        // Iterates the currentIndex and assigns currentChar to the next character in inputString
        currentChar = inputString.at(++currentIndex);
    }
    // This section is only entered when a delimiter is reached
    // Decision branching decides whether the outputString is added as a courseNumber, courseTitle, or prerequisite in newCourse
    if (recursionIndex == 0) {
        newCourse->setCourseNumber(outputString);
    }
    else if (recursionIndex == 1) {
        newCourse->setCourseTitle(outputString);
    }
    else {
        newCourse->appendPrerequisite(outputString);
    }
    // Recursively calls parseInput with a new recursionIndex until the entire inputString is parsed
    // The remaining substring is sent into the recursive call as a substring
    parseInput(inputString.substr(++currentIndex, inputString.length() - currentIndex), delimiter, ++recursionIndex, newCourse);
}

// Loads information from file specified by path into the specified BinarySearchTree data structure
void load(BinarySearchTree* courseBST, string path) {
    // Local Variables
    ifstream inFS; // Input stream used to access file
    string currentLine; // Line currently being parsed
    Course* tempCourse; // Stores course to be input into data structure: courseBST
    try {
        // Open the file and check if it was successful
        inFS.open(path);
        if (!inFS.is_open()) {
            cout << "Could not open file" << endl;
            return;
        }
        // Open first line of file and store into currentLine
        getline(inFS, currentLine);
        while (!inFS.fail()) { // Loop continues until all lines are parsed or an error occurs
            // Create new course object and parse currentLine to fill tempCourse's fields
            tempCourse = new Course();
            parseInput(currentLine, ',', 0, tempCourse);
            // Error checking if tempCourse's fields were not filled correctly
            if (tempCourse->getCourseNumber() == "" || tempCourse->getCourseTitle() == "") {
                throw runtime_error("Invalid Format for course title/ course number");
            }
            // Error checking if prerequisites do not already exist in the courseBST data struct
            for (string prerequisite : tempCourse->getPrerequisites()) {
                Course newCourse = courseBST->Search(calcKey(prerequisite));
                if (newCourse.getKey() == UINT_MAX) {
                    throw runtime_error("Invalid Course, prerequisite does not exist");
                }
            }
            // Add tempCourse into courseBST data structure and move on to next line of file
            courseBST->Insert(*tempCourse);
            getline(inFS, currentLine);
        }
        cout << "Courses successfully loaded from " << path << endl;
        inFS.close();
    }
    catch (runtime_error& excpt) { // Error catching for invalid format, course, and file input
        inFS.close();
        cout << excpt.what() << endl;
    }
    return;
}

// Entry point for application
int main() {
    // Local Variables
    BinarySearchTree* courseBST = new BinarySearchTree(); // Data structure to hold courses
    string path = "sample.txt"; // Path to input file
    int choice = 0; // Navigates menu switch statement
    string inputBuffer; // Ensures error response for incorrect input

    cout << "Welcome to the course planner." << endl;
    while (choice != 4) {
        // Output's menu options
        cout << endl << "Menu:" << endl;
        cout << "  1. Load Data Structure." << endl;
        cout << "  2. Print Course List." << endl;
        cout << "  3. Print Course." << endl;
        cout << "  4. Exit" << endl;
        
        cin >> inputBuffer; // Initial input
        // Error checking for incorrect input. Ensures input is a single character digit.
        if (inputBuffer.size() > 1 || inputBuffer.size() < 1 || inputBuffer.at(0) < 48 || inputBuffer.at(0) > 57) {
            cout << inputBuffer << " is not a valid option" << endl;
            continue;
        }
        choice = stoi(inputBuffer);
        // Switch statement navigates to different options based on input
        switch (choice) {
        case 1: // Loads input from path and stores into courseBST
            delete courseBST;
            courseBST = new BinarySearchTree();
            load(courseBST, path);
            break;
        case 2: // Outputs current contents of courseBST in alphanumeric order
            courseBST->InOrder();
            break;
        case 3: // Finds specific course based on input for courseNumber
            search(courseBST);
            break;
        case 4: // Exits the loop
            break;
        default: // Provides input validation for digits other than 1, 2, 3, 4
            cout << choice << " is not a valid option" << endl;
            break;
        }
    }
    cout << "Thank you for using the course planner!" << endl;
}

