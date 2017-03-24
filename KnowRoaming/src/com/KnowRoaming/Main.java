package com.KnowRoaming;

import com.KnowRoaming.SQLCommunicator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;




public class Main {
	final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	
	/**
	 * Prints out usage information records in a reasonable format
	 * @param l An ArrayList of UsageRecords that should not have any null fields
	 */
	public static void printUsageList(ArrayList<UsageRecord> l) {
		if (l.isEmpty())
			System.out.println("There are no usage records in the time period requested");
		for (UsageRecord uRecord : l) {
			System.out.println("(user: " + uRecord.getUserId() +", time_stamp: " 
		+ uRecord.getTimeStamp().toString()  + ", "
				+ "data_type: "+ uRecord.getDataType() + ")");
		}
	}
	
	/**
	 * Creates and commits a new UsageRecord in the database
	 * @param id The unique ID of the user to be associated with this record
	 * @param timeStampStr The string representation of the timestamp in the appropriate format
	 * @param dataStr The string representation of the data type for this UsageRecord
	 * @param sqlCom The SQLCommunication object
	 */
	private static void runNewUsage(String id, 
			String timeStampStr, String dataStr, SQLCommunicator sqlCom) {
		try {
			LocalDate timeStamp = LocalDate.parse(timeStampStr, DATE_FORMAT);

			
			UsageRecord r = new UsageRecord(id, timeStamp, dataStr, sqlCom );
			r.commitNew();
			
			
		} 
		
		catch (DateTimeParseException e) {
			System.out.println("Incorrect date format! Please use 'dd-MM-yyyy'");
		}catch (Exception e) {
		
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Creates and commits a new UserRecord with the appropriate values
	 * @param name The name of the new user
	 * @param email The email of the new user
	 * @param phoneNumber The phone number (String) of the new user
	 * @param sqlCom The current SQLCommunication object
	 */
	private static void runNewUser(String name, String email, String phoneNumber, SQLCommunicator sqlCom) {
		try {
			UserRecord r = new UserRecord(name, email, phoneNumber, sqlCom);
			r.commitNew();
			System.out.println(r.getUserId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Retrieves UsageRecords with the given parameters and then prints them out neatly
	 * @param userId The unique ID of the user associated with these records
	 * @param startDateStr The beginning of the date range we are interested in 
	 * @param endDateStr The end of the date range we are interested in 
	 * @param sqlCom The current SQLCommunication object 
	 */
	private static void runGetUsageRecords(String userId,
			String startDateStr, String endDateStr, SQLCommunicator sqlCom) {
		UserRecord r;
		try {
			r = new UserRecord(userId, sqlCom);
			LocalDate startDate = LocalDate.parse(startDateStr, DATE_FORMAT);
			LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMAT);
			ArrayList<UsageRecord> usageRList = r.findAllRecords(startDate, endDate);
			
			printUsageList(usageRList);
			
			
			
		} catch (NonExistentRecordException e) {
			System.out.println(e.getMessage());
			
		} catch (DateTimeParseException e) {
			System.out.println("Incorrect date format! Please use 'dd-MM-yyyy'");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Simple parser for our three commands 
	 * @param inStr
	 * @param sqlCom
	 */
	public static void parseAndExecute(String inStr, SQLCommunicator sqlCom) {
		if (inStr.length() == 0) return; 
		
		// The user might want to give a name with a space in it, if so we must look
		// for something in quotes
		String nameStr = null;
		
		String[] findName = inStr.split("'");
		// If there's a quote in the input string, then we can retrieve the name 
		// that the user provided
		if (findName.length > 2) {
			nameStr = findName[1];
			inStr = findName[0] +"name" + findName[2];
		}
		
		String[] inputParts = inStr.split(" "); // Split string into command and arguments
		
		
		if (inputParts.length == 0 ) return;

		
		if (inputParts[0].equals("NEWUSER")) {

			if (inputParts.length < 4)  {
				System.out.println("Incorrect usage: NEWUSER 'name' email phoneNumber\n");
				return;
			}
			
			if (nameStr == null) {
				nameStr = inputParts[1];
			}
				
		
			
			runNewUser(nameStr, inputParts[2], inputParts[3], sqlCom);
		}
		
		else if (inputParts[0].equals("USAGE")) {
			if (inputParts.length < 4) {
				System.out.println("Incorrect usage: USAGE userId timeStamp dataType");
				return;
			}
			
			runNewUsage(inputParts[1], inputParts[2],inputParts[3], sqlCom);
			
		

		}
		
		else if (inputParts[0].equals("SHOW")) {
			UserRecord r;
			if (inputParts.length < 6) {
				System.out.println("Incorrect usage: SHOW user_id FROM dd-MM-yyyy TO dd-MM-yyyy");
				return;
			}
			
			runGetUsageRecords(inputParts[1], inputParts[3], inputParts[5], sqlCom);
			
		}
		else
			System.out.println("Invalid command!");
		
	}
	
	
	
	public static void main(String[] args) {
		UserRecord r;
		UsageRecord u;
		SQLCommunicator sqlCom = null;
		Scanner reader = new Scanner(System.in); 
		
		
		BufferedReader br = null;
		FileReader fr = null;
		
		// By default we look for settings.txt in the current folder
		String fname = "settings.txt";
		if (args.length > 0) {
			// User can provide a different settings file
			fname = args[0];
		}

		try {
			// Retrieve DB configuration information from 
			// settings.txt file
			fr = new FileReader(fname);
			br = new BufferedReader(fr);
			
			String usernameConfig = br.readLine();
			String pswdConfig = br.readLine();
			String dbNameConfig = br.readLine();
	
			// Establish a connection with the DB
			sqlCom = new SQLCommunicator(usernameConfig, pswdConfig, dbNameConfig);

			
		}
		catch (IOException e) {
			System.err.println("Error reading settings.txt");
			//e.printStackTrace();
			System.exit(-1);
		} catch(Exception e) {
			// Note: It seems that rather than throwing 
			System.err.println("Error connecting to MySQL DB");
			//e.printStackTrace();
			System.exit(-1);
		}
		
		
		System.out.println("Welcome to the KnowRoaming phone record app!");
		System.out.println("Commands:\nNEWUSER name email phoneNumber");
		System.out.println("USAGE userId dd-MM-yyyy dataType");
		System.out.println("SHOW user_id FROM dd-MM-yyyy TO dd-MM-yyyy");
		// Enter User Input Loop
		while (true) {
			
			System.out.print(">>>");
			String uInput = reader.nextLine();
			
			if (uInput.equals("exit")) break;
			
			// Parse user input
			parseAndExecute(uInput, sqlCom);
		
		}
		
		try {
			sqlCom.close();
		} catch (Exception e) {
			System.err.println("Error closing SQL connection");
			e.printStackTrace();
			
		}
		
		
		
		
		
	}

}
