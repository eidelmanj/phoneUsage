package com.KnowRoaming;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import java.math.BigInteger;

/**
 * UserRecord captures all the information we are able to know about a
 * particular user. All interactions with the DB are encapsulated in UserRecord
 * methods.
 * 
 * @author Jonathan Eidelman
 */
public class UserRecord implements SQLRecord<String> {

	private SQLCommunicator sqlCom;
	private SecureRandom random = new SecureRandom();
	private String name, email, phoneNumber, uniqueId;

	/**
	 * Creates a new UserRecord object with all of the relevant information this
	 * constructor does not attempt to commit this record to the DB
	 * 
	 * @param name
	 *            User's name
	 * @param email
	 *            User's email address
	 * @param phoneNumber
	 *            User's phone number
	 * @param sqlCom
	 *            The SQL connection object associated with this record
	 * @throws Exception
	 *             If user tries to give a null SQLCommunicator object
	 */
	public UserRecord(String name, String email, String phoneNumber, SQLCommunicator sqlCom) throws Exception {
		// Ensure that the SQL connection exists
		if (sqlCom == null)
			throw new Exception("You must pass a valid SQLCommunicator object");
		
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		
		this.sqlCom = sqlCom;

	}

	/**
	 * This constructor assumes that there is already a user record in the DB
	 * and constructs an instance to match that user record
	 * @param id
	 *            The unique identifier
	 * @param sqlCom
	 *            SQLCommunicator object to talk to the DB
	 * @throws Exception
	 *             If there is no user record in the DB with this id or if
	 *             sqlCom is null
	 */
	public UserRecord(String id, SQLCommunicator sqlCom) throws Exception {
		if (sqlCom == null)
			throw new Exception("You must pass a valid SQLCommunicator object");
		this.sqlCom = sqlCom;
		this.constructFromPrimaryKey(id);
	}

	/**
	 * Checks the validity of the name of this UserRecord
	 * 
	 * @return true if name is valid, false otherwise
	 */
	private boolean validName() {
		if (this.name == null)
			return false;
		if (this.name.length() >= 20)
			return false;
		for (int i = 0; i < this.name.length(); i++) {
			if (!Character.isLetter(this.name.charAt(i)))
				return false;
		}
		return true;

	}

	/**
	 * Checks the validity of email for this UserRecord
	 * 
	 * @return true if email is valid, false otherwise
	 */
	private boolean validEmail() {
		
		if (this.email == null)
			return false;
		
		if (this.email.length() >= 50)
			return false;
		int atCharIdx = this.email.indexOf('@');
		int periodCharIdx = this.email.lastIndexOf('.');

		if (atCharIdx < 0 || periodCharIdx < 0)
			return false;

		if (atCharIdx > periodCharIdx)
			return false;
		return true;
	}

	/**
	 * Checks the validity of phone number for this UserRecord
	 * 
	 * @return true if phone number is valid, false otherwise
	 */
	private boolean validPhoneNumber() {
		if (this.phoneNumber == null) return false;
		if (this.phoneNumber.length() >= 20) return false;
		for (int i = 0; i < this.phoneNumber.length(); i++) {
			if (!Character.isDigit(this.phoneNumber.charAt(i)) && this.phoneNumber.charAt(i) != '-')
				return false;
		}
		return true;
	}
	
	
	/**
	 * Generates a random string of characters to act as a unique identifier for 
	 * this user
	 * @return Random String of 10 characters
	 */
	private String generateUniqueId() {

		String randStr = new BigInteger(130, random).toString(32);

		randStr = randStr.substring(0, Math.min(randStr.length(), 10));
		return randStr;
	}

	/**
	 * Validates all fields in the UserRecord. If there is a field that is invalid,
	 * an Exception is thrown
	 * @throws InvalidArgumentException If any argument is not valid
	 */
	public void validate() throws InvalidArgumentException {
		if (this.name == null)
			throw new InvalidArgumentException("name_null", "You must provide a name for the user");

		if (this.email == null)
			throw new InvalidArgumentException("email_null", "You must provide an email address");
		if (this.phoneNumber == null)
			throw new InvalidArgumentException("phoneNumber_null", "You must provide a phone number");

		if (!validName())
			throw new InvalidArgumentException("name_invalid", "Invalid name. Name must be less than 20 characters and a valid phone number.");
		if (!validEmail())
			throw new InvalidArgumentException("email_invalid", "Invalid email. Email must be less than 50 characters, and a valid email.");
		if (!validPhoneNumber())
			throw new InvalidArgumentException("phoneNumber_invalid", "Invalid phone number. Phone number must be less than 20 characters, and a valid phone number.");
	}

