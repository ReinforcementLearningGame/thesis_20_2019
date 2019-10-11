package gr.eap.RLGameEcoServer.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.java_websocket.WebSocket;
import gr.eap.RLGameEcoServer.comm.ConnectionState;
import gr.eap.RLGameEcoServer.db.MySQLHelper;
import gr.eap.RLGameEcoServer.db.MySQLHelper.parameterValue;

public abstract class Player {
	private String userName;
	private transient String password;
	private transient WebSocket connection;
	private ConnectionState connectionState;
	private int score;
	private int id;
	private String name;
	private boolean isHuman;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public WebSocket getConnection() {
		return connection;
	}

	public void setConnection(WebSocket connection) {
		this.connection = connection;
	}

	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		//Everytime the score changes, the database should be updated
		if (this.score != score){
			this.score = score;
			writeScore();
		}
	}

	
	private void writeScore() {
		ArrayList<parameterValue<?>> params = new ArrayList<MySQLHelper.parameterValue<?>>();
		params.add(new MySQLHelper.parameterValue<Integer>(score));
		params.add(new MySQLHelper.parameterValue<Integer>(id));
	
		MySQLHelper.getInstance().exec("Update Players set Score = ? where ID = ?", params);
		
		PlayersRegister.getInstance().sendPlayersList();
		
	}
	public boolean isHuman() {
		return isHuman;
	}
	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}
	
	
	public static Player getPlayer(int id) {
		// Gets the player
		ArrayList<parameterValue<?>> params = new ArrayList<MySQLHelper.parameterValue<?>>();
		params.add(new MySQLHelper.parameterValue<Integer>(id));
		return getPlayer("players.ID = ?", params);
	}

	public static Player getPlayer(String userName, String password) {
		ArrayList<parameterValue<?>> params = new ArrayList<MySQLHelper.parameterValue<?>>();
		params.add(new MySQLHelper.parameterValue<String>(userName));
		params.add(new MySQLHelper.parameterValue<String>(password));
		return getPlayer("players.Username = ? and players.Password = ?", params);

	}

	private static Player getPlayer(String sqlCondition, ArrayList<parameterValue<?>> params) {
		Player newPlayer = null;
		try {
			String sqlString = "SELECT" + "	players.ID player_ID," + "	players.Name player_Name,"
					+ "	players.Username player_Username," + "	players.Password player_Password,"
					+ "	players.Is_Human player_Is_Human," + "players.Score player_Score," + "	avatar.ID avatar_ID,"
					+ "	avatar.Name avatar_name," + "	avatar.Username avatar_Username,"
					+ "	avatar.Password avatar_Password" + " FROM" + "	players left outer JOIN"
					+ "	players avatar ON" + "		players.ID = avatar.Owner_Player_ID" + " WHERE " + sqlCondition;
			ResultSet rs = MySQLHelper.getInstance().query(sqlString, params);
			if (rs != null && rs.next()) {
				boolean isHuman = rs.getBoolean("player_Is_Human");
				if (isHuman)
					newPlayer = new Member();
				else
					newPlayer = new Avatar();

				newPlayer.setHuman(isHuman);
				newPlayer.setId(rs.getInt("player_ID"));
				newPlayer.setName(rs.getString("player_Name"));
				newPlayer.setUserName(rs.getString("player_Username"));
				newPlayer.setPassword(rs.getString("player_Password"));
				newPlayer.setScore(rs.getInt("player_Score"));
				if (isHuman && rs.getObject("avatar_ID") != null) {
					Avatar newPlayerAvatar = new Avatar();
					newPlayerAvatar.setId(rs.getInt("avatar_ID"));
					newPlayerAvatar.setName(rs.getString("avatar_Name"));
					newPlayerAvatar.setUserName(rs.getString("avatar_Username"));
					newPlayerAvatar.setPassword(rs.getString("avatar_Password"));
					((Member) newPlayer).setAvatar(newPlayerAvatar);
				}

			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return newPlayer;
	}

	// The player's unique id is enough. It will be used for the Players
	// Register
	@Override
	public int hashCode() {
		return getId();
	}

	// Equal IDs should be enough for equal objects
	@Override
	public boolean equals(Object object) {
		return (((Player) object).getId() == getId());
	}

}
