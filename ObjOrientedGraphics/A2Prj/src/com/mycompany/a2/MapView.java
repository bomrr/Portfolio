package com.mycompany.a2;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.plaf.Border;

public class MapView extends Container implements Observer {
	private GameWorld gw;
	
	public MapView(GameWorld gw) {
		this.gw = gw;
		
		this.getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.rgb(255, 0, 0)));
	}
	
	public void update(Observable o, Object arg) {
		// code here to call the method in GameWorld (Observable) that output the
		// game object information to the console
		gw.updateMap();
	}
}
