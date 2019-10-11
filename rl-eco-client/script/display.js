$(window).resize(function() {
	var size = Math.min($("#gameContent").width(), $("#gameContent").height() - $("#gamesCommands").height());
	console.log($("#gameContent").width() + ", " + $("#gameContent").height());
	console.log(size);
	$("#tblGameBoard").width(size).height(size);
});
