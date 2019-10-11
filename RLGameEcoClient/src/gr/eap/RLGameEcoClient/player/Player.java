package gr.eap.RLGameEcoClient.player;


import org.java_websocket.WebSocket;

import gr.eap.RLGameEcoClient.comm.ConnectionState;


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
		this.score = score;
	}
	
	public boolean isHuman() {
		return isHuman;
	}
	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
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
