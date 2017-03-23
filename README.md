
User Phone Record Database
==========================

Stores and retrieves information about users and their phone network use




Usage Instructions
------------------
*Requirements* 
- JRE System Library 1.8
- JUnit 4
- MySQL Server version 5.7.17
- Eclipse

This project is packaged as an Eclipse project. In Eclipse, go to
*file menu -> import -> Existing Projects into Workspace*

And select the root directory of this git repo as the root directory of the project. You may have to set the build path to include your own JRE (1.8 or higher). Project compliance should be 1.7 or higher. 

Before running the project, you must ensure that there is an appropriate database set up on your MySQL server. To do this, create a new database in mysql with an appropriate name. I have chosen the name "knowroaming". If you use a different name, please modify the first line of "generate_db.sql" file to be "USE db_name;" where "db_name" is the database name you have chosen.

To import the database, run the following command:
*mysql -u USERNAME -p knowroaming < generate_db.sql* 

Where "USERNAME" is your mysql username.

Finally, update the settings.txt file to reflect your MySQL settings. The first line should be the database name. The second line should be your MySQL username. The third line should be your MySQL password.



Design Decisions
------------------