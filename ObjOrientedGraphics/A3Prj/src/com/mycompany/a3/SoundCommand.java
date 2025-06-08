package com.mycompany.a3;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class SoundCommand extends Command implements ActionListener {
	private GameWorld gw;
	private Game game;

	public SoundCommand(String command, GameWorld gw, Game game) {
		super(command);
		this.gw = gw;
		this.game = game;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		gw.setSound(!gw.isSound());
		
		if (gw.isSound() && !game.isPaused()) {
			gw.getBackgroundSound().play();
			
			// For testing
			//System.out.println("Sound unpased. isPaused: " + game.isPaused());
			
		} else {
			gw.getBackgroundSound().pause();
		}
	}
}