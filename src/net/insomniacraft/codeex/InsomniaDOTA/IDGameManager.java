package net.insomniacraft.codeex.InsomniaDOTA;

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

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;

import net.insomniacraft.codeex.InsomniaDOTA.structures.nexus.*;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.*;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret.Turret;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;

public class IDGameManager {

	private static IDNexus blueNexus = null;
	private static IDNexus redNexus = null;

	public static boolean gameStarted = false;

	private static File nexusFile = new File(InsomniaDOTA.pFolder, "nexus.txt");

	private static int nH = 50;

	public static void preStartGame(Player p) {
		if (!(IDTeamManager.isAllReady())) {
			p.sendMessage("Not all players are ready yet!");
		}
		if ((blueNexus == null) || redNexus == null) {
			p.sendMessage("Blue and red nexus must be set before a game can begin.");
			return;
		}
		if (IDTurretManager.isAllSet() == false) {
			p.sendMessage("All turrets must be set before a game can begin.");
			return;
		}
		InsomniaDOTA.broadcast(p.getName() + " wants to start a game.");
	}

	public static void startGame() {
		gameStarted = true;
	}

	public static void endGame() {
		// Rollback block changes to the world using logblock
		LogBlock logblock = (LogBlock) InsomniaDOTA.s.getPluginManager().getPlugin("LogBlock");
		if (logblock != null) {
			QueryParams params = new QueryParams(logblock);
			params.world = InsomniaDOTA.s.getWorld("dota");
			// Set to true on release?
			params.silent = false;
			try {
				logblock.getCommandsHandler().new CommandRollback(InsomniaDOTA.s.getConsoleSender(), params, true);
			} catch (Exception ex) {
				InsomniaDOTA.l.severe("Unable to rollback changes!");
				ex.printStackTrace();
			}
		} else {
			InsomniaDOTA.l.severe("Could not find LogBlock to rollback changes!");
		}
		// Reset all structures
		IDTurret[] rt = IDTurretManager.getRedTurrets();
		IDTurret[] bt = IDTurretManager.getBlueTurrets();
		for (IDTurret t : rt) {
			if (t == null) {
				continue;
			}
			t.reset();
			System.out.println("[DEBUG] " + t.getTeam() + " " + t.getId() + " is reset.");
		}
		for (IDTurret t : bt) {
			if (t == null) {
				continue;
			}
			t.reset();
			System.out.println("[DEBUG] " + t.getTeam() + " " + t.getId() + " is reset.");
		}
		if (redNexus != null) {
			redNexus.reset();
			System.out.println("[DEBUG] " + redNexus.getColour() + " nexus is reset.");
		}
		if (blueNexus != null) {
			blueNexus.reset();
			System.out.println("[DEBUG] " + blueNexus.getColour() + " nexus is reset.");
		}
		gameStarted = false;
	}

	public static void setNexus(Block[] blocks, Colour col, int hp) {
		ArrayList<Block> bl = new ArrayList<Block>();
		for (Block b: blocks) {
			bl.add(b);
		}
		if (col.toString().equals("BLUE")) {
			blueNexus = new IDNexus(bl, hp, Colour.BLUE);
		} else if (col.toString().equals("RED")) {
			redNexus = new IDNexus(bl, hp, Colour.RED);
		}
	}

	public static void save() throws IOException {
		nexusFile.createNewFile();
		FileWriter fw = new FileWriter(nexusFile);
		PrintWriter pw = new PrintWriter(fw);

		if (redNexus != null) {		
			String xyz = "";
			for (Block b: redNexus.getBlocks()) {
				xyz = xyz + b.getX() + ";" + b.getY() + ";" + b.getZ() + ":";
			}
			System.out.println("[DEBUG] Writing: "+redNexus.getColour().toString()+"-"+redNexus.getHealth());
			pw.println(redNexus.getColour().toString()+"-"+redNexus.getHealth());
			System.out.println("[DEBUG] Writing: "+xyz);
			pw.println(xyz);
		}
		if (blueNexus != null) {
			String xyz = "";
			for (Block b: blueNexus.getBlocks()) {
				xyz = xyz + b.getX() + ";" + b.getY() + ";" + b.getZ() + ":";
			}
			System.out.println("[DEBUG] Writing: "+blueNexus.getColour().toString()+"-"+blueNexus.getHealth());
			pw.println(blueNexus.getColour().toString()+"-"+blueNexus.getHealth());
			System.out.println("[DEBUG] Writing: "+xyz);
			pw.println(xyz);
		}
		pw.close();
		System.out.println("[DEBUG] Successfully saved nexus!");
	}

