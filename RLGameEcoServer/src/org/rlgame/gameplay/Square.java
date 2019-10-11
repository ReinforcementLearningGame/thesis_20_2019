package org.rlgame.gameplay;


public class Square {
    private int xCoord;
    private int yCoord;
    
	private boolean isFree; // no pawn is on that square
	private boolean isInWBase;
	private boolean isInBBase;
	private int boardSize;
	private int baseSize;

	public Square(int boardSize, int baseSize) {
		
	}
	
	public Square(int x, int y, boolean empty, int boardSize, int baseSize) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		xCoord = x;
		yCoord = y;
		isFree = empty;
	}

	public Square(int x, int y, int dimBoard, int dimBase) {
		this.boardSize = dimBoard;
		this.baseSize = dimBase;
		xCoord = x;
		yCoord = y;
		isFree = true;
		isInWBase = ((x < dimBase) && (y < dimBase));
		isInBBase = ((x > dimBoard - dimBase - 1) && (y > dimBoard - dimBase - 1));
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree() {
		isFree = true;
	}

	public void setUnFree() {
		isFree = false;
	}

	public boolean isInWhiteBase() {
		return ((xCoord < baseSize) && (yCoord < baseSize));
	}

	public boolean isInBlackBase() {
		return ((xCoord > boardSize - baseSize - 1) && (yCoord > boardSize - baseSize - 1));
	}

	public void setXCoord(int x) {
		xCoord = x;
	}

	public void setYCoord(int y) {
		yCoord = y;
	}

	public int getXCoord() {
		return xCoord;
	}

	public int getYCoord() {
		return yCoord;
	}



	// tag is a number I map each square with. I use it for describing the move
	// when human players are involved
	public int square2Tag() {
		return xCoord * boardSize + yCoord + 1;
	}

	public Square tag2Square(int tg) {
		int tag = tg - 1;
		Square tagSq = new Square(boardSize, baseSize);
//		tagSq.xCoord = (int) tag / DIMBOARD;
//		tagSq.yCoord = (int) tag - DIMBOARD * (tagSq.xCoord);

		tagSq.xCoord = (int) (tag / boardSize);
		tagSq.yCoord = (int) (tag - boardSize * (tagSq.xCoord));
		
		return tagSq;
	}

	
	public String getPrintInfo() {
		if (this.isInWBase) return "W";
		if (this.isInBBase) return "B";
		return this.isFree ? "0" : "1"; 
	}	
	
	@Override
	public int hashCode() {
		int hash = 23;
		hash = 31 * hash + getXCoord();
		hash = 31 * hash + getYCoord();
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		return (((Square) object).getXCoord() == getXCoord() && ((Square) object).getYCoord() == getYCoord());
	}

}
