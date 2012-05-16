package net.insomniacraft.codeex.InsomniaDOTA.teams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;


import org.bukkit.Location;
import org.bukkit.entity.Player;


public class IDTeamManager {
	
	private static IDTeam blue;
	private static IDTeam red;
	private static IDTeam neutral;
	
	private static int bluePlayers = 0;
	private static int redPlayers = 0;
	
	private static File redFile = new File(InsomniaDOTA.pFolder, "red.txt");
	private static File blueFile = new File(InsomniaDOTA.pFolder, "blue.txt");
	
	static {
		red = new IDTeam(Colour.RED);
		blue = new IDTeam(Colour.BLUE);
		neutral = new IDTeam(Colour.NEUTRAL);
	}
	
	public static void setTeam(Colour col, Player p) {
		removePlayer(getTeam(p), p);
		addPlayer(col, p);
	}
	
	public static void removeFromTeam(Player p) {
		removePlayer(getTeam(p), p);
	}
	
	public static void addReady(Colour col, Player p) {
		if (col.toString().equals("RED")) {
			red.readyPlayer(p);
			InsomniaDOTA.broadcast(p.getName()+" is now ready!");
		} else if (col.toString().equals("BLUE")) {
			blue.readyPlayer(p);
		}
	}
	
	public static void removeReady(Colour col, Player p) {
		if (col.toString().equals("RED")) {
			red.unreadyPlayer(p);
		} else if (col.toString().equals("BLUE")) {
			blue.unreadyPlayer(p);
		}
	}
	
	public static boolean isPlayerReady(Player p) {
		boolean r = red.isPlayerReady(p);
		boolean b = blue.isPlayerReady(p);
		if (r || b) {
			return true;
		}
		return false;
	}

	//Method that returns the team colour of the player passed in
	//If player is not on any team will return null
	public static Colour getTeam(Player p) {
		if (blue.hasPlayer(p)) {
			return Colour.BLUE;
		}
		else if (red.hasPlayer(p)) {
			return Colour.RED;
		}
		else if (neutral.hasPlayer(p)) {
			return Colour.NEUTRAL;
		}
		return null;
	}
	
	public static int getRedCount() {
		return redPlayers;
	}
	
	public static int getBlueCount() {
		return bluePlayers;
	}
	
	public static void reset() {
		//Set all players to neutral team
		for (Player p: blue.getPlayers()) {
			setTeam(Colour.NEUTRAL, p);
		}
		for (Player p: red.getPlayers()) {
			setTeam(Colour.NEUTRAL, p);
		}
		//Reset player count
		bluePlayers = 0;
		redPlayers = 0;
	}
	
	public static boolean isAllReady() {
		if (blue.isReady() && red.isReady()) {
			return true;
		}
		return false;
	}
	
	public static void setSpawn(Colour col, Location l) {
		if (col.toString().equals("RED")) {
			red.setSpawn(l);
		} else if (col.toString().equals("BLUE")){
			blue.setSpawn(l);
		}
	}
	public static Location getSpawn(Colour col){
		if (col.toString().equals("RED")) {
			return red.getSpawn();
		} else if (col.toString().equals("BLUE")){
			return blue.getSpawn();
		}
		return null;
	}
	public static void save() throws IOException {
		redFile.createNewFile();
		blueFile.createNewFile();
		
		FileWriter rfw = new FileWriter(redFile);
		FileWriter bfw = new FileWriter(blueFile);
		PrintWriter rpw = new PrintWriter(rfw);
		PrintWriter bpw = new PrintWriter(bfw);
		String blueString = "";
		String redString = "";
		for (String s: blue.toStringArr()) {
			blueString = blueString + s + ";";
		}
		bpw.println(blueString);
		Location bl = blue.getSpawn();
		if (bl != null) {
			bpw.println(bl.getX() + ";" + bl.getY() + ";" + bl.getZ());
		}
		for (String s: red.toStringArr()) {
			redString = redString + s + ";";
		}
		rpw.println(redString);
		Location rl = red.getSpawn();
		if (rl != null) {
			rpw.println(rl.getX() + ";" + rl.getY() + ";" + rl.getZ());
		}
		rpw.close();
		bpw.close();
		System.out.println("[DEBUG] Successfully saved teams!");
	}
	
