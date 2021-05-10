package me.DMan16.AxArmors;

import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Events.ArmorEquipEvent;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxItems.Restrictions.ItemRestrictedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ArmorListener extends Listener {
	
	public ArmorListener() {
		register(AxArmors.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void customDurabilityEvent(PlayerItemDamageEvent event) {
		int dmg = event.getDamage();
		Player player = event.getPlayer();
		if (event.isCancelled() || dmg <= 0 || player == null) return;
		ItemStack item = event.getItem();
		AxArmor armor = AxArmor.getAxArmor(item);
		if (armor == null) return;
		event.setCancelled(true);
		event.setDamage(0);
		EquipmentSlot slot = null;
		if (item.equals(player.getInventory().getHelmet())) slot = EquipmentSlot.HEAD;
		else if (item.equals(player.getInventory().getChestplate())) slot = EquipmentSlot.CHEST;
		else if (item.equals(player.getInventory().getLeggings())) slot = EquipmentSlot.LEGS;
		else if (item.equals(player.getInventory().getBoots())) slot = EquipmentSlot.FEET;
		if (slot == null) return;
		armor.damage(dmg);
		if (armor.isBroken()) {
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
			Item droppedItem = Utils.givePlayer(player,armor.item(player),false);
			player.getInventory().setItem(slot,droppedItem != null ? null : armor.item(player));
			if (droppedItem != null) droppedItem.remove();
		} else player.getInventory().setItem(slot,armor.item(player));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void disableEquipArmorsEvent(ItemRestrictedEvent event) {
		if (event.isCancelled()) return;
		AxArmor armor = AxArmor.getAxArmor(event.item);
		if (armor == null || !armor.isBroken()) return;
		event.setCancelMSG(Component.translatable("item.aldreda.armor.broken_no_equip").color(TextColor.color(NamedTextColor.RED)).decoration(TextDecoration.ITALIC,false));
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void disableEquipArmorsEvent(ArmorEquipEvent event) {
		if (event.isCancelled()) return;
		ItemStack item = event.getNewArmor();
		if (Utils.isNull(item) || !ArmorType.isArmor(item.getType())) return;
		if (AxArmor.getAxArmor(item) == null) event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void fixCreativeArmorsEvent(InventoryClickEvent event) {
		if (event.isCancelled() || !(event.getWhoClicked() instanceof Player) || event.getInventory().getType() != InventoryType.CRAFTING) return;
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode() != GameMode.CREATIVE) return;
		ItemStack item = event.getCursor();
		if (!ArmorType.isArmor(item.getType()) || AxArmor.getAxArmor(item) != null) return;
		new BukkitRunnable() {
			public void run() {
				try {
					event.getClickedInventory().setItem(event.getSlot(),AxArmor.getLegalAxArmor(item).item(player));
				} catch (Exception e) {
					event.getClickedInventory().setItem(event.getSlot(),null);
				}
			}
		}.runTask(AxArmors.getInstance());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void changeCreativeArmorDropEvent(PlayerDropItemEvent event) {
		if (event.isCancelled() || event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
		Item drop = event.getItemDrop();
		ItemStack item = event.getItemDrop().getItemStack();
		if (Utils.isNull(item)) return;
		if (ArmorType.isArmor(item.getType()) && AxArmor.getAxArmor(item) == null) {
			try {
				drop.setItemStack(AxArmor.getLegalAxArmor(item).item(event.getPlayer()));
			} catch (Exception e) {
				drop.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void fixLootGenerateArmorsEvent(LootGenerateEvent event) {
		if (event.getLoot() == null || event.getLoot().isEmpty()) return;
		List<ItemStack> loot = new ArrayList<ItemStack>();
		ItemStack item;
		for (int i = 0; i < event.getLoot().size(); i++) {
			item = event.getLoot().get(i);
			if (Utils.isNull(item)) continue;
			if (ArmorType.isArmor(item.getType()) && AxArmor.getAxArmor(item) == null) {
				try {
					loot.add(AxArmor.getLegalAxArmor(item).item(null));
				} catch (Exception e) {}
			}
		}
		event.setLoot(loot);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void disableGiveArmorsCommandEvent(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().toLowerCase().replace("/","");
		String command = cmd.split(" ")[0];
		if (command.contains(":")) command = command.split(":")[1];
		String[] cmdSplit = cmd.split(" ");
		if (cmdSplit.length >= 3 && command.equals("give")) {
			String name = cmdSplit[2].split("\\{")[0];
			boolean stop = false;
			for (ArmorSlot slot : ArmorSlot.values()) if (name.contains(slot.name().toLowerCase())) stop = true;
			if (stop) {
				event.setCancelled(true);
				Utils.chatColors(event.getPlayer(),"&cCommand disabled for armors. Use &f/armor give &cinstead.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void disableGrindstoneRepairEvent(InventoryClickEvent event) {
		if (event.isCancelled() || event.getInventory().getType() != InventoryType.GRINDSTONE) return;
		new BukkitRunnable() {
			public void run() {
				ItemStack item1 = event.getInventory().getItem(0);
				ItemStack item2 = event.getInventory().getItem(1);
				ItemStack result = event.getInventory().getItem(2);
				if (Utils.isNull(result) || (Utils.isNull(item1) && Utils.isNull(item2))) return;
				AxArmor armor = AxArmor.getAxArmor(result);
				if (armor == null) return;
				if (Utils.isNull(item1) || Utils.isNull(item2)) event.getInventory().setItem(2,armor.item((Player) event.getWhoClicked()));
			}
		}.runTask(AxArmors.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void fixArmorAnvilEvent(PrepareAnvilEvent event) {
		if (event.getViewers().isEmpty()) return;
		Player player = (Player) event.getView().getPlayer();
		new BukkitRunnable() {
			public void run() {
				ItemStack item1 = event.getInventory().getItem(0);
				ItemStack result = event.getInventory().getItem(2);
				AxArmor armor1 = AxArmor.getAxArmor(item1);
				AxArmor armor = AxArmor.getAxArmor(result);
				if (armor1 == null) return;
				ItemStack item2 = event.getInventory().getItem(1);
				if (Utils.isNull(item2)) {
					if (armor != null) event.getInventory().setItem(2,armor.item(player));
				} else if (armor.repairItemKey != null && armor.repairItemKey.equals(AxItem.getAxItem(item2).key())) {
					int amount = armor1.itemMaterialAmountToFull();
					amount = Math.min(amount,item2.getAmount());
					if (amount <= 0) return;
					armor.repairWithMaterial(amount);
					event.getInventory().setItem(2,armor.item(player));
					player.updateInventory();
				}
			}
		}.runTask(AxArmors.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void clickAnvilArmorEvent(InventoryClickEvent event) {
		if (event.isCancelled() || event.getInventory().getType() != InventoryType.ANVIL) return;
		ItemStack item1 = event.getInventory().getItem(0);
		ItemStack item2 = event.getInventory().getItem(1);
		AxArmor armor1 = AxArmor.getAxArmor(item1);
		if (armor1 == null || Utils.isNull(item2)) return;
		new BukkitRunnable() {
			public void run() {
				if (event.isCancelled()) return;
				if (armor1.repairItemKey != null && armor1.repairItemKey.equals(AxItem.getAxItem(item2).key()))
					Utils.uniqueCraftingHandle(event,Math.min(item2.getAmount(),armor1.itemMaterialAmountToFull()),1);
				else if (item2.getType() == Material.ENCHANTED_BOOK) Utils.uniqueCraftingHandle(event,1,1);
			}
		}.runTask(AxArmors.getInstance());
	}
}