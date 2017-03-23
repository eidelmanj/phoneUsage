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

	
	//Print Usage Information List
	public static void printUsageList(ArrayList<UsageRecord> l) {
		if (l.isEmpty())
			System.out.println("There are no usage records in the time period requested");
		for (UsageRecord uRecord : l) {
			System.out.println("(user: " + uRecord.getUserId() +", time_stamp: " 
		+ uRecord.getTimeStamp().toString()  + ", "
				+ "data_type: "+ uRecord.getDataType() + ")");
		}
	}
	
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
	
	private static void runNewUser(String name, String email, String phoneNumber, SQLCommunicator sqlCom) {
		try {
			UserRecord r = new UserRecord(name, email, phoneNumber, sqlCom);
			r.commitNew();
			System.out.println(r.getUserId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
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
		String[] inputParts = inStr.split(" "); // Split string into command and arguments
		
		
		if (inputParts.length == 0 ) return;

				
		if (inputParts[0].equals("NEWUSER")) {

			if (inputParts.length < 4)  {
				System.out.println("Incorrect usage: NEWUSER name email phoneNumber\n");
				return;
			}
				
			
			runNewUser(inputParts[1], inputParts[2], inputParts[3], sqlCom);
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
		
		
		
		try {
			// Retrieve DB configuration information from 
			// settings.txt file
			fr = new FileReader("settings.txt");
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
			
			
			parseAndExecute(uInput, sqlCom);
		
		}
		
		
		
		
		
	}

}
