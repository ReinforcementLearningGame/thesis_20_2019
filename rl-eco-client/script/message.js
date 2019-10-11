angular
.module('rlEcoClient')
.controller('MessageController', function(GameServerConnection){
	this.shout = function(messageText){
		GameServerConnection.lastCommandId++;
		GameServerConnection.send({
			messageText : messageText,
			recipientsIds : [],
			id : GameServerConnection.data.lastCommandId,
			userId : GameServerConnection.data.userId,
			type : "gr.eap.RLGameEcoServer.comm.MessageCommand"
		});
	};
});