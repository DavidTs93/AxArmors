package me.DMan16.AxArmors.Armors;

import java.lang.reflect.Field;

import org.bukkit.Material;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.ReflectionUtils;

public enum ArmorType {
	BRONZE("LEATHER",
			1,			// Defense
			0,			// Toughness
			0,			// Stamina
			0,			// Strength
			0,			// Durability
			1,			// Tier
			null,		// Repair item
			true		// Original
			),
	CHAINMAIL("CHAINMAIL",
			2,			// Defense
			0,			// Toughness
			0,			// Stamina
			0,			// Strength
			0,			// Durability
			2,			// Tier
			"IRON_INGOT",		// Repair item
			true		// Original
			),
	IRON("IRON",
			3,			// Defense
			0,			// Toughness
			0,			// Stamina
			0,			// Strength
			0,			// Durability
			3,			// Tier
			"IRON_INGOT",		// Repair item
			true		// Original
			),
	STEEL("IRON",
			4,			// Defense
			0,			// Toughness
			0,			// Stamina
			0,			// Strength
			0,			// Durability
			4,			// Tier
			null,		// Repair item
			false		// Original
			),
	MITHRIL("DIAMOND",
			6,			// Defense
			0,			// Toughness
			0,			// Stamina
			4,			// Strength
			0,			// Durability
			5,			// Tier
			null,		// Repair item
			false		// Original
			),
	ADAMANT("DIAMOND",
			8,			// Defense
			2,			// Toughness
			2,			// Stamina
			0,			// Strength
			0,			// Durability
			5,			// Tier
			null,		// Repair item
			false		// Original
			),
	PRISMARINE("DIAMOND",
			7,			// Defense
			0,			// Toughness
			0,			// Stamina
			6,			// Strength
			10,			// Durability
			6,			// Tier
			"PRISMARINE",		// Repair item
			false		// Original
			),
	COBALT("NETHERITE",
			9,			// Defense
			4,			// Toughness
			4,			// Stamina
			0,			// Strength
			15,			// Durability
			6,			// Tier
			"LAPIS_LAZULI",		// Repair item
			false		// Original
			),
	MAGMA("NETHERITE",
			8,			// Defense
			0,			// Toughness
			0,			// Stamina
			8,			// Strength
			15,			// Durability
			7,			// Tier
			"MAGMA_BLOCK",		// Repair item
			false		// Original
			),
	EMERALD("DIAMOND",
			10,			// Defense
			6,			// Toughness
			6,			// Stamina
			0,			// Strength
			20,			// Durability
			7,			// Tier
			"EMERALD",		// Repair item
			false		// Original
			),
	DIAMOND("DIAMOND",
			9,			// Defense
			0,			// Toughness
			0,			// Stamina
			10,			// Strength
			25,			// Durability
			8,			// Tier
			"DIAMOND",		// Repair item
			true		// Original
			),
	OBSIDIAN("NETHERITE",
			11,			// Defense
			8,			// Toughness
			8,			// Stamina
			0,			// Strength
			30,			// Durability
			9,			// Tier
			"OBSIDIAN",		// Repair item
			false		// Original
			),
	NETHERITE("NETHERITE",
			10,			// Defense
			0,			// Toughness
			0,			// Stamina
			12,			// Strength
			35,			// Durability
			9,			// Tier
			"NETHERITE_INGOT",		// Repair item
			true		// Original
			),
	WOLFRAM("IRON",
			12,			// Defense
			10,			// Toughness
			10,			// Stamina
			0,			// Strength
			40,			// Durability
			9,			// Tier
			"NETHERITE_INGOT",		// Repair item
			false		// Original
			);
	
	private static int[] mults = new int[] {11,16,15,13};
	private int UnbreakableMaxTier = 7;
	static final String translateItemBase = "item.aldreda.armor.";
	private final String translateItemMC = "item.minecraft.";
	
	static {
		try {
			Class<?> EnumArmorMaterial = Class.forName("net.minecraft.server." + ReflectionUtils.version + ".EnumArmorMaterial");
			Field h = EnumArmorMaterial.getDeclaredField("h");
			h.setAccessible(true);
			int[] multsMC = (int[]) h.get(null);
			mults = new int[] {multsMC[3],multsMC[2],multsMC[1],multsMC[0]};
		} catch (Exception e) {}
	}
	
	private final String material;
	private final int defense;
	private final int toughness;
	private final int stamina;
	private final int strength;
	private final int durability;
	private final int tier;
	private final String repairItemKey;
	private final boolean original;
	
	private ArmorType(String material, int defense, int toughness, int stamina, int strength, int durability, int tier, String repairItemKey, boolean original) {
		this.material = material;
		this.defense = defense;
		this.toughness = toughness;
		this.stamina = stamina;
		this.strength = strength;
		this.durability = durability;
		this.tier = tier;
		this.repairItemKey = repairItemKey;
		this.original = original;
	}
	
	public static ArmorType getByName(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return null;
	}
	
	public static boolean isArmor(Material material) {
		if (material == null) return false;
		return Tags.HELMETS.contains(material) || Tags.CHESTPLATES.contains(material) || Tags.LEGGINGS.contains(material) || Tags.BOOTS.contains(material);
	}
	
	public Material getMaterial(ArmorSlot slot) {
		return Material.valueOf((material + "_" + slot.name()).toUpperCase());
	}
	
	public int getDefense(ArmorSlot slot) {
		int bonus = 0;
		if (slot == ArmorSlot.CHESTPLATE) bonus = 3;
		else if (slot == ArmorSlot.LEGGINGS) bonus = 2;
		return defense + bonus;
	}
	
	public int getToughness(ArmorSlot slot) {
		return toughness;
	}
	
	public int getStamina(ArmorSlot slot) {
		return stamina;
	}
	
	public int getStrength(ArmorSlot slot) {
		return strength;
	}
	
	/**
	 * 0 = Unbreakable
	 */
	public int getMaxDurability(ArmorSlot slot) {
		if (tier < UnbreakableMaxTier) return 0;
		return durability * mults[slot.ordinal()];
	}
	
	public int getTier(ArmorSlot slot) {
		return tier;
	}
	
	public String getRepairKey() {
		return repairItemKey;
	}
	
	public String getTranslatableName(ArmorSlot slot) {
		if (slot == null) return null;
		return ((material.equals(name()) ? translateItemMC : translateItemBase) + name() + "_" + slot.name()).toLowerCase();
	}
	
	public boolean isOriginal() {
		return original;
	}
}