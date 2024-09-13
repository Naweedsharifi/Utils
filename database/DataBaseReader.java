package com.wbg.selenium.qa.database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.wbg.selenium.qa.configReader.ConfigFileReader;
import com.wbg.selenium.qa.configReader.Xlsx_FileReader;
import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.CreateResultFolderStructure;
import com.wbg.selenium.qa.utils.LoggerHelper;
import com.wbg.selenium.qa.utils.WebElementWrappers;

public class DataBaseReader extends WebDriverManager {
	Logger log = LoggerHelper.getLogger(DataBaseReader.class);
	ConfigFileReader filereader;
	Properties propW = new Properties();
	Xlsx_FileReader xlsx_FileReader = PageFactory.initElements(driver, Xlsx_FileReader.class);
	CreateResultFolderStructure createResultFolderStructure = PageFactory.initElements(driver,
			CreateResultFolderStructure.class);

	/* Get connection data from common data Sheet */
	private static String databaseURL = Xlsx_FileReader.excelreader.getCellData("CommonData", "DBConnection", 2);
	private static String dataBaseUserName = Xlsx_FileReader.excelreader.getCellData("CommonData", "DBUserName", 2);
	private static String dataBasePassword = Xlsx_FileReader.excelreader.getCellData("CommonData", "DBPassword", 2);
	
