package gr.eap.RLGameEcoClient.comm;

import java.util.UUID;

import gr.eap.RLGameEcoClient.player.Participant;

public class JoinGameCommand extends Command {

	Participant.Role role;
	UUID gameUid;
	
	UUID getGameUid() {
		return gameUid;
	}

	void setGameUid(UUID gameUid) {
		this.gameUid = gameUid;
	}

	Participant.Role getRole() {
		return role;
	}

	void setRole(Participant.Role role) {
		this.role = role;
	}

	public JoinGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.JoinGameCommand");
	}


}
