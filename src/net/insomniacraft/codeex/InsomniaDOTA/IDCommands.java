package net.insomniacraft.codeex.InsomniaDOTA;

import java.util.ArrayList;
import java.util.Random;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class IDCommands implements CommandExecutor {

	public static boolean setup = false;
	public static String setupPlayer = null;
	public static boolean isRecalling = false;
	Plugin p;

	public IDCommands(Plugin p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		// Player Commands
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			// Player commands
			// DOTA PLAY PERMISSIONS
			if (sender.hasPermission("DOTA.play")) {

				// JOIN COMMAND
				if (cmd.getName().equalsIgnoreCase("join")) {
					Colour c = IDTeamManager.getTeam(player);
					if ((c.toString().equals("RED"))
							|| (c.toString().equals("BLUE"))) {
						player.sendMessage("You are already on a team!");
						return true;
					}
					int blue = IDTeamManager.getBlueCount();
					int red = IDTeamManager.getRedCount();
					if (blue > red) {
						IDTeamManager.setTeam(Colour.RED, player);
						return true;
					} else if (red > blue) {
						IDTeamManager.setTeam(Colour.BLUE, player);
						return true;
					} else {
						Random r = new Random();
						if (r.nextBoolean()) {
							IDTeamManager.setTeam(Colour.BLUE, player);
						} else {
							IDTeamManager.setTeam(Colour.RED, player);
						}
						return true;
					}
				}
				// JOIN COMMAND END

				// READY COMMAND
				if (cmd.getName().equalsIgnoreCase("rdy")) {
					Colour c = IDTeamManager.getTeam(player);
					if (c.toString().equals("NEUTRAL")) {
						player.sendMessage("You are not on a team!");
						return true;
					}
					if (IDGameManager.isStarted()) {
						player.sendMessage("A game is already in progress.");
						return true;
					}
					if (IDTeamManager.isPlayerReady(player)) {
						IDTeamManager.removeReady(c, player);
						return true;
					} else {
						IDTeamManager.addReady(c, player);
						if (IDTeamManager.isPlayerReady(player)) {
							player.sendMessage("You are now ready.");
						} else {
							System.out
									.println("[DEBUG] Player was not set to ready.");
						}
						return true;
					}
				}
				// RDY COMMAND END

				// RECALL COMMAND
				// exp needed to get to level 50 =
				// exp to 51 =
				// exp for 51 = 
				if (cmd.getName().equalsIgnoreCase("recall")) {
					Colour col = IDTeamManager.getTeam((Player) sender);
					Location l = IDTeamManager.getSpawn(col);
					Bukkit.getServer().getScheduler()
						.scheduleSyncDelayedTask(p, new Runnable() {
								public void run() {
									for (int x = 0; x < 89; x++){
										try {
											Thread.sleep(13);
										} catch (Exception e) {
											System.out.println("[DEBUG] ");
										}
										player.setExp(-2);
										if (player.getExp() == 2) {
											break;
										}
									}
								}
							}, 1L);
					if (l != null) {
						((Player) sender).teleport(l);
						((Player) sender).setExp(0);
						((Player) sender).giveExp(4625);
					}
				}

				// RECALL COMMAND END
			}
			// DOTA PLAY PERMISSIONS END
		}
		// Moderator commands
		// DOTA MOD PERMISSIONS
		if (sender.hasPermission("DOTA.mod")) {

			// REMOVE COMMAND
			if (cmd.getName().equalsIgnoreCase("remove")) {
				if (!(args.length == 1)) {
					return false;
				}
				Player pl = p.getServer().getPlayer(args[0]);
				IDTeamManager.setTeam(Colour.NEUTRAL, pl);
			}
			// REMOVE COMMAND END

			// ADD COMMAND
			if (cmd.getName().equalsIgnoreCase("add")) {
				if (!(args.length == 2)) {
					return false;
				}
				Player pl = ((InsomniaDOTA) p).findPlayer(args[0]);
				if (pl == null) {
					sender.sendMessage("That player does not exist!");
					return true;
				}
				if (args[1].equalsIgnoreCase("blue")) {
					IDTeamManager.setTeam(Colour.BLUE, pl);
					return true;
				} else if (args[1].equalsIgnoreCase("red")) {
					IDTeamManager.setTeam(Colour.RED, pl);
					return true;
				} else if (args[1].equalsIgnoreCase("neutral")) {
					IDTeamManager.setTeam(Colour.NEUTRAL, pl);
					return true;
				} else {
					sender.sendMessage("That team does not exist!");
					return true;
				}
			}
			// ADD COMMAND END

			// TEAMSWITCH COMMAND
			if (cmd.getName().equalsIgnoreCase("teamswitch")) {
				if (!(args.length == 1)) {
					return false;
				}
				Player pl = ((InsomniaDOTA) p).findPlayer(args[0]);
				if (pl == null) {
					sender.sendMessage("That player does not exist!");
					return true;
				}
				Colour col = IDTeamManager.getTeam(pl);
				if (col.toString().equals("BLUE")) {
					IDTeamManager.setTeam(Colour.RED, pl);
					return true;
				} else if (col.toString().equals("RED")) {
					IDTeamManager.setTeam(Colour.BLUE, pl);
					return true;
				} else {
					sender.sendMessage("Player is not on a team.");
					return true;
				}
			}
			// TEAMSWITCH COMMAND END
		}
		// DOTA MOD PERMISSIONS END
		// Admin commands
		// DOTA ADMIN PERMISSIONS
		if (sender.hasPermission("DOTA.admin")) {
			// RESET COMMAND
			if (cmd.getName().equalsIgnoreCase("reset")) {
				IDGameManager.endGame();
				sender.sendMessage("Game reset!");
				return true;
			}
			// RESET COMMAND END

			// SETUP COMMAND
			if (cmd.getName().equalsIgnoreCase("setup")) {
				if (sender instanceof ConsoleCommandSender) {
					setup = !setup;
					InsomniaDOTA.l.info("[DEBUG] Setup is now " + setup + ".");
					return true;
				}
				if (setup == false) {
					sender.sendMessage("Now in setup mode.");
					sender.sendMessage("Select blocks by clicking on them then");
					sender.sendMessage("Type /set [colour] [structure] to set a structure.");
					sender.sendMessage("To exit setup mode type /setup again.");
					setup = true;
					setupPlayer = ((Player) sender).getName();
					return true;
				} else if (setup == true) {
					if (setupPlayer.equalsIgnoreCase(((Player) sender)
							.getName())) {
						sender.sendMessage("Now exiting setup mode.");
						setup = false;
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						sender.sendMessage("Only " + setupPlayer
								+ " or console can exit setup mode!");
						return true;
					}
				}
				return false;
			}
			// SETUP COMMAND END

			// INFO COMMAND
			if (cmd.getName().equalsIgnoreCase("info")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("BLUE")) {
						for (IDTurret t : IDTurretManager.getBlueTurrets()) {
							if (t == null) {
								sender.sendMessage("Blue turret is null!");
							} else {
								sender.sendMessage(t.getTeam() + " "
										+ t.getId() + " HP:" + t.getHealth());
							}
						}
						return true;
					} else if (args[0].equalsIgnoreCase("RED")) {
						for (IDTurret t : IDTurretManager.getRedTurrets()) {
							if (t == null) {
								sender.sendMessage("Red turret is null!");
							} else {
								sender.sendMessage(t.getTeam() + " "
										+ t.getId() + " HP:" + t.getHealth());
							}
						}
						return true;
					}
					return false;
				} else if (args.length == 2) {
					return true;
				}
				return false;
			}
			// INFO COMMAND END

			// SET COMMAND
			if (cmd.getName().equalsIgnoreCase("set")) {
				if (IDCommands.setup == false) {
					sender.sendMessage("Server is not in setup mode.");
					return true;
				}
				if (!(args.length == 2)) {
					return false;
				}
				// If turret, set turret
				if (args[1].equalsIgnoreCase("TOP_OUTER")
						|| args[1].equalsIgnoreCase("TOP_INNER")
						|| args[1].equalsIgnoreCase("MID_OUTER")
						|| args[1].equalsIgnoreCase("MID_INNER")
						|| args[1].equalsIgnoreCase("BOT_OUTER")
						|| args[1].equalsIgnoreCase("BOT_INNER")) {
					ArrayList<Block> bl = IDBlockSelector.getSelected();
					IDTurretParams itp;
					try {
						itp = new IDTurretParams(bl, args[0], args[1],
								IDTurretManager.getDefaultHealth());
					} catch (Exception e) {
						sender.sendMessage("Error: " + e.getMessage());
						return true;
					}
					if (itp != null) {
						IDTurretManager.setTurret(itp);
						sender.sendMessage("Turret set!");
					}
					IDBlockSelector.clearBlocks();
				}
				// If nexus, set nexus
				else if (args[1].equalsIgnoreCase("NEXUS")) {
					if (args[0].equalsIgnoreCase("RED")) {
						IDGameManager.setNexus(
								IDBlockSelector.getArraySelected(), Colour.RED,
								IDGameManager.getNexusDefHealth());
						sender.sendMessage("Red nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						IDGameManager.setNexus(
								IDBlockSelector.getArraySelected(),
								Colour.BLUE, IDGameManager.getNexusDefHealth());
						sender.sendMessage("Blue nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				}
				// If spawn, set spawn
				else if (args[1].equalsIgnoreCase("SPAWN")) {
					Location l = ((Player) sender).getLocation();
					if (args[0].equalsIgnoreCase("RED")) {
						IDTeamManager.setSpawn(Colour.RED, l);
						sender.sendMessage("Red spawn set!");						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						IDTeamManager.setSpawn(Colour.BLUE, l);
						sender.sendMessage("Blue spawn set!");
						return true;
					} else {
						sender.sendMessage("Not a valid colour!");
						return true;
					}
				}
				// If none of the above...
				else {
					return false;
				}

			}
			// SET COMMAND END

			// CLEAR COMMAND
			if (cmd.getName().equalsIgnoreCase("clear")) {
				if (setup == false) {
					sender.sendMessage("Server is not in setup mode.");
					return true;
				} else {
					IDBlockSelector.clearBlocks();
					sender.sendMessage("Blocks cleared.");
					return true;
				}
			}
			// CLEAR COMMAND END
		}
		// DOTA ADMIN PERMISSIONS END
		return true;
	}
}
