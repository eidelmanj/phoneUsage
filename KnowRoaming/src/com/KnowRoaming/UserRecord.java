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
public class UserRecord implements SQLRecord {

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
		if (sqlCom == null)
			throw new Exception("You must pass a valid SQLCommunicator object");
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.uniqueId = generateUniqueId();
		this.sqlCom = sqlCom;

	}

	/**
	 * This constructor assumes that
	 * 
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
		String sqlQuery = "SELECT * FROM user_records WHERE" + " unique_id = \"" + id + "\"";

		ResultSet rSet = sqlCom.executeQuery(sqlQuery);

		// Since User ID's are unique, there can only be one result
		boolean nonEmpty = rSet.next();
		if (!nonEmpty) {
			throw new NonExistentUserException("There is no user with id " + id, id);
		}
		String qName, qEmail, qPhoneNumber;
		qName = rSet.getString("name");
		qEmail = rSet.getString("email");
		qPhoneNumber = rSet.getString("phone_number");

		if (qName != null)
			this.name = qName;
		if (qEmail != null)
			this.email = qEmail;
		if (qPhoneNumber != null)
			this.phoneNumber = qPhoneNumber;
		this.uniqueId = id;
	}

	/**
	 * Checks the validity of the name of this UserRecord
	 * 
	 * @return true if name is valid, false otherwise
	 */
	private boolean validName() {
		if (this.name == null)
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
		if (this.name == null)
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
		for (int i = 0; i < this.phoneNumber.length(); i++) {
			if (!Character.isDigit(this.phoneNumber.charAt(i)) && this.phoneNumber.charAt(i) != '-')
				return false;
		}
		return true;
	}
	
	
	/**
	 * Generates a random string of characters to act as a unique identifier for 
	 * this user
	 * @return Random String of characters
	 */
	private String generateUniqueId() {
		// Google justification for this
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
			throw new InvalidArgumentException("name_invalid", "Invalid name");
		if (!validEmail())
			throw new InvalidArgumentException("email_invalid", "Invalid email");
		if (!validPhoneNumber())
			throw new InvalidArgumentException("phoneNumber_invalid", "Invalid phone number");
	}

	/**
	 * Finds all UsageRecords that are associated with this user that apply between
	 * the startDate and the endDate
	 * @param startDate The beginning of the range of dates we are interested in
	 * @param endDate The end of the range of dates we are interested in
	 * @return An ArrayList containing all UsageRecords in the DB associated with
	 * 		      this user, and with start dates > startDate and
	 *            end dates < endDate
	 */
	public ArrayList<UsageRecord> findAllRecords(LocalDate startDate, LocalDate endDate) {
		ArrayList<UsageRecord> recordList = new ArrayList<UsageRecord>();

		String sqlCmd = "SELECT usage_records.time_stamp, data_types.tp_name"
				+ " FROM usage_records JOIN data_types ON usage_records.tp_ID = data_types.ID" + " WHERE user_ID = \""
				+ this.getUserId() + "\"" + " AND usage_records.time_stamp > " + this.sqlCom.DateToString(startDate)
				+ " AND usage_records.time_stamp < " + this.sqlCom.DateToString(endDate);

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
	public void commit() throws Exception {

		this.validate();
		String sqlCmd = "INSERT INTO user_records (unique_id, name, email, phone_number) VALUES (" + "\""
				+ this.uniqueId + "\", \"" + this.name + "\", \"" + this.email + "\", \"" + this.phoneNumber + "\");";

		try {
			this.sqlCom.commitUpdate(sqlCmd);
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
				throw new InvalidArgumentException("email_taken", "That email address is taken");
			}
		}
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

}
