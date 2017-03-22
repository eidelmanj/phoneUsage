package com.KnowRoaming;

import com.KnowRoaming.SQLCommunicator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;




public class InputReader {
	
	// Parse User Input, and execute given command
	public static void parseAndExecute(String inStr, SQLCommunicator sqlCom) {
		String[] inputParts = inStr.split(" "); // Split string into command and arguments
		
		
		if (inputParts.length == 0 ) return;
		//for (int i =0 ; i< inputParts.length; i ++ ) System.out.println(inputParts[i]);
		
		
		if (inputParts.length > 1 && inputParts[0].equals("NEW") && inputParts[1].equals("USER")) {
			//System.out.println("in here");
			if (inputParts.length < 5)  {
				System.out.println("Incorrect usage: NEW USER name email phoneNumber\n");
				return;
				
			}
				
			UserRecord r = new UserRecord(inputParts[2], inputParts[3], inputParts[4], sqlCom);
			try {
				r.commit();
				System.out.println(r.getUserId());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		else if (inputParts.length > 1 && inputParts[0].equals("NEW") && inputParts[1].equals("USAGE")) {
			if (inputParts.length < 6) {
				System.out.println("Incorrect usage: NEW USAGE userId startDate endDate dataType");
				return;
			}
			
			final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			
		
			try {
				LocalDate startDate = LocalDate.parse(inputParts[3], DATE_FORMAT);
				LocalDate endDate = LocalDate.parse(inputParts[4], DATE_FORMAT);
				
				UsageRecord r = new UsageRecord(inputParts[2], startDate, endDate, inputParts[5], sqlCom );
				r.commit();
				
				
			} catch (DateTimeParseException e) {
				System.out.println("Incorrect date format! Please use 'dd-MM-yyyy'");
			}catch (Exception e) {
			
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	
	
	public static void main(String[] args) {
		UserRecord r;
		UsageRecord u;
		SQLCommunicator sqlCom = null;
		Scanner reader = new Scanner(System.in); 
		
		// Establish a connection
		try {
			sqlCom = new SQLCommunicator("root", "root", "knowroaming");

			
		} catch(Exception e) {
			System.err.println("Error connecting to MySQL DB");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		// Enter User Input Loop
		while (true) {
			
			System.out.print(">>>");
			String uInput = reader.nextLine();
			
			if (uInput.equals("exit")) break;
			
			
			parseAndExecute(uInput, sqlCom);
		
		}
		
		
		
		
		
	}

}
