package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.UUID;

public class GameProcess implements Runnable{		
	
	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private Integer whiteHumanId;
	private Integer blackHumanId;
	private int gameID;
	private String filename;
	
	public GameProcess(byte boardSize, byte baseSize, byte numberOfPawns, Integer whiteHumanId, Integer blackHumanId,
			int gameID, String filename) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.whiteHumanId = whiteHumanId;
		this.blackHumanId = blackHumanId;
		this.gameID = gameID;
		this.filename = filename;
	}

	@Override
	public void run() {
		
		
		try {
			CompetitionProcess.semGameControl.acquire();
			
			// Server creates a new game's UUID
			UUID uid = UUID.randomUUID();
			
			
			// Server creates a new game
			CreateGame createGame = new CreateGame(boardSize, baseSize, numberOfPawns, uid, whiteHumanId, blackHumanId);
			Thread createGameThread = new Thread(createGame);		
			createGameThread.start();
			
			
			// Wait the game to end
			CheckFinish checkFinish = new CheckFinish(uid);
			Thread checkFinishThread = new Thread(checkFinish);
			checkFinishThread.start();
			
			
			// Write the results to report file
			WriteResults writeResults = new WriteResults(uid, gameID, filename);
			Thread writeResultsThread = new Thread(writeResults);
			writeResultsThread.start();
			
			
			//Remove the game
			CloseGame closeGame = new CloseGame(uid);
			Thread closeGameThread = new Thread(closeGame);
			closeGameThread.start();
			
			//Wait until the game to be removed from server
			CheckExit checkExit = new CheckExit(uid);
			Thread checkExitThread = new Thread(checkExit);
			checkExitThread.start();
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
