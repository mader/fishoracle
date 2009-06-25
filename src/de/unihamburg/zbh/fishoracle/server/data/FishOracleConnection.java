package de.unihamburg.zbh.fishoracle.server.data;

import java.sql.*;

public class FishOracleConnection {

	public static Connection connect() throws Exception {
		String url = "jdbc:mysql://localhost/oracle";
		String userName = "fouser";
		String password = "fish4me";
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		return (DriverManager.getConnection(url,userName,password));
	}
	
	
	public static String getErrorMessage(Exception e) {
		StringBuffer s = new StringBuffer();
		if (e instanceof SQLException){
			s.append("Error message: " + e.getMessage() + "\n");
			s.append("Error code: " + ((SQLException) e).getErrorCode() + "\n");
		} else {
			s.append(e + "\n");
		}
		return (s.toString());
	}
	
	public static void printErrorMessage(Exception e){
		System.err.println(FishOracleConnection.getErrorMessage(e));
	}
}
