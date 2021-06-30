package me.DMan16.AxArmors;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItemPerishable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class AxArmor extends AxItemPerishable {
	protected final static NamespacedKey typeKey = new NamespacedKey(AxArmors.getInstance(),"axarmor_type");
	
	public final ArmorType type;
	public final int defense;
	public final int toughness;
	public final int stamina;
	public final int strength;
	public final int tier;
	public final ArmorSlot slot;
	
	public AxArmor(ArmorType type, ArmorSlot slot) {
		super(Utils.makeItem(type.getMaterial(slot),null,ItemFlag.values()),defaultKey(type,slot),
				Component.translatable(type.getTranslatableName(slot)).decoration(TextDecoration.ITALIC,false),
				type.original() ? type.getMaterial(slot) : null,type.getMaxDurability(slot),type.getRepairKey());
		this.type = type;
		this.defense = type.getDefense(slot);
		this.toughness = type.getToughness(slot);
		this.stamina = type.getStamina(slot);
		this.strength = type.getStrength(slot);
		this.tier = type.getTier(slot);
		this.slot = slot;
		PersistentDataContainerSet(typeKey,PersistentDataType.STRING,type.name());
		addKeywords(Tags.get(type.getMaterial(slot)).stream().map(tag -> tag.name()).collect(Collectors.toList()));
		addKeywords("armor",type.name(),slot.name());
		addStats(type.getStats(slot));
	}

	public static String defaultKey(ArmorType type, ArmorSlot slot) {
		return ("armor_" + Objects.requireNonNull(type.name()) + "_" + Objects.requireNonNull(slot).name()).toLowerCase();
	}
}