package me.DMan16.AxArmors;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.ReflectionUtils;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxStats.AxStat;
import me.DMan16.AxStats.AxStats;
import me.DMan16.AxStats.EquipSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.*;

public enum ArmorType {
	BRONZE("LEATHER",1,0,0,0,0,1,null,true),
	CHAINMAIL("CHAINMAIL",2,0,0,0,0,2,"IRON_INGOT",true),
	IRON("IRON",3,0,0,0,0,3,"IRON_INGOT",true),
	STEEL("IRON",4,0,0,0,0,4,null,false),
	MITHRIL("DIAMOND",6,0,0,4,0,5,null,false),
	ADAMANT("DIAMOND",8,2,2,0,0,5,null,false),
	PRISMARINE("DIAMOND",7,0,0,6,10,6,"PRISMARINE",false),
	COBALT("NETHERITE",9,4,4,0,15,6,"LAPIS_LAZULI",false),
	MAGMA("NETHERITE",8,0,0,8,15,7,"MAGMA_BLOCK",false,TextColor.color(0xff4e01),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.strength(),1,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),1,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),1,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),2,null,false,EquipSlot.SET))))),
	EMERALD("DIAMOND",10,6,6,0,20,7,"EMERALD",false,TextColor.color(0x50c878),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.stamina(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),1,null,false,EquipSlot.SET))))),
	DIAMOND("DIAMOND",9,0,0,10,25,8,"DIAMOND",true,TextColor.color(0xb9f2ff),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.strength(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),2,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),4,null,false,EquipSlot.SET))))),
	OBSIDIAN("NETHERITE",11,8,8,0,30,9,"OBSIDIAN",false,TextColor.color(0x343637),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.stamina(),4,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),4,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),4,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),2,null,false,EquipSlot.SET))))),
	NETHERITE("NETHERITE",10,0,0,12,35,9,"NETHERITE_INGOT",true,TextColor.color(0x4f4f4f),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.strength(),3,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),3,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),3,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),6,null,false,EquipSlot.SET))))),
	WOLFRAM("IRON",12,10,10,0,40,9,"NETHERITE_INGOT",false,TextColor.color(0xc0c0c0),makeStatsMap(Arrays.asList(1,2,3,4),
			Arrays.asList(Arrays.asList(new AxStat(AxStats.stamina(),6,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),6,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.stamina(),6,null,false,EquipSlot.SET)),
					Arrays.asList(new AxStat(AxStats.strength(),3,null,false,EquipSlot.SET)))));

	private static SortedMap<Integer,List<AxStat>> makeStatsMap(List<Integer> counts, List<List<AxStat>> stats) {
		SortedMap<Integer,List<AxStat>> map = new TreeMap<Integer,List<AxStat>>();
		for (int i = 0; i < Math.min(counts.size(),stats.size()); i++) map.put(counts.get(i),stats.get(i));
		return map;
	}
	
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
	private final TextColor color;
	private final SortedMap<Integer,List<AxStat>> stats;

	private ArmorType(String material, int defense, int toughness, int stamina, int strength, int durability, int tier, String repairItemKey, boolean original) {
		this(material,defense,toughness,stamina,strength,durability,tier,repairItemKey,original,null,null);
	}
	
	private ArmorType(String material, int defense, int toughness, int stamina, int strength, int durability, int tier, String repairItemKey, boolean original, TextColor color,
					SortedMap<Integer,List<AxStat>> stats) {
		this.material = material;
		this.defense = defense;
		this.toughness = toughness;
		this.stamina = stamina;
		this.strength = strength;
		this.durability = durability;
		this.tier = tier;
		this.repairItemKey = repairItemKey;
		this.original = original;
		this.color = color;
		this.stats = stats;
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
	
	public AxStat[] getStats(ArmorSlot slot) {
		List<AxStat> stats = new ArrayList<AxStat>();
		int defense = getDefense(slot);
		int toughness = getToughness(slot);
		int stamina = getStamina(slot);
		int strength = getStrength(slot);
		if (defense != 0) stats.add(new AxStat(AxStats.defense(),defense,null,false,slot.slot));
		if (toughness != 0) stats.add(new AxStat(AxStats.toughness(),toughness,null,false,slot.slot));
		if (stamina != 0) stats.add(new AxStat(AxStats.stamina(),stamina,null,false,slot.slot));
		if (strength != 0) stats.add(new AxStat(AxStats.strength(),strength,null,false,slot.slot));
		return stats.toArray(new AxStat[0]);
	}
	
	TextColor getColor() {
		return color;
	}
	
	SortedMap<Integer,List<AxStat>> getSetStats() {
		return stats;
	}
	
	/**
	 * 0 = Unbreakable
	 */
	public int getMaxDurability(ArmorSlot slot) {
		if (isUnbreakable()) return 0;
		return durability * mults[slot.ordinal()];
	}
	
	public boolean isUnbreakable() {
		return tier < UnbreakableMaxTier;
	}
	
	public int getTier(ArmorSlot slot) {
		return tier;
	}
	
	public String getRepairKey() {
		return repairItemKey;
	}
	
	public AxItem getRepairItem() {
		return AxItem.getAxItem(repairItemKey);
	}
	
	public String getTranslatableName(ArmorSlot slot) {
		if (slot == null) return null;
		return ((material.equals(name()) ? translateItemMC : translateItemBase) + name() + "_" + slot.name()).toLowerCase();
	}
	
	public boolean isOriginal() {
		return original;
	}
}