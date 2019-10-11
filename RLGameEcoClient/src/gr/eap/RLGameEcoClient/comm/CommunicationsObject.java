package gr.eap.RLGameEcoClient.comm;

import org.java_websocket.WebSocket;

public abstract class CommunicationsObject {
	private int userId;
//	private UUID gameId;
	private String type;
	private transient WebSocket socket;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
//	public UUID getGameId() {
//		return gameId;
//	}
//	public void setGameId(UUID gameId) {
//		this.gameId = gameId;
//	}
	public String getType() {
		return type;
	}
	//The type property should only be set by concrete subclasses
	protected void setType(String type) {
		this.type = type;
	}

	public WebSocket getSocket() {
		return socket;
	}


	public void setSocket(WebSocket socket) {
		this.socket = socket;
	}
	
	public void send(){
		JsonCommObjectSerializer js = new JsonCommObjectSerializer();
		String outco = js.serialize(this);
		if (this.getSocket().isOpen()){
			this.socket.send(outco);
		}
	}
}
