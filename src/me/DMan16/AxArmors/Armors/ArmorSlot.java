package me.DMan16.AxArmors.Armors;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.Aldreda.AxUtils.Utils.Utils;

public enum ArmorSlot {
	HELMET(EquipmentSlot.HEAD,5),
	CHESTPLATE(EquipmentSlot.CHEST,6),
	LEGGINGS(EquipmentSlot.LEGS,7),
	BOOTS(EquipmentSlot.FEET,8);
	
	public final EquipmentSlot slot;
	public final int slotNumber;
	
	private ArmorSlot(EquipmentSlot slot, int slotNumber) {
		this.slot = slot;
		this.slotNumber = slotNumber;
	}
	
	public static ArmorSlot getByName(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return null;
	}
	
	public static ArmorSlot getSlot(ItemStack item) {
		if (Utils.isNull(item)) return null;
		return getSlot(item.getType());
	}
	
	public static ArmorSlot getSlot(Material material) {
		if (material == null) return null;
		for (ArmorSlot slot : values()) if (material.getEquipmentSlot() == slot.slot) return slot;
		return null;
	}
	
	public static ArmorSlot getSlot(EquipmentSlot EquipmentSlot) {
		for (ArmorSlot slot : values()) if (slot.slot == EquipmentSlot) return slot;
		return null;
	}
}