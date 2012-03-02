package com.nuclearw.watchpm;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WatchPm extends JavaPlugin {
	static String mainDirectory = "plugins" + File.separator + "WatchPM";
	static File version = new File(mainDirectory + File.separator + "VERSION");
	static File config = new File(mainDirectory + File.separator + "config");
	static Properties prop = new Properties();
	
	private final WatchPmPlayerListener playerListener = new WatchPmPlayerListener(this);
	
	public String[] mCommands = new String[64];
	public String[] rCommands = new String[64];
	
	//public Player[] stalkers = null; 
	
	Logger log = Logger.getLogger("Minecraft");
	Logger pmlog = Logger.getLogger("PMLog");
	
	public Boolean pmLogging = true;
	
	public void onEnable() {
		new File(mainDirectory).mkdir();
		
		if((new File(mainDirectory + "no.log")).exists()) this.pmLogging = false;
		
		if(!config.exists()) {
			try {
				config.createNewFile();
				FileOutputStream out = new FileOutputStream(config);
				prop.put("MessageCommands", "msg,tell,pm");
				prop.put("ReplyCommands", "reply,r");
				prop.store(out, "Comma separated commands to watch for messages on.");
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		try {
		    FileHandler pmlogHandler = new FileHandler(mainDirectory + File.separator + "pm.log", true);
		    pmlogHandler.setFormatter(new WatchPmFormatter());
		    this.pmlog.addHandler(pmlogHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream in;
		try {
			in = new FileInputStream(config);
			prop.load(in);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		String mCommandsProp = prop.getProperty("MessageCommands");
		String rCommandsProp = prop.getProperty("ReplyCommands");
		
		int i = 0;
		
		if(mCommandsProp.contains(",")) {
			String mCommandsCut = mCommandsProp;
			do {
				int commaindex = mCommandsCut.indexOf(",");
				if(commaindex == -1) break;
				this.mCommands[i] = mCommandsCut.substring(0, commaindex);
				mCommandsCut = mCommandsCut.substring(commaindex+1);
				log.info("Loaded message command: "+this.mCommands[i]);
				i++;
			} while (mCommandsCut.indexOf(",") != -1);
			this.mCommands[i] = mCommandsCut;
			log.info("Loaded message command: "+this.mCommands[i]);
		} else {
			this.mCommands[0] = mCommandsProp;
			log.info("Loaded message command: "+this.mCommands[0]);
		}
		
		if(rCommandsProp.contains(",")) {
			String rCommandsCut = rCommandsProp;
			do {
				int commaindex = rCommandsCut.indexOf(",");
				if(commaindex == -1) break;
				this.rCommands[i] = rCommandsCut.substring(0, commaindex);
				rCommandsCut = rCommandsCut.substring(commaindex+1);
				log.info("Loaded message command: "+this.rCommands[i]);
				i++;
			} while (rCommandsCut.indexOf(",") != -1);
			this.rCommands[i] = rCommandsCut;
			log.info("Loaded message command: "+this.rCommands[i]);
		} else {
			this.rCommands[0] = rCommandsProp;
			log.info("Loaded message command: "+this.rCommands[0]);
		}
		
		if(!version.exists()) {
			updateVersion();
		} else {
			String vnum = readVersion();
			if(vnum.equals("0.1")) updateFrom(1);
			else if(vnum.equals("0.1.1") || vnum.equals("0.2") || vnum.equals("0.2.1")
					|| vnum.equals("0.2.2")) updateFrom(1);
			//In the future past versions can be checked for and dealt with here.
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		
		log.info("[WatchPM] version "+this.getDescription().getVersion()+" loaded.");
	}
	
	public void updateFrom(int age) {
		switch (age) {
			case 1:
				updateVersion();
			default:
				updateVersion();
		}
	}
	
	public void onDisable() {
		log.info("[WatchPM] version "+this.getDescription().getVersion()+" unloaded.");
	}
	
    public void broadcastToStalkers(String mSender, String mReceiver, String mMessage) {
    	Player[] online = getServer().getOnlinePlayers();
    	for(int i = 0; i < online.length; i++) {
    		if(!online[i].hasPermission("watchpm.stalker")) break;
    		online[i].sendMessage(mSender + " -> " + mReceiver + ": " + mMessage);
    	}
    	if(this.pmLogging) this.pmlog.info(mSender + " -> " + mReceiver + ": " + mMessage);
	}

    public void broadcastToStalkers(String rSender, String rMessage) {
    	Player[] online = getServer().getOnlinePlayers();
    	for(int i = 0; i < online.length; i++) {
    		if(!online[i].hasPermission("watchpm.stalker")) break;
    		online[i].sendMessage(rSender + " replied: " + rMessage);
    	}
    	if(this.pmLogging) this.pmlog.info(rSender + " replied: " + rMessage);
	}
    
	public boolean isPlayer(CommandSender sender) {
        return sender != null && sender instanceof Player;
    }
	
	public void updateVersion() {
		try {
			version.createNewFile();
			BufferedWriter vout = new BufferedWriter(new FileWriter(version));
			vout.write(this.getDescription().getVersion());
			vout.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}
	
	

	public String readVersion() {
		byte[] buffer = new byte[(int) version.length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(version));
			f.read(buffer);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		
		return new String(buffer);
	}
}
