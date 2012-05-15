package net.insomniacraft.codeex.InsomniaDOTA;

import java.util.ArrayList;

import net.insomniacraft.codeex.InsomniaDOTA.structures.nexus.IDNexus;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurretManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

public class IDListener implements Listener {
	
	Plugin pl;
	
	private static ArrayList<Block> blocks = new ArrayList<Block>();
	
	public IDListener(Plugin p) {
		this.pl = p;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		IDTeamManager.setTeam(Colour.NEUTRAL, p);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (p.getName().equalsIgnoreCase(IDCommands.setupPlayer)) {
			pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), "setup");
		}
		IDTeamManager.removeFromTeam(p);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!IDCommands.setup) {
			return;
		}
		if (!(IDCommands.setupPlayer.equalsIgnoreCase(e.getPlayer().getName()))) {
			return;
		}
		Block b = e.getClickedBlock();
		if (b == null) {
			return;
		}
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		//Check if already selected.
		for (Block bl: blocks) {
			if ((bl.getX() == x) && (bl.getY() == y) && (bl.getZ() == z)) {
				pl.getServer().getPlayer(e.getPlayer().getName()).sendMessage("Duplicate block selected!");
				return;
			}
		}
		//Check if already in use w/ turrets
		if (IDTurretManager.getTurret(b) != null) {
			e.getPlayer().sendMessage("Block already in use!");
			return;
		}
		blocks.add(b);
		e.getPlayer().sendMessage(blocks.size()+" block(s) selected.");
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String m = e.getMessage();
		Colour col = IDTeamManager.getTeam(p);
		if (col.toString().equals("BLUE")) {
			e.setFormat(ChatColor.BLUE+"<"+p.getName()+"> "+m);
		}
		else if (col.toString().equals("RED")) {
			e.setFormat(ChatColor.RED+"<"+p.getName()+"> "+m);
		}
		else if (col.toString().equals("NEUTRAL")) {
			e.setFormat(ChatColor.GRAY+"<"+p.getName()+"> "+m);
		}
	}
	
	@EventHandler
	public void onArrowShot(ProjectileHitEvent e) {
		Entity projectile = e.getEntity();
		//Check if not arrow
		if (!(projectile instanceof Arrow)) {
			return;
		}
		Arrow arrow = (Arrow) projectile;
		//Check if not player
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		World w = arrow.getWorld();
		//Find block arrow hit
		BlockIterator bi = new BlockIterator(w, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
		Block hit = null;
		while (bi.hasNext()) {
			hit = bi.next();
			if (hit.getTypeId() != 0)
			{
				break;
			}
		}
		if (IDTurretManager.getHit(hit, arrow)) {
			IDTurret turret = IDTurretManager.getTurret(hit);
			turret.doDamage();
			pl.getServer().broadcastMessage(String.valueOf(turret.getHealth()));
		}
		if (IDGameManager.getNexusHit(hit, arrow)) {
			IDNexus nex = IDGameManager.getNexus(hit);
			nex.doDamage();
			pl.getServer().broadcastMessage(String.valueOf(nex.getHealth()));
		}
	}
	
	public static void clearBlocks() {
		blocks.clear();
		System.out.println("[DEBUG] Blocks cleared.");
	}
	
	public static ArrayList<Block> getBlocks() {
		return blocks;
	}
}