	public static void load() throws IOException {
		if (!(redFile.exists()) || !(blueFile.exists())) {
			return;
		}
		//Read info from file
		FileReader rfw = new FileReader(redFile);
		FileReader bfw = new FileReader(blueFile);
		BufferedReader rbw = new BufferedReader(rfw);
		BufferedReader bbw = new BufferedReader(bfw);
		String blueString = bbw.readLine();
		String blueSpawn =bbw.readLine();
		String redString = rbw.readLine();
		String redSpawn = rbw.readLine();
		rbw.close();
		bbw.close();
		
		//Set/process players
		String[] blueSP = blueString.split(";");
		String[] redSP = redString.split(";");
		
		for (String str: blueSP) {
			Player p = InsomniaDOTA.s.getPlayer(str);
			if (p == null) {
				continue;
			}
			if (p.isOnline()) {
				setTeam(Colour.BLUE, p);
			}
		}
		for (String str: redSP) {
			Player p = InsomniaDOTA.s.getPlayer(str);
			if (p == null) {
				continue;
			}
			if (p.isOnline()) {
				setTeam(Colour.RED, p);
			}
		}
		
		Player[] players = InsomniaDOTA.s.getOnlinePlayers();
		for (Player p: players) {
			if (getTeam(p) == null) {
				setTeam(Colour.NEUTRAL, p);
			}
		}
		//Set/process spawns
		if (blueSpawn != null){
			String [] bSpawnSP = blueSpawn.split(";");
			double x = Double.parseDouble(bSpawnSP [0]);
			double y = Double.parseDouble(bSpawnSP [1]);
			double z = Double.parseDouble(bSpawnSP [2]);
			Location l = new Location (InsomniaDOTA.s.getWorld("dota"), x, y,z);
			blue.setSpawn(l);
		}
		if (redSpawn != null){
			String [] rSpawnSP = redSpawn.split(";");
			double x = Double.parseDouble(rSpawnSP [0]);
			double y = Double.parseDouble(rSpawnSP [1]);
			double z = Double.parseDouble(rSpawnSP [2]);
			Location l = new Location (InsomniaDOTA.s.getWorld("dota"), x, y,z);
		}
		
		System.out.println("[DEBUG] Successfully loaded teams!");
	}
	
	private static void addPlayer(Colour col, Player p) {
		if (col == null) {
			return;
		}
		
		if (col.toString().equals("BLUE")) {
			blue.addPlayer(p);
			System.out.println("[DEBUG] Adding "+p.getName()+" to team blue!");
		}
		else if (col.toString().equals("RED")) {
			red.addPlayer(p);
			System.out.println("[DEBUG] Adding "+p.getName()+" to team red!");
		}
		else if (col.toString().equals("NEUTRAL")) {
			neutral.addPlayer(p);
			System.out.println("[DEBUG] Adding "+p.getName()+" to team neutral!");
		}
		countPlayers();
	}
	
	private static void removePlayer(Colour col, Player p) {
		if (col == null) {
			return;
		}
		
		if (col.toString().equals("BLUE")) {
			blue.removePlayer(p);
			System.out.println("[DEBUG] Removing "+p.getName()+" from team blue!");
		}
		else if (col.toString().equals("RED")) {
			red.removePlayer(p);
			System.out.println("[DEBUG] Removing "+p.getName()+" from team red!");
		}
		else if (col.toString().equals("NEUTRAL")) {
			neutral.removePlayer(p);
			System.out.println("[DEBUG] Removing "+p.getName()+" from team neutral!");
		}
		countPlayers();
	}
	
	private static void countPlayers() {
		bluePlayers = blue.getPlayerCount();
		redPlayers = red.getPlayerCount();
	}
}
