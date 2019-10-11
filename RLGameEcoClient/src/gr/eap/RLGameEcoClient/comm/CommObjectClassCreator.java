package gr.eap.RLGameEcoClient.comm;

public class CommObjectClassCreator {
	public static Class<?> create(String commObjectType){
		if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.LoginCommand"))
			return LoginCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.MessageCommand"))
			return MessageCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.CreateGameCommand"))
			return CreateGameCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.JoinGameCommand"))
			return JoinGameCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.ConfirmStartGameCommand"))
			return ConfirmStartGameCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.LeaveGameCommand"))
			return LeaveGameCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.MoveCommand"))
			return MoveCommand.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.MessageResponse"))
			return MessageResponse.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.PlayersListResponse"))
			return PlayersListResponse.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.GamesListResponse"))
			return GamesListResponse.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.GameStateResponse"))
			return GameStateResponse.class;
		else if (commObjectType.equals("gr.eap.RLGameEcoServer.comm.RepeatedGamesCommand"))
			return RepeatedGamesCommand.class;
		else
			return null;
	}
	
}
