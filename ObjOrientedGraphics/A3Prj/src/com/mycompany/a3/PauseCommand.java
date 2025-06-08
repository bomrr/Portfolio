package com.mycompany.a3;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.UITimer;

public class PauseCommand extends Command implements ActionListener {
	private GameWorld gw;
	private UITimer clockTimer;
	private Game game;
	private Boolean isPaused = false;
	private String command;
	private Button pauseButton;

	public PauseCommand(String command, GameWorld gw, UITimer clockTimer, Game game, Button pauseButton) {
		super(command);
		this.gw = gw;
		this.clockTimer = clockTimer;
		this.game = game;
		this.command = command;
		this.pauseButton = pauseButton;
	}

	/**
	 * Pauses or resumes the game when triggered.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		isPaused = !isPaused;
		
		if (isPaused) {
			clockTimer.cancel();
			gw.getBackgroundSound().pause();
			game.setPaused(isPaused);
			gw.setPaused(isPaused);
			
			command = "Play";
			pauseButton.setText(command);
			game.revalidate();
			System.out.println("Game paused.");
		} else {
			clockTimer.schedule(20, true, game);
			game.setPaused(isPaused);
			gw.setPaused(isPaused);
			
			if (gw.isSound() && isPaused == false) {
				gw.getBackgroundSound().play();
			}
			
			command = "Pause";
			pauseButton.setText(command);
			game.revalidate();
			System.out.println("Game resumed.");
			
			// Unselect all objects
			IIterator iterator = gw.getGameObs();
			iterator.reset();
			GameObject shape = null;
			while (iterator.hasNext()) {
				shape = iterator.getNext();
				
				if (shape.isSelected()) {
					shape.setSelected(false);
				}
			}
		}
	}
	
	public boolean isPaused() {
		return isPaused;
	}
}