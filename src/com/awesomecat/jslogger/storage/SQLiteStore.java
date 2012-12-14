package com.awesomecat.jslogger.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.awesomecat.jslogger.JavaScriptLogger;

public class SQLiteStore extends AbstractStore {
	Connection connection = null;
	public SQLiteStore() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException("Could not find org.sqlite.JDBC controller.");
		}

		try {
			// create a database connection
			connection = DriverManager
					.getConnection("jdbc:sqlite:"+JavaScriptLogger.getConfig().getString("databaseFile"));
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			statement.executeUpdate("CREATE TABLE if not exists Expressions "
					+ "(id INTEGER NOT NULL, " + "validDuration INTEGER, "
					+ "creationTimeE LONG, " + "expression TEXT(25), "
					+ "runOnce INTEGER, " + "windowSize INTEGER, "
					+ "PRIMARY KEY (id)) ");
			statement.executeUpdate("CREATE TABLE if not exists AssociatedId "
					+ "(associatedId VARCHAR(25) NOT NULL, " + "sessionId INTEGER, "
					+ "expressionId INTEGER, " + "creationTimeA LONG, "
					+ "PRIMARY KEY (associatedId)) ");
			statement.executeUpdate("CREATE TABLE if not exists Sessions "
					+ "(id INTEGER NOT NULL, " + "type INTEGER, "
					+ "value VARCHAR(50), " + "PRIMARY KEY (id)) ");
			statement.close();
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}

		deleteExpiredAssociatedIds();
	}

	@Override
	public Expression getExpression(int id) {
		ResultSet rs = null;
		int val_dur = 0;
		String express = "";
		boolean run_once = true;
		int run_once_int = 0;
		int wind_size = 2;
		try {
			Statement statement = connection.createStatement();
			rs = statement.executeQuery("SELECT * from Expressions "
					+ "WHERE id=" + id);
			while (rs.next()) {
				// read the result set
				val_dur = rs.getInt("validDuration");
				express = rs.getString("expression");
				run_once_int = rs.getInt("runOnce");
				wind_size = rs.getInt("windowSize");

			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		run_once = (run_once_int == 1) ? true : false;
		return new Expression(val_dur, express, run_once, wind_size);
	}

	@Override
	public int storeExpression(Expression expression) {
		int val_dur = expression.validDuration;
		String express = expression.expression;
		int run_once_int = (expression.runOnce == true) ? 1 : 0;
		int wind_size = expression.windowSize;
		int out_id = -999;
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * from Expressions WHERE "
					+ "validDuration=" + val_dur + " AND " + "expression="
					+ "'" + express + "'" + " AND " + "runOnce=" + run_once_int
					+ " AND " + "windowSize=" + wind_size);
			if (!rs.isBeforeFirst()) {
				statement.executeUpdate("INSERT into Expressions VALUES("
						+ "NULL" + "," + expression.validDuration + ","
						+ Expression.getCurrentTime() + "," + "'"
						+ expression.expression + "'" + "," + run_once_int
						+ "," + expression.windowSize + ")");
				rs.close();
				rs = null;
				rs = statement.executeQuery("SELECT last_insert_rowid() as id from Expressions");
				while (rs.next()) {
					out_id = rs.getInt("id");
				}
			} else {
				while (rs.next()) {
					out_id = rs.getInt("id");
					return out_id;
				}
			}
			rs.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out_id;
	}

	@Override
	public int getSessionId(SessionType type, String value) {
		int type_int = type.ordinal();
		int out_id = -999;
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * from Sessions WHERE "
					+ "type=" + type_int + " AND " + "value=" + "'" + value
					+ "'");
			if (!rs.isBeforeFirst()) {
				statement.executeUpdate("INSERT into Sessions VALUES(" + "NULL"
						+ "," + type_int + "," + "'" + value + "'" + ")");
				rs.close();
				rs = null;
				rs = statement.executeQuery("SELECT last_insert_rowid() as id from Sessions");
				while (rs.next()) {
					out_id = rs.getInt("id");
				}
			} else {
				while (rs.next()) {
					out_id = rs.getInt("id");
					return out_id;
				}
			}
			rs.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out_id;
	}

	@Override
	public void deleteExpiredAssociatedIds() {
		int validDuration, sessionId;
		long creationTime;
		String ass_id;
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * from AssociatedId LEFT JOIN Expressions" +
					" ON AssociatedId.expressionId=Expressions.id");
			while (rs.next()) {
				
				validDuration = rs.getInt("validDuration");
				creationTime = rs.getLong("creationTimeA");
				sessionId = rs.getInt("sessionId");
				ass_id = rs.getString("associatedId");
				if(sessionId != staticSessionId && validDuration < java.util.Calendar.getInstance().getTimeInMillis() - creationTime){
					deleteAssociatedId(ass_id);
				}
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAssociatedId(String id) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM AssociatedId " + "WHERE associatedId ="
					+ "'"+id+"'");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getAssociatedIds(int sessionId, int expressionId) {
		ResultSet rs = null;
		int windowSize = getWindowSize(expressionId);
		String[] s = new String[windowSize];
		int counter = 0;
		try {
			Statement statement = connection.createStatement();
			rs = statement.executeQuery("SELECT * from AssociatedId WHERE "
					+ "sessionId=" + sessionId + " AND " + "expressionId=" + expressionId);
			while (rs.next() && counter<windowSize) {
				s[counter++] = rs.getString("associatedId");
				if(counter == windowSize) return s;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] s2 = new String[counter];
		for(int i=0;i<counter;i++){
			s2[i] = s[i];
		}
		return s2;
	}

	@Override
	public String createAssociatedId(int sessionId, int expressionId) {
		// Clear a space if it's already taken
		// There is no space limit for static sessionId
		if(sessionId != staticSessionId && getAssociatedIds(sessionId, expressionId).length >= getWindowSize(expressionId)){
			deleteOldestAssociatedId(sessionId, expressionId);
		}

		String ass_id = generateAssociatedId();
		String out_string = ass_id;
		long creationTime = (sessionId == staticSessionId?Long.MAX_VALUE:java.util.Calendar.getInstance().getTimeInMillis());
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT into AssociatedId VALUES(" + "'"+ass_id+"'"
					+ "," + sessionId + "," + expressionId + ","+creationTime+")");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out_string;
	}

	@Override
	public void deleteOldestAssociatedId(int sessionId, int expressionId) {
		if(sessionId == staticSessionId) return;
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("delete from AssociatedId where " +
					"creationTimeA = (select min([creationTimeA]) from AssociatedId where sessionId = '"+sessionId+"' AND expressionId = '"+expressionId+"')" + 
					" AND sessionId = '"+sessionId+"' AND expressionId = '"+expressionId+"'");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Expression getExpressionFromAssociatedId(String associatedId) {
		return getExpression(getExpressionIdFromAssociatedId(associatedId));
	}

	@Override
	public int getExpressionIdFromAssociatedId(String associatedId) {
		int expr_id = -999;
		try {
			ResultSet rs = null;
			Statement statement = connection.createStatement();
			rs = statement.executeQuery("SELECT * from AssociatedId WHERE "
					+ "associatedId=" + "'" + associatedId + "'");;
			while (rs.next()) {
				 expr_id = rs.getInt("expressionId");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return expr_id;
	}

}