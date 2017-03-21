package com.KnowRoaming;

import java.util.Date;

public class UsageRecord implements SQLRecord {
	SQLCommunicator sqlCom;
	String userId, dataType;
	Date startDate, endDate;
	
	public UsageRecord(String userId, Date startDate, Date endDate, String dataType, 
			SQLCommunicator sqlCom) {
		this.userId = userId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dataType = dataType;
		this.sqlCom = sqlCom;
	}
	
	boolean validDates() {
		if (startDate.after(endDate)) return false;
		if (endDate.before(startDate)) return false;
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
		//String sqlCmd =
		//	 "INSERT INTO usage_records (ID, 

		//this.sqlCom.commitUpdate(sqlCmd);
	
	}
	
	

}
