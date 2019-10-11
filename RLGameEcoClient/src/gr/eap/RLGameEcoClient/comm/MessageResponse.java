package gr.eap.RLGameEcoClient.comm;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rlgame.gameplay.MMPlayer;
import org.rlgame.gameplay.RLPlayer;
import org.rlgame.gameplay.RandomPlayer;
import org.rlgame.gameplay.Settings;
import org.rlgame.gameplay.pkgMARLPlayer01.MARLPlayer01;
import org.rlgame.gameplay.pkgRLPlayer02.RLPlayer02;
import org.rlgame.gameplay.pkgRLPlayer03.RLPlayer03;

import gr.eap.RLGameEcoClient.Client;
import gr.eap.RLGameEcoClient.player.Player;
import gr.eap.RLGameEcoClient.player.Member;
import gr.eap.RLGameEcoClient.player.Participant.Role;

public class MessageResponse extends Response {
	private Message message;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public MessageResponse() {
		this.setType("gr.eap.RLGameEcoServer.comm.MessageResponse");
	}

	@Override
	public void process() {
		//Replace the following code to determine how your avatar manages incoming messages
		
		Player sender = getMessage().getSender();
		String reply = "";
		byte boardSize = 0, baseSize = 0, numberOfPawns = 0;
		UUID joinGameUid = null;
		Role joinRole = Client.joinRole;		
		String messageType="";
		String currentMessage = getMessage().getText();
		
		//Avatar tries to understand if its creator asked it to create a game
		String regexCreateGame = "(?i)((?!not).)*create.*game.*board.*size.*?(\\d+).*base.*size.*?(\\d+).*?(\\d+).*pawns.*";
		Matcher matchCreateGame = Pattern.compile(regexCreateGame,Pattern.CASE_INSENSITIVE).matcher(currentMessage);
		if (matchCreateGame.matches()) messageType = "CreateGame";
		
		//Avatar tries to understand if its creator asked it to join a game
		String regexJoinGame = "(join|spectate|joinWhite|joinBlack) UID:(.+)\\sboardsize:(\\d+)\\sbasesize:(\\d+)\\spawns:(\\d+)";
		Matcher matchJoinGame = Pattern.compile(regexJoinGame,Pattern.CASE_INSENSITIVE).matcher(currentMessage);
		if (matchJoinGame.matches()) messageType = "JoinGame";
		
		
		
		// Respond according to message
		if (sender != null && sender.isHuman() && ((Member)sender).getAvatar().equals(Client.me)){
			switch(messageType) {
			
			case "CreateGame":
				try {
					boardSize = Byte.parseByte(matchCreateGame.group(2));
					baseSize = Byte.parseByte(matchCreateGame.group(3));
					numberOfPawns = Byte.parseByte(matchCreateGame.group(4));
					reply = "Creating the game. Hope you join me!";
					joinRole = null;
				} catch (NumberFormatException e) {
					reply = "I couldn't quite get the numbers, sorry :(";
				}
				break;
			
			case "JoinGame":
				try {
					if (matchJoinGame.group(1).equals("join")) joinRole = Role.BLACKPLAYER;
					else if (matchJoinGame.group(1).equals("spectate")) joinRole = Role.SPECTATOR;
					else if (matchJoinGame.group(1).equals("joinWhite")) joinRole = Role.WHITEPLAYER;
					else joinRole = Role.BLACKPLAYER;
				
					joinGameUid = UUID.fromString(matchJoinGame.group(2));
					boardSize = Byte.parseByte(matchJoinGame.group(3));
					baseSize = Byte.parseByte(matchJoinGame.group(4));
					numberOfPawns = Byte.parseByte(matchJoinGame.group(5));
					reply = "Joining game...";
				} catch (NumberFormatException e) {
					reply = "I couldn't quite get the numbers, sorry :(";
				}
				break;
			
			default :	
				reply = "OK";
				
			}
		}
		
		if (joinRole != Client.joinRole) Client.joinRole = joinRole;
		
		if (reply != ""){
			MessageCommand replyCommand = new MessageCommand();
			replyCommand.setMessageText(reply);
			replyCommand.getRecipientsIds().add(sender.getId());
			replyCommand.setSocket(getSocket());
			replyCommand.setUserId(getUserId());
			replyCommand.send();
		}
		
		if (boardSize != 0 && baseSize != 0 && numberOfPawns != 0){
			int ident, opponent, plies;
			if (joinGameUid == null || joinRole == Role.SPECTATOR) {
				ident = Settings.WHITE_PLAYER;
				opponent = Settings.BLACK_PLAYER;
				plies = Settings.PLAYER_W_PLIES;
			}
			else
			{
				if(joinRole == Role.WHITEPLAYER) {
					ident = Settings.WHITE_PLAYER;
					opponent = Settings.BLACK_PLAYER;
					plies = Settings.PLAYER_W_PLIES;
				}else {
					ident = Settings.BLACK_PLAYER;
					opponent = Settings.WHITE_PLAYER;
					plies = Settings.PLAYER_B_PLIES;
				}				
			}
			
			String playerType = Client.clientSettings.getProperty("playerType");
			switch (playerType) {
			//Create your player class with your algorithm, by implementing the IPlayer interface
			case "RANDOM_PLAYER":
				Client.machine = new RandomPlayer(ident);
				break;
			case "MM_PLAYER":
				Client.machine = new MMPlayer(ident, plies, opponent, boardSize, baseSize, numberOfPawns);
				break;
			case "RL_PLAYER":
				Client.machine = new RLPlayer(ident, boardSize, baseSize, numberOfPawns);
				break;
			case "RL2_PLAYER":
				Client.machine = new RLPlayer02(ident, boardSize, baseSize, numberOfPawns);
				break;
			case "RL3_PLAYER":
				Client.machine = new RLPlayer03(ident, boardSize, baseSize, numberOfPawns);
				break;
			case "MARL1_PLAYER":
				Client.machine = new MARLPlayer01(ident, boardSize, baseSize, numberOfPawns);
				break;

			default:
				break;
			}
			
			Client.currentBaseSize = baseSize;
			Client.currentBoardSize = boardSize;
			Client.currentNumberOfPawns = numberOfPawns;
			
			if (joinGameUid != null) {
				Client.lastState = null;
				JoinGameCommand joinCommand = new JoinGameCommand();
				joinCommand.setGameUid(joinGameUid);
				joinCommand.setRole(joinRole);
				joinCommand.setId(0);
				joinCommand.setSocket(getSocket());
				joinCommand.setUserId(getUserId());
				joinCommand.send();
			}
			else
			{
				CreateGameCommand createGameCommand = new CreateGameCommand();
				createGameCommand.setBaseSize(baseSize);
				createGameCommand.setBoardSize(boardSize);
				createGameCommand.setNumberOfPawns(numberOfPawns);
				createGameCommand.setSocket(getSocket());
				createGameCommand.setUserId(getUserId());
				createGameCommand.send();
			}
			
			
		}
		
		
	}

	private void elseif(boolean equals) {
		// TODO Auto-generated method stub
		
	}

}
