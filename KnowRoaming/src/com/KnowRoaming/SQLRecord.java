package com.KnowRoaming;

/**
 * Interface that defines behaviour for objects that correspond precisely to 
 * table entries in our DB.
 * @author Jonathan Eidelman
 *
 */
public interface SQLRecord {
	
	/**
	 * Creates a new row in the DB corresponding to the current SQLRecord instance
	 * this method should assume that there is no other row corresponding to the 
	 * primary key of this instance
	 * @throws Exception If the primary key of this instance is already in the DB
	 */
	public void commit() throws Exception;

}
