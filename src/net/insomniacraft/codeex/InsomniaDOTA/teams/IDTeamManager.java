package net.insomniacraft.codeex.InsomniaDOTA.teams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class IDTeamManager {

	private static IDTeam blue;
	private static IDTeam red;
	private static IDTeam neutral;

	private static int bluePlayers = 0;
	private static int redPlayers = 0;

	private static File redFile = new File(InsomniaDOTA.pFolder, "redTeam.txt");
	private static File blueFile = new File(InsomniaDOTA.pFolder, "blueTeam.txt");

	static {
		red = new IDTeam(Colour.RED);
		blue = new IDTeam(Colour.BLUE);
		neutral = new IDTeam(Colour.NEUTRAL);
	}

	public static void setTeam(Colour col, Player p) {
		removePlayer(getTeam(p), p);
		addPlayer(col, p);
		p.sendMessage("You are now on team "+col.toString()+"!");
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

	public static ArrayList<Player> getBluePlayers() {
		ArrayList<Player> b = new ArrayList<Player>();
		for (Player p: blue.getPlayers()) {
			b.add(p);
		}
		return b;
	}

	public static ArrayList<Player> getRedPlayers() {
		ArrayList<Player> r = new ArrayList<Player>();
		for (Player p: red.getPlayers()) {
			r.add(p);
		}
		return r;
	}

	public static ArrayList<Player> getNeutralPlayers() {
		ArrayList<Player> n = new ArrayList<Player>();
		for (Player p: neutral.getPlayers()) {
			n.add(p);
		}
		return n;
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
		String blueSpawn = bbw.readLine();
		String redString = rbw.readLine();
		String redSpawn = rbw.readLine();
		rbw.close();
		bbw.close();

		//Set/process players
		if (blueString != null) {
			String[] blueSP = blueString.split(";");
			for (int i = 0; i < blueSP.length; i++) {
				System.out.println("Testing blue for "+blueSP[i]);
				Player p = InsomniaDOTA.s.getPlayerExact(blueSP[i]);
				if (p == null) {
					continue;
				}
				if (p.isOnline()) {
					setTeam(Colour.BLUE, p);
				}
			}
		}
		if (redString != null) {
			String[] redSP = redString.split(";");
			for (int i = 0; i < redSP.length; i++) {
				System.out.println("Testing red for "+redSP[i]);
				Player p = InsomniaDOTA.s.getPlayerExact(redSP[i]);
				if (p == null) {
					continue;
				}
				if (p.isOnline()) {
					setTeam(Colour.RED, p);
				}
			}
		}

		Player[] players = InsomniaDOTA.s.getOnlinePlayers();
		for (Player p: players) {
			if (getTeam(p) == null) {
				setTeam(Colour.NEUTRAL, p);
			}
		}

		//Set/process spawns
		if (blueSpawn != null) {
			String [] bSpawnSP = blueSpawn.split(";");
			double x = Double.parseDouble(bSpawnSP [0]);
			double y = Double.parseDouble(bSpawnSP [1]);
			double z = Double.parseDouble(bSpawnSP [2]);
			Location l = new Location (InsomniaDOTA.s.getWorld("dota"), x, y,z);
			blue.setSpawn(l);
		}
		if (redSpawn != null) {
			String [] rSpawnSP = redSpawn.split(";");
			double x = Double.parseDouble(rSpawnSP [0]);
			double y = Double.parseDouble(rSpawnSP [1]);
			double z = Double.parseDouble(rSpawnSP [2]);
			Location l = new Location (InsomniaDOTA.s.getWorld("dota"), x, y,z);
			red.setSpawn(l);
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
			p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short)0, (Byte)Byte.valueOf("11")));
		}
		else if (col.toString().equals("RED")) {
			red.addPlayer(p);
			System.out.println("[DEBUG] Adding "+p.getName()+" to team red!");
			p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short)0, (Byte)Byte.valueOf("14")));
		}
		else if (col.toString().equals("NEUTRAL")) {
			neutral.addPlayer(p);
			System.out.println("[DEBUG] Adding "+p.getName()+" to team neutral!");
			p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
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
