angular
.module('rlEcoClient')
.controller('ConnectionController', function($scope, GameServerConnection){
	this.data = GameServerConnection.data;
	console.log("ConnectionController: " + GameServerConnection.data.state);
	GameServerConnection.data.socket.onMessage(function(message) {
		var response = JSON.parse(message.data);
		//data.userId = response.userId;
		switch(response.type){
			case "gr.eap.RLGameEcoServer.comm.MessageResponse":
				$scope.$broadcast('scrollTextChanged');
				$("#receivedMessages").scrollTop($("#receivedMessages")[0].scrollHeight);
				break;
			case "gr.eap.RLGameEcoServer.comm.GameStateResponse":
				$scope.$broadcast('scaleGameBoard');
				$(window).trigger("resize");
				break;
		}
	});
	GameServerConnection.data.socket.onClose(function() {
				$scope.$broadcast('scrollTextChanged');

	});


});
