package com.KnowRoaming;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;



public class UsageRecord implements SQLRecord {
	SQLCommunicator sqlCom;
	String userId, dataType;
	LocalDate startDate, endDate;
	
	public UsageRecord(String userId, LocalDate startDate, LocalDate endDate, String dataType, 
			SQLCommunicator sqlCom) {
		this.userId = userId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dataType = dataType;
		this.sqlCom = sqlCom;
	}
	
	boolean validDates() {
		if (startDate.isAfter(endDate)) return false;
		if (endDate.isBefore(startDate)) return false;
		return true;
	}
	
	boolean validData() {
		if (!(
				dataType.equals("DATA")
				|| dataType.equals("VOICE")
				|| dataType.equals("ALL")
				|| dataType.equals("SMS")))
			return false;
		return true;
	}
	
	void validate() throws Exception {
		if (!validDates()) throw new InvalidArgumentException("dates_invalid", "Your start date cannot be after your end date");
		if (!validData()) 
			throw new InvalidArgumentException("datatype_invalid", "Please choose from the given data type options (DATA, VOICE, ALL, SMS)");
		
	}
	
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
			if (e.getMessage().contains("a foreign key constraint fails"))
				throw new Exception("The user ID you provided does not exist");
			else throw e;
		}
	
	}
	
	

}
