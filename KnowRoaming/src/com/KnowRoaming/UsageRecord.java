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
public class UsageRecord implements SQLRecord<Integer> {
	private SQLCommunicator sqlCom;
	private String userId, dataType;
	private LocalDate timeStamp;
	private Integer primaryKey;
	
	
	
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
	public UsageRecord(String userId, LocalDate timeStamp, String dataType, 
			SQLCommunicator sqlCom) {
		
		this.userId = userId;
		this.timeStamp = timeStamp;
		this.dataType = dataType;
		this.sqlCom = sqlCom;
	}
	
	/**
	 * Constructs a new UsageRecord entry where all fields are left null except the SQL object
	 * This is meant to be an empty object that will be filled using a database read call at a later
	 * time
	 * @param sqlCom SQLCommunicator to be associated with this instance
	 */
	public UsageRecord(SQLCommunicator sqlCom) {
		this.sqlCom = sqlCom;
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

		// if (!validDates()) throw new InvalidArgumentException("dates_invalid", "Your start date cannot be after your end date");
		if (!validData()) 
			throw new InvalidArgumentException("datatype_invalid", "Please choose from the given data type options (DATA, VOICE, ALL, SMS)");
		
	}
	
	/**
	 * Validates all fields and commits them to the DB if possible. 
	 * @throws Exception If commit fails. This should only happen if the User ID
	 *         does not exist.
	 */
	public void commitNew() throws Exception {
		this.validate();
	
		String sqlCmd =
			 "INSERT INTO usage_records (ID, user_ID, tp_ID, time_stamp) VALUES"
			 + "(DEFAULT,"
			 + "\"" + this.userId + "\","
			 + "(SELECT ID FROM data_types WHERE tp_name = \"" + this.dataType + "\"),"
			 + this.sqlCom.DateToString(this.timeStamp) +");";
		
		
		
		try {
			Integer primKey = (Integer) this.sqlCom.commitUpdate(sqlCmd);
			if (primKey != null) this.primaryKey = primKey;
			
			
			
		} catch (SQLException e) {
			// The SQL default message is not very useful when the foreign key isn't found
			// so I have replaced it
			if (e.getMessage().contains("a foreign key constraint fails"))
				throw new Exception("The user ID you provided does not exist");
			else throw e;
		}
	
	}
	
	/**
	 * Validates all fields and updates an existing row in the DB to match
	 * this instance. If there is no such row, this will throw an exception
	 * @throws Exception If the record you want to update does not exist
	 */
	public void commitUpdate() throws Exception {
		if (this.primaryKey == null) throw new InvalidArgumentException("null_primary", "There is no primary key "
				+ "associated with this instance");
		this.validate();
		
		String sqlCmd =
			 "UPDATE usage_records SET "
			 + "tp_ID = (SELECT ID FROM data_types WHERE tp_name = \"" + this.dataType + "\"),"
			 + "time_stamp = " + this.sqlCom.DateToString(this.timeStamp) 
			 + " WHERE usage_records.ID = '" + this.primaryKey + "';";
		// System.out.println(sqlCmd);
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
	
	/**
	 * Retrieves record in the DB associated with a primary key and updates the fields of this instance
	 * to match that record.
	 * @param key The primary key of the record we wish to take from the DB
	 * @throws NonExistentRecordException If there is no record associated with key
	 * @throws Exception If there is an error executing SQL query
	 */
	public void constructFromPrimaryKey(Integer key) throws NonExistentRecordException, Exception {

		String sqlQuery = "SELECT usage_records.user_ID, usage_records.time_stamp, data_types.tp_name"
				+ "  FROM usage_records JOIN data_types ON usage_records.tp_ID = data_types.ID"
				+ " WHERE" + " usage_records.ID = " + key.toString();
		// System.out.println(sqlQuery);
		ResultSet rSet = sqlCom.executeQuery(sqlQuery);

		// Since User ID's are unique, there can only be one result
		boolean nonEmpty = rSet.next();
		if (!nonEmpty) {
			throw new NonExistentRecordException("There is no user with id " + key.toString(), key.toString());
		}
		String qUserId, qDataType;
		LocalDate qTimeStamp;
		
		qUserId = rSet.getString("user_ID");
		qDataType = rSet.getString("tp_name");
		qTimeStamp = rSet.getDate("time_stamp").toLocalDate();
		
		if (qUserId != null) 
			this.userId = qUserId;
		if (qDataType != null)
			this.dataType = qDataType;
		if (qTimeStamp != null)
			this.timeStamp = qTimeStamp;
	}
	
	
	/* Getters for private fields */
	public LocalDate getTimeStamp() {
		return this.timeStamp;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public Integer getPrimaryKey() {
		return this.primaryKey;
	}
	
	
	/* Setters for private fields */
	public void setTimeStamp(LocalDate d) {
		this.timeStamp = d;
	}
	
	public void setDataType(String str) {
		this.dataType = str;
	}
	
	public void setUserId(String str) {
		this.userId = str;
	}
	
	

}
