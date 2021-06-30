package me.DMan16.AxArmors;

import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Events.ArmorEquipEvent;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItemPerishable;
import me.DMan16.AxItems.Restrictions.ItemRestrictedEvent;
import me.DMan16.AxItems.Restrictions.Restrictions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;

public class ArmorListener extends Listener {
	
	public ArmorListener() {
		register(AxArmors.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true)
	public void disableEquipArmorsEvent(ItemRestrictedEvent event) {
		if (event.isCancelled() || !event.restriction.equals(Restrictions.Unequippable)) return;
		AxItemPerishable item = AxItemPerishable.getAxItem(event.item);
		if (item != null && item.isBroken())
			event.setCancelMSG(Component.translatable("item.aldreda.armor.broken_no_equip").color(TextColor.color(NamedTextColor.RED)).decoration(TextDecoration.ITALIC,false));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void disableEquipArmorsEvent(ArmorEquipEvent event) {
		if (event.isCancelled() || Utils.isNull(event.getNewArmor())) return;
		AxItemPerishable item = AxItemPerishable.getAxItem(event.getNewArmor());
		if (item == null || !(item instanceof AxArmor)) event.setCancelled(true);
	}
}