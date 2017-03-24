package com.KnowRoaming;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Rule;

public class Tests {
	final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private SQLCommunicator cleanDB() {
		SQLCommunicator sqlCom = null;
		try {
			sqlCom = new SQLCommunicator("root", "root", "knowroaming");
			// We must clear the test entry from the DB

			sqlCom.commitUpdate("DELETE usage_records FROM usage_records INNER JOIN user_records"
					+ " ON usage_records.user_ID = user_records.unique_id WHERE user_records.email = 'check@test.com'");
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
		
		SQLCommunicator sqlCom = cleanDB();
		UserRecord userRecord;
		UsageRecord usageRecord, newUsageRecord;
		
		LocalDate testDate = LocalDate.parse("01-01-1991", DATE_FORMAT);
		LocalDate newTestDate = LocalDate.parse("02-02-1992", DATE_FORMAT);
		
		
		try {
			userRecord = new UserRecord("test", "check@test.com", "12345", sqlCom);
			userRecord.commitNew();
			usageRecord = new UsageRecord(userRecord.getUserId(), testDate, "DATA", sqlCom);
			usageRecord.commitNew();
			
			usageRecord = new UsageRecord(userRecord.getUserId(), newTestDate, "SMS", sqlCom);
			usageRecord.commitNew();
			
			ArrayList<UsageRecord> recList = userRecord.findAllRecords(testDate, newTestDate);
			assertEquals(recList.size(), 2);
			
			recList = userRecord.findAllRecords(testDate, newTestDate.minusDays(1));
			assertEquals(recList.size(), 1);
			assertEquals(recList.get(0).getDataType(), "DATA");
			
			
		} catch(Exception e) {
			e.printStackTrace();
			assert(false);
		}
		
	}

	
	@Test
	public void testUsageRecordUpdate() {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord userRecord;
		UsageRecord usageRecord, newUsageRecord;
		
		LocalDate testDate = LocalDate.parse("01-01-1991", DATE_FORMAT);
		LocalDate newTestDate = LocalDate.parse("02-02-1992", DATE_FORMAT);
		
		
		try {
			userRecord = new UserRecord("test", "check@test.com", "12345", sqlCom);
			userRecord.commitNew();
			usageRecord = new UsageRecord(userRecord.getUserId(), testDate, "DATA", sqlCom);
			usageRecord.commitNew();
			usageRecord.setDataType("SMS");
			usageRecord.setTimeStamp(newTestDate);
			usageRecord.commitUpdate();
			newUsageRecord = new UsageRecord(sqlCom);
			newUsageRecord.constructFromPrimaryKey(usageRecord.getPrimaryKey());
			assertEquals(newUsageRecord.getDataType(), "SMS");
			assertEquals(newUsageRecord.getTimeStamp(), newTestDate);
			
		} catch(Exception e) {
			e.printStackTrace();
			assert(false);
		}
		
		
		
		
	} 
	
	
	@Test
	public void testEmailLength() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord  uRecord = null;

		try {
			uRecord = new UserRecord("aa", "aaa@aa.comm"
					+ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm", "11", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();
	}
	
	
	@Test
	public void testPhoneNumberLength() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord  uRecord = null;

		try {
			uRecord = new UserRecord("aa", "check@test.com", "111111111111111111111111111111111111111111111111111111111111", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();
	}
	
	@Test
	public void testNameLength() throws InvalidArgumentException {
		SQLCommunicator sqlCom = cleanDB();
		UserRecord  uRecord = null;

		try {
			uRecord = new UserRecord("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "check@test.com", "123-456", sqlCom);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		exception.expect(InvalidArgumentException.class);
		uRecord.validate();
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
	
	@Test
	public void testUserRecordUpdate() {
		SQLCommunicator sqlCom;
		sqlCom = cleanDB();
		
		UserRecord uRecord = null;
		try {
			uRecord = new UserRecord("test", "check@test.com", "123-456", sqlCom);
			uRecord.commitNew(); 
			String id = uRecord.getUserId();
			uRecord.setName("testtwo");
			uRecord.setPhoneNumber("122222");
			uRecord.commitUpdate();
			
			uRecord = new UserRecord(id, sqlCom);
			assertEquals(uRecord.getName(), "testtwo");
			assertEquals(uRecord.getPhoneNumber(), "122222");
			
		}  catch(Exception e) {
			// There should not be any errors
			e.printStackTrace(); 
			assert(false);
		}
	}
}