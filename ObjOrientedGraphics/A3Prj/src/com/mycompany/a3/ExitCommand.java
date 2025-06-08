package com.mycompany.a3;

import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class ExitCommand extends Command implements ActionListener {
	private GameWorld gw;

	public ExitCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Command cOk = new Command("Yes");
		Command cCancel = new Command("No");
		
		Command[] cmds = new Command[]{cOk, cCancel};
		Command c = Dialog.show("Are you sure you want to exit?", "", cmds);
		
		if (c == cOk) {
			gw.exitGame('y');
		} else {
			gw.exitGame('n');
		}
	}
}
