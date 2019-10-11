package gr.eap.RLGameEcoServer.comm;

import java.util.ArrayList;
import java.util.List;
import org.java_websocket.WebSocket;
import gr.eap.RLGameEcoServer.player.Member;
import gr.eap.RLGameEcoServer.player.Player;

public class Message {
	public enum Type {
		SYSTEM_INFO, SYSTEM_WARNING, SYSTEM_ALERT, SYSTEM_INTERNAL, USER_BROADCAST, USER_PERSONAL, USER_GAME, USER_TEAM
	}

	private String text = null;
	private Type type = null;
	private Player sender = null;
	private List<Player> recipients = new ArrayList<Player>();
	private transient WebSocket socket = null;


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	public Player getSender() {
		return sender;
	}


	public void setSender(Player sender) {
		this.sender = sender;
	}


	public List<Player> getRecipients() {
		return recipients;
	}



	public void setRecipients(List<Player> recipients) {
		this.recipients = recipients;
	}


	public WebSocket getSocket() {
		return socket;
	}


	public void setSocket(WebSocket socket) {
		this.socket = socket;
	}


	public Message() {
	}

	
	//Standard message send by a player to another player
	public Message(String text, Type type, Player sender, Player recipient) {
		this.text = text;
		this.type = type;
		this.sender = sender;
		this.recipients.add(recipient);
	}

	//Message sent by the system to a player
	public Message(String text, Type type, Player recipient) {
		this.text = text;
		this.type = type;
		this.recipients.add(recipient);
	}
	
	//Message sent by the system to a connection (non logged in client). A player cannot send such message.
	public Message(String text, Type type, WebSocket socket) {
		this.text = text;
		this.type = type;
		this.socket = socket;
	}

	
	public void send() {
		send(-1);
	}

	public void send(int commandId) {
		if ((type.equals(Type.SYSTEM_INTERNAL) || text != null && !text.isEmpty()) && type != null && (!recipients.isEmpty() || socket != null)) {
			
			MessageResponse response = new MessageResponse();
			if (commandId>0) response.setCommandID(commandId);
			response.setMessage(this);
			if (!recipients.isEmpty()){
				for (Player recipient : recipients){
					response.setConnectionState(recipient.getConnectionState());
					response.setSocket(recipient.getConnection());
					response.setUserId(recipient.getId());
					if (recipient.isHuman() && ((Member)recipient).getAvatar() != null) response.setAvatarId(((Member)recipient).getAvatar().getId());
					response.setConnectionState(recipient.getConnectionState());
					response.send();
				}
			}
			else
			{
				response.setConnectionState(ConnectionState.CONNECTED); // if there is no recipient, means that the system sends message to a connected client
				response.setSocket(socket);
				response.send();
			}
		}

	}
}
