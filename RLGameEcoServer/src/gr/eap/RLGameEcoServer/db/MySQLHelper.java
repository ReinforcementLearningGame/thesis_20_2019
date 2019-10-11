package gr.eap.RLGameEcoServer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MySQLHelper {
	private static MySQLHelper __me = null;

	private MySQLHelper() {
	}

	private static String dbLocation;
	private static String userName;
	private static String password;
	private static Connection connection;

	public void initializeConnectionParameters(String dbLocation, String userName, String password) {
		MySQLHelper.dbLocation = dbLocation;
		MySQLHelper.userName = userName;
		MySQLHelper.password = password;
	}

	public static MySQLHelper getInstance() {
		if (__me == null)
			__me = new MySQLHelper();
		return __me;
	}

	private Connection getConnection() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(MySQLHelper.dbLocation, MySQLHelper.userName,
						MySQLHelper.password);
				//String connectionString = String.format("mysql://1$:2$@localhost/mydb?autoReconnect=true", args)
				
			} catch (SQLException e) {
				System.err.println("Connect: " + e);
				return null;
			}
		}
		return connection;
	}

	public static class parameterValue<T> {
		public T value;

		public parameterValue(T value) {
			this.value = value;
		}
	}

	public ResultSet query(String SQLString, List<parameterValue<?>> parameters) {
		Connection conn = getConnection();
		ResultSet rs = null;
		int count = 0;
		while (count<2){
			
			try {
				PreparedStatement statement = conn.prepareStatement(SQLString, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY); //Statement doesn't get destroyed after execution. TODO Check for potential memory leak
				for (int i = 0; i < parameters.size(); i++) {
					statement.setObject(i + 1, parameters.get(i).value);
				}
	
				rs = statement.executeQuery();
				count=2;
			} catch (Exception e) {
				if (e.getMessage().contains("The last packet successfully received from the server was"))
					count++;
				else
					count=2;
				
				System.err.println("Query: " + e.getMessage());
			} 
		}
		return rs;
	}
	public boolean exec(String SQLString, List<parameterValue<?>> parameters) {
		Connection conn = getConnection();
		boolean rs = false;
		int count = 0;
		while (count<2){
			
			try {
				PreparedStatement statement = conn.prepareStatement(SQLString, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY); //Statement doesn't get destroyed after execution. TODO Check for potential memory leak
				for (int i = 0; i < parameters.size(); i++) {
					statement.setObject(i + 1, parameters.get(i).value);
				}
	
				rs = statement.execute();
				count=2;
			} catch (Exception e) {
				if (e.getMessage().contains("The last packet successfully received from the server was"))
					count++;
				else
					count=2;
				
				System.err.println("Query: " + e.getMessage());
			} 
			// finally {
			// if (rs != null)
			// try {
			// rs.close();
			// } catch (SQLException e) {
			// System.err.println(e.getMessage());
			// }
			// }
		}
		return rs;
	}
}
