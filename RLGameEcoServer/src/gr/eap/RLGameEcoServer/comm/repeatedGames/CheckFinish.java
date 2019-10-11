package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.UUID;
import gr.eap.RLGameEcoServer.game.GamesRegister;

public class CheckFinish implements Runnable{
	
	private UUID uid;
	
	public CheckFinish(UUID uid) {
		this.uid = uid;
	}
	   
	@Override
	public void run() {
		
		try {
			CompetitionProcess.semCheckFinish.acquire();
			
			while (!"FINISHED".equals(GamesRegister.getInstance().getGameByUid(uid).getStatus().toString())){
				assert true;
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CompetitionProcess.semWriteResults.release();
		}
		
		
		
		
    	
	}
	
}
