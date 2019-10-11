angular.module('rlEcoClient').controller('GameplayController', function($scope, GameServerConnection) {
	// GameServerConnection.data.socket.onMessage(function(message) {
		// var response = JSON.parse(message.data);
		// //data.userId = response.userId;
		// switch(response.type){
			// case "gr.eap.RLGameEcoServer.comm.GameStateResponse":
				// $scope.$broadcast('scaleGameBoard');
				// $(window).trigger("resize");
				// break;
		// }
	// });
	var move = {
			pawnId: null,
			xCoord: null,
			yCoord: null
		};
	this.pendingMove = move;
	this.canConfirmMove = function(){
		if (move.pawnId != null && move.xCoord != null && move.yCoord != null) return true; else return false;
	};
	this.canClearMove = function(){
		if (move.pawnId != null || move.xCoord != null || move.yCoord != null) return true; else return false;
	};
	this.clearMove = function(){
		var cellToSelect = null;
		var g = GameServerConnection.data.gamesList[0];
		var gs = GameServerConnection.data.gameState;
		var gb = GameServerConnection.data.gameBoardUI;
		var boardSize = GameServerConnection.data.gameState.boardSize;
		for (r = 0; (move.pawnId != null || move.xCoord != null || move.yCoord != null) && r < boardSize; r++){
			for (c = 0; (move.pawnId != null || move.xCoord != null || move.yCoord != null) && c < boardSize; c++){
				if (gb[r][c].xCoord == move.xCoord && gb[r][c].yCoord == move.yCoord){
					gb[r][c].sClass = "gameBoardCellUnselected";
					move.xCoord = null;
					move.yCoord = null;
				}
				if (gb[r][c].pawnId == move.pawnId){
					gb[r][c].sClass = "gameBoardCellUnselected";
					move.pawnId = null;
				}
			}
		}
		
	};
	this.confirmMove = function(){
		GameServerConnection.lastCommandId++;
		GameServerConnection.send({
			pawnId: move.pawnId,
			toXCoord: move.xCoord,
			toYCoord: move.yCoord,
			gameUid: GameServerConnection.data.gameUid,
			id : GameServerConnection.data.lastCommandId,
			userId : GameServerConnection.data.userId,
			type : "gr.eap.RLGameEcoServer.comm.MoveCommand"
		});
		this.clearMove();
	};
	this.cellSizePercentage = function(){
		if (GameServerConnection.data.gameState != null) return (100.0 / GameServerConnection.data.gameState.boardSize) + "%";
		else return "100%";
	};
	this.gameBoardWidth = function(){
		var width = document.getElementById("gameContent").clientWidth;
		var height = document.getElementById("gameContent").clientHeight;
		
		return Math.min(width, height);
	};
	this.getBoard = function() {
		
		var returnBoard = [];
		var originalBoard = [];
		var whitePawns = [];
		var blackPawns = [];
		
		if (GameServerConnection.data.gameState != null) {
			if (GameServerConnection.data.newGameState){
	
				var boardSize = GameServerConnection.data.gameState.boardSize;
				originalBoard = GameServerConnection.data.gameState.gameBoard;
				whitePawns = GameServerConnection.data.gameState.whitePawns;
				blackPawns = GameServerConnection.data.gameState.blackPawns;
	
				for ( r = boardSize - 1; r >= 0; r--) {
					var row = [];
					for ( c = 0; c< boardSize ; c++) {
						var boardCell = {};
						boardCell.xCoord = originalBoard[r][c].xCoord;
						boardCell.yCoord = originalBoard[r][c].yCoord;
						boardCell.isFree = originalBoard[r][c].isFree;
						boardCell.sClass = "gameBoardCellUnselected";
						if (originalBoard[r][c].isInWBase) boardCell.cClass = "gameBoardCellIsInWBase";
						else if (originalBoard[r][c].isInBBase) boardCell.cClass = "gameBoardCellIsInBBase";
						else boardCell.cClass = "gameBoardCellPlain";
						row.push(boardCell);
					}
					returnBoard.push(row);
				}
				for (i = 0; i < whitePawns.length; i++){
					var p = whitePawns[i];
					if (p.alive && returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pawnId == null){
						returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pClass = "whitePawn";
						returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pawnId = p.id;
					}
				}
				for (i = 0; i < blackPawns.length; i++){
					var p = blackPawns[i];
					if (p.alive && returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pawnId == null){
						returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pClass = "blackPawn";
						returnBoard[boardSize-1-p.position.xCoord][p.position.yCoord].pawnId = p.id;
					}
				}


				GameServerConnection.data.gameBoardUI = returnBoard;
				GameServerConnection.data.newGameState = false;
			}
		}
		return GameServerConnection.data.gameBoardUI;
	};
	this.prompt = function(){
		var gs = GameServerConnection.data.gameState;
		var g = GameServerConnection.data.gamesList[0];
		if (gs != null && g != null){
			var pl;
			if (g.status == "Finished")
				pl = "The game is over.";
			else if (gs.turn == 1 && GameServerConnection.whitePlayerContainsUser(g, GameServerConnection.data.userId) || gs.turn == 2 && GameServerConnection.blackPlayerContainsUser(g, GameServerConnection.data.userId)){
				pl = "It's your turn";
			} else if (gs.turn == 1){
				pl = "It's White Player's turn";
			} else{
				pl = "It's Black Player's turn";
			}
			return pl;
		}	
		return null;
	};
	this.selectNextCell = function(xCoord, yCoord){
		var cellToSelect = null;
		var g = GameServerConnection.data.gamesList[0];
		if (g.status == "In progress"){
			
			var gs = GameServerConnection.data.gameState;
			var gb = GameServerConnection.data.gameBoardUI;
			var boardSize = GameServerConnection.data.gameState.boardSize;
			for (r = 0; cellToSelect == null && r < boardSize; r++){
				for (c = 0; cellToSelect == null && c < boardSize; c++){
					if (gb[r][c].xCoord == xCoord && gb[r][c].yCoord == yCoord){
						cellToSelect = gb[r][c];
					}
				}
			}
			
			if (move.pawnId == null){
				var pawns = [];
				if (gs.turn == 1 && GameServerConnection.whitePlayerContainsUser(g, GameServerConnection.data.userId)){
					pawns = GameServerConnection.data.gameState.whitePawns;
				}
				if (gs.turn == 2 && GameServerConnection.blackPlayerContainsUser(g, GameServerConnection.data.userId)){
					pawns = GameServerConnection.data.gameState.blackPawns;
				}
				for (i = 0; move.pawnId == null && i < pawns.length; i++){
					var p = pawns[i];
					if (p.position.xCoord == xCoord && p.position.yCoord == yCoord){
						move.pawnId = p.id;
						cellToSelect.sClass = "gameBoardCellSelected";
						console.log(move);
					}
				}
			}
			else if (move.xCoord == null && move.yCoord == null) {
				if ((gs.turn == 1 && GameServerConnection.whitePlayerContainsUser(g, GameServerConnection.data.userId) || gs.turn == 2 && GameServerConnection.blackPlayerContainsUser(g, GameServerConnection.data.userId)) && cellToSelect.isFree){
					move.xCoord = xCoord;
					move.yCoord = yCoord;
					cellToSelect.sClass = "gameBoardCellSelected";
					console.log(move);
				}
			}
		}
	};
	this.leaveGame = function(){
		GameServerConnection.data.lastCommandId++;
		GameServerConnection.send(
			{
				gameUid: GameServerConnection.data.gameUid,
				id: GameServerConnection.data.lastCommandId,
				userId: GameServerConnection.data.userId,
				type: "gr.eap.RLGameEcoServer.comm.LeaveGameCommand"
			}
		);
	};

});

