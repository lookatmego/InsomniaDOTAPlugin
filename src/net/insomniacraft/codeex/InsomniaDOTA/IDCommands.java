package net.insomniacraft.codeex.InsomniaDOTA;

import java.util.ArrayList;
import java.util.Random;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.*;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret.Turret;

import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class IDCommands implements CommandExecutor {

	public static boolean setup = false;
	public static String setupPlayer = null;

	Plugin p;

	public IDCommands(Plugin p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Player Commands
		if (sender instanceof Player) {
			Player player = (Player)sender;
			// Player commands
			if (sender.hasPermission("DOTA.play")) {
				if (cmd.getName().equalsIgnoreCase("join")) {
					Colour c = IDTeamManager.getTeam(player);
					if ((c.toString().equals("RED")) || (c.toString().equals("BLUE"))) {
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
							System.out.println("[DEBUG] Player was not set to ready.");
						}
						return true;
					}
				}
				if (cmd.getName().equalsIgnoreCase("")) {
					
				}
			}
		}
		// Moderator commands
		if (sender.hasPermission("DOTA.mod")) {
			if (cmd.getName().equalsIgnoreCase("remove")) {
				if (!(args.length == 1)) {
					return false;
				}
				Player pl = p.getServer().getPlayer(args[0]);
				IDTeamManager.setTeam(Colour.NEUTRAL, pl);
			}
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
		}
		// Admin commands
		if (sender.hasPermission("DOTA.admin")) {
			if (cmd.getName().equalsIgnoreCase("reset")) {
				IDGameManager.endGame();
				sender.sendMessage("Game reset!");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("setup")) {
				if (sender instanceof ConsoleCommandSender) {
					setup = !setup;
					InsomniaDOTA.l.info("[DEBUG] Setup is now "+setup+".");
					return true;
				}
				if (setup == false) {
					sender.sendMessage("Now in setup mode.");
					sender.sendMessage("Select blocks by clicking on them then");
					sender.sendMessage("Type /set [colour] [structure] to set a structure.");
					sender.sendMessage("To exit setup mode type /setup again.");
					setup = true;
					setupPlayer = ((Player)sender).getName();
					return true;
				} else if (setup == true) {
					if (setupPlayer.equalsIgnoreCase(((Player)sender).getName())) {
						sender.sendMessage("Now exiting setup mode.");
						setup = false;
						IDBlockSelector.clearBlocks();
						return true;
					}
					else {
						sender.sendMessage("Only "+setupPlayer+" or console can exit setup mode!");
						return true;
					}
				}
				return false;
			}
			if (cmd.getName().equalsIgnoreCase("info")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("BLUE")) {
						for (IDTurret t: IDTurretManager.getBlueTurrets()) {
							if (t == null) {
								sender.sendMessage("Blue turret is null!");
							} else {
								sender.sendMessage(t.getTeam()+" "+t.getId()+" HP:"+t.getHealth());
							}
						}
						return true;
					}
					else if (args[0].equalsIgnoreCase("RED")) {
						for (IDTurret t: IDTurretManager.getRedTurrets()) {
							if (t == null) {
								sender.sendMessage("Red turret is null!");
							} else {
								sender.sendMessage(t.getTeam()+" "+t.getId()+" HP:"+t.getHealth());
							}
						}
						return true;
					}
					return false;
				}
				else if (args.length == 2) {
					return true;
				}
				return false;
			}
			if (cmd.getName().equalsIgnoreCase("set")) {
				if (IDCommands.setup == false) {
					sender.sendMessage("Server is not in setup mode.");
					return true;
				}
				if (!(args.length == 2)) {
					return false;
				}
				if (args[1].equalsIgnoreCase("TOP_OUTER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.TOP_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.TOP_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						sender.sendMessage(String.valueOf(IDTurretManager.isAllSet()));
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("TOP_INNER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.TOP_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.TOP_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("MID_OUTER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.MID_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.MID_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("MID_INNER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.MID_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.MID_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("BOT_OUTER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.BOT_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.BOT_OUTER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("BOT_INNER")) {
					if (args[0].equalsIgnoreCase("RED")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.RED, Turret.BOT_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						ArrayList<Block> bl = IDBlockSelector.getSelected();
						if (bl.size() < 1) {
							sender.sendMessage("No blocks are selected!");
							return true;
						}
						else if (bl.size() > 1) {
							sender.sendMessage("Turret weak points are limited to only 1 block.");
							IDBlockSelector.clearBlocks();
							return true;
						}
						IDTurretManager.setTurret(bl, Colour.BLUE, Turret.BOT_INNER, IDTurretManager.getDefaultHealth());
						sender.sendMessage("Turret set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("NEXUS")) {
					ArrayList<Block> bl = IDBlockSelector.getSelected();
					String xyz = "";
					for (Block b: bl) {
						int x = b.getX();
						int y = b.getY();
						int z = b.getZ();
						xyz = xyz + x + ":" + y + ":" + z + " ";
					}
					if (args[0].equalsIgnoreCase("RED")) {
						IDGameManager.setNexus(IDBlockSelector.getArraySelected(), Colour.RED, IDGameManager.getNexusDefHealth());
						sender.sendMessage("Red nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						IDGameManager.setNexus(IDBlockSelector.getArraySelected(), Colour.BLUE, IDGameManager.getNexusDefHealth());
						sender.sendMessage("Blue nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
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
		}
		// Console Commands
		else if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
			if (cmd.getName().equalsIgnoreCase("startgame")) {
				IDGameManager.gameStarted = !IDGameManager.gameStarted;
				return true;
			}
			return true;
		}
		return true;
	}
}
