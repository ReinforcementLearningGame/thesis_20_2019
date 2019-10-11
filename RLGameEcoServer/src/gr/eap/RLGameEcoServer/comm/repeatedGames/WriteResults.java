package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.UUID;

import org.rlgame.gameplay.Settings;
import gr.eap.RLGameEcoServer.extra.FileManagement;
import gr.eap.RLGameEcoServer.game.GamesRegister;

public class WriteResults implements Runnable{
	
	private UUID uid;
	private int gameID;
	private String filename;
	
	public WriteResults(UUID uid, int gameID, String filename) {
		this.uid = uid;
		this.gameID = gameID;
		this.filename = filename;
	}
	   
	@Override
	public void run() {
		
		
		try {
			CompetitionProcess.semWriteResults.acquire();
			if( GamesRegister.getInstance().getGameByUid(uid).getState().getTurn() == Settings.WHITE_PLAYER) {
				FileManagement.appendToFile(filename, gameID+": white");
			} else {
				FileManagement.appendToFile(filename,gameID+": black");
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CompetitionProcess.semCloseGame.release();
		}
    	
	}
	
}
