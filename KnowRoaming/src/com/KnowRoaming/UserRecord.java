package com.KnowRoaming;
import java.security.SecureRandom;
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
    public void validate() throws Exception {
	if (this.name == null) throw new Exception();
	
    if (this.email == null) throw new Exception();
    if (this.phoneNumber == null) throw new Exception();

    if (!validName(this.name)) throw new Exception();
    if (!validEmail(this.email)) throw new Exception();
    if (!validPhoneNumber(this.phoneNumber)) throw new Exception();
}



// TODO Make better exception                                                                                                                                                                           
public void commit() throws Exception {

    this.validate();
    String sqlCmd =
        "INSERT INTO user_records (ID, unique_id, name, email, phone_number) VALUES (DEFAULT, \"" +
        this.uniqueId + "\", \"" + this.name + "\", \"" + this.email + "\", \"" + this.phoneNumber + "\");";

    this.sqlCom.commitUpdate(sqlCmd);
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



}


