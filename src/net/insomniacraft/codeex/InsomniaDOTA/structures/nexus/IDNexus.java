package net.insomniacraft.codeex.InsomniaDOTA.structures.nexus;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;

import net.insomniacraft.codeex.InsomniaDOTA.IDGameManager;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;
import net.insomniacraft.codeex.InsomniaDOTA.structures.*;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

public class IDNexus extends IDStructure {

	private boolean isDestroyed;
	private Colour col;

	public IDNexus(ArrayList<Block> blocks, int health, Colour col) {
		super(blocks, health);
		isDestroyed = false;
		this.col = col;
	}

	public void doDamage() {
		super.doDamage();
		if (health <= 0) {
			isDestroyed = true;
			for (Block b : coreBlocks) {
				Location l = b.getLocation();
				World world = b.getWorld();
				b.breakNaturally();
				world.spawn(l, TNTPrimed.class);
			}
			Colour winner = Colour.NEUTRAL;
			if (col.toString().equals("BLUE")) {
				winner = Colour.RED;
			}
			else if (col.toString().equals("RED")) {
				winner = Colour.BLUE;
			}
			InsomniaDOTA.broadcast("Team "+winner.toString()+" has won the game!");
			System.out.println("Ending game...");
			IDGameManager.endGame();
		}
	}

	public boolean getDestroyed() {
		return isDestroyed;
	}

	public Colour getColour() {
		return col;
	}
	
	public void reset() {
		isDestroyed = false;
		setHealth(IDGameManager.getNexusDefHealth());
	}

}
