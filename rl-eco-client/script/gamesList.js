angular.module('rlEcoClient').controller('GamesListController', function(GameServerConnection) {
	this.selectGame = function(game) {
		if (!(GameServerConnection.data.lastSelectedGame == null)) {
			GameServerConnection.data.lastSelectedGame.selected = false;
			GameServerConnection.data.lastSelectedGame.liClass = "list-group-item";
		}
		// var BreakException = {};
		var whitePlayerContainsUser = GameServerConnection.whitePlayerContainsUser(game, GameServerConnection.data.userId);
		var blackPlayerContainsUser = GameServerConnection.blackPlayerContainsUser(game, GameServerConnection.data.userId);
		var spectatorContainsUser = GameServerConnection.spectatorContainsUser(game, GameServerConnection.data.userId);

		var whitePlayerContainsAvatar = false;
		var blackPlayerContainsAvatar = GameServerConnection.blackPlayerContainsUser(game, GameServerConnection.data.avatarId);
		var spectatorContainsAvatar = GameServerConnection.spectatorContainsUser(game, GameServerConnection.data.avatarId);
		var avatarConnected = GameServerConnection.userConnected(GameServerConnection.data.avatarId);


		var whitePlayerContainsAvatar = GameServerConnection.whitePlayerContainsUser(game, GameServerConnection.data.avatarId);
		var blackPlayerContainsAvatar = GameServerConnection.blackPlayerContainsUser(game, GameServerConnection.data.avatarId);
		var spectatorContainsAvatar = GameServerConnection.spectatorContainsUser(game, GameServerConnection.data.avatarId);
		var avatarConnected = GameServerConnection.userConnected(GameServerConnection.data.avatarId);
		
		game.okToJoinWhitePlayer = true;
		game.okToJoinBlackPlayer = true;
		game.okToSpectate = true;
		game.okToStart = false;
		game.okToLeave = false;
		game.okToJoinAsAvatar = true;
		game.okToSpectateAsAvatar = true;
		



		if (!(avatarConnected) || whitePlayerContainsAvatar || spectatorContainsAvatar) {
			game.okToJoinAsAvatar = false;
		}

		if (!(avatarConnected) || whitePlayerContainsAvatar || blackPlayerContainsAvatar) {
			game.okToSpectateAsAvatar = false;
		}


		if (whitePlayerContainsUser || blackPlayerContainsUser || spectatorContainsUser) {
			game.okToJoinWhitePlayer = false;
			game.okToJoinBlackPlayer = false;
			game.okToSpectate = false;
			game.okToLeave = true;
		}


		
		if (game.okToJoinWhitePlayer && game.whitePlayerReady) game.okToJoinWhitePlayer = false;
		if (game.okToJoinBlackPlayer && game.blackPlayerReady) game.okToJoinBlackPlayer = false;
		
		if (
			whitePlayerContainsUser && 
			game.whitePlayer.teamLeader.id == GameServerConnection.data.userId &&
			!(game.whitePlayerReady)
				||
			blackPlayerContainsUser && 
			game.blackPlayer.teamLeader.id == GameServerConnection.data.userId &&
			!(game.blackPlayerReady)
			)
			game.okToStart = true;


		game.selected = true;
		game.liClass = "list-group-item active";

		GameServerConnection.data.lastSelectedGame = game;

	};
}); 