package net.insomniacraft.codeex.InsomniaDOTA.structures.turrets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import net.insomniacraft.codeex.InsomniaDOTA.IDCommands;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret.Turret;

public class IDTurretManager {

	private static IDTurret[] redTurrets = new IDTurret[6];
	private static IDTurret[] blueTurrets = new IDTurret[6];

	private static int defaultHealth = 20;

	private static boolean allSet = false;

	private static File turretFile = new File(InsomniaDOTA.pFolder, "turrets.txt");

	public static void setTurret(ArrayList<Block> bl, Colour c, Turret id, int health) {
		if (c.toString().equals("RED")) {
			// See if turret already exists
			for (int i = 0; i < 6; i++) {
				if (redTurrets[i] == null) {
					continue;
				}
				if (redTurrets[i].getId().toString().equals(id.toString())) {
					redTurrets[i] = null;
				}
			}
			// Once here it is sure there are no turrets of that id & colour so make one
			IDTurret rTurret = new IDTurret(bl, defaultHealth, id, c);
			redTurrets[tIndex(redTurrets)] = rTurret;
		} else if (c.toString().equals("BLUE")) {
			// See if turret already exists
			for (int i = 0; i < 6; i++) {
				if (blueTurrets[i] == null) {
					continue;
				}
				//If one does, make it null
				if (blueTurrets[i].getId().toString().equals(id.toString())) {
					blueTurrets[i] = null;
				}
			}
			// Once here it is sure there are no turrets of that id & colour so make one
			IDTurret bTurret = new IDTurret(bl, health, id, c);
			blueTurrets[tIndex(blueTurrets)] = bTurret;
		}
		// After each single turret set, check if they are all set
		allSet = checkAllSet();
	}
	
	public static void setTurret(IDTurretParams itp) {
		if (itp.col.toString().equals("RED")) {
			// See if turret already exists
			for (int i = 0; i < 6; i++) {
				if (redTurrets[i] == null) {
					continue;
				}
				if (redTurrets[i].getId().toString().equals(itp.id.toString())) {
					redTurrets[i] = null;
				}
			}
			// Once here it is sure there are no turrets of that id & colour so make one
			IDTurret rTurret = new IDTurret(itp);
			redTurrets[tIndex(redTurrets)] = rTurret;
		} else if (itp.col.toString().equals("BLUE")) {
			// See if turret already exists
			for (int i = 0; i < 6; i++) {
				if (blueTurrets[i] == null) {
					continue;
				}
				//If one does, make it null
				if (blueTurrets[i].getId().toString().equals(itp.id.toString())) {
					blueTurrets[i] = null;
				}
			}
			// Once here it is sure there are no turrets of that id & colour so make one
			IDTurret bTurret = new IDTurret(itp);
			blueTurrets[tIndex(blueTurrets)] = bTurret;
		}
		// After each single turret set, check if they are all set
		allSet = checkAllSet();
	}

	public static boolean isAllSet() {
		return allSet;
	}