	public static void load() throws IOException, ClassNotFoundException {
		// Don't load if no save exists
		if (!nexusFile.exists()) {
			return;
		}
		FileReader fr = new FileReader(nexusFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String xyz;
		while (((line = br.readLine()) != null) && ((xyz = br.readLine()) != null)) {
			String[] split = line.split("-");
			String[] coords = xyz.split(":");
			Block[] blocks = new Block[coords.length];
			for (int i = 0; i < coords.length; i++) {
				String[] nums = coords[i].split(";");
				Block b = InsomniaDOTA.s.getWorld("dota").getBlockAt(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]));
				blocks[i] = b;
			}
			setNexus(blocks, IDTeam.getColourFromStr(split[0]), Integer.parseInt(split[1]));
			System.out.println("[DEBUG] Loaded " + split[0] + " nexus @ " + split[1] + " hp");
		}
		br.close();
		System.out.println("[DEBUG] Successfully loaded nexus!");
	}

	public static int getNexusDefHealth() {
		return nH;
	}

	public static boolean isStarted() {
		return gameStarted;
	}

	public static ArrayList<Block> getNexusBlocks() {
		ArrayList<Block> all = new ArrayList<Block>();
		if (redNexus != null) {
			for (Block b : redNexus.getBlocks()) {
				all.add(b);
			}
		}
		if (blueNexus != null) {
			for (Block b : blueNexus.getBlocks()) {
				all.add(b);
			}
		}
		return all;
	}

	public static IDNexus getNexus(Block b) {
		if (redNexus != null) {
			for (Block nb : redNexus.getBlocks()) {
				if ((b.getX() == nb.getX()) && (b.getY() == nb.getY()) && (b.getZ() == nb.getZ())) {
					return redNexus;
				}
			}
		}
		if (blueNexus != null) {
			for (Block nb : blueNexus.getBlocks()) {
				if ((b.getX() == nb.getX()) && (b.getY() == nb.getY()) && (b.getZ() == nb.getZ())) {
					return blueNexus;
				}
			}
		}
		return null;
	}

	public static boolean getNexusHit(Block hit, Arrow a) {
		//Just to avoid null exceptions
		if (hit == null) {
			return false;
		}
		//Get nexus hit
		IDNexus nex = getNexus(hit);
		//If nothing is returned you didn't hit a nexus
		if (nex == null) {
			return false;
		}
		//Get nexus colour
		Colour nCol = nex.getColour();
		//Get player and their colour
		Player p = (Player)a.getShooter();
		Colour pCol = IDTeamManager.getTeam(p);
		// ------START CHECKS------
		//1. Cannot hit nexus in setup mode
		if (IDCommands.setup) {
			p.sendMessage("Server is in setup mode.");
			return false;
		}
		//2. Neutral players cant hit nexus
		if (pCol.toString().equals("NEUTRAL")) {
			p.sendMessage("You must join a team to play!");
			return false;
		}
		//3. Cannot hit your own nexus
		if (nCol.toString().equals(pCol.toString())) {
			p.sendMessage("This is your team's nexus!");
			return false;
		}
		//4. Check if a lane of turrets is dead to hit nexus
		if (!canDamageNexus(nCol)) {
			p.sendMessage("You must destroy a lane of turrets before attacking the nexus!");
			return false;
		}
		//------END CHECKS------
		//If it passes all until now, then player can damage nexus
		InsomniaDOTA.s.broadcastMessage(nCol.toString()+" nexus has been hit!");
		return true;
	}
	
	private static boolean canDamageNexus(Colour col) {
		boolean topLane = false;
		boolean midLane = false;
		boolean botLane = false;
		IDTurret top_outer = IDTurretManager.getTurret(Turret.TOP_OUTER, col);
		IDTurret top_inner = IDTurretManager.getTurret(Turret.TOP_INNER, col);
		//TOP LANE CHECK
		//If both dead you can attack nexus
		if (top_inner != null && top_outer != null) {
			if (top_outer.isDead() && top_inner.isDead()) {
				topLane = true;
			}
		}
		IDTurret mid_outer = IDTurretManager.getTurret(Turret.MID_OUTER, col);
		//MID LANE CHECK
		//If dead you can attack nexus
		if (mid_outer != null) {
			if (mid_outer.isDead()) {
				midLane = true;
			}
		}
		IDTurret bot_outer = IDTurretManager.getTurret(Turret.BOT_OUTER, col);
		IDTurret bot_inner = IDTurretManager.getTurret(Turret.BOT_INNER, col);
		//BOT LANE CHECK
		//If both dead you can attack nexus
		if (bot_inner != null && bot_outer != null) {
			if (bot_outer.isDead() && bot_inner.isDead()) {
				botLane = true;
			}
		}
		//If any of these are true then nexus is attackable
		if (topLane || midLane || botLane) {
			System.out.println("[DEBUG] Nexus is attackable!");
			return true;
		}
		return false;
	}
}
