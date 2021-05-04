package me.DMan16.AxArmors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxArmors.Armors.ArmorSlot;
import me.DMan16.AxArmors.Armors.ArmorType;
import me.DMan16.AxArmors.Armors.AxArmor;
import me.DMan16.AxArmors.Listeners.ArmorListener;
import me.DMan16.AxArmors.Listeners.CommandListener;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxItems.Items.AxSet;

public class AxArmors extends JavaPlugin {
	private static AxArmors instance;
	
	public void onEnable() {
		instance = this;
		new CommandListener();
		new ArmorListener();
		createArmors();
		Utils.chatColorsLogPlugin("&fAxArmors &aloaded!");
	}
	
	private void createArmors() {
		for (ArmorType type : ArmorType.values()) {
			boolean unbreakable = type.getMaxDurability(ArmorSlot.CHESTPLATE) <= 0;
			List<String> armors = unbreakable ? null : new ArrayList<String>();
			for (ArmorSlot slot : ArmorSlot.values()) {
				AxArmor armor = new AxArmor(type,slot);
				armor.register();
				if (!unbreakable) armors.add(armor.key());
			}
			if (!unbreakable) try {
				Utils.chatColorsLogPlugin("Set registered: " + (new AxSet(type.name(),armors.toArray(new String[0])).register()).name());
			} catch (Exception e) {}
		}

		AxItem.addDisabledVanillas(Tags.HELMETS.getMaterials());
		AxItem.addDisabledVanillas(Tags.CHESTPLATES.getMaterials());
		AxItem.addDisabledVanillas(Tags.LEGGINGS.getMaterials());
		AxItem.addDisabledVanillas(Tags.BOOTS.getMaterials());
	}

	public static AxArmors getInstance() {
		return instance;
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		Utils.chatColorsLogPlugin("&fAxArmors &adisabed");
	}
}