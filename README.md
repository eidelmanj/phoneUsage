
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

Before running the project, you must ensure that there is an appropriate database set up on your MySQL server. To do this, create a new database in mysql with an appropriate name. I have chosen the name "knowroaming_eidelman". If you use a different name, please modify the first twl lines of "generate_db.sql" to reflect your name choice.

To import the database, run the following command:
```
mysql -u USERNAME -p knowroaming < generate_db.sql
```

Where "USERNAME" is your mysql username.

Finally, update the settings.txt file to reflect your MySQL settings.  The first line should be your MySQL username. The second line should be your MySQL password. The third line should be the database name (knowroaming_eidelman by default).


This project is packaged as an Eclipse project. In Eclipse, go to
```
file menu -> import -> Existing Projects into Workspace
```

And select the root directory of this git repo as the root directory of the project. You may have to set the build path to include your own JRE (1.8 or higher). Project compliance should be 1.7 or higher. 

I have also included a prebuilt runnable JAR file in the *jar/* directory. This can be run with the following command:
```
cd jar
java -jar KnowRoaming.jar
```








Design Decisions
------------------
I have implemented a simple framework to encapsulate interaction with the MySQL database. Each type of record in the Database (user_record, usage_record) has a class associated with it that implements the SQLRecord class. These records are able to commit themselves to the database, update records in the DB as well as update themselves to match a corresponding record in the DB.

