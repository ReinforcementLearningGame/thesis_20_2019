angular
.module('rlEcoClient')
.controller('LoginController', function(GameServerConnection) {
	//{"userName":"kyriakos","password":"pass","id":1,"userId":2,"gameId":"00000000-0000-0000-0000-000000000001","commandType":"gr.eap.RLGameEcoServer.comm.LogonCommand"} -->
	this.login = function(userName, password){
		console.log(userName);
		console.log(password);
		GameServerConnection.data.lastCommandId++;
		GameServerConnection.send(
			{
				userName: userName, 
				password: password,
				id: GameServerConnection.data.lastCommandId,
				userId: GameServerConnection.data.userId,
				type: "gr.eap.RLGameEcoServer.comm.LoginCommand"
			}
		);
	};
});