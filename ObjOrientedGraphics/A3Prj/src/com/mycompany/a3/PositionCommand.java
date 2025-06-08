package com.mycompany.a3;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class PositionCommand extends Command implements ActionListener {
	private GameWorld gw;

	public PositionCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (gw.isPaused()) {
			gw.setSelectedMove(true);
			System.out.println("Select new location.");
		}
		
	}
}
