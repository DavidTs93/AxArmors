package me.DMan16.AxArmors.Armors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Aldreda.AxUtils.Classes.Pair;
import me.Aldreda.AxUtils.Enums.Tags;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxItems.Items.AxItem;
import me.DMan16.AxItems.Restrictions.Restrictions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@SuppressWarnings("unchecked")
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
		super(Utils.makeItem(type.getMaterial(slot),Component.translatable(type.getTranslatableName(slot)).decoration(TextDecoration.ITALIC,false),
				ItemFlag.values()),defaultKey(type,slot),
				/*(info) -> {
					info.second().getPlayer().sendMessage(Component.text(Utils.chatColors("&bEquip ")).append(info.first().name().hoverEvent(
							info.first().item().asHoverEvent())));
				},
				null,*/
				Utils.joinLists(Tags.get(type.getMaterial(slot)).stream().map(tag -> tag.name()).collect(Collectors.toList()),
						Arrays.asList("armor",type.name(),slot.name())).toArray(new String[0]));
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
		updateArmor();
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
	
	protected int getHealthBonus() {
		if (stamina == 0) return 0;
		float mult = 1;
		return Math.round(stamina * mult);
	}
	
	protected int getStrengthBonus() {
		if (strength == 0) return 0;
		float mult = 0.5F;
		return Math.round(strength * mult);
	}

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
	
	public AxArmor updateArmor() {
		if (maxDurability == 0) unbreakable(true);
		return updateName().updateAttributes().updateDurability();
	}
	
	protected AxArmor updateName() {
		Component name = Component.translatable(type.getTranslatableName(slot)).decoration(TextDecoration.ITALIC,false);
		name(name);
		return this;
	}
	
	protected AxArmor updateLore() {
		List<Component> lore = new ArrayList<Component>();
		lore.add(Component.empty());
		if (defense != 0)
			lore.add(Component.translatable(translateStatBase + "defense",NamedTextColor.AQUA).args(Component.text(defense).color(NamedTextColor.LIGHT_PURPLE)).
					decoration(TextDecoration.ITALIC,false));
		if (toughness != 0)
			lore.add(Component.translatable(translateStatBase + "toughness",NamedTextColor.DARK_GRAY).args(Component.text(toughness).color(NamedTextColor.LIGHT_PURPLE)).
					decoration(TextDecoration.ITALIC,false));
		if (stamina != 0)
			lore.add(Component.translatable(translateStatBase + "stamina",NamedTextColor.YELLOW).args(Component.text(stamina).color(NamedTextColor.LIGHT_PURPLE)).
					decoration(TextDecoration.ITALIC,false));
		if (strength != 0)
			lore.add(Component.translatable(translateStatBase + "strength",NamedTextColor.RED).args(Component.text(strength).color(NamedTextColor.LIGHT_PURPLE)).
					decoration(TextDecoration.ITALIC,false));
		if (getEnchantments().entrySet().size() > 0) {
			lore.add(Component.empty());
			for (Entry<Enchantment,Integer> ench : getEnchantments().entrySet()) {
				NamespacedKey key = ench.getKey().getKey();
				Component comp = Component.translatable("enchantment." + key.getNamespace() + "." + key.getKey());
				if (ench.getValue() !=  ench.getKey().getStartLevel() || ench.getKey().getStartLevel() !=  ench.getKey().getMaxLevel())
					comp = comp.append(Component.space()).append(Component.text(Utils.toRoman(ench.getValue())));
				lore.add(comp.color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
			}
		}
		lore.add(Component.empty());
		Component durabilityLine;
		if (maxDurability > 0) {
			TextColor color;
			double ratio = (double)maxDurability /durability;
			if (ratio >= 100) color = NamedTextColor.RED;
			else if (ratio >= 20) color = NamedTextColor.GOLD;
			else if (ratio >= 2) color = NamedTextColor.YELLOW;
			else color = NamedTextColor.GREEN;
			durabilityLine = Component.translatable("item.durability",NamedTextColor.GRAY,Component.text(durability,color),
					Component.text(maxDurability,NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC,false);
		} else durabilityLine = Component.translatable("item.unbreakable",NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,false);
		lore.add(durabilityLine);
		lore(lore);
		return this;
	}
	
	protected AxArmor updateAttributes() {
		addAttributes(Pair.of(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(),attributeKey,0,Operation.ADD_NUMBER,
				EquipmentSlot.OFF_HAND)));
		if (stamina != 0) addAttributes(Pair.of(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(),attributeKey,getHealthBonus(),
				Operation.ADD_NUMBER,slot.slot)));
		if (strength != 0) addAttributes(Pair.of(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),attributeKey,getStrengthBonus(),
				Operation.ADD_NUMBER,slot.slot)));
		return this;
	}
	
	protected AxArmor updateDurability() {
		durability = Math.max(Math.min(durability,maxDurability),0);
		if (maxDurability > 0) PersistentDataContainerSet(durabilityKey,PersistentDataType.INTEGER,durability);
		broken = durability <= 0 && maxDurability > 0;
		return updateLore();
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
	
	@Override
	public AxArmor addEnchantments(Pair<Enchantment,Integer> ... enchants) {
		super.addEnchantments(enchants);
		updateLore();
		return this;
	}

	@Override
	public AxArmor removeEnchantments(Enchantment ... enchants) {
		super.removeEnchantments(enchants);
		updateLore();
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
	
	public static AxArmor getAldredaArmor(ItemStack item) {
		try {
			if (item.getItemMeta().getPersistentDataContainer().has(brokenKey,PersistentDataType.STRING))
				return getAldredaArmor((ItemStack) Utils.ObjectFromBase64(item.getItemMeta().getPersistentDataContainer().get(brokenKey,PersistentDataType.STRING)));
			AxArmor armor = (AxArmor) AxItem.getAxItem(item);
			if (armor.maxDurability > 0)
				armor.damage(armor.maxDurability - item.getItemMeta().getPersistentDataContainer().get(durabilityKey,PersistentDataType.INTEGER));
			return armor;
		} catch (Exception e) {}
		return null;
	}
	
	public static AxArmor getLegalAldredaArmor(ItemStack item) {
		AxArmor armor = getAldredaArmor(item);
		if (armor != null) return armor;
		for (ArmorType type : ArmorType.values()) for (ArmorSlot slot : ArmorSlot.values()) try {
				if (type.isOriginal() && item.getType().equals(type.getMaterial(slot))) return (AxArmor) AxItem.getAxItem(defaultKey(type,slot));
		} catch (Exception e) {}
		return null;
	}
}