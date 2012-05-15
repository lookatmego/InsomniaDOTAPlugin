package net.insomniacraft.codeex.InsomniaDOTA.structures.turrets;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.insomniacraft.codeex.InsomniaDOTA.structures.IDStructure;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

public class IDTurret extends IDStructure {

	public enum Turret {TOP_OUTER, TOP_INNER, MID_OUTER, MID_INNER, BOT_OUTER, BOT_INNER};
	
	private Block turretBlock;
	private boolean isDead;
	private Turret id;
	private Colour c;

	public IDTurret(ArrayList<Block> blocks, int health, Turret id, Colour col) {
		super (blocks, health);
		turretBlock = blocks.get(0);
		isDead = false;
		this.id = id;
		this.c = col;
	}
	
	public Block getTurretBlock() {
		return turretBlock;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public Turret getId() {
		return id;
	}
	
	public Colour getTeam() {
		return c;
	}
	
	public void doDamage() {
		if (isDead) {
			return;
		}
		super.doDamage();
		if (health == 0) {
			isDead = true;
			Location l = turretBlock.getLocation();
			World world = turretBlock.getWorld();
			turretBlock.breakNaturally();
			world.createExplosion(l, 12.0F);
		}
	}
	
	public void reset() {
		isDead = false;
		setHealth(IDTurretManager.getDefaultHealth());
	}
	
	public static Turret getIdFromStr(String s) {
		if (s.equalsIgnoreCase("TOP_OUTER")) {
			return Turret.TOP_OUTER;
		} else if (s.equalsIgnoreCase("TOP_INNER")) {
			return Turret.TOP_INNER;
		} else if (s.equalsIgnoreCase("MID_OUTER")) {
			return Turret.MID_OUTER;
		} else if (s.equalsIgnoreCase("MID_INNER")) {
			return Turret.MID_INNER;
		} else if (s.equalsIgnoreCase("BOT_OUTER")) {
			return Turret.BOT_OUTER;
		} else if (s.equalsIgnoreCase("BOT_INNER")) {
			return Turret.BOT_INNER;
		} else {
			return null;
		}
	}
}
