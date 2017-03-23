package com.KnowRoaming;

/**
 * Interface that defines behaviour for objects that correspond precisely to 
 * table entries in our DB.
 * @author Jonathan Eidelman
 *
 */
public interface SQLRecord <T>{
	
	/**
	 * Creates a new row in the DB corresponding to the current SQLRecord instance
	 * this method should assume that there is no other row corresponding to the 
	 * primary key of this instance
	 * @throws Exception If the data in the object instance is not valid
	 * @throws Exception If the primary key of this instance is already in the DB
	 */
	public void commitNew() throws Exception;
	
	
	/**
	 * Updates an existing row in the DB to match the current object.
	 * Updates whichever row is associated with the unique primary key
	 * in the object
	 * @throws Exception If the data in the object instance is not valid
	 * @throws Exception If there is no entry in the DB matching the primary key
	 *                   recorded in the instance
	 */
	public void commitUpdate() throws Exception;
	
	/**
	 * Fills the current instance with data to match a record in the DB
	 * record is identified by primary key
	 * @param key The String version of the primary key we are looking for
	 * @throws NonExistentRecordException If desired record does not exist in DB
	 * @throws Exception If there is an error executing query
	 */
	public void constructFromPrimaryKey(T key) throws NonExistentRecordException, Exception;

}
