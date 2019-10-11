package gr.eap.RLGameEcoServer.comm;

import java.util.UUID;

import org.rlgame.gameplay.GameState;

public class GameStateResponse extends Response {
	private GameState state;
	private UUID gameUid;
	
	public UUID getGameUid() {
		return gameUid;
	}

	public GameState getState() {
		return state;
	}
	
	public GameStateResponse(GameState state, UUID gameUid) {
		this.state = state;
		this.gameUid = gameUid;
		this.setType("gr.eap.RLGameEcoServer.comm.GameStateResponse");
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}


}
