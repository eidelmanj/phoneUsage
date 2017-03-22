package com.KnowRoaming;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.math.BigInteger;

/*                                                                                                                                                                                                          
Record of essential User Information                                                                                                                                                                        
 */
public class UserRecord implements SQLRecord {

	private SQLCommunicator sqlCom;
    private SecureRandom random = new SecureRandom();
    private String name, email, phoneNumber, uniqueId;

    public UserRecord(String name, String email, String phoneNumber, SQLCommunicator sqlCom) {
	this.name = name;
    this.email = email;
	this.phoneNumber = phoneNumber;
    this.uniqueId = generateUniqueId();
    this.sqlCom = sqlCom;

    }
    
    // Creates a UserRecord object that retrieves a User that already exists within the database
    public UserRecord(String id, SQLCommunicator sqlCom) throws Exception {
    	String sqlQuery = "SELECT * FROM user_records WHERE"
    			+ " unique_id = \"" + id + "\"";
    	
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
    	
    	if (qName != null) this.name = qName;
    	if (qEmail != null) this.email = qEmail;
    	if (qPhoneNumber != null) this.phoneNumber = qPhoneNumber;
    }

    public UserRecord() {

    }

    // Valid name must be made up of characters                                                                                                                                                             
    private boolean validName(String name) {
        for (int i =0; i< name.length(); i ++ ) {
            if (!Character.isLetter(name.charAt(i))) return false;
        }
        return true;

    }

    // Valid Email has an '@' and a '.' in the correct order                                                                                                                                                
    private boolean validEmail(String email) {
        int atCharIdx = email.indexOf('@');
        int periodCharIdx = email.lastIndexOf('.');




        if (atCharIdx < 0 || periodCharIdx < 0) return false;

        if (atCharIdx > periodCharIdx) return false;
	return true;
    }

    private boolean validPhoneNumber(String phoneNumber) {
	for (int i =0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))
		&& phoneNumber.charAt(i) != '-') return false;
	}
        return true;
    }


    // TODO Should this be void?                                                                                                                                                                            
    // TODO Make a better Exception                                                                                                                                                                         
    public void validate() throws InvalidArgumentException {
	if (this.name == null) throw new InvalidArgumentException("name_null", "You must provide a name for the user");
	
    if (this.email == null) throw new InvalidArgumentException("email_null", "You must provide an email address");
    if (this.phoneNumber == null) throw new InvalidArgumentException("phoneNumber_null", "You must provide a phone number");

    if (!validName(this.name)) throw new InvalidArgumentException("name_invalid", "Invalid name");
    if (!validEmail(this.email)) throw new InvalidArgumentException("email_invalid", "Invalid email");
    if (!validPhoneNumber(this.phoneNumber)) throw new InvalidArgumentException("phoneNumber_invalid", "Invalid phone number");
}


public ArrayList<UsageRecord> findAllRecords(LocalDate startDate, LocalDate endDate) {
	ArrayList<UsageRecord> recordList = new ArrayList<UsageRecord>();
	
	String sqlCmd =
	       "SELECT * FROM usage_records WHERE user_ID = " + this.getUserId()
	       		+ " AND start_date > " + 
	       		"STR_TO_DATE('"+startDate.getDayOfMonth()+"-" 
			 	+ startDate.getMonthValue() + "-" 
			 	+ startDate.getYear() + "', '%d-%m-%Y')" 
			 	+ " AND end_date < "  
	       		+ "STR_TO_DATE('"+endDate.getDayOfMonth()+"-" 
			 	+ startDate.getMonthValue() + "-" 
			 	+ startDate.getYear() + "', '%d-%m-%Y')";
	try {
		this.sqlCom.executeQuery(sqlCmd);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return recordList;
}

// TODO Make better exception                                                                                                                                                                           
public void commit() throws Exception {

    this.validate();
    String sqlCmd =
        "INSERT INTO user_records (unique_id, name, email, phone_number) VALUES (" 
        + "\"" + this.uniqueId + "\", \"" + this.name + "\", \"" + this.email + "\", \"" + this.phoneNumber + "\");";

    try {
    	this.sqlCom.commitUpdate(sqlCmd);
    } catch (SQLException e) {
    	if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
    		throw new InvalidArgumentException("email_taken", "That email address is taken");
    	}
    }
}





private String generateUniqueId() {
    // Google justification for this         
	String randStr =  new BigInteger(130, random).toString(32);
	
	randStr = randStr.substring(0, Math.min(randStr.length(), 10));
    return randStr;
}

// Getters for private fields                                                                                                                                                                           
public String getName() {
    return this.name;
}

public String getEmail() {
    return this.email;
}

public String getPhoneNumber() {
    return this.phoneNumber;
}

public String getUserId() {
	return this.uniqueId;
}



}