	/**
	 * Finds all UsageRecords that are associated with this user that apply between
	 * the startDate and the endDate
	 * @param startDate The beginning of the range of dates we are interested in
	 * @param endDate The end of the range of dates we are interested in
	 * @return An ArrayList containing all UsageRecords in the DB associated with
	 * 		      this user, and with start dates > startDate and
	 *            end dates < endDate
	 * @throws InvalidArgumentException If startDate is after endDate
	 */
	public ArrayList<UsageRecord> findAllRecords(LocalDate startDate, LocalDate endDate) throws InvalidArgumentException {
		if (startDate.isAfter(endDate)) 
			throw new InvalidArgumentException("invalid_dates", "Start date must be before end date");
		
		ArrayList<UsageRecord> recordList = new ArrayList<UsageRecord>();

		String sqlCmd = "SELECT usage_records.time_stamp, data_types.tp_name"
				+ " FROM usage_records JOIN data_types ON usage_records.tp_ID = data_types.ID" + " WHERE user_ID = \""
				+ this.getUserId() + "\"" + " AND usage_records.time_stamp >= " + this.sqlCom.DateToString(startDate)
				+ " AND usage_records.time_stamp <= " + this.sqlCom.DateToString(endDate);

		try {

			ResultSet rSet = this.sqlCom.executeQuery(sqlCmd);
			while (rSet.next()) {
				LocalDate curTimeStamp = rSet.getDate("time_stamp").toLocalDate();

				String dataType = rSet.getString("tp_name");

				recordList.add(new UsageRecord(this.uniqueId, curTimeStamp, dataType, this.sqlCom));
			}

		} catch (Exception e) {
			// There should really never be an Exception here, since we have
			// already verified that
			// the user exists in the DB by the time this runs. Therefore we
			// print a StackTrace
			e.printStackTrace();
		}

		return recordList;
	}

	/**
	 * Commits a new UserRecord to the DB. This method assumes that no record for this
	 * user email or ID exists in the DB. This method validates all fields with respect to the
	 * specifications before committing
	 * @throws Exception If there is already an entry in the DB for a user with the same email address
	 */
	public void commitNew() throws Exception {
		
		/* Generating unique key: In theory it is possible to generate
		 a string of characters that already exists in the DB
		 Rather than doing an expensive check on whether the ID 
		 exists, we are optimistic. We assume that no such ID 
		 exists and retry if we fail. Since collisions are 
		 extremely improbable, this is more efficient */
		boolean commited = false; 
		while (!commited) {
			this.uniqueId = this.generateUniqueId();
			this.validate();
			String sqlCmd = "INSERT INTO user_records (unique_id, name, email, phone_number) VALUES (" + "\""
				+ this.uniqueId + "\", \"" + this.name + "\", \"" + this.email + "\", \"" + this.phoneNumber + "\");";

			try {
				this.sqlCom.commitUpdate(sqlCmd);
				commited = true;
			} catch (SQLException e) {
				if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
					throw new InvalidArgumentException("email_taken", "That email address is taken");
				}
				else if (e.getMessage().contains("Duplicate entry") 
						&& e.getMessage().contains("unique_id")) {
					// We have generated a unique key that already exists in the DB. 
					// we must go around again
					commited = false;
				}
				else {
					throw e;
				}

			}
		}
	}

	/**
	 * Commits an update to the DB to match this UserRecord instance
	 * Assumes that there is already a row in the DB associated with the unique ID
	 * of this User. 
	 * @throws Exception If update query application fails
	 */
	public void commitUpdate() throws Exception {
		this.validate();
		String sqlCmd = "UPDATE user_records SET "
				+ " name = '" + this.name + "', email = '" + this.email + "', phone_number ='" + this.phoneNumber + "'"
				+ " WHERE unique_ID = '" + this.uniqueId + "';";


		this.sqlCom.commitUpdate(sqlCmd);

	}
	
	/**
	 * Fills all the field in this object to match the DB entry associated with user unique id
	 * @param key The primary key associated with the user record in the DB we are interested in
	 * @throws Exception If there is a problem executing SQL query
	 * @throws NonExistentRecordException If there is not user record associated with this unique key
	 */
	public void constructFromPrimaryKey(String key) throws Exception {
		if (sqlCom == null)
			throw new Exception("You must pass a valid SQLCommunicator object");

		String sqlQuery = "SELECT * FROM user_records WHERE" + " unique_id = \"" + key + "\"";

		ResultSet rSet = sqlCom.executeQuery(sqlQuery);

		// Since User ID's are unique, there can only be one result
		boolean nonEmpty = rSet.next();
		if (!nonEmpty) {
			throw new NonExistentRecordException("There is no user with id " + key, key);
		}
		String qName, qEmail, qPhoneNumber;
		qName = rSet.getString("name");
		qEmail = rSet.getString("email");
		qPhoneNumber = rSet.getString("phone_number");

	
		this.name = qName;
		this.email = qEmail;
		this.phoneNumber = qPhoneNumber;
		this.uniqueId = key;
	}
	

	/**
	 * Getter for name field
	 * @return name for this user
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter for email
	 * @return email for this user
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Getter for phoneNumber
	 * @return Phone number string for this user
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Getter for user unique ID
	 * @return String with unique ID for this user
	 */
	public String getUserId() {
		return this.uniqueId;
	}
	
	// Setters for private fields
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUserId(String uId) {
		this.uniqueId = uId;
	}
	
	public void setEmail(String email)  {
		this.email = email;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
