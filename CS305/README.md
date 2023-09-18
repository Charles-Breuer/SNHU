# CS305

## Briefly summarize your client, Artemis Financial, and their software requirements. Who was the client? What issue did they want you to address?

  Artemis Financial is a company that creates and manages financial plans for domestic and international clientel. Artemis Financial deals with sensitive information for their customers and require secure software to manage this information. In this project, Artemis Financial requested a checksum verification function to ensure documents sent to their customers are verified for integrity using a hash function. This project also needs to address HTTPS communication and industry best standards for refactored code.
  
## What did you do very well when you found your client’s software security vulnerabilities? Why is it important to code securely? What value does software security add to a company’s overall wellbeing?

  I was able to document the vulnerabilities and compare them to current industry best practices. An example of this is establishing a trust boundary between the software and HTML output to reduce the risk of HTML injection. Coding securely is important since any code created without security concerns addressed incur an indeterminate amount of risk. Allowing insecure code to remain unaddressed can require costly revisions at later points in a software development life cycle and have disasterous effects on a company's reputation/finances if a breach in software occurs. Addressing security concerns early allows for a smoother SDLC.
  
## What part of the vulnerability assessment was challenging or helpful to you?

  The most challenging part of the vulnerability assessment was ensuring that the ciphers being used were the best options. Choosing an insecure cipher can have disasterous effects for a company and the definition of secure changes rapidly as technology advances.
  
## How did you increase layers of security? In the future, what would you use to assess vulnerabilities and decide which mitigation techniques to use?

  The vulnerability assessment form is a good starting point for identifying the relevant security issues for a given project. In this case, cryptography, input validation, client/server distribution, and code quality were relevant. It is important to research best practices for relevant security issues and write code with these concepts in mind. Employing manual reviews and static testing is important to ensure security in newly written software.
  
## How did you make certain the code and software application were functional and secure? After refactoring the code, how did you check to see whether you introduced new vulnerabilities?

 An easy way to check that the software is functional is to employ a static dependency checker. This shows whether new third-party applications introduced any vulnerabilities. It is important to look at the types of software used to create the functional code and check if that software has any relevant issues. Using the vulnerability assessment form is a good starting point for checking whether new areas of security concern are relevant for refactored code. A final helpful tip is to be aware of current industry best practices to avoid adding new issues.
  
## What resources, tools, or coding practices did you use that might be helpful in future assignments or tasks?

  The keytool is a useful software that is available in the command line and as a part of the eclipse installation. The OWASP dependency checker is also a useful piece of software that can quickly display all of the known vulnerabilities when working with third-party software.
  
## Employers sometimes ask for examples of work that you have successfully completed to show your skills, knowledge, and experience. What might you show future employers from this assignment?

  I would display ability to create self-signed certificates for manaagement of development software. Knowledge of keystores and truststores is typically reserved for system administrators, and this is a unique skill to have. I would also display the refactored code for creating a checksum verification scheme. This could have a number of applications that can work with other ciphers such as bulk encryption algorithms for safe data transfer.
  