	/*
	 * @description : Method to get Connection
	 * 
	 * @param : NA
	 * 
	 * @return : database connection
	 * 
	 * @date : 27 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static Connection getConnection() {
		Connection connection = null;
		/* Create connection */
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(databaseURL, dataBaseUserName, dataBasePassword);
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
		if (connection != null) {
			WebElementWrappers.Reporter(driver,
					"Verify that the connection is established for the database, Connection should be established for the database, Connection is established for the database ,Pass");
		} else {
			WebElementWrappers.Reporter(driver,
					"Verify that the connection is established for the database, Connection should be established for the database, Connection is not established for the database,Fail");
		}
		Assert.assertEquals(connection != null, true, "Verify that the connection is established for the database");
		return connection;
	}

	/*
	 * @description : Method to get inbound data
	 * 
	 * @param : database query
	 * 
	 * @return : NA
	 * 
	 * @date : 27 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void inboundValidation(String query, String strPath) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement stmt = null;
		try {
			/* fetch data from database */
			String filePath = strPath + "/Inbound.txt";
			boolean strFile = CreateResultFolderStructure.createFile(filePath);
			connection = getConnection();
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for inbound validation,The database query fetched data from the database for inbound validation, The database query fetched data from the database for inbound validation ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for inbound validation,The database query fetched data from the database for inbound validation, The database query not fetched data from the database for inbound validation,Fail");
			}
			Assert.assertEquals(resultSet != null, true,
					"Verify thate the database query fetched data from the database for inbound validation");
			while (resultSet.next()) {
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(resultSet.getString(3));
				bufferedWriter.newLine();
				bufferedWriter.close();
				if (resultSet.getString(3) != null && strFile) {
					WebElementWrappers.Reporter(driver,
							"Verify that the inbound data is fetched from database and stored in a file,The inbound data should be fetched from database and stored in a file, The inbound data is fetched from database and stored in a file ,Pass");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify that the inbound data is fetched from database and stored in a file,The inbound data should be fetched from database and stored in a file, The inbound data is not fetched from database and not stored in a file,Fail");
				}
				Assert.assertEquals(resultSet.getString(3) != null && strFile, true,
						"Verify that the inbound data is fetched from database and stored in a file");
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			// connection.close();
			methodToCloseThemAll(resultSet, stmt, connection);

		}
	}


	/*
	 * @description : Method to verify MqInq record in DB
	 * 
	 * @param : query
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyMQINQrecordinDB(String query) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement stmt = null;
		try {
			/* verify acknowledgement deal creation */
			connection = getConnection();
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify  the acknowledgement of Deal Creation Quantum for database, Deal acknowledgement should be created for Quantum database, Deal acknowledgement is created for Quantum database ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify  the acknowledgement of Deal Creation Quantum for database, Deal acknowledgement should be created for Quantum database, Deal acknowledgement is not created for Quantum database,Fail");
			}
			Assert.assertEquals(resultSet != null, true,
					"Verify the acknowledgement of Deal Creation Quantum for database");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			connection.close();
		}
	}

	/*
	 * @description : Method to verify CSIP record in DB
	 * 
	 * @param : query
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyCSIPrecordinDB(String query) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is fetched from the database ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is not fetched from the database ,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "Verify that the data is fetched from the database");

			while (resultSet.next()) {
				if (resultSet.getString(1).startsWith("<APACK")) {
					WebElementWrappers.Reporter(driver,
							"Verify the data 'APACK' is validated against the queried data, Data should be validated against the queried data ,Data is validated against the queried data,Pass");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify the data 'APACK' is validated against the queried data, Data should be validated against the queried data ,Data is not validated against the queried data,Fail");
				}
				Assert.assertEquals(resultSet.getString(1).startsWith("<APACK"), true,
						"Verify the data is validated against the queried data");

			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			methodToCloseThemAll(resultSet, stmt, connection);
		}
	}

	/*
	 * @description : Method to verify MqInq record in SAM
	 * 
	 * @param : query,mQINQMsgTypeDealReleaseinQuantum
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyMQINQrecordinDBSAM(String query, String mQINQMsgTypeDealReleaseinQuantum)
			throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is fetched from the database ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is not fetched from the database ,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "Verify that the data is fetched from the database");

			while (resultSet.next()) {
				if (resultSet.getString(1).equals(mQINQMsgTypeDealReleaseinQuantum)) {
					WebElementWrappers.Reporter(driver,"Verify the data Message Type is validated against the queried data :"
							+ mQINQMsgTypeDealReleaseinQuantum
							+ ", Data should be validated against the queried data ,Data is validated against the queried data,Pass");
				} else {
					WebElementWrappers.Reporter(driver,"Verify the data Message Type is validated against the queried data :"
							+ mQINQMsgTypeDealReleaseinQuantum
							+ ", Data should be validated against the queried data ,Data is not validated against the queried data,Fail");
				}

				Assert.assertEquals(resultSet.getString(1).equals(mQINQMsgTypeDealReleaseinQuantum), true,
						"Verify the data is validated against the queried data");
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			methodToCloseThemAll(resultSet, stmt, connection);
		}
	}

	/*
	 * @description : Method to verify CSIP record deal in SAM
	 * 
	 * @param : query
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyCSIPrecordinDBDealinSAM(String query) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is fetched from the database ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is fetched from the database, Data should be fetched from the database,Data is not fetched from the database ,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "Verify that the data is fetched from the database");

			while (resultSet.next()) {
				if (resultSet.getString(1).startsWith("<APACK")) {
					WebElementWrappers.Reporter(driver,
							"Verify the data 'APACK' is validated against the queried data, Data should be validated against the queried data ,Data is validated against the queried data,Pass");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify the data 'APACK' is validated against the queried data, Data should be validated against the queried data ,Data is not validated against the queried data,Fail");
				}
				Assert.assertEquals(resultSet.getString(1).startsWith("<APACK"), true,
						"Verify the data is validated against the queried data");
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			methodToCloseThemAll(resultSet, stmt, connection);
		}
	}

	public static void methodToCloseThemAll(ResultSet resultSet, Statement statement, Connection connection) {
		if (resultSet != null) {
			try {
				if (!resultSet.isClosed()) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				if (!statement.isClosed()) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * @description : Method to get mqOutQ data
	 * @param  : query 
	 * @return : NA
	 * @date   : 27 Dec	2020
	 * @author : Infosys Limited
	 */
	public static void mqOutQDBValidation(String query ,String strPath) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = getConnection();
			Statement stmt = connection.createStatement();		
			resultSet = stmt.executeQuery(query+" And MQAPPINF_RCVAPPCOD='AGQT' ORDER BY MQAPPINF_RCVAPPCOD DESC");
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query fetched data from the database for mqOutQ validation ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query not fetched data from the database for mqOutQ validation,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "The database query fetched data from the database for mqOutQ validation");
			
			while (resultSet.next()) {				
				String filePath =strPath+"/MqOutQ.txt";
				boolean strFile = CreateResultFolderStructure.createFile(filePath);
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(resultSet.getString(1));
				bufferedWriter.newLine();
				bufferedWriter.close();
				if (resultSet.getString(1) != null && strFile) {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is fetched from database and stored in a file ,Pass");
					Assert.assertEquals(resultSet.getString(1) != null && strFile, true,
							"The MQOUTQ data is fetched from database and stored in the file");
					break;
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is not fetched from database and not stored in a file,Fail");
					Assert.fail();
				}
				
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			connection.close();
		}
	}
	
	/*
	 * @description : Method to get mqOutQ data for swiftonly
	 * @param  : query 
	 * @return : NA
	 * @date   : 27 Dec	2020
	 * @author : Infosys Limited
	 */
	public static void mqOutQDBValidationSwiftOnly(String query ,String strPath) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = getConnection();
			Statement stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query fetched data from the database for mqOutQ validation ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query not fetched data from the database for mqOutQ validation,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "The database query fetched data from the database for mqOutQ validation");
			while (resultSet.next()) {				
				String filePath =strPath+"/MqOutQ.txt";
				boolean strFile = CreateResultFolderStructure.createFile(filePath);
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(resultSet.getString(1));
				bufferedWriter.newLine();
				bufferedWriter.close();
				if (resultSet.getString(1) != null && strFile) {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is fetched from database and stored in a file ,Pass");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is not fetched from database and not stored in a file,Fail");
				}
				Assert.assertEquals(resultSet.getString(1) != null && strFile, true,
						"The MQOUTQ data is fetched from database and stored in the file");
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			connection.close();
		}
	}
	
	/*
	 * @description : Method to get mqOutQ data
	 * @param  : query 
	 * @return : NA
	 * @date   : 27 Dec	2020
	 * @author : Infosys Limited
	 */
	public static void mqOutQDBValidation304(String query ,String strPath) throws SQLException, IOException {
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = getConnection();
			Statement stmt = connection.createStatement();		
			resultSet = stmt.executeQuery(query+" And MQAPPINF_RCVAPPCOD='AGQT' ORDER BY MQAPPINF_RCVAPPCOD DESC");
			if (resultSet != null) {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query fetched data from the database for mqOutQ validation ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify thate the database query fetched data from the database for mqOutQ validation,The database query fetched data from the database for mqOutQ validation, The database query not fetched data from the database for mqOutQ validation,Fail");
			}
			Assert.assertEquals(resultSet != null, true, "The database query fetched data from the database for mqOutQ validation");
			int count=1;
			while (resultSet.next()) {				
				String filePath =strPath+"/MqOutQ"+count+".txt";
				boolean strFile = CreateResultFolderStructure.createFile(filePath);
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(resultSet.getString(1));
				bufferedWriter.newLine();
				bufferedWriter.close();
				count++;
				if (resultSet.getString(1) != null && strFile) {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is fetched from database and stored in a file ,Pass");
					Assert.assertEquals(resultSet.getString(1) != null && strFile, true,
							"The MQOUTQ data is fetched from database and stored in the file");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify that the MQOUTQ data is fetched from database and stored in a file,The MQOUTQ data should be fetched from database and stored in a file, The MQOUTQ data is not fetched from database and not stored in a file,Fail");
					Assert.fail();
				}
				
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		} finally {
			connection.close();
		}
	}

}
