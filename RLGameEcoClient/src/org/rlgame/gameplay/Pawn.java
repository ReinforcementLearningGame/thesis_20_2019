package org.rlgame.gameplay;


public class Pawn {
	private int id; // pawn id
	private boolean white; // belongs to white??
	private boolean alive; // is alive??
	public Square position; // the square where this pawn resides
	private int boardSize;
	private int baseSize;
	private int maxNumberOfPawnMoves;

	private Square whiteBase = null; 
	private Square blackBase = null; 
						

	public Pawn(int boardSize, int baseSize) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.maxNumberOfPawnMoves = baseSize * 2;
		// initiates the squares that make the white base	
		whiteBase = new Square(0, 0, boardSize, baseSize); 
		// initiates the squares that make the black base
		blackBase = new Square(boardSize - 1, boardSize - 1, boardSize, baseSize); 
		alive = true;
		position = whiteBase;
	}

	public Pawn(int noumero, boolean whiteColor, int boardSize, int baseSize) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.maxNumberOfPawnMoves = baseSize * 2;
		// initiates the squares that make the white base	
		whiteBase = new Square(0, 0, boardSize, baseSize); 
		// initiates the squares that make the black base
		blackBase = new Square(boardSize - 1, boardSize - 1, boardSize, baseSize); 
		id = noumero;
		white = (whiteColor == true) ? true : false;
		position = (whiteColor == true) ? whiteBase : blackBase;
		alive = true;
	}

	public Pawn(int noumero, boolean whiteColor, Square toSquare, boolean aliveStatus, int boardSize, int baseSize) {
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.maxNumberOfPawnMoves = baseSize * 2;
		id = noumero;
		white = (whiteColor == true) ? true : false;
		position = toSquare;
		
		alive = aliveStatus;
	}	
	

	public int getBoardSize() {
		return boardSize;
	}

	public int getBaseSize() {
		return baseSize;
	}

	
	
	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}

	public void setBaseSize(int baseSize) {
		this.baseSize = baseSize;
		this.maxNumberOfPawnMoves = baseSize * 2;
	}

	// move pawn from fromSquare to toSquare
	public void movePawn(Square fromSquare, Square toSquare) {
		fromSquare.setFree(); // set free the source square
		position = toSquare;
		
		//ngav added on Apr 12
		position.setUnFree();
		toSquare.setUnFree(); // and set unfree the sink square
	}

	public boolean isPawnInOwnBase() {
		return ((this.isAlive() == true) && (((this.white) && (this.position
				.isInWhiteBase())) || ((!this.white) && (this.position
				.isInBlackBase())))) ? true : false;
	}

	public boolean isPawnInEnemyBase() {
		boolean res = false;
		if ((this.isAlive())
				&& ((this.white) && (this.position.isInBlackBase())))
			res = true;

		if ((this.isAlive())
				&& ((!this.white) && (this.position.isInWhiteBase())))
			res = true;
		return res;
		// return
		// ((this.isAlive())&&(((this.white)&&(this.position.isInBlackBase()))||(!this.white)&&(this.position.isInWhiteBase())))?true:false;
	}

	public boolean isAlive() {
		return alive;
	}

	public void killPawn() {
		position.setFree(); // release the square
		alive = false;
	}

	// finds all possible moves a pawn can make, without checking legality
	public Square[] getAllMovesForWhitePawn(Square[][] outSquare) {
		Square helpSquare[] = new Square[maxNumberOfPawnMoves];

		for (int i = 0; i < maxNumberOfPawnMoves; i++) {
			helpSquare[i] = new Square(boardSize, baseSize);
		}
		
		if (this.isAlive()) {
			if (this.isPawnInOwnBase()) {
				for (int i = 0; i < baseSize; i++) {
					helpSquare[i] = outSquare[baseSize][i];
				}
				for (int i = baseSize; i < 2 * baseSize; i++) {
					helpSquare[i] = outSquare[i - baseSize][baseSize];
				}
			} else if (position.getXCoord() == 0) {
				helpSquare[0] = outSquare[1][position.getYCoord()];
				helpSquare[1] = outSquare[0][position.getYCoord() - 1];
				if (position.getYCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[0][position.getYCoord() + 1];
				}
			} else if (position.getYCoord() == 0) {
				helpSquare[0] = outSquare[position.getXCoord()][1];
				helpSquare[1] = outSquare[position.getXCoord() - 1][0];
				if (position.getXCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[position.getXCoord() + 1][0];
				}
			} else if (position.getYCoord() == boardSize - 1) {
				helpSquare[0] = outSquare[position.getXCoord()][boardSize - 2];
				helpSquare[1] = outSquare[position.getXCoord() - 1][boardSize - 1];

				//Ngav Added to correct bug 
				//null pointer exception when position.getXCoord() = 7
				if (position.getXCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[position.getXCoord() + 1][boardSize - 1];
				}
	
			} else if (position.getXCoord() == boardSize - 1) {
				helpSquare[0] = outSquare[boardSize - 1][position.getYCoord() - 1];
				helpSquare[1] = outSquare[boardSize - 2][position.getYCoord()];

				//to avoid null pointer exception, when position.getYCoord() = 7
 				if (position.getYCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[boardSize - 1][position.getYCoord() + 1];
				}
 				
			} else {
				helpSquare[0] = outSquare[position.getXCoord() + 1][position.getYCoord()];
				helpSquare[1] = outSquare[position.getXCoord() - 1][position.getYCoord()];
				helpSquare[2] = outSquare[position.getXCoord()][position.getYCoord() + 1];
				helpSquare[3] = outSquare[position.getXCoord()][position.getYCoord() - 1];
			}
		}

		return helpSquare;
	}

	// finds all possible moves a pawn can make, without checking legality
	public Square[] getAllMovesForBlackPawn(Square[][] outSquare) {
		Square helpSquare[] = new Square[2 * baseSize];
		for (int i = 0; i < 2 * baseSize; i++) {
			helpSquare[i] = new Square(boardSize, baseSize);
		}
		if (this.isAlive()) {
			if (this.isPawnInOwnBase()) {
				for (int i = 0; i < baseSize; i++) {
					helpSquare[i] = outSquare[boardSize - 1 - baseSize][boardSize
							- 1 - i];
				}
				for (int i = baseSize; i < 2 * baseSize; i++) {
					helpSquare[i] = outSquare[boardSize + baseSize - 1 - i][boardSize
							- 1 - baseSize];
				}
			} else if (position.getXCoord() == 0) {
				helpSquare[0] = outSquare[1][position.getYCoord()];
				helpSquare[1] = outSquare[0][position.getYCoord() - 1];
				
				if (position.getYCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[0][position.getYCoord() + 1];
				}
			} else if (position.getYCoord() == 0) {
				helpSquare[0] = outSquare[position.getXCoord()][1];
				helpSquare[1] = outSquare[position.getXCoord() - 1][0];
				
				if (position.getXCoord() < boardSize - 1) {
					helpSquare[2] = outSquare[position.getXCoord() + 1][0];
				}
			} else if (position.getYCoord() == boardSize - 1) {
				helpSquare[0] = outSquare[position.getXCoord() + 1][boardSize - 1];
				helpSquare[1] = outSquare[position.getXCoord()][boardSize - 2];
				if (position.getXCoord() > 0) {
					helpSquare[2] = outSquare[position.getXCoord() - 1][boardSize - 1];
				}
			} else if (position.getXCoord() == boardSize - 1) {
				helpSquare[0] = outSquare[boardSize - 1][position.getYCoord() - 1];
				helpSquare[1] = outSquare[boardSize - 1][position.getYCoord() + 1];
				helpSquare[2] = outSquare[boardSize - 2][position.getYCoord()];
			} else {
				helpSquare[0] = outSquare[position.getXCoord() + 1][position.getYCoord()];
				helpSquare[1] = outSquare[position.getXCoord() - 1][position.getYCoord()];
				helpSquare[2] = outSquare[position.getXCoord()][position.getYCoord() + 1];
				helpSquare[3] = outSquare[position.getXCoord()][position.getYCoord() - 1];
			}
		}

		return helpSquare;
	}

	public Boolean isMoveLegit(Square toSquare){
		//First of all the square that the pawn is moved to, should be within board limits
		if (toSquare.getXCoord() < 0 || toSquare.getYCoord() < 0 || toSquare.getXCoord() >= boardSize || toSquare.getYCoord() >= boardSize) return false;

		int distance1, distance2;
		if (this.isAlive()) {
			if (white){
				if (this.isPawnInOwnBase()) 
				{
					if ((toSquare.getXCoord() == baseSize && toSquare.getYCoord() < baseSize) || (toSquare.getYCoord() == baseSize && toSquare.getXCoord() < baseSize))
						return true; //toSquare is adjacent to the base
					else
						return false; //toSquare is not adjacent to the base
				}
				// else if (( 
				//			position.getXCoord() == toSquare.getXCoord() && Math.abs(position.getYCoord() - toSquare.getYCoord()) == 1
				//			||
				//			position.getYCoord() == toSquare.getYCoord() && Math.abs(position.getXCoord() - toSquare.getXCoord()) == 1
				//		))
				else if ( (position.getXCoord() == toSquare.getXCoord() && Math.abs(position.getYCoord() - toSquare.getYCoord()) == 1)
						||
						  (position.getYCoord() == toSquare.getYCoord() && Math.abs(position.getXCoord() - toSquare.getXCoord()) == 1)
						)
				{
					distance1 = Math.max(position.getXCoord() + 1 - baseSize, position.getYCoord() + 1 - baseSize);
					distance2 = Math.max(toSquare.getXCoord() + 1 - baseSize, toSquare.getYCoord() + 1 - baseSize);
				}
				else
				{
					return false;
				}
			}
			else 
			{
				if (this.isPawnInOwnBase()) 
				{
					if ((toSquare.getXCoord() == boardSize-1-baseSize && toSquare.getYCoord() > boardSize-1-baseSize) || (toSquare.getYCoord() == boardSize-1-baseSize && toSquare.getXCoord() > boardSize-1-baseSize))
						return true; //toSquare is adjacent to the base
					else
						return false; //toSquare is not adjacent to the base
				}
				else if ((
							(position.getXCoord() == toSquare.getXCoord() && (Math.abs(position.getYCoord() - toSquare.getYCoord()) == 1))
							||
							(position.getYCoord() == toSquare.getYCoord() && (Math.abs(position.getXCoord() - toSquare.getXCoord()) == 1))
						))
				{
					distance1 = Math.max(boardSize - baseSize - position.getXCoord(), boardSize - baseSize - position.getYCoord());
					distance2 = Math.max(boardSize - baseSize - toSquare.getXCoord(), boardSize - baseSize - toSquare.getYCoord());
				}
				else
				{
					return false;
				}
				
			}
		}
		else{
			return false;
		}
		if (toSquare.isFree() && (distance2 >= distance1))
			return true;
		else
			return false;
	}
	
	// finds all legal moves for a pawn
	public Square[] getLegitMovesForWhitePawn(Square[][] outSquare) {
		int i = 0;
		int j = 0;
		Square legSquare[] = new Square[maxNumberOfPawnMoves];
		Square[] helpSquare = new Square[maxNumberOfPawnMoves];
		for (int ii = 0; ii < maxNumberOfPawnMoves; ii++) {
			helpSquare[ii] = new Square(boardSize, baseSize);
			legSquare[ii] = new Square(boardSize, baseSize);
		}
		legSquare = this.getAllMovesForWhitePawn(outSquare);
		while ((i < maxNumberOfPawnMoves) && ((legSquare[i].getXCoord() != 0) || (legSquare[i].getYCoord() != 0))) {
			int distanse = Math.max(position.getXCoord() + 1 - baseSize, position.getYCoord() + 1 - baseSize);
			int distanse2 = Math.max(legSquare[i].getXCoord() + 1 - baseSize, legSquare[i].getYCoord() + 1 - baseSize);
			if (legSquare[i].isFree() && (distanse2 >= distanse)) {
				helpSquare[j++] = legSquare[i];
			}
			i++;
		}
		return helpSquare;
	}

	// finds all legal moves for a pawn
	public Square[] getLegitMovesForBlackPawn(Square[][] outSquare) {
		int i = 0;
		int j = 0;
		Square[] legSquare = new Square[maxNumberOfPawnMoves];
		Square[] helpSquare = new Square[maxNumberOfPawnMoves];
		for (int ii = 0; ii < maxNumberOfPawnMoves; ii++) {
			helpSquare[ii] = new Square(boardSize, baseSize);
			legSquare[ii] = new Square(boardSize, baseSize);
		}
		legSquare = this.getAllMovesForBlackPawn(outSquare);
		while ((i < maxNumberOfPawnMoves) && ((legSquare[i].getXCoord() != 0) || (legSquare[i].getYCoord() != 0))) {
			int distanse = Math.max(boardSize - baseSize - position.getXCoord(), boardSize - baseSize - position.getYCoord());
			int distanse2 = Math.max(boardSize - baseSize - legSquare[i].getXCoord(), boardSize - baseSize - legSquare[i].getYCoord());
			if (legSquare[i].isFree() && (distanse2 >= distanse)) {
				helpSquare[j++] = legSquare[i];
			}
			i++;
		}
		return helpSquare;
	}

	// checking if a pawn can move, so that we can kill it
	public boolean isWhitePawnMovable(Square[][] outSquare) {
		Square[] helpSquare = new Square[2 * baseSize];
		helpSquare = this.getLegitMovesForWhitePawn(outSquare);
		
		boolean toRet = ((helpSquare[0].getXCoord() == 0) && (helpSquare[0].getYCoord() == 0)) ? false : true;
		
		return toRet;
	}

	// checking if a pawn can move, so that we can kill it
	public boolean isBlackPawnMovable(Square[][] outSquare) {
		// //nikolini
		if ((this.isPawnInEnemyBase()))// &&(this.position.getXCoord()!=0)&&(this.position.getYCoord()!=0))
			return true;
		// //nikolini
		Square[] helpSquare = new Square[2 * baseSize];
		helpSquare = this.getLegitMovesForBlackPawn(outSquare);
		/*
		 * System.out.println("-d-d-d-d-d-d-"); for (int
		 * ij=0;ij<helpSquare.length;ij++)
		 * System.out.print(helpSquare[ij].getXCoord
		 * ()+""+helpSquare[ij].getYCoord()+" "); System.out.println("");
		 */return ((helpSquare[0].getXCoord() == 0) && (helpSquare[0].getYCoord() == 0)) ? false
				: true;
	}

	// transform a pawn to a unique number
	public int pawn2Tag() {
		if (this.isAlive()) {
			return position.getXCoord() * boardSize + position.getYCoord() + 1;
		} else
			return boardSize * boardSize + 1;
	}

	// the inverse of the above procedure
	public Pawn tag2Pawn(int tg) {
		int tag = tg - 1;
		Pawn tagPawn = new Pawn(boardSize, baseSize);
		tagPawn.position.setXCoord((int) tag / boardSize);
		tagPawn.position.setYCoord((int) tag - boardSize * tagPawn.position.getXCoord());
//TODO CHECK
//		tagPawn.position.setXCoord((int) (tag / boardSize));
//		tagPawn.position.setYCoord((int) (tag - boardSize * (tagPawn.position.getXCoord())));
		
		return tagPawn;
	}



	public Square getPosition() {
		return position;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return 31 * 23 + getId();
	}

	// Equal IDs should be enough for equal objects
	@Override
	public boolean equals(Object object) {
		return (((Pawn) object).getId() == getId());
	}

	
}
