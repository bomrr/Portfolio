package com.mycompany.a2;

import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class AboutCommand extends Command implements ActionListener {

	public AboutCommand(String command) {
		super(command);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Command cOk = new Command("Close");
		
		Command c = Dialog.show("About:", "Chris Parons\nCSC-133", cOk);
	}
}