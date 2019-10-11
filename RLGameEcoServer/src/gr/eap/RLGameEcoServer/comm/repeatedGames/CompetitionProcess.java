package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

import gr.eap.RLGameEcoServer.extra.FileManagement;
import gr.eap.RLGameEcoServer.player.Member;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class CompetitionProcess implements Runnable{
	
	public static Semaphore semCompetitionControl = new Semaphore(1);
	public static Semaphore semGameControl = new Semaphore(1);
	public static Semaphore semCreateGame = new Semaphore(1);
	public static Semaphore semCheckFinish = new Semaphore(0);
	public static Semaphore semWriteResults = new Semaphore(0);	
	public static Semaphore semCloseGame = new Semaphore(0);
	public static Semaphore semCheckExit = new Semaphore(0);	
	
	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private Integer whiteHumanId;
	private Integer blackHumanId;	
	public int numberOfGames;
	
	public CompetitionProcess(byte boardSize, byte baseSize, byte numberOfPawns, 
			Integer whiteHumanId, Integer blackHumanId, int numberOfGames) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.whiteHumanId = whiteHumanId;
		this.blackHumanId = blackHumanId;
		this.numberOfGames = numberOfGames;
	}
	
	@Override
	public void run() {		
		// Create report file with basic informations
		String user = PlayersRegister.getInstance().getPlayerById(whiteHumanId).getName();
		Date time = new Date();
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(time);
		String filename = user+"_"+timeStamp+".txt";
		FileManagement.createfile(filename);
		FileManagement.appendToFile(filename, "Date: "+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time));
		
		Player whitePlayer = PlayersRegister.getInstance().getPlayerById(whiteHumanId);
		Integer whiteAvatarId = ((Member)whitePlayer).getAvatar().getId();
		Player whiteAvatar = PlayersRegister.getInstance().getPlayerById(whiteAvatarId);
		FileManagement.appendToFile(filename, "WHITE PLAYER Name: "+ whiteAvatar.getName());
		
		Player blackPlayer = PlayersRegister.getInstance().getPlayerById(blackHumanId);
		Integer blackAvatarId = ((Member)blackPlayer).getAvatar().getId();
		Player blackAvatar = PlayersRegister.getInstance().getPlayerById(blackAvatarId);
		FileManagement.appendToFile(filename, "BLACK PLAYER Name: "+ blackAvatar.getName());
		
		FileManagement.appendToFile(filename, "Game Configuration: "+boardSize+"x"+baseSize+"x"+numberOfPawns);
		
		FileManagement.appendToFile(filename, "Number of Games: "+ numberOfGames);
		FileManagement.appendToFile(filename, "");
		
		int counter = numberOfGames;
		
		while (true) {
			try {
				semCompetitionControl.acquire();
				
				GameProcess gameProcess = new GameProcess(boardSize, baseSize, numberOfPawns, whiteHumanId, blackHumanId, 
						numberOfGames-counter, filename);
				Thread gameProcessThread = new Thread(gameProcess);
				gameProcessThread.start();
				
				counter--;
				if(counter <=0) break;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

}
