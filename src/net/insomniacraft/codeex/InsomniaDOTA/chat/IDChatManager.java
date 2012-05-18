package net.insomniacraft.codeex.InsomniaDOTA.chat;

import java.util.ArrayList;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class IDChatManager {

	private static ArrayList<Player> allChat = new ArrayList<Player>();

	public static String getFormat(Player p, String m) {
		String format;
		String name = "<" + p.getName() + ">";
		if (allChat.contains(p)) {
			name = "(All) <" + p.getName() + ">";
			allChat.remove(p);
		}
		Colour col = IDTeamManager.getTeam(p);
		if (col.toString().equals("RED")) {
			format = ChatColor.RED + name + " " + ChatColor.WHITE + m;
		} else if (col.toString().equals("BLUE")) {
			format = ChatColor.BLUE + name + " " + ChatColor.WHITE + m;
		} else {
			format = ChatColor.GRAY + name + " " + ChatColor.WHITE + m;
		}
		return format;
	}

	public static boolean isAllChat(Player p) {
		if (allChat.contains(p)) {
			return true;
		}
		return false;
	}

	public static void addToAllChat(Player p) {
		allChat.add(p);
	}

}
