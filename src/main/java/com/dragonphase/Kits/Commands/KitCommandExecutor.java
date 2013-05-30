package com.dragonphase.Kits.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.dragonphase.Kits.Kits;
import com.dragonphase.Kits.Util.Kit;
import com.dragonphase.Kits.Util.Message;

public class KitCommandExecutor implements CommandExecutor{
	
    public static Kits plugin;
    
	public KitCommandExecutor(Kits instance) {
	    plugin = instance;
	}

	@SuppressWarnings("deprecation")
    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
	    if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
	    
	    if (args.length == 0){
	        player.sendMessage(Message.info("/kit <kitname|create|edit|remove>"));
	    }else{
	        String arg = args[0];
	        
	        if (args.length == 1){
                if (arg.equalsIgnoreCase("create")){
                    player.sendMessage(Message.info("/kit create <kitname>"));
                }else if (arg.equalsIgnoreCase("edit")){
                    player.sendMessage(Message.info("/kit edit <kitname>"));
                }else if (arg.equalsIgnoreCase("remove")){
                    player.sendMessage(Message.info("/kit remove <kitname>"));
                }else{
                    if (player.hasPermission("kits.spawn." + arg)){
                        if (Kits.playerDelayed(player)){
                            if (Kits.getRemainingTime(player) < 1){
                                Kits.removeDelayedPlayer(player);
                            }else{
                                int remaining = Kits.getRemainingTime(player);
                                String seconds = remaining == 1 ? " second" : " seconds";
                                player.sendMessage(Message.warning("你已經選擇了職業,不可以選擇其他職業了"));
                                return false;
                            }
                        }
                        if (Kit.exists(arg)){
                            ItemStack[] itemList = Kit.getKit(arg);
                            for (int i = 0; i < itemList.length; i ++){
                                player.getInventory().setItem(i, itemList[i]);
                            }
                            player.updateInventory();
                            player.sendMessage(Message.info("Kit " + arg + " spawned."));
                            
                            if (!player.hasPermission("kits.bypassdelay") && Kits.getDelay(1) > 0) Kits.addDelayedPlayer(player);
                        }else{
                            player.sendMessage(Message.warning("Kit " + arg + " does not exist."));
                        }
                    }else{
                        player.sendMessage(Message.warning("Incorrect Permissions."));
                    }
                }
	        }else if (args.length > 1){
	            if (arg.equalsIgnoreCase("create")){
	                if (player.hasPermission("kits.admin")){
                        if (!Kit.exists(args[1])){
                            Kit.create(plugin, player, args[1]);
                            player.setMetadata("editingKit", new FixedMetadataValue(plugin, false));
                            player.setMetadata("creatingKit", new FixedMetadataValue(plugin, true));
                        }else{
                            player.sendMessage(Message.warning("Kit " + args[1] + " already exists."));
                        }
	                }else{
	                    sender.sendMessage(Message.warning("Incorrect Permissions."));
	                }
	            }else if (arg.equalsIgnoreCase("edit")){
                    if (player.hasPermission("kits.admin")){
    	                if (Kit.exists(args[1])){
                            Kit.edit(plugin, player, args[1]);
                            player.setMetadata("editingKit", new FixedMetadataValue(plugin, true));
                            player.setMetadata("creatingKit", new FixedMetadataValue(plugin, false));
                        }else{
                            player.sendMessage(Message.warning("Kit " + args[1] + " does not exist."));
                        }
                    }else{
                        sender.sendMessage(Message.warning("Incorrect Permissions."));
                    }
                }else if (arg.equalsIgnoreCase("remove")){
                    if (player.hasPermission("kits.admin")){
                        if (Kit.exists(args[1])){
                            Kits.kitsFile.set(args[1], null, false);
                            player.sendMessage(Message.info("Kit " + args[1] + " has been removed."));
                        }else{
                            player.sendMessage(Message.warning("Kit " + args[1] + " does not exist."));
                        }
                    }else{
                        sender.sendMessage(Message.warning("Incorrect Permissions."));
                    }
                }else{
                    if (player.hasPermission("kits.others.spawn." + arg)){
                        Player receiver = Bukkit.getPlayerExact(args[1]);
                        
                        if (Kits.playerDelayed(receiver)){
                            if (Kits.getRemainingTime(receiver) < 1){
                                Kits.removeDelayedPlayer(receiver);
                            }else{
                                int remaining = Kits.getRemainingTime(receiver);
                                String seconds = remaining == 1 ? " second" : " seconds";
                                player.sendMessage(Message.warning(receiver.getName() + " must wait " + remaining + seconds + " before spawning another kit."));
                                return false;
                            }
                        }
                        
                        if (Kit.exists(arg)){
                            if (receiver != null){
                                ItemStack[] itemList = Kit.getKit(arg);
                                for (int i = 0; i < itemList.length; i ++){
                                    receiver.getInventory().setItem(i, itemList[i]);
                                }
                                receiver.updateInventory();
                                receiver.sendMessage(Message.info("Kit " + arg + " spawned by " + player.getName() + "."));
                                player.sendMessage(Message.info("Kit " + arg + " spawned for " + receiver.getName() + "."));
                                
                                if (!receiver.hasPermission("kits.bypassdelay") && Kits.getDelay(1) > 0) Kits.addDelayedPlayer(receiver);
                            }else{
                                player.sendMessage(Message.warning(args[1] + " is not online."));
                            }
                        }else{
                            player.sendMessage(Message.warning("Kit " + arg + " does not exist."));
                        }
                    }else{
                        player.sendMessage(Message.warning("Incorrect Permissions."));
                    }
                }
	        }
	    }
		return false;
	}
}
