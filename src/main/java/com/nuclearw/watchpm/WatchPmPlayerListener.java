package com.nuclearw.watchpm;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.nuclearw.watchpm.WatchPm;


public class WatchPmPlayerListener implements Listener {
	public static WatchPm plugin;

	public WatchPmPlayerListener(WatchPm instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		if(!command.startsWith("/")) return;
		command = command.substring(1);

		//String[] args = null;
		String cmd = null;
		ArrayList<String> args = new ArrayList<String>();
		int i =0;

		if(command.contains(" ")) {
			String commandCut = command;
			int spaceindex = commandCut.indexOf(" ");
			if(spaceindex != -1) {
				cmd = commandCut.substring(0, spaceindex);
				commandCut = commandCut.substring(spaceindex+1);
			}
			do {
				spaceindex = commandCut.indexOf(" ");
				if(spaceindex == -1) break;
				args.add(i, commandCut.substring(0, spaceindex));
				commandCut = commandCut.substring(spaceindex+1);
				i++;
			} while (commandCut.indexOf(" ") != -1);
			args.add(i, commandCut);
		} else {
			cmd = command;
		}

		for(int j = 0; j < plugin.mCommands.length; j++) {
			if(cmd.equalsIgnoreCase(plugin.mCommands[j])) {
				if(args.size() < 2) return;
				String mSender = event.getPlayer().getName();
				Player tPlayer = plugin.getServer().getPlayer(args.get(0));
				if(tPlayer == null) return;
				if(!tPlayer.isOnline()) return;
				String mReceiver = plugin.getServer().getPlayer(args.get(0)).getName();
				String mMessage = args.get(1);
				for(int k = 2; k <= args.size()-1 ; k++) {
					mMessage = mMessage + " " + args.get(k);
				}
				mMessage = mMessage.trim();
				plugin.broadcastToStalkers(mSender, mReceiver, mMessage);
			}
		}
		for(int j = 0; j < plugin.rCommands.length; j++) {
			if(cmd.equalsIgnoreCase(plugin.rCommands[j])) {
				if(args.size() < 1) return;
				String rSender = event.getPlayer().getName();
				String rMessage = args.get(0);
				for(int k = 1; k <= args.size()-1; k++) {
					rMessage = rMessage + " " + args.get(k);
				}
				rMessage = rMessage.trim();
				plugin.broadcastToStalkers(rSender, rMessage);
			}
		}
	}
}
