package org.rlgame.gameplay;


public class Settings {

	//Constants
	//public static final int DIMBOARD	= 8; 	// board size
//	public static final int DIMBOARD	= 6; 	// board size

	//public static final int DIMBASE		= 2; 	// base size - used in Neural Net
	//public static final int NUMOFPAWNS	= 10;	
	//public static final int MAX_NUM_PAWN_MOVES = DIMBASE * 2; //4 for dimbase 2 
	
	
	//RLGame 2 * NEURAL_INPUT_SIZE will be the actual input size
//	public static final int NEURAL_HIDDEN_SIZE = DIMBOARD * DIMBOARD - 2 * DIMBASE * DIMBASE + 5;
//	public static final int NEURAL_INPUT_SIZE = 2 * (DIMBOARD * DIMBOARD - 2 * DIMBASE * DIMBASE + 5);
	public static final int NEURAL_OUTPUT_SIZE = 1;

	public static final int WHITE_PLAYER = 1;
	public static final int BLACK_PLAYER = 2;	

//	public static final boolean MINIMAX = true; 
	
//	public static final int PLIES = 3;
	public static final int WIN = 100;
	public static final int LOSS = -100;
	
	public static final int RL_PLAYER = 1;
	public static final int MM_PLAYER = 2;
	public static final int RANDOM_PLAYER = 3;
	public static final int HUMAN_PLAYER = 4;
	public static final int RL2_PLAYER = 5;
	
	public static final int MAX_MOVES_COUNTER = 10000;
	
	public static final String DATE_MASK= "yyyyMMdd_HH_mm_ss";
	//-ft
	public static int FIRST_TURN = WHITE_PLAYER;
	
	//-pw
	public static int PLAYER_W_MODE = RL_PLAYER;

	//-dw
	public static int PLAYER_W_PLIES = 3;
	
	//-pb
	public static int PLAYER_B_MODE = RL_PLAYER;
	
	//-db
	public static int PLAYER_B_PLIES = 3;	

	
	//Parameters
	public static double eGreedyWhite	= 0.9;
	public static double eGreedyBlack 	= 0.9;
	public static double alpha 			= 0.001;
	public static double lambda 		= 0.5;
	
	public static double whiteGamma		= 0.95;
	public static double blackGamma		= 0.95;
	public static double whiteLamda		= 0.5;
	public static double blackLamda		= 0.5;	
	
	public static double whiteReward	= 100;
	public static double blackReward	= 100;
	
	public static String whiteVWeightsName  = "whVWeights";
	public static String whiteWWeightsName  = "whWWeights";	

	public static String blackVWeightsName  = "blVWeights";
	public static String blackWWeightsName  = "blWWeights";	

	
//	public static int whiteNeuralHiddenSize = (DIMBOARD * DIMBOARD - 2 * DIMBASE * DIMBASE + 5);
//	public static int blackNeuralHiddenSize = (DIMBOARD * DIMBOARD - 2 * DIMBASE * DIMBASE + 5);	
	
	///Static Methods
	public static String playerModeSet(int turn, int val) {
		String playerMode = "RL";
		if (turn == Settings.WHITE_PLAYER) {
			if (val > 0 && val <= 5) {
				Settings.PLAYER_W_MODE = val;
				if (val == Settings.MM_PLAYER) {
					playerMode = "MINMAX";
				}
				if (val == Settings.RANDOM_PLAYER) {
					playerMode = "RANDOM";
				}
				if (val == Settings.HUMAN_PLAYER) {
					playerMode = "HUMAN";
				}
				if (val == Settings.RL2_PLAYER) {
					playerMode = "RL2";
				}
			}
		} else if (turn == Settings.BLACK_PLAYER) {
			if (val > 0 && val <= 5) {
				Settings.PLAYER_B_MODE = val;
				if (val == Settings.MM_PLAYER) {
					playerMode = "MINMAX";
				}
				if (val == Settings.RANDOM_PLAYER) {
					playerMode = "RANDOM";
				}
				if (val == Settings.HUMAN_PLAYER) {
					playerMode = "HUMAN";
				}
				if (val == Settings.RL2_PLAYER) {
					playerMode = "RL2";
				}
			}			
		}
		return playerMode;
	}
	
	public static String getPlayerMode(int turn) {
		String playerMode = "RL";
		if (turn == Settings.WHITE_PLAYER) {
			if (Settings.PLAYER_W_MODE == Settings.MM_PLAYER) {
				playerMode = "MINMAX";
			}
			if (Settings.PLAYER_W_MODE == Settings.RANDOM_PLAYER) {
				playerMode = "RANDOM";
			}
			if (Settings.PLAYER_W_MODE == Settings.HUMAN_PLAYER) {
				playerMode = "HUMAN";
				
			}
			if (Settings.PLAYER_W_MODE == Settings.RL2_PLAYER) {
				playerMode = "RL2";
			}
		} else if (turn == Settings.BLACK_PLAYER) {
			if (Settings.PLAYER_B_MODE == Settings.MM_PLAYER) {
				playerMode = "MINMAX";
			}
			if (Settings.PLAYER_B_MODE == Settings.RANDOM_PLAYER) {
				playerMode = "RANDOM";
			}
			if (Settings.PLAYER_B_MODE == Settings.HUMAN_PLAYER) {
				playerMode = "HUMAN";
			}
			if (Settings.PLAYER_B_MODE == Settings.RL2_PLAYER) {
				playerMode = "RL2";
			}
		}
		return playerMode;
	}	
}
