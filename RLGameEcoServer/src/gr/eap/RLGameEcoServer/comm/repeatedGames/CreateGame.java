package gr.eap.RLGameEcoServer.comm.repeatedGames;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gr.eap.RLGameEcoServer.comm.Message;
import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.player.Member;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class CreateGame implements Runnable{
	
	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private UUID uid;
	private Integer whiteHumanId;
	private Integer blackHumanId;
	
	public CreateGame(byte boardSize, byte baseSize, byte numberOfPawns, UUID uid, Integer whiteHumanId, Integer blackHumanId) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.uid = uid;
		this.whiteHumanId = whiteHumanId;
		this.blackHumanId = blackHumanId;
	}
	   
	@Override
	public void run() {
		
					
		
		try {
			CompetitionProcess.semCreateGame.acquire();
		
			Player whitePlayer = PlayersRegister.getInstance().getPlayerById(whiteHumanId);
			Integer whiteAvatarId = ((Member)whitePlayer).getAvatar().getId();
			Player whiteAvatar = PlayersRegister.getInstance().getPlayerById(whiteAvatarId);
				
			Player blackPlayer = PlayersRegister.getInstance().getPlayerById(blackHumanId);
			Integer blackAvatarId = ((Member)blackPlayer).getAvatar().getId();
			Player blackAvatar = PlayersRegister.getInstance().getPlayerById(blackAvatarId);
			
			GamesRegister.getInstance().createGame(boardSize, baseSize, numberOfPawns, uid);		
			GamesRegister.getInstance().sendGamesList();
				
			// white player joins the game
			Message messageWhite = new Message();
				
			String messageWhiteText = "joinWhite UID:" + uid +" boardsize:" + boardSize + " basesize:" + baseSize + " pawns:" + numberOfPawns;
				
			messageWhite.setText(messageWhiteText);
				
			messageWhite.setType(Type.USER_PERSONAL);
				
			messageWhite.setSender(whitePlayer);
						
			List<Player> recipientsWhite = new ArrayList<Player>();
			recipientsWhite.add(whiteAvatar);
			recipientsWhite.add(whitePlayer);
			messageWhite.setRecipients(recipientsWhite);
			messageWhite.send();
			GamesRegister.getInstance().sendGamesList();
				
			// black player joins the game
			Message messageBlack = new Message();
				
			String messageBlackText = "join UID:" + uid +" boardsize:" + boardSize + " basesize:" + baseSize + " pawns:" + numberOfPawns;
				
			messageBlack.setText(messageBlackText);
				
			messageBlack.setType(Type.USER_PERSONAL);
				
			messageBlack.setSender(blackPlayer);
						
			List<Player> recipientsBlack = new ArrayList<Player>();
			recipientsBlack.add(blackAvatar);
			recipientsBlack.add(blackPlayer);
			messageBlack.setRecipients(recipientsBlack);
			messageBlack.send();
			GamesRegister.getInstance().sendGamesList();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			CompetitionProcess.semCheckFinish.release();
		}
			
		
	}
	
	
}
