package gr.eap.RLGameEcoClient.comm;

import java.util.UUID;


public class LeaveGameCommand extends Command {
	private UUID gameUid;
	
	public UUID getGameUid() {
		return gameUid;
	}

//	private void setGameUid(UUID gameUid) {
//		this.gameUid = gameUid;
//	}
	
	public LeaveGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.LeaveGameCommand");
	}


}
