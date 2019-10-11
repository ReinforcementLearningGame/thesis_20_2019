package org.rlgame.common;

public class AgentAction {

	private int pawnId;
	private int targetCoordX;
	private int targetCoordY;
	private int targetCoordZ;
	
	private boolean exploitMode;
	private double maxValue;

	
	
	public AgentAction() {
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

	public boolean isExploitMode() {
		return exploitMode;
	}
	public void setExploitMode(boolean exploitMode) {
		this.exploitMode = exploitMode;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	
	
	
	
}
