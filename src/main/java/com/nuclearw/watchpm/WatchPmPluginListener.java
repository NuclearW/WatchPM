package com.nuclearw.watchpm;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class WatchPmPluginListener extends ServerListener {
	public static WatchPm plugin;
	
	public WatchPmPluginListener(WatchPm instance) {
		plugin = instance;
	}
	
    public void onPluginEnable(PluginEnableEvent event) {
        WatchPmPermissionsHandler.onEnable(event.getPlugin());
    }
}
