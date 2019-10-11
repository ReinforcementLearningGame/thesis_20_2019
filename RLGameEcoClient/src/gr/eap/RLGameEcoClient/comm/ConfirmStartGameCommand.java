package gr.eap.RLGameEcoClient.comm;

import java.util.UUID;


public class ConfirmStartGameCommand extends Command {

	UUID gameUid;
	
	UUID getGameUid() {
		return gameUid;
	}

	void setGameUid(UUID gameUid) {
		this.gameUid = gameUid;
	}
	
	public ConfirmStartGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.ConfirmStartGameCommand");
	}


}
