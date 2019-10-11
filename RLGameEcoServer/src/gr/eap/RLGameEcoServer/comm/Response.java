package gr.eap.RLGameEcoServer.comm;

public abstract class Response extends CommunicationsObject {
	private int commandID;
//	private ArrayList<String> availableCommands;
//	private GameState gameState;
	private ConnectionState connectionState;

	public int getCommandID() {
		return commandID;
	}

	public void setCommandID(int commandID) {
		this.commandID = commandID;
	}

	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}
	
	public abstract void process();

}
