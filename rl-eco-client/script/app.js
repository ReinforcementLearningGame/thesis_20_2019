angular
.module('rlEcoClient', ['ngWebSocket'])
.factory('GameServerConnection', function($websocket) {
	// Open a WebSocket connection
	var dataStream = $websocket('ws://192.168.56.101:33313');

	var data = {
		socket : dataStream,
		lastCommandId : 0,
		userId : 0,
		avatarId : 0,
		messages : [{"text": "Attempting connection to server...", "type" : "SYSTEM_INFO"}],
		state : "DISCONNECTED",
		playersList: [],
		gamesList: [],
		lastSelectedGame: null,
		gameState: null,
		newGameState: false,
		gameBoardUI: [],
		gameUid: null,
		lastSelectedPlayer: null
	};

	var addMessage = function(msg){
		data.messages.push(msg);
		
	};

	dataStream.onOpen(function(){
		console.log("Connection open!");
		state = "CONNECTED";
	});

	dataStream.onClose(function(){
		console.log("Connection closed!");
		state = "DISCONNECTED";
		addMessage({"text": "Connection closed!", "type" : "SYSTEM_INFO"});
	});

	dataStream.onMessage(function(message) {
		console.log(message.data);
		try{
			var response = JSON.parse(message.data);
			//data.userId = response.userId;
			switch(response.type){
				case "gr.eap.RLGameEcoServer.comm.MessageResponse":
					data.userId = response.userId;
					data.avatarId = response.avatarId;
					if (response.message != null && !(response.message == '')) addMessage(response.message);
					console.log(response.message);
					break;
				case "gr.eap.RLGameEcoServer.comm.PlayersListResponse":
					data.playersList = response.playersList.slice();
					//console.log(data.playersList);
					break;
				case "gr.eap.RLGameEcoServer.comm.GamesListResponse":
					data.gamesList = response.gamesList.slice();
					data.lastSelectedGame = null; //Maybe find game with UUID?
					//console.log(data.playersList);
					break;
				case "gr.eap.RLGameEcoServer.comm.GameStateResponse":
					data.gameState = response.state;
					data.newGameState = true;
					data.gameUid = response.gameUid;
					break;
			}
			data.state = response.connectionState;
			console.log(data.state);
			console.log(data.messages);
		}
		catch(err){
			console.log(err.message);
		}
	});

	var send = function(msg){
		dataStream.send(JSON.stringify(msg));
	};
	var BreakException = {};

	// http://stackoverflow.com/questions/2641347/how-to-short-circuit-array-foreach-like-calling-break
	var whitePlayerContainsUser = function(game, userId){
		var p = game.whitePlayer.players;
		var returnValue = false;
		try{
			p.forEach(function(item) {
				if (item.id == userId) {
					returnValue = true;
					throw BreakException;
				}
			});
		} catch (e) {
			if (e !== BreakException)
				throw e;
		}
		return returnValue;

	};
	
	var blackPlayerContainsUser = function(game, userId){
		var returnValue = false;
		if (!(game.blackPlayer == null)){
			var p = game.blackPlayer.players;
			try{
				p.forEach(function(item) {
					if (item.id == userId) {
						returnValue = true;
						throw BreakException;
					}
				});
			} catch (e) {
				if (e !== BreakException)
					throw e;
			}
		}
		return returnValue;
	};
	var spectatorContainsUser = function(game, userId){
		var returnValue = false;
		if (!(game.spectator == null)){
			var p = game.spectator.players;
			try{
				p.forEach(function(item) {
					if (item.id == userId) {
						returnValue = true;
						throw BreakException;
					}
				});
			} catch (e) {
				if (e !== BreakException)
					throw e;
			}
		}
		return returnValue;
	};
	var userConnected = function(userId){
		var returnValue = false;
		var p = data.playersList;
		if (!(p == null)){
			try{
				p.forEach(function(item){
					if (item.id == userId) {
						returnValue = true;
						throw BreakException;
					}
				});
			} catch (e) {
				if (e !== BreakException)
					throw e;
			}
		}
		return returnValue;	
	};

	return {
				data: data, 
				send : send, 
				whitePlayerContainsUser: whitePlayerContainsUser, 
				blackPlayerContainsUser: blackPlayerContainsUser, 
				spectatorContainsUser: spectatorContainsUser,
				userConnected: userConnected
			} ;
	//return {data: data, send : send} ;
})
.directive('scrollDown', ['$timeout', function ($timeout) {
    return {
        link: function ($scope, element, attrs) {
            $scope.$on('scrollTextChanged', function () {
                $timeout(function () { // You might need this timeout to be sure its run after DOM render.
					$("#receivedMessages").scrollTop($("#receivedMessages")[0].scrollHeight);
                }, 0, false);
            });
        }
    };
}])
.directive('heightStuff', ['$timeout', function ($timeout) {
    return {
        link: function ($scope, element, attrs) {
            $scope.$on('scaleGameBoard', function () {
                $timeout(function () { // You might need this timeout to be sure its run after DOM render.
					var size = Math.min($("#gameContent").width(), $("#gameContent").height() - element.height());
					$("#tblGameBoard").width(size).height(size);
                }, 0, false);
            });
        }
    };
}])

;
