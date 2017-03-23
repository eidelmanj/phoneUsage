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




public class InputReader {
	

	
	//Print Usage Information List
	public static void printUsageList(ArrayList<UsageRecord> l) {
		if (l.isEmpty())
			System.out.println("There are no usage records in the time period requested");
		for (UsageRecord uRecord : l) {
			System.out.println("(user: " + uRecord.userId +", start_date: " 
		+ uRecord.startDate.toString() + ", end_date: " + uRecord.endDate.toString() + ", "
				+ "data_type: "+ uRecord.dataType + ")");
		}
	}
	
	/**
	 * Simple parser for our three commands 
	 * @param inStr
	 * @param sqlCom
	 */
	public static void parseAndExecute(String inStr, SQLCommunicator sqlCom) {
		String[] inputParts = inStr.split(" "); // Split string into command and arguments
		
		final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		if (inputParts.length == 0 ) return;
		//for (int i =0 ; i< inputParts.length; i ++ ) System.out.println(inputParts[i]);
		
		
		if (inputParts.length > 1 && inputParts[0].equals("NEW") && inputParts[1].equals("USER")) {
			//System.out.println("in here");
			if (inputParts.length < 5)  {
				System.out.println("Incorrect usage: NEW USER name email phoneNumber\n");
				return;
				
			}
				
			
			try {
				UserRecord r = new UserRecord(inputParts[2], inputParts[3], inputParts[4], sqlCom);
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
		
		else if (inputParts[0].equals("SHOW")) {
			UserRecord r;
			if (inputParts.length < 6) {
				System.out.println("Incorrect usage: SHOW user_id FROM dd-MM-yyyy TO dd-MM-yyyy");
				return;
			}
			try {
				r = new UserRecord(inputParts[1], sqlCom);
				LocalDate startDate = LocalDate.parse(inputParts[3], DATE_FORMAT);
				LocalDate endDate = LocalDate.parse(inputParts[5], DATE_FORMAT);
				ArrayList<UsageRecord> usageRList = r.findAllRecords(startDate, endDate);
				
				printUsageList(usageRList);
				
				
				
			} catch (NonExistentUserException e) {
				System.out.println(e.getMessage());
				
			} catch (DateTimeParseException e) {
				System.out.println("Incorrect date format! Please use 'dd-MM-yyyy'");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
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

			
		} catch (IOException e) {
			System.err.println("Error reading settings.txt");
			e.printStackTrace();
			System.exit(-1);
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
