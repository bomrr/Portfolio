package com.mycompany.a3;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.plaf.Border;

public class MapView extends Container implements Observer {
	private GameWorld gw;

	public MapView(GameWorld gw) {
		this.gw = gw;

		this.getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.rgb(255, 0, 0)));
		
		// For testing
		//System.out.println("Mapview size: " + getWidth() + ", " + getHeight());
		//System.out.println("Mapview position: " + getX() + ", " + getY());
	}

	public void update(Observable o, Object arg) {
		// code here to call the method in GameWorld (Observable) that output the
		// game object information to the console
		gw.updateMap();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		gw.getGameObs().reset();

		while (gw.getGameObs().hasNext()) {
			GameObject currentObject = gw.getGameObs().getNext();
			
			//g.setColor(ColorUtil.rgb(255, 0, 0));
			//g.drawRect(getX(), getY(), getWidth()-1, getHeight()-1);
			
			//Point properPosition = new Point();
			//properPosition.setX(getAbsoluteX() + gw.getGameObject(currentObject).getLocation().getX());
			//properPosition.setY(getAbsoluteY() + gw.getGameObject(currentObject).getLocation().getX());
			
			//g.drawRect(0, 0, (int) gw.getWorldWidth(), (int) gw.getWorldHeight());

			currentObject.draw(g, gw.getGameObject(currentObject).getLocation());
			//currentObject.draw(g, properPosition);
		}
	}

	@Override
	public void pointerPressed(int x, int y) {
		// make pointer location relative to parent's origin
		x = x - getParent().getAbsoluteX();
		y = y - getParent().getAbsoluteY();
		Point pPtrRelPrnt = new Point(x, y);
		Point pCmpRelPrnt = new Point(getX(), getY());
		
		// Store the last point clicked in the game world
		int[] values = {x, y};
		gw.setLastPointClicked(values);
		
		// For testing
		System.out.println("Click: " + x + ", " + y);
		
		if (!gw.isPaused()) {
			return;
		}

		// If in selection mode, move the object instead
		if (gw.isSelectedMove()) {
			gw.getGameObs().reset();
			GameObject current = null;

			// Find the selected object
			while (gw.getGameObs().hasNext()) {
				current = gw.getGameObs().getNext();

				if (current.isSelected() == true) {
					// For testing
					System.out.println("Object selected: " + current.toString());

					// Tell the game world to move the object
					gw.moveObject(current);

					gw.setSelectedMove(false);
					
					repaint();
					return;
				}
			}
		}
		
		IIterator iterator = gw.getGameObs();
		iterator.reset();
		GameObject shape = null;
		while (iterator.hasNext()) {
			shape = iterator.getNext();
			
			if (shape.contains(pPtrRelPrnt, pCmpRelPrnt) && shape instanceof ISelectable) {
				shape.setSelected(true);
				
				// For testing
				System.out.println("Shape selected: " + shape.toString());
				
				break;
			}
		}
		
		// Unselect all other objects
		iterator.reset();
		while (iterator.hasNext()) {
			GameObject obj = iterator.getNext();
			if (obj instanceof ISelectable && shape != obj) {
				((ISelectable)obj).setSelected(false);
			}
		}
		
		repaint();
	}
}
