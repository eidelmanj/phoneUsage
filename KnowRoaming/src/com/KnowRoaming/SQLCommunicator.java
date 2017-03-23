package com.KnowRoaming;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




/**
 * This class encapsulates all of the behaviours of the MySQL connection. 
 * The goal here is so that executing queries can be done in a single step
 * and meta-information about our DB can be passed around elegantly
 * @author      Jonathan Eidelman
 */
public class SQLCommunicator {
	private String uname;
	private String pswd;
	private String dbname;
	private String dbUrl;
	
	                                                                                                                                                             

    Connection conn = null;
   
    
    /**
     * Connects to database associated with the current SQLCommunicator instance
     * If this fails, we print the stack-trace, since there is no elegant way to continue 
     * execution without access to the DB.
     * @throws ClassNotFoundException If we are missing the JDBC driver
     * @throws SQLException If SQL connection fails
     */
    private void connectSQL() throws SQLException, ClassNotFoundException {	


    		//STEP 2: Register JDBC driver                                                                                                                                                                  
  	  		Class.forName("com.mysql.jdbc.Driver");

  	  		//STEP 3: Open a connection                                                                                                                                                                     
  	  		this.conn = DriverManager.getConnection(this.dbUrl, this.uname, this.pswd);

    }
    
    /**
     * Closes current connection
     * @throws Exception, if there is an error closing connection
     */
    public void close() throws Exception {

    	this.conn.close();
    	
    }

    
    /**
     * Converts a LocalDate object into a String that MySQL can understand
     * eg: STR_TO_DATE(01-01-1991)
     * @param d the LocalDate object we wish to convert to an SQL Date string
     * @return the String that can be fed into an SQL command
     */
    public String DateToString(LocalDate d) {
    
    	String sqlDateStr = "STR_TO_DATE('"+d.getDayOfMonth()+"-" 
			 	+ d.getMonthValue() + "-" 
			 	+ d.getYear() + "', '%d-%m-%Y')";
    	return sqlDateStr;
    	
    }
    
    /**
     * This method updates the DB according to the SQL query given in 
     * updateStr
     * @param updateStr, the SQL query we wish to run
     * @throws Exception , if DB update fails
     */
    public Object commitUpdate(String updateStr) throws Exception {
    	PreparedStatement pInsertOid = conn.prepareStatement(updateStr, Statement.RETURN_GENERATED_KEYS);
    	/*Statement stmt = conn.createStatement();
    	stmt.executeUpdate(updateStr);*/
    	
    	pInsertOid.executeUpdate();
    	ResultSet rs = pInsertOid.getGeneratedKeys();
    	if (rs.next()) {
    	  int newId = rs.getInt(1);
    	  return new Integer(newId);
    	}
    	
    	return null;
    }
    
    /**
     * Executes an SQL query and returns the results 
     * @param query The SQL query string you want
     * @return The set of results of that Query
     * @throws Exception If there is a problem running the query
     */
    public ResultSet executeQuery(String query) throws Exception {
    	Statement stmt =  conn.prepareStatement(query);
    	
    
    	ResultSet r = stmt.executeQuery(query);
    	return r;
    	
    }
	
    /**
     *  Creates a new SQLCommunicator object, and immediately connects to local database with JDBC
     * @param uname Database user name
     * @param pswd Database password
     * @param dbname Name of database we are interested in
     * @throws Exception If connection attempt fails
     */
	public SQLCommunicator(String uname, String pswd, String dbname) throws Exception {
		this.uname = uname;
		this.pswd = pswd;
		this.dbname = dbname;
		this.dbUrl = "jdbc:mysql://localhost/"+ dbname +"?"
	    		+ "autoReconnect=true&useSSL=false";
		this.connectSQL();
		
	}
	

}
