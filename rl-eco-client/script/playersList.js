angular.module('rlEcoClient').controller('PlayersListController', function(GameServerConnection) {
	this.self = function(player){
		return (GameServerConnection.data.userId == player.id);
	};
	this.togglePlayer = function(player) {
		if (!this.self(player)){
		
			if (!(GameServerConnection.data.lastSelectedPlayer == null) && !(GameServerConnection.data.lastSelectedPlayer === player)) {
				GameServerConnection.data.lastSelectedPlayer.selected = false;
				GameServerConnection.data.lastSelectedPlayer.liClass = "list-group-item";
			}
			
			if (player.selected)
			{
				player.selected = false;
				player.liClass = "list-group-item";
				GameServerConnection.data.lastSelectedPlayer = null;
			}
			else
			{
				player.selected = true;
				player.liClass = "list-group-item active";
				GameServerConnection.data.lastSelectedPlayer = player;
			}
		}

	};
	this.getLiClass = function(player){
		//if (this.self(player)) return "list-group-item list-group-item-info";
		if (!(GameServerConnection.data.lastSelectedPlayer == null) && GameServerConnection.data.lastSelectedPlayer === player) return "list-group-item active";
		return "list-group-item";
	};
});