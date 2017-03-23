package com.KnowRoaming;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

/**
 * UsageRecord captures all relevant information about usage record entries.
 * All interaction with the DB is encapsulated in UsageRecord methods
 * @author Jonathan Eidelman
 *
 */
public class UsageRecord implements SQLRecord {
	SQLCommunicator sqlCom;
	String userId, dataType;
	LocalDate startDate, endDate;
	
	/**
	 * Constructor for new UsageRecord entry. The assumption is that this entry does not
	 * yet exist in the DB. This constructor does not attempt to commit any changes to the
	 * DB. Constructor  does not validate entries by default.
	 * @param userId Unique 10 character ID for a particular user associated with this record
	 * @param startDate Start date for this usage record
	 * @param endDate End date for this usage record
	 * @param dataType Data 
	 * @param sqlCom
	 */
	public UsageRecord(String userId, LocalDate startDate, LocalDate endDate, String dataType, 
			SQLCommunicator sqlCom) {
		
		this.userId = userId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dataType = dataType;
		this.sqlCom = sqlCom;
	}
	
	/**
	 * Validates dates for this object to guarantee that start date is
	 * before end date
	 * @return true if dates are valid, false otherwise
	 */
	private boolean validDates() {
		if (startDate.isAfter(endDate)) return false;
		if (endDate.isBefore(startDate)) return false;
		return true;
	}
	
	/**
	 * Validates dataType entry for this object to guarantee it is a dataType that
	 * exists in the DB
	 * @return true if the dataType is valid, false otherwise
	 */
	private boolean validData() {
		String sqlQuery = "SELECT * FROM data_types WHERE tp_name = '" + this.dataType + "'";
		
		try {
			ResultSet rSet = sqlCom.executeQuery(sqlQuery);
			boolean found = rSet.next();
			return found;
			
		} catch (Exception e) {
			// There is no reason for this to fail
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Validates all fields in this object. Runs without doing anything
	 * if all fields are valid
	 * @throws InvalidArgumentException If there is a field that is not valid
	 */
	void validate() throws InvalidArgumentException {
		if (!validDates()) throw new InvalidArgumentException("dates_invalid", "Your start date cannot be after your end date");
		if (!validData()) 
			throw new InvalidArgumentException("datatype_invalid", "Please choose from the given data type options (DATA, VOICE, ALL, SMS)");
		
	}
	
	/**
	 * Validates all fields and commits them to the DB if possible. 
	 * @throws Exception If commit fails. This should only happen if the User ID
	 *         does not exist.
	 */
	public void commit() throws Exception {
		this.validate();
	
		String sqlCmd =
			 "INSERT INTO usage_records (ID, user_ID, tp_ID, start_date, end_date) VALUES"
			 + "(DEFAULT,"
			 + "\"" + this.userId + "\","
			 + "(SELECT ID FROM data_types WHERE tp_name = \"" + this.dataType + "\"),"
			 + this.sqlCom.DateToString(this.startDate) + ", "
			 + this.sqlCom.DateToString(this.endDate) +");";
		
		try {
			this.sqlCom.commitUpdate(sqlCmd);
		} catch (SQLException e) {
			// The SQL default message is not very useful when the foreign key isn't found
			// so I have replaced it
			if (e.getMessage().contains("a foreign key constraint fails"))
				throw new Exception("The user ID you provided does not exist");
			else throw e;
		}
	
	}
	
	

}
