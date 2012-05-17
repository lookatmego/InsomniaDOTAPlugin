package net.insomniacraft.codeex.InsomniaDOTA.structures.turrets;

import java.util.ArrayList;

import org.bukkit.block.Block;

import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret.Turret;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

public class IDTurretParams {
	
	public ArrayList<Block> blocks;
	public Block turretBlock;
	public Colour col;
	public Turret id;
	public int hp;
	
	public IDTurretParams(ArrayList<Block> bl, String sCol, String sID, int hp) throws Exception {
		//Check arraylist for any blocks
		if (bl.size() < 1) {
			Exception e = new Exception("No blocks are selected!");
			throw e;
		}
		//Check arraylist for 1 block only
		if (bl.size() > 1) {
			Exception e = new Exception("Turret weak points are limited to only 1 block.");
			throw e;
		}
		//Set blocks
		this.blocks = bl;
		//Set turret block
		this.turretBlock = bl.get(0);
		if (turretBlock == null) {
			Exception e = new Exception("Could not find turret block!");
			throw e;
		}
		//Set colour
		col = IDTeam.getColourFromStr(sCol);
		if (col == null || col.toString().equals("NEUTRAL")) {
			Exception e = new Exception(sCol+" is not a valid colour.");
			throw e;
		}
		//Set ID
		id = IDTurret.getIdFromStr(sID);
		if (id == null) {
			Exception e = new Exception("Turret type "+sID+" does not exist!");
			throw e;
		}
		//Set HP
		this.hp = hp;
	}
}





