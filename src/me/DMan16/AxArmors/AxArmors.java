package me.DMan16.AxArmors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxArmors.Armors.ArmorSlot;
import me.DMan16.AxArmors.Armors.ArmorType;
import me.DMan16.AxArmors.Armors.AxArmor;
import me.DMan16.AxArmors.Listeners.ArmorListener;
import me.DMan16.AxArmors.Listeners.CommandListener;

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
		try {
			for (ArmorType type : ArmorType.values()) for (ArmorSlot slot : ArmorSlot.values()) new AxArmor(type,slot).register();
		} catch (Exception e) {e.printStackTrace();}
	}

	public static AxArmors getInstance() {
		return instance;
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		Utils.chatColorsLogPlugin("&fAxArmors &adisabed");
	}
}