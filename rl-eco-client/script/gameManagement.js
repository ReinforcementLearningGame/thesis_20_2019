angular
.module('rlEcoClient')
.controller('GameManagementController', function(GameServerConnection) {
	this.createGame = function(boardSize, baseSize, numberOfPawns, avatar, invitation){
		GameServerConnection.data.lastCommandId++;
		if (avatar){
			GameServerConnection.send({
				messageText : "create game board size " + boardSize.toString() + " base size " + baseSize.toString() + " " + numberOfPawns.toString() + " pawns",
				recipientsIds : [GameServerConnection.data.avatarId],
				id : GameServerConnection.data.lastCommandId,
				userId : GameServerConnection.data.userId,
				type : "gr.eap.RLGameEcoServer.comm.MessageCommand"
			});
		}
		else
		{
			if (invitation && !(GameServerConnection.data.lastSelectedPlayer == null)){
				GameServerConnection.send(
					{
						boardSize: boardSize,
						baseSize: baseSize,
						numberOfPawns: numberOfPawns,
						id: GameServerConnection.data.lastCommandId,
						userId: GameServerConnection.data.userId,
						invitedPlayerId: GameServerConnection.data.lastSelectedPlayer.id,
						type: "gr.eap.RLGameEcoServer.comm.InviteCommand"
					}
				);
			}
			else
			{
				GameServerConnection.send(
					{
						boardSize: boardSize,
						baseSize: baseSize,
						numberOfPawns: numberOfPawns,
						id: GameServerConnection.data.lastCommandId,
						userId: GameServerConnection.data.userId,
						type: "gr.eap.RLGameEcoServer.comm.CreateGameCommand"
					}
				);
			}
		}
	};
	this.joinGame = function(game, role, avatar){
		GameServerConnection.data.lastCommandId++;
		if (avatar){
			var msg =  "UID:" + game.uid.toString() + " boardsize:" + game.boardSize.toString() + " basesize:" + game.baseSize.toString() + " pawns:" + game.numberOfPawns.toString();
			if (role == "BLACKPLAYER") msg = "join " + msg; else msg = "spectate " + msg; 
			GameServerConnection.send({
				messageText : "join UID:" + game.uid.toString() + " boardsize:" + game.boardSize.toString() + " basesize:" + game.baseSize.toString() + " pawns:" + game.numberOfPawns.toString(),
				recipientsIds : [GameServerConnection.data.avatarId],
				id : GameServerConnection.data.lastCommandId,
				userId : GameServerConnection.data.userId,
				type : "gr.eap.RLGameEcoServer.comm.MessageCommand"
			});
		}
		else
		{
			GameServerConnection.send(
				{
					gameUid: game.uid,
					role: role,
					id: GameServerConnection.data.lastCommandId,
					userId: GameServerConnection.data.userId,
					type: "gr.eap.RLGameEcoServer.comm.JoinGameCommand"
				}
			);
		}
	};
	this.confirmStartGame = function(gameUid){
		GameServerConnection.data.lastCommandId++;
		GameServerConnection.send(
			{
				gameUid: gameUid,
				id: GameServerConnection.data.lastCommandId,
				userId: GameServerConnection.data.userId,
				type: "gr.eap.RLGameEcoServer.comm.ConfirmStartGameCommand"
			}
		);
	};
	this.leaveGame = function(gameUid){
		GameServerConnection.data.lastCommandId++;
		GameServerConnection.send(
			{
				gameUid: gameUid,
				id: GameServerConnection.data.lastCommandId,
				userId: GameServerConnection.data.userId,
				type: "gr.eap.RLGameEcoServer.comm.LeaveGameCommand"
			}
		);
	};
	this.okToCreateGameAsAvatar = function(){
		var app = GameServerConnection;
		return (app.userConnected(app.data.avatarId));
	};
	this.invitationCheckboxClass = function(){
		if (!(GameServerConnection.data.lastSelectedPlayer == null)) return "checkbox";
		return "checkbox disabled";
	};
	
	this.createRepeatedGame = function(boardSize, baseSize, numberOfPawns, numberOfGames, opponent){	
		
		GameServerConnection.data.lastCommandId++;		
		
		GameServerConnection.send({
			boardSize: boardSize,
			baseSize: baseSize,
			numberOfPawns: numberOfPawns,
			numberOfGames: numberOfGames,
			opponentId: opponent.id,
			id: GameServerConnection.data.lastCommandId,
			userId: GameServerConnection.data.userId,
			type: "gr.eap.RLGameEcoServer.comm.RepeatedGamesCommand"
		});
		
		// -------------- use an alert box to display data
		//window.alert(5 + 6);
		//window.alert(6 + 6);
		
		// -------------- use the console.log() method to display data.
		//console.log("----------------------------------------");
					
	};
	
});