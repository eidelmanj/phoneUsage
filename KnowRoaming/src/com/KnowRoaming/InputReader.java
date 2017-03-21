package com.KnowRoaming;

import com.KnowRoaming.SQLCommunicator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;




public class InputReader {
	
	
	
	public static void main(String[] args) {
		UserRecord r;
		try {
			SQLCommunicator sqlCom = new SQLCommunicator("root", "root", "knowroaming");
			r = new UserRecord("Jim", "jim@jim.com", "123-4567", sqlCom);
			r.commit();
		} catch(Exception e) {
			System.err.println("Error connecting to MySQL DB");
			e.printStackTrace();
		}
	}

}
