package com.mycompany.a3;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class SetFoodConsumptionCommand extends Command implements ActionListener {
	private GameWorld gw;

	public SetFoodConsumptionCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		gw.setConsumeRate();
	}
}
