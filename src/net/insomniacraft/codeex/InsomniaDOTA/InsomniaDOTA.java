package net.insomniacraft.codeex.InsomniaDOTA;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurretManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.IDGameManager;

public class InsomniaDOTA extends JavaPlugin {
	
	public static Logger l = Logger.getLogger("Minecraft");
	public static Server s;
	public static File pFolder = new File("plugins/InsomniaDOTA");

	public void onEnable() {
		s = getServer();
		IDListener listener = new IDListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		IDCommands commands = new IDCommands(this);
		getCommand("setup").setExecutor(commands);
		getCommand("set").setExecutor(commands);
		getCommand("clear").setExecutor(commands);
		getCommand("rdy").setExecutor(commands);
		getCommand("join").setExecutor(commands);
		getCommand("reset").setExecutor(commands);
		getCommand("info").setExecutor(commands);
		getCommand("all").setExecutor(commands);
		getCommand("teamswitch").setExecutor(commands);
		getCommand("b").setExecutor(commands);		
		
		if (!pFolder.exists()) {
			pFolder.mkdir();
		}
		
		try {
			//Load teams
			IDTeamManager.load();
			//Load turrets
			IDTurretManager.load();
			//Load nexus
			IDGameManager.load();
		} catch (Exception e) {
			l.severe("Error loading files!");
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		//Save teams
		try {
			IDTeamManager.save();
		} catch (Exception e) {
			l.severe("Error saving teams!");
			e.printStackTrace();
		}
		//Save turrets
		try {
			IDTurretManager.save();
		} catch (Exception e) {
			l.severe("Error saving turrets!");
			e.printStackTrace();
		}
		//Save nexus
		try {
			IDGameManager.save();
		} catch (Exception e) {
			l.severe("Error saving nexus!");
			e.printStackTrace();
		}
	}
	
	public Player findPlayer(String spl) {
		Player[] players = getServer().getOnlinePlayers();
		for (Player p: players) {
			if (p.getName().equalsIgnoreCase(spl)) {
				return p;
			}
		}
		return null;
	}
	
	public static void broadcast(String str) {
		s.broadcastMessage(str);
	}
}
