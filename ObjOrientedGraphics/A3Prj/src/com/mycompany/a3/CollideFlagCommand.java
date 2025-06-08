package com.mycompany.a3;

import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class CollideFlagCommand extends Command implements ActionListener {
	private GameWorld gw;

	public CollideFlagCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		TextField numberInput = new TextField();
		Command cOk = new Command("Ok");
		Command cCancel = new Command("Cancel");
		
		Command[] cmds = new Command[]{cOk, cCancel};
		Command c = Dialog.show("Enter flag number:", numberInput, cmds);
		
		try {
			if (c == cOk) {
				gw.flagTouch(Integer.parseInt(numberInput.getText()));
			} else {
				// Do nothing
			}
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid flag input detected.");
		}
	}
}
