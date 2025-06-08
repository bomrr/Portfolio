package com.mycompany.a2;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class HelpCommand extends Command implements ActionListener {

	public HelpCommand(String command) {
		super(command);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Command cOk = new Command("Close");
		Dialog textDialog = new Dialog();
		
		TextArea commandsLabel = new TextArea();
		commandsLabel.setText( "a = Accelerate\n"
				+ "b = Brake\n"
				+ "l = Left turn\n"
				+ "r = Right turn\n"
				+ "c = Set food consumption\n"
				+ "f = Collide with food station\n"
				+ "g = Collide with spider\n"
				+ "t = Advance the game clock\n");
		commandsLabel.setEditable(false);
		
		textDialog.add(commandsLabel);
		
		Button closeButton = new Button(cOk);
		textDialog.add(closeButton);
		
		textDialog.show();
	}
}