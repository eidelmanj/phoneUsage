package com.KnowRoaming;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;

public class Tests {
	
	private SQLCommunicator cleanDB() {
		SQLCommunicator sqlCom = null;
		try {
			sqlCom = new SQLCommunicator("root", "root", "knowroaming");
			// We must clear the test entry from the DB
			sqlCom.commitUpdate("DELETE FROM user_records WHERE email = 'check@test.com'"
					+ " OR email = 'newmail@test.com';");
			sqlCom.commitUpdate("DELETE FROM user_records WHERE email = 'check@test.com'"
					+ " OR email = 'newmail@test.com';");

		} catch (Exception e) {
			// Connecting to database should not fail
			e.printStackTrace();
			assert(false);

		}
		return sqlCom;
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	// TODO
	@Test
	public void testUsageRetrieval() {
		
	}
	
	// TODO
	@Test
	public void testUsageDateRule() {
		
	}

	@Test
	public void testNameRule() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test2", "check@test.com", "123-456", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();

		
	}
	
	@Test
	public void testEmailAtRule() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test", "checktest.com", "123-456", sqlCom);
		} catch(Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();
		
	}
	
	
	@Test
	public void testEmailDotRule() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();

		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test", "ch.eck@testcom", "123-456", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();

		
	}
	
	@Test
	public void testPhoneNumberRule() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();

		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test", "check@test.com", "123-a456", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		
		
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();
		
	}
	
	@Test
	public void testUserRecord() throws Exception {	  
		SQLCommunicator sqlCom;
		sqlCom = cleanDB();
		
		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test", "check@test.com", "123-456", sqlCom);
			uRecord.commitNew(); 
		}  catch(Exception e) {
			// There should not be any errors
			e.printStackTrace(); 
			assert(false);
		}
		
		
		try {
			UserRecord uRecordRetrieve = new UserRecord(uRecord.getUserId(), sqlCom);
			assertEquals(uRecordRetrieve.getEmail(), "check@test.com");
			assertEquals(uRecordRetrieve.getName(), "test");
			assertEquals(uRecordRetrieve.getPhoneNumber(), "123-456");
		} catch (Exception e) {
			// This should not cause an error
			e.printStackTrace();
			assert(false);
		}
		
		UserRecord uRecordDuplicate = new UserRecord("abc", "check@test.com", "5555", sqlCom);
		exception.expect(InvalidArgumentException.class);
		uRecordDuplicate.commitNew();

		
	}
}