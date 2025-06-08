package com.mycompany.a3;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;

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
	
	/**
	 * Draws an upside-down triangle at the flag's location with the flag number displayed.
	 */
	@Override
	public void draw(Graphics g, Point pCmpRelPrnt) {
		// Get the object's current location
		int centerX = (int)getLocation().getX();
		int centerY = (int)getLocation().getY();
		
		int topLeftX = centerX - getSize() / 2;
		int topLeftY = centerY - getSize() / 2;
		
		// Set color
		g.setColor(getColor());
		
		int[] xPoints = {
				topLeftX,
				topLeftX + getSize(),
				topLeftX + (getSize())/2
		};
		int[] yPoints = {
				topLeftY,
				topLeftY,
				topLeftY + (getSize())
		};
		
		g.drawPolygon(xPoints, yPoints, 3);
		g.fillPolygon(xPoints, yPoints, 3);
		
		// Draw  the flag number
		g.setColor(ColorUtil.BLACK);
		g.drawString("" + this.getFlagNum(), topLeftX+(getSize()/3), topLeftY+(getSize()/3));
		
		if (isSelected()) {
			g.drawRect(topLeftX, topLeftY, this.getSize(), this.getSize());
		}
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
