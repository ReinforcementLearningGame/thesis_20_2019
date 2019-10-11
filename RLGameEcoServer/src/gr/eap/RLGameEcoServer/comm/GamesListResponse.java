package gr.eap.RLGameEcoServer.comm;

import java.util.ArrayList;

import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;

public class GamesListResponse extends Response {
	private ArrayList<Game> gamesList;

	public GamesListResponse() {
		gamesList = GamesRegister.getInstance().getGamesList();
		this.setType("gr.eap.RLGameEcoServer.comm.GamesListResponse");
	}

	public GamesListResponse(ArrayList<Game> gamesList) {
		this.gamesList = gamesList;
		this.setType("gr.eap.RLGameEcoServer.comm.GamesListResponse");
	}
	
	public ArrayList<Game> getGamesList() {
		return gamesList;
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

}
