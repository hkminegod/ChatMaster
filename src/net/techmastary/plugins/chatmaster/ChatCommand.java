package net.techmastary.plugins.chatmaster;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {
	static ChatMaster plugin;

	public String helpCmd(String cmdname, String desc) {
		String s = "  " + ChatColor.DARK_AQUA + "/cm " + ChatColor.AQUA + cmdname + "" + ChatColor.GRAY + " : " + desc;
		return s;
	}

	public String cmPrefix(String string) {
		return ChatColor.GOLD + "[" + ChatColor.RED + "ChatMaster" + ChatColor.GOLD + "] " + string;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(cmPrefix(ChatColor.YELLOW + "Use \"/cm help\" for help."));
			return true;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GOLD + ".oOo.___.[ " + ChatColor.YELLOW + "ChatMaster" + ChatColor.GOLD + " ].___.oOo.");
				if (sender.hasPermission("chat.silence")) {
					sender.sendMessage(helpCmd("silence", "Enable/ disable global chat"));
				}
				if (sender.hasPermission("chat.status")) {
					sender.sendMessage(helpCmd("status", "Checks the current status of global chat."));
				}
				if (sender.hasPermission("chat.clean")) {
					sender.sendMessage(helpCmd("cleanchat <all/playername>", "Clean player chat."));
				}
				if (sender.hasPermission("chat.deafen")) {
					sender.sendMessage(helpCmd("deafen <playername> ", "Deafens a player."));
				}
				if (sender.hasPermission("chat.fakeop")) {
					sender.sendMessage(helpCmd("fakeop <playername>", "Fake ops player."));
				}
				if (sender.hasPermission("chat.pping")) {
					sender.sendMessage("  " + ChatColor.AQUA + "/pping <playername>" + ChatColor.GRAY + " : Alert a player with a sound.");
				}
				if (sender.hasPermission("chat.reload")) {
					sender.sendMessage(helpCmd("reload", "Reloads configuration file."));
				}

				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("chat.reload")) {
					plugin.reloadConfig();
				} else {
					sender.sendMessage(Messages.no_permission);
				}
			}

			if (args[0].equalsIgnoreCase("silence")) {
				if (sender.hasPermission("chat.silence")) {
					if (!ChatMaster.Silenced) {
						ChatMaster.Silenced = true;
						sender.sendMessage(ChatColor.GRAY + "You silenced global chat.");
						Bukkit.broadcastMessage(ChatColor.GRAY + "" + sender.getName() + " disabled global chat.");
						return true;
					} else {
						ChatMaster.Silenced = false;
						sender.sendMessage(ChatColor.GRAY + "You have resumed global chat.");
						Bukkit.broadcastMessage(ChatColor.GRAY + "" + sender.getName() + " resumed global chat.");
						return true;
					}
				} else {
					sender.sendMessage(Messages.no_permission);
				}
			}

			if (args[0].equalsIgnoreCase("status")) {
				if (sender.hasPermission("chat.status") && (ChatMaster.Silenced)) {
					sender.sendMessage(ChatColor.GRAY + "Global chat is currently" + ChatColor.RED + " DISABLED" + ChatColor.GRAY + ".");
				}
				if (sender.hasPermission("chat.status") && (!ChatMaster.Silenced)) {
					sender.sendMessage(ChatColor.GRAY + "Global chat is currently" + ChatColor.GREEN + " ENABLED" + ChatColor.GRAY + ".");
				}
			}
			if (args[0].equalsIgnoreCase("cleanchat") || (args[0].equalsIgnoreCase("clearchat"))) {
				if (args.length == 1) {
					if (sender.hasPermission("chat.clean")) {
						for (int x = 0; x < 120; x++) {
							sender.sendMessage("");
							if (x == 119) {
								sender.sendMessage(ChatColor.GRAY + "You cleared your chat.");
							}
						}
					} else {
						sender.sendMessage(Messages.no_permission);
					}
				} else if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("all")) {
						if (sender.hasPermission("chat.clean.all")) {
							for (int x = 0; x < 120; x++) {
								Bukkit.broadcastMessage("");
							}
							Bukkit.broadcastMessage(ChatColor.GRAY + "Your chat has been cleared by: " + sender.getName());
							sender.sendMessage(ChatColor.GRAY + "You cleared everybody's chat.");
							System.out.println("chat cleared for everybody.");
						} else {
							sender.sendMessage(Messages.no_permission);
						}
					} else {
						if (sender.hasPermission("chat.clean.others")) {
							Player p = Bukkit.getServer().getPlayer(args[1]);
							if (Bukkit.getServer().getPlayer(args[1]) != null) {
								for (int x = 0; x < 120; x++) {
									p.sendMessage("");
								}
								sender.sendMessage(ChatColor.GRAY + "You cleared chat for: " + p.getName());
								p.sendMessage(ChatColor.GRAY + "Your chat has been cleared by: " + sender.getName());
								System.out.println("chat cleared for " + p.getName());
							} else {
								sender.sendMessage(Messages.player_not_found);
							}
						} else {
							sender.sendMessage(Messages.no_permission);
						}
					}
				}
			}

			if (args[0].equalsIgnoreCase("deafen")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("Not allowed to execute through console.");
					return true;
				}
				if (sender.hasPermission("chat.deafen")) {
					if (!ChatEventListener.nochat.contains(sender.getName())) {
						ChatEventListener.nochat.add(sender.getName());
						sender.sendMessage(ChatColor.GRAY + "You are now deafened.");
					} else {
						ChatEventListener.nochat.remove(sender.getName());
						sender.sendMessage(ChatColor.GRAY + "You are now undeafened.");
					}
				}

				if (args.length >= 2) {
					if (sender.hasPermission("chat.deafen.other")) {
						Player target = Bukkit.getServer().getPlayer(args[1]);
						if (!ChatEventListener.nochat.contains(target.getName())) {
							if (Bukkit.getServer().getPlayer(args[1]) != null) {
								target.sendMessage(ChatColor.GRAY + "You have been deafened by " + sender.getName());
								sender.sendMessage(ChatColor.GRAY + "You have deafened " + target.getName());
								ChatEventListener.nochat.add(target.getName());
							} else {
								sender.sendMessage(Messages.player_not_found);
							}
						}
						if (ChatEventListener.nochat.contains(target.getName())) {
							target.sendMessage(ChatColor.GRAY + "You have been deafened by " + sender.getName());
							sender.sendMessage(ChatColor.GRAY + "You have deafened " + target.getName());
							ChatEventListener.nochat.remove(target.getName());
						}
					} else {
						sender.sendMessage(Messages.no_permission);
					}
				}
			}
			if (args[0].equalsIgnoreCase("fakeop")) {
				if (sender.hasPermission("chat.fakeop")) {
					if (args.length == 1) {
						sender.sendMessage(cmPrefix("Unknown Syntax. (/cm fakeop <player>"));
					} else if (args.length >= 2) {
						Player faked = Bukkit.getServer().getPlayer(args[1]);
						if (Bukkit.getServer().getPlayer(args[1]) != null) {
							faked.sendMessage(ChatColor.WHITE + "Opped " + faked.getName());
							faked.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[CONSOLE: Opped " + faked.getName() + "]");
						} else {
							sender.sendMessage(Messages.player_not_found);
						}
					}
				} else {
					sender.sendMessage(Messages.no_permission);
				}
			}
		}
		return true;
	}
}
