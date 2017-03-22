package com.KnowRoaming;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SQLCommunicator {
	private String uname;
	private String pswd;
	private String dbname;
	private String dbUrl;
	
	 // JDBC driver name and database URL                                                                                                                                                                    
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/feedback?"
    		+ "autoReconnect=true&useSSL=false";
    Connection conn = null;
   
    private void connectSQL() {	
    	try{
    		Connection conn = null;
    		Statement stmt = null;
    		//STEP 2: Register JDBC driver                                                                                                                                                                  
  	  		Class.forName("com.mysql.jdbc.Driver");

  	  		//STEP 3: Open a connection                                                                                                                                                                     
  	  		this.conn = DriverManager.getConnection(this.dbUrl, this.uname, this.pswd);
    	}  catch(SQLException se){
        	//Handle errors for JDBC                                                                                                                                                                        
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName                                                                                                                                                               
            e.printStackTrace();
        }
    }
    
    public void close() throws Exception {

    	this.conn.close();
    	
    }

    // Converts a LocalDate object into a String that MySQL can understand
    public String DateToString(LocalDate d) {
    
    	String sqlDateStr = "STR_TO_DATE('"+d.getDayOfMonth()+"-" 
			 	+ d.getMonthValue() + "-" 
			 	+ d.getYear() + "', '%d-%m-%Y')";
    	return sqlDateStr;
    	
    }
    
    public void commitUpdate(String updateStr) throws Exception {
    	Statement stmt = conn.createStatement();
    	stmt.executeUpdate(updateStr);
    }
    
    public ResultSet executeQuery(String query) throws Exception {
    	Statement stmt =  conn.prepareStatement(query);
    	
    
    	ResultSet r = stmt.executeQuery(query);
    	return r;
    	
    }
	
	public SQLCommunicator(String uname, String pswd, String dbname) throws Exception {
		this.uname = uname;
		this.pswd = pswd;
		this.dbname = dbname;
		this.dbUrl = "jdbc:mysql://localhost/"+ dbname +"?"
	    		+ "autoReconnect=true&useSSL=false";
		this.connectSQL();
		
	}
	

}
