package org.rlgame.common;

public class ObservationCandidateMove {
	private int pawnId;
	private int targetCoordX;
	private int targetCoordY;
	private int targetCoordZ;
	private double [] inputNode;
	private double envReward;

	
	public ObservationCandidateMove() {
		targetCoordX = -1;
		targetCoordY = -1;
		targetCoordZ = -1;
	}

	public int getPawnId() {
		return pawnId;
	}
	public void setPawnId(int pawnId) {
		this.pawnId = pawnId;
	}
	public int getTargetCoordX() {
		return targetCoordX;
	}
	public void setTargetCoordX(int targetCoordX) {
		this.targetCoordX = targetCoordX;
	}
	public int getTargetCoordY() {
		return targetCoordY;
	}
	public void setTargetCoordY(int targetCoordY) {
		this.targetCoordY = targetCoordY;
	}
	
	
	public int getTargetCoordZ() {
		return targetCoordZ;
	}

	public void setTargetCoordZ(int targetCoordZ) {
		this.targetCoordZ = targetCoordZ;
	}

	public double[] getInputNode() {
		return inputNode;
	}
	public void setInputNode(double[] inputNode) {
		this.inputNode = inputNode;
	}
	public double getEnvReward() {
		return envReward;
	}
	public void setEnvReward(double envReward) {
		this.envReward = envReward;
	}
	
	
	
}
