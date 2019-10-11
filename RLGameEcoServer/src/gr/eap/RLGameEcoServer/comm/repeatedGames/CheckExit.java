package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.UUID;
import gr.eap.RLGameEcoServer.game.GamesRegister;

public class CheckExit implements Runnable{
	
	private UUID uid;
	
	public CheckExit(UUID uid) {
		this.uid = uid;
	}
	   
	@Override
	public void run() {
		
		try {
			CompetitionProcess.semCheckExit.acquire();
			
			while (GamesRegister.getInstance().getGameByUid(uid) != null){
				//waiting for exit
			}

			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			CompetitionProcess.semCreateGame.release();
			CompetitionProcess.semGameControl.release();
			CompetitionProcess.semCompetitionControl.release();
		}
		
	}
	
}
