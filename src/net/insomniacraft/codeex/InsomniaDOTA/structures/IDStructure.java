package net.insomniacraft.codeex.InsomniaDOTA.structures;

import java.util.ArrayList;

import org.bukkit.block.Block;

public class IDStructure {
	
	protected int health;
	protected ArrayList<Block> coreBlocks;

	public IDStructure(ArrayList<Block> blocks, int health) {
		this.coreBlocks = blocks;
		this.health = health;
	}
	
	public ArrayList<Block> getBlocks() {
		return coreBlocks;
	}
	
	public void doDamage() {
		health = health-1;
	}
	
	public void setHealth(int h) {
		if (h > 0) {
			this.health = h;
		}
	}
	
	public int getHealth() {
		return health;
	}
	
}
