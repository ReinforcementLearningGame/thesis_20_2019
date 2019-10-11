package gr.eap.RLGameEcoClient.comm;

import java.util.UUID;

import org.rlgame.gameplay.GameState;

import gr.eap.RLGameEcoClient.Client;
import gr.eap.RLGameEcoClient.game.Move;
import gr.eap.RLGameEcoClient.player.Participant.Role;

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
		//When game state is deserialized, pawns are created without their boardSize and baseSize properties set
		//We will check if this is true and correct it
		if (getState().getWhitePawns()[0].getBoardSize() == 0){
			for (byte i = 0; i < Client.currentNumberOfPawns; i++){
				getState().getWhitePawns()[i].setBoardSize(Client.currentBoardSize);
				getState().getWhitePawns()[i].setBaseSize(Client.currentBaseSize);
				getState().getBlackPawns()[i].setBoardSize(Client.currentBoardSize);
				getState().getBlackPawns()[i].setBaseSize(Client.currentBaseSize);
			}
		}
		
		if (Client.joinRole != null && Client.joinRole == Role.SPECTATOR && Client.lastState == null) Client.lastState = getState(); 
		
		if (getState().isFinal()) {
			Client.machine.finishGameSession(state);
		} 
		else {
			if (getState().getTurn() == Client.machine.getId()){
				if (Client.joinRole != Role.SPECTATOR){
					
					Move pickedMove = Client.machine.pickMove(getState());
					
					MoveCommand mc = new MoveCommand();
					mc.setSocket(getSocket());
					mc.setPawnId(pickedMove.getPawn().getId());
					mc.setToXCoord(pickedMove.getToSquare().getXCoord());
					mc.setToYCoord(pickedMove.getToSquare().getYCoord());
					mc.setUserId(getUserId());
					mc.setGameUid(getGameUid());
					mc.send();
				} 
				else {
					Client.lastState = getState();
				}
			}
			else if (getState().getTurn() != Client.machine.getId() && (Client.joinRole == Role.SPECTATOR)){
				Move observedMove = getState().findMoveFromLastState(Client.lastState);
				Client.machine.pickMove(getState(), observedMove);
			}

		}
		
	}


}
