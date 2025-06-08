package com.mycompany.a2;

import com.codename1.charts.models.Point;

/**
 * The ant must reach all flags in order before winning.
 */
public class Flag extends Fixed {
	private int seqNum;

	/**
	 * Constructor.
	 * @param size
	 * @param color
	 * @param location
	 * @param flagNum
	 */
	public Flag(int size, int color, Point location, int flagNum) {
		super(size, color, location);
		this.seqNum = flagNum;
	}

	public int getFlagNum() {
		return seqNum;
	}

	public void setFlagNum(int flagNum) {
		this.seqNum = flagNum;
	}

	@Override
	public void setColor(int color) {
		// The color of a flag cannot be changed.
	}
	
	@Override
	public String toString() {
		return "Flag: " + super.toString() + ", seqNum=" + seqNum;
	}
	
}