	public static boolean isAllDead(Colour col) {
		if (col.toString().equals("RED")) {
			for (IDTurret r : redTurrets) {
				if (r == null) {
					continue;
				}
				if (!r.isDead()) {
					return false;
				}
			}
			return true;
		}
		else if (col.toString().equals("BLUE")) {
			for (IDTurret b : blueTurrets) {
				if (b == null) {
					continue;
				}
				if (!b.isDead()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	//Return arraylist of turret blocks for getHit() method to cycle through
	public static ArrayList<Block> getTurretBlocks() {
		ArrayList<Block> tBlocks = new ArrayList<Block>();
		//Check blue
		for (int i = 0; i < 6; i++) {
			//Cannot check if null
			if (blueTurrets[i] == null) {
				continue;
			}
			//Do not check if dead
			if (blueTurrets[i].isDead()) {
				continue;
			}
			//Add to arraylist
			tBlocks.add(blueTurrets[i].getTurretBlock());
		}
		//Check red
		for (int i = 0; i < 6; i++) {
			//Cannot check if null
			if (redTurrets[i] == null) {
				continue;
			}
			//Do not check if dead
			if (redTurrets[i].isDead()) {
				continue;
			}
			//Add to arraylist
			tBlocks.add(redTurrets[i].getTurretBlock());
		}
		return tBlocks;
	}

	//Use a block to find a specific turret
	public static IDTurret getTurret(Block b) {
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		// Search blue
		for (int i = 0; i < 6; i++) {
			if (blueTurrets[i] == null) {
				continue;
			}
			Block t = blueTurrets[i].getTurretBlock();
			if ((x == t.getX()) && (y == t.getY()) && (z == t.getZ())) {
				return blueTurrets[i];
			}
		}
		// Search red
		for (int i = 0; i < 6; i++) {
			if (redTurrets[i] == null) {
				continue;
			}
			Block t = redTurrets[i].getTurretBlock();
			if ((x == t.getX()) && (y == t.getY()) && (z == t.getZ())) {
				return redTurrets[i];
			}
		}
		// If not in either array return null
		return null;
	}

	//Use ID and Colour to find a specific turret
	public static IDTurret getTurret(Turret id, Colour col) {
		if (col.toString().equals("BLUE")) {
			for (IDTurret t : blueTurrets) {
				if (t == null) {
					continue;
				}
				if (t.getId().toString().equals(id.toString())) {
					return t;
				}
			}
		} else if (col.toString().equals("RED")) {
			for (IDTurret t : redTurrets) {
				if (t == null) {
					continue;
				}
				if (t.getId().toString().equals(id.toString())) {
					return t;
				}
			}
		}
		//If no match is found by now, return null
		return null;
	}

	//Get all red turrets
	public static IDTurret[] getRedTurrets() {
		return redTurrets;
	}

	//Get all blue turrets
	public static IDTurret[] getBlueTurrets() {
		return blueTurrets;
	}

	//Get default turret health
	public static int getDefaultHealth() {
		return defaultHealth;
	}

	//Check if turret is destroyable for getHit() method
	public static boolean canDamage(Turret id, Colour col) {
		//Get turret before in chain (top_inner -> top_outer)
		Turret beforeID = getTurretBefore(id);
		//If there is no before then you can damage
		if (beforeID == null) {
			return true;
		}
		//Check if turret before is actually set
		IDTurret t = IDTurretManager.getTurret(beforeID, col);
		if (t == null) {
			return true;
		}
		//Check if turret before is dead
		if (t.isDead()) {
			return true;
		} else {
			return false;
		}
	}

	public static void save() throws IOException {
		turretFile.createNewFile();
		FileWriter fw = new FileWriter(turretFile);
		PrintWriter pw = new PrintWriter(fw);
		for (IDTurret t: redTurrets) {
			if (t == null) {
				System.out.println("[DEBUG] A Red Turret was null.");
				continue;
			}
			Block b = t.getTurretBlock();
			//Save as X-Y-Z-ID-COLOUR-HP
			String s = b.getX() + ";" + b.getY() + ";" + b.getZ() + ";" + t.getId().toString() + ";" + t.getTeam().toString() + ";" + t.getHealth();
			System.out.println("[DEBUG] Writing: "+s);
			pw.println(s);
		}
		for (IDTurret t: blueTurrets) {
			if (t == null) {
				System.out.println("[DEBUG] A Blue Turret was null.");
				continue;
			}
			Block b = t.getTurretBlock();
			//Save as X-Y-Z-ID-COLOUR-HP
			String s = b.getX() + ";" + b.getY() + ";" + b.getZ() + ";" + t.getId().toString() + ";" + t.getTeam().toString() + ";" + t.getHealth();
			System.out.println("[DEBUG] Writing: "+s);
			pw.println(s);
		}
		pw.close();
		System.out.println("[DEBUG] Successfully saved turrets!");
	}

	public static void load() throws IOException, ClassNotFoundException {
		// If no save file exists then don't bother trying to load anything
		if (!turretFile.exists()) {
			return;
		}
		FileReader fr = new FileReader(turretFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			String[] split = line.split(";");
			int x;
			int y;
			int z;
			int hp;
			try {
				x = Integer.parseInt(split[0]);
				y = Integer.parseInt(split[1]);
				z = Integer.parseInt(split[2]);
				hp = Integer.parseInt(split[5]);
			}
			catch (Exception ex) {
				x = 0;
				y = 0;
				z = 0;
				hp = defaultHealth;
			}
			String id = split[3];
			String colour = split[4];
			System.out.println("x="+x);
			System.out.println("y="+y);
			System.out.println("z="+z);
			System.out.println("hp="+hp);
			System.out.println("id="+id);
			System.out.println("colour="+colour);
			ArrayList<Block> bl = new ArrayList<Block>();
			bl.add(InsomniaDOTA.s.getWorld("dota").getBlockAt(x, y, z));
			setTurret(bl, IDTeam.getColourFromStr(colour), IDTurret.getIdFromStr(id), hp);
			System.out.println("[DEBUG] Loaded " + colour + " " + id + " @ " + hp + "hp" );
		}
		System.out.println("[DEBUG] Successfully loaded turrets!");
	}

	public static boolean getHit(Block hit, Arrow a) {
		//Just in case, to avoid null exceptions
		if (hit == null) {
			return false;
		}
		//Get turret hit
		IDTurret turret = getTurret(hit);
		//If no turret is returned then you didnt hit a turret...
		if (turret == null) {
			return false;
		}
		//Get the turret colour & ID
		Colour tCol = turret.getTeam();
		Turret tID = turret.getId();
		//Get player and its colour
		Player p = (Player)a.getShooter();
		Colour pCol = IDTeamManager.getTeam(p);
		// ------START CHECKS------
		//1. Cannot hit turrets in setup mode
		if (IDCommands.setup) {
			p.sendMessage("Server is in setup mode.");
			return false;
		}
		//2. Neutral players cannot damage turrets
		if (pCol.toString().equals("NEUTRAL")) {
			p.sendMessage("You must join a team to play!");
			return false;
		}
		//3. Cannot hit turrets of your team
		if (tCol.toString().equals(pCol.toString())) {
			p.sendMessage("This is your team's turret!");
			return false;
		}
		//4. Check if turret before is dead or not
		if (!canDamage(tID, tCol)) {
			p.sendMessage("You must destroy the turret before this one first!");
			return false;
		}
		//------END CHECKS------
		//If it passes all until now, then player can damage turret
		InsomniaDOTA.s.broadcastMessage(tCol.toString()+" "+tID.toString()+" has been hit!");
		return true;
	}

	// Method that finds the next null index in a turret array, if none is found return -1
	private static int tIndex(IDTurret[] arr) {
		for (int i = 0; i < 6; i++) {
			if (arr[i] == null) {
				return i;
			}
		}
		return -1;
	}

	private static boolean checkAllSet() {
		for (IDTurret t : redTurrets) {
			if (t == null) {
				return false;
			}
		}
		for (IDTurret t : blueTurrets) {
			if (t == null) {
				return false;
			}
		}
		return true;
	}

	private static Turret getTurretBefore(Turret id) {
		if (id.toString().equals("TOP_INNER")) {
			return Turret.TOP_OUTER;
		}
		//else if (id.toString().equals("MID_INNER")) {
		//	return Turret.MID_OUTER;
		//} 
		else if (id.toString().equals("BOT_INNER")) {
			return Turret.BOT_OUTER;
		} else {
			return null;
		}
	}
}
