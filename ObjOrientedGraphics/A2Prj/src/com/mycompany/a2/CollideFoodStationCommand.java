package com.mycompany.a2;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class CollideFoodStationCommand extends Command implements ActionListener {
	private GameWorld gw;

	public CollideFoodStationCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		gw.foodStationTouch();
	}
}
