package com.KnowRoaming;

import com.KnowRoaming.SQLCommunicator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;




public class InputReader {
	
	
	
	
	
	public static void main(String[] args) {
		UserRecord r;
		UsageRecord u;
		SQLCommunicator sqlCom;
		
		
		try {
			sqlCom = new SQLCommunicator("root", "root", "knowroaming");
			
		} catch(Exception e) {
			System.err.println("Error connecting to MySQL DB");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		
		
		
		
	}

}
