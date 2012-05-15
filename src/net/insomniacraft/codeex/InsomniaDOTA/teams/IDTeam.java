package net.insomniacraft.codeex.InsomniaDOTA.teams;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class IDTeam {

	public enum Colour {RED, BLUE, NEUTRAL};
	
	private final Colour colour;
	private ArrayList<Player> players;
	private ArrayList<Player> readyPlayers;
	
	//Constructor
	public IDTeam(Colour c) {
		this.colour = c;
		players = new ArrayList<Player>();
		readyPlayers = new ArrayList<Player>();
	}
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public void readyPlayer(Player p) {
		if (players.contains(p)) {
			readyPlayers.add(p);
		}
	}
	
	public void unreadyPlayer(Player p) {
		if (readyPlayers.contains(p)) {
			readyPlayers.remove(p);
		}
	}
	
	public boolean isReady() {
		return readyPlayers.containsAll(players);
	}
	
	public boolean isPlayerReady(Player p) {
		if (players.contains(p) && readyPlayers.contains(p)) {
			return true;
		}
		return false;
	}
	
	public String getColor() {
		return colour.toString();
	}
	
	public boolean hasPlayer(Player p) {
		for (Player player: players) {
			if (player.getName().equalsIgnoreCase(p.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	public String[] toStringArr() {
		String[] pString = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			pString[i] = players.get(i).getName();
		}
		return pString;
	}
	
	public static Colour getColourFromStr(String s) {
		if (s.equalsIgnoreCase("RED")) {
			return Colour.RED;
		} else if (s.equalsIgnoreCase("BLUE")) {
			return Colour.BLUE;
		} else {
			return null;
		}
	}
}
