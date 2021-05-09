package me.DMan16.AxArmors.Armors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxItems.Restrictions.Restrictions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AxArmor extends AxItem {
	protected final String translateStatBase = "attribute.name.aldreda.";
	protected final static NamespacedKey typeKey = Utils.namespacedKey("type");
	protected final static NamespacedKey durabilityKey = Utils.namespacedKey("durability");
	protected final String attributeKey = Utils.namespacedKey("attribute").toString();
	protected final static NamespacedKey brokenKey = Utils.namespacedKey("broken");
	
	public final ArmorType type;
	public final int defense;
	public final int toughness;
	public final int stamina;
	public final int strength;
	public final int maxDurability;
	protected int durability;
	public final int tier;
	public final String repairItemKey;
	public final ArmorSlot slot;
	protected boolean broken;
	
	public AxArmor(ArmorType type, ArmorSlot slot) {
		super(Utils.makeItem(type.getMaterial(slot),null,ItemFlag.values()),													// item
				defaultKey(type,slot),																							// key
				Component.translatable(type.getTranslatableName(slot)).decoration(TextDecoration.ITALIC,false),					// name
				null, /* topLore */ null, /* bottomLore */
				/*(info) -> {
					info.second().getPlayer().sendMessage(Component.text(Utils.chatColors("&bEquip ")).append(info.first().name().hoverEvent(
							info.first().item().asHoverEvent())));
				},
				null,*/
				Utils.joinLists(Tags.get(type.getMaterial(slot)).stream().map(tag -> tag.name()).collect(Collectors.toList()),Arrays.asList("armor",type.name(),slot.name())),
				type.getStats(slot)																								// stats
				);
		this.type = type;
		this.defense = type.getDefense(slot);
		this.toughness = type.getToughness(slot);
		this.stamina = type.getStamina(slot);
		this.strength = type.getStrength(slot);
		this.maxDurability = type.getMaxDurability(slot);
		this.durability = this.maxDurability;
		this.tier = type.getTier(slot);
		this.repairItemKey = type.getRepairKey();
		this.slot = slot;
		this.broken = false;
		PersistentDataContainerSet(typeKey,PersistentDataType.STRING,type.name());
		if (this.maxDurability <= 0) unbreakable(true);
	}

	public static String defaultKey(ArmorType type, ArmorSlot slot) {
		return ("armor_" + Objects.requireNonNull(type.name()) + "_" + Objects.requireNonNull(slot).name()).toLowerCase();
	}
	
	/*private int getDefenseBonus() {
		return defense;
	}
	
	private int getToughnessBonus() {
		return toughness;
	}*/

	public boolean isBroken() {
		return broken;
	}
	
	public int getDurability() {
		return durability;
	}
	
	@Override
	public ItemStack item() {
		if (broken) return brokenArmor();
		return super.item();
	}
	
	// !!!
	@Override
	protected List<Component> belowBottomLore() {
		List<Component> belowBottomLore = new ArrayList<Component>();
		Component durabilityLine;
		if (maxDurability > 0) {
			TextColor color;
			double ratio = (double)maxDurability / durability;
			if (ratio >= 100) color = NamedTextColor.RED;
			else if (ratio >= 20) color = NamedTextColor.GOLD;
			else if (ratio >= 2) color = NamedTextColor.YELLOW;
			else color = NamedTextColor.GREEN;
			durabilityLine = Component.translatable("item.durability",NamedTextColor.GRAY,Component.text(durability,color),
					Component.text(maxDurability,NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC,false);
		} else durabilityLine = Component.translatable("item.unbreakable",NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,false);
		belowBottomLore.add(durabilityLine);
		return belowBottomLore;
	}
	
	/*protected AxArmor updateAttribute() {
		addAttributes(Pair.of(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(),attributeKey,0,Operation.ADD_NUMBER,
				EquipmentSlot.OFF_HAND)));
		if (stamina != 0) addAttributes(Pair.of(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(),attributeKey,getHealthBonus(),
				Operation.ADD_NUMBER,slot.slot)));
		if (strength != 0) addAttributes(Pair.of(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),attributeKey,getStrengthBonus(),
				Operation.ADD_NUMBER,slot.slot)));
		return this;
	}*/
	
	protected AxArmor updateDurability() {
		durability = Math.max(Math.min(durability,maxDurability),0);
		if (maxDurability > 0) PersistentDataContainerSet(durabilityKey,PersistentDataType.INTEGER,durability);
		broken = durability <= 0 && maxDurability > 0;
		return this;
	}
	
	public AxArmor damage(int amount) {
		return changeDurability(-Math.abs(amount),false);
	}
	
	public AxArmor repair(int amount) {
		return changeDurability(Math.abs(amount),false);
	}
	
	public AxArmor repairWithMaterial(int times) {
		return changeDurability(Math.abs(times),true);
	}
	
	public int itemMaterialAmountToFull() {
		if (maxDurability <= 0) return 0;
		return (int) Math.ceil((((double)maxDurability - durability) / maxDurability) * 4);
	}
	
	protected AxArmor changeDurability(int amount, boolean percent) {
		if (maxDurability <= 0 || amount == 0) return this;
		int change;
		if (durability > 0) change = percent ? (maxDurability * amount) / 4 : amount;
		else change = amount > 0 ? 1 : -1;
		durability = Math.min(maxDurability,Math.max(0,durability + change));
		updateDurability();
		return this;
	}
	
	protected ItemStack brokenArmor() {
		Component name = name().append(Component.text(" (").append(Component.translatable(ArmorType.translateItemBase +
				"broken")).append(Component.text(")")).decoration(TextDecoration.ITALIC,false));
		ItemStack item = Utils.makeItem(type.getMaterial(slot),name,ItemFlag.values());
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(),attributeKey,0,Operation.ADD_NUMBER,
				EquipmentSlot.OFF_HAND));
		meta.getPersistentDataContainer().set(brokenKey,PersistentDataType.STRING,Utils.ObjectToBase64(super.item()));
		item.setItemMeta(meta);
		return Restrictions.Unequippable.add(item);
	}
	
	@Override
	protected AxItem update(ItemStack item) {
		return getAxArmor(item);
	}
	
	public static AxArmor getAxArmor(ItemStack item) {
		try {
			if (item.getItemMeta().getPersistentDataContainer().has(brokenKey,PersistentDataType.STRING))
				return getAxArmor((ItemStack) Utils.ObjectFromBase64(item.getItemMeta().getPersistentDataContainer().get(brokenKey,PersistentDataType.STRING)));
			AxArmor armor = (AxArmor) AxItem.getAxItem(item);
			if (armor.maxDurability > 0)
				armor.damage(armor.maxDurability - item.getItemMeta().getPersistentDataContainer().get(durabilityKey,PersistentDataType.INTEGER));
			return armor;
		} catch (Exception e) {}
		return null;
	}
	
	public static AxArmor getLegalAxArmor(ItemStack item) {
		AxArmor armor = getAxArmor(item);
		if (armor != null) return armor;
		for (ArmorType type : ArmorType.values()) for (ArmorSlot slot : ArmorSlot.values()) try {
				if (type.isOriginal() && item.getType().equals(type.getMaterial(slot))) return (AxArmor) AxItem.getAxItem(defaultKey(type,slot));
		} catch (Exception e) {}
		return null;
	}
}