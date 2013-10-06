package net.otxs.bridge.core.commands.act;

import net.otxs.bridge.core.commands.Command;
import net.otxs.bridge.core.commands.Commands;
import net.otxs.bridge.core.exceptions.OTException;
import net.otxs.bridge.core.modules.Module;

public class MetaCommands extends Commands {

	public static void init() {
		addToCommands(new Verbose(), Category.META, Sophistication.TOP);
	}

	public static class Verbose extends Command {
		@Override
		protected void action(String[] args) throws OTException {
			Module.toggleVerbose();
		}
	}

}
