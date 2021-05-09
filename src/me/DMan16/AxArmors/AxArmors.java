package me.DMan16.AxArmors;

import java.util.ArrayList;
import java.util.Arrays;
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
import me.DMan16.AxStats.AxStat;
import me.DMan16.AxStats.AxStats;
import me.DMan16.AxStats.EquipSlot;

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
			boolean unbreakable = type.isUnbreakable();
			List<String> armors = unbreakable ? null : new ArrayList<String>();
			for (ArmorSlot slot : ArmorSlot.values()) {
				AxArmor armor = new AxArmor(type,slot);
				armor.register();
				if (!unbreakable) armors.add(armor.key());
			}
			if (!unbreakable) /*try {
				Utils.chatColorsLogPlugin("Set registered: " + (*/
				new AxSet(type.name(),Arrays.asList(new AxStat(AxStats.stamina(),4,null,false,(EquipSlot)null)),armors).register();
			/*).name());
			} catch (Exception e) {}*/
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