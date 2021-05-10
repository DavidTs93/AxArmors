package me.DMan16.AxArmors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DMan16.AxArmors.AxArmors;
import me.DMan16.AxArmors.AxArmor;

public class CommandListener implements CommandExecutor,TabCompleter {
	public CommandListener() {
		PluginCommand command = AxArmors.getInstance().getCommand("armor");
		command.setExecutor(this);
		command.setTabCompleter(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		/*if (args[0].equalsIgnoreCase("give")) { 
			try{
				Player playerGive = Utils.getOnlinePlayer(args[1]);
				ArmorType type = ArmorType.getByName(args[2]);
				ArmorSlot slot = ArmorSlot.getByName(args[3]);
				int amount = 1;
				try{
					amount = Math.max(1,Integer.parseInt(args[4]));
				} catch (Exception e) {}
				ItemStack item = AxItem.getAxItem(AxArmor.defaultKey(type,slot)).item();
				String name = type.getTranslatableName(slot);
				for (int i = 0; i < amount; i++) Utils.givePlayer(playerGive,item,false);
				player.sendMessage(Component.translatable("commands.give.success.single").args(Component.text(amount),Component.translatable(name),
						Component.text(playerGive.getName())).hoverEvent(item.asHoverEvent()).decoration(TextDecoration.ITALIC,false));
			} catch (Exception e) {}
		} else */
		if (args[0].equalsIgnoreCase("damage")) {
			try{
				int amount = Integer.parseInt(args[1]);
				ItemStack item = player.getInventory().getItemInMainHand();
				AxArmor armor = AxArmor.getAxArmor(item);
				player.getInventory().setItemInMainHand(armor.damage(amount).item(player));
			} catch (Exception e) {}
		} else if (args[0].equalsIgnoreCase("repair")) {
			try{
				ItemStack item = player.getInventory().getItemInMainHand();
				AxArmor armor = AxArmor.getAxArmor(item);
				int amount = args.length > 1 ? Integer.parseInt(args[1]) : armor.maxDurability;
				player.getInventory().setItemInMainHand(armor.repair(amount).item(player));
			} catch (Exception e) {}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> resultList = new ArrayList<String>();
		if (args.length == 1) {
			List<String> base = Arrays.asList(/*"give",*/"damage","repair");
			for (String str : base) if (contains(args[0],str)) resultList.add(str);
		}/* else if (args[0].equalsIgnoreCase("give")) {
			if (args.length == 2) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) if (contains(args[1],player.getName())) resultList.add(player.getName());
			} else if (args.length == 3) {
				for (ArmorType armor : ArmorType.values()) if (contains(args[2],armor.name())) resultList.add(armor.name().toLowerCase());
			} else if (args.length == 4) {
				for (ArmorSlot slot : ArmorSlot.values()) if (contains(args[3],slot.name())) resultList.add(slot.name().toLowerCase());
			}
		}*/
		return resultList;
	}
	
	private boolean contains(String arg1, String arg2) {
		return (arg1 == null || arg1.isEmpty() || arg2.toLowerCase().contains(arg1.toLowerCase()));
	}
}