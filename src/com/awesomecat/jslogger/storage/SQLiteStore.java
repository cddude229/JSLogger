package com.awesomecat.jslogger.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: @Aaron: Implement SQLiteStore
public class SQLiteStore extends AbstractStore {
	Connection connection;
	Statement statement;
	public static void main(String[] args){
		SQLiteStore sqliteStore = null;
		try {
			sqliteStore = new SQLiteStore();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Expression expression = new Expression();
		sqliteStore.storeExpression(expression);
	}
	public SQLiteStore() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
	    
	    Connection connection = null;
	    try
	    {
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:./mydatabase.db");
	      statement = connection.createStatement();
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.
	      statement.executeUpdate(
	    		  "CREATE TABLE if not exists Expressions " +
	    		  "(id INTEGER NOT NULL, " +
	    		  "validDuration INTEGER, " +
	    		  "creationTime INTEGER, " +
	    		  "expression TEXT(25), " +
	    		  "runOnce INTEGER, " +
	    		  "windowSize INTEGER, " +
	    		  "PRIMARY KEY (id)) "
	    		  );
	      statement.executeUpdate(
	    		  "CREATE TABLE if not exists AssociatedId " +
	    		  "(id VARCHAR(25) NOT NULL, " +
	    		  "sessionId INTEGER, " +
	    		  "expressionId INTEGER, " +
	    		  "creationTime INTEGER, " +
	    		  "PRIMARY KEY (id)) "
	    		  );
	      statement.executeUpdate(
	    		  "CREATE TABLE if not exists Sessions " +
	    		  "(id INTEGER NOT NULL, " +
	    		  "type INTEGER, " +
	    		  "value VARCHAR(50), " +
	    		  "PRIMARY KEY (id)) "
	    		  );
	      deleteExpiredAssociatedIds();
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
//	    finally
//	    {
//	      try
//	      {
//	        if(connection != null)
//	          connection.close();
//	      }
//	      catch(SQLException e)
//	      {
//	        // connection close failed.
//	        System.err.println(e);
//	      }
//	    }
	}
	
	@Override
	public Expression getExpression(int id) {
		ResultSet rs = null;
		try {
			rs = statement.executeQuery("SELECT * from Expressions "+ 
						"WHERE id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int val_dur = 0;
		String express = "";
		boolean run_once = true;
		int run_once_int = 0;
		int wind_size = 2;
	      try {
			while(rs.next())
			  {
			    // read the result set
				val_dur = rs.getInt("validDuration");
				express = rs.getString("expression");
				run_once_int = rs.getInt("runOnce");
				wind_size = rs.getInt("windowSize");
			  }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	      run_once = (run_once_int == 1)?true:false; 
		return new Expression(val_dur, express, run_once, wind_size);
	}

	@Override
	public int storeExpression(Expression expression) {
		int run_once_int = (expression.runOnce==true)?1:0;
		try {
			statement.executeUpdate("INSERT into Expressions VALUES(" +
					expression.validDuration+"," +
					expression.getCurrentTime()+"," +
					"'"+expression.expression+"'"+"," +
					run_once_int+"," +
					expression.windowSize+"," +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSessionId(SessionType type, String value) {
		return 0;
	}

	@Override
	public void deleteExpiredAssociatedIds() {
	}

	@Override
	public void deleteAssociatedId(String id) {
	}

	@Override
	public String[] getAssociatedIds(int sessionId, int expressionId) {
		return null;
	}

	@Override
	public String createAssociatedId(int sessionId, int expressionId) {
		return null;
	}

	@Override
	public void deleteOldestAssociatedId(int sessionId, int expressionId) {
	}

	@Override
	public Expression getExpressionFromAssociatedId(String associatedId) {
		return null;
	}

	@Override
	public int getExpressionIdFromAssociatedId(String associatedId) {
		return -1;
	}

}