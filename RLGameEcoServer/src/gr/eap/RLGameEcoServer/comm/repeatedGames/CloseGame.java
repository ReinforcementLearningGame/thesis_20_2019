package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.UUID;
import gr.eap.RLGameEcoServer.game.GamesRegister;

public class CloseGame implements Runnable{
	
	private UUID uid;
	
	public CloseGame(UUID uid) {
		this.uid = uid;
	}
	   
	@Override
	public void run() {	

		
		try {
			CompetitionProcess.semCloseGame.acquire();
			GamesRegister.getInstance().getGameByUid(uid).shareState();
			GamesRegister.getInstance().removeGame(GamesRegister.getInstance().getGameByUid(uid));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			CompetitionProcess.semCheckExit.release();
		}
		
		
		
		
    	
	}
	
}
