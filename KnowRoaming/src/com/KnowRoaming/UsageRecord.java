package com.KnowRoaming;

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
		if (!validDates()) throw new Exception();
		if (!validData()) throw new Exception();
		
	}
	
	public void commit() throws Exception {
		this.validate();
	
		String sqlCmd =
			 "INSERT INTO usage_records (ID, user_ID, tp_ID, start_date, end_date) VALUES"
			 + "(DEFAULT,"
			 + "(SELECT ID FROM user_records WHERE unique_id = \"" + this.userId + "\"),"
			 + "(SELECT ID FROM data_types WHERE tp_name = \"" + this.dataType + "\"),"
			 + "STR_TO_DATE('"+this.startDate.getDayOfMonth()+"-" 
			 	+ this.startDate.getMonthValue() + "-" 
			 	+ this.startDate.getYear() + "', '%d-%m-%Y'),"
			 + "STR_TO_DATE('"+this.endDate.getDayOfMonth()+"-" + this.endDate.getMonthValue() + "-" 
				 + this.endDate.getYear() + "', '%d-%m-%Y'));";
		System.out.println(sqlCmd);
		this.sqlCom.commitUpdate(sqlCmd);
	
	}
	
	

}
