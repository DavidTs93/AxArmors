package me.DMan16.AxArmors;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxItems.Items.AxSet;
import me.DMan16.AxStats.AxStat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class AxArmors extends JavaPlugin {
	private static AxArmors instance;
	
	public void onEnable() {
		instance = this;
		new ArmorListener();
		createArmors();
		Utils.chatColorsLogPlugin("&fAxArmors &aloaded!");
	}
	
	private void createArmors() {
		for (ArmorType type : ArmorType.values()) {
			boolean unbreakable = type.isUnbreakable();
			SortedMap<Integer,List<AxStat>> stats = type.getSetStats();
			List<String> armors = unbreakable ? null : new ArrayList<String>();
			for (ArmorSlot slot : ArmorSlot.values()) {
				AxArmor armor = new AxArmor(type,slot);
				armor.register();
				if (stats != null) armors.add(armor.key());
			}
			if (stats != null) new AxSet(type.name(),type.getColor(),stats,armors).register();
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