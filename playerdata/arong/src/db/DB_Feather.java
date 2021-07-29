package db;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DB_Feather {
	
	public static ItemStack[] get_all_items()
	{
		ItemStack[] items = 
		{
			weapon_feather(),
			potion_feather(),
			skill_feather()		
		};
		
		return items;
	}
	
	private static ItemStack get_feather_item()
	{  
		return new ItemStack(Material.FEATHER);
	}
	
	public static ItemStack get_random_item()
	{
		ItemStack[] items = get_all_items();
		
		int size = items.length;
		int index = (int) (Math.random() * size);
		return items[index];
	}
	
//	private static List<String> get_lore(String text)
//	{
//		List<String> lore = new ArrayList<String>();
//		lore.add(text);
//		
//		return lore;
//	}
	
	
	// MARK: weapon feather
	private static ItemStack weapon_feather()
	{
		
		ItemStack item = get_feather_item();
		item = set_item_meta_display_name(item, "Weapon");
		
		// MARK: list.add()할때마다 개행됨
		// MARK: \n 사용불가
		String[] lore = {
				"random weapon"
		};
		item = set_item_meta_lore(item, lore);
		
		return item;
	}
	
	public static ItemStack random_weapon()
	{
		ItemStack[] items = 
			{
				weapon_1(),
				weapon_2(),
				weapon_3(),
				weapon_4(),
				weapon_5(),
				weapon_6(),
				weapon_7(),
				weapon_8(),
				weapon_9(),
				weapon_10(),
				weapon_11(),
			};
		
		int size = items.length;
		int index = (int) (Math.random() * size);
		
		return items[index];
	}
	
	private static ItemStack weapon_1()
	{
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		item.addEnchantment(Enchantment.KNOCKBACK, 1);
		return item;
	}
	
	private static ItemStack weapon_2()
	{
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		return item;
	}
	
	private static ItemStack weapon_3()
	{
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		return item;
	}
	
	private static ItemStack weapon_4()
	{
		ItemStack item = new ItemStack(Material.ARROW, 30);
		return item;
	}
	
	private static ItemStack weapon_5()
	{
		ItemStack item = new ItemStack(Material.BOW, 1);
		item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		return item;
	}
	
	private static ItemStack weapon_6()
	{
		ItemStack item = new ItemStack(Material.BOW, 1);
		item.addEnchantment(Enchantment.ARROW_FIRE, 1);
		return item;
	}
	
	private static ItemStack weapon_7()
	{
		ItemStack item = new ItemStack(Material.BOW, 1);
		item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		return item;
	}
	
	private static ItemStack weapon_8()
	{
		ItemStack item = new ItemStack(Material.GOLD_HELMET, 1);
		item.addEnchantment(Enchantment.THORNS, 1);
		return item;
	}
	
	private static ItemStack weapon_9()
	{
		ItemStack item = new ItemStack(Material.GOLD_LEGGINGS, 1);
		item.addEnchantment(Enchantment.THORNS, 1);
		return item;
	}
	
	private static ItemStack weapon_10()
	{
		ItemStack item = new ItemStack(Material.GOLD_BOOTS, 1);
		item.addEnchantment(Enchantment.THORNS, 1);
		return item;
	}
	
	private static ItemStack weapon_11()
	{
		ItemStack item = new ItemStack(Material.IRON_PICKAXE, 1);
		item.addEnchantment(Enchantment.DIG_SPEED, 1);
		return item;
	}
	
	// MARK: potion feather
	private static ItemStack potion_feather()
	{
		
		ItemStack item = get_feather_item();
		
		item = set_item_meta_display_name(item, "Potion");

		String[] lore = {
				random_potion_effect()
		};
		item = set_item_meta_lore(item, lore);
		
		return item;
	}
	
	public static String random_potion_effect()
	{
		String[] effects = get_potion_effects_list();
		
		int size = effects.length;
		int index = (int) (Math.random() * size);
		
		return effects[index];
	}
	
	public static String[] get_potion_effects_list()
	{
		String[] effects = {
				"speed",
				"increase_damage",
				"regeneration",
				"damage_resistance",
				"absorption",		
				"fire_resistance",
				"jump"
		};
		
		return effects;
	}
	
	// MARK: skill feather
	private static ItemStack skill_feather()
	{
		
		ItemStack item = get_feather_item();
		
		item = set_item_meta_display_name(item, "Skill");

		String[] lore = {
				random_skill()
		};
		item = set_item_meta_lore(item, lore);
		
		return item;
	}
	
	public static String random_skill()
	{
		String[] skills = get_skills_list();
		
		int size = skills.length;
		int index = (int) (Math.random() * size);
		
		return skills[index];
	}
	
	public static String[] get_skills_list()
	{
		String[] skills = {
				"hide",
				"heal",
				"chat",
				"swap_token",
//				"killer_tp",
//				"fishing_tp",
				"thanos",
				"superstar"
		};
		
		return skills;
	}
	
	
	static ItemStack set_item_meta_display_name(ItemStack item, String display_name)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(display_name);
		item.setItemMeta(meta);
		
		return item;
	}
	
	static ItemStack set_item_meta_lore(ItemStack item, String[] lores)
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore_list = new ArrayList<String>();
		
		for(String lore : lores)
		{
			lore_list.add(lore);
		}
		
		meta.setLore(lore_list);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	
	// MARK: debug cmd
	public static ItemStack get_all_weapon_feather()
	{
		ItemStack weapon_feather = get_feather_item();
		
		weapon_feather = set_item_meta_display_name(weapon_feather, "Weapon");
		String[] lore = {"random weapon"};
		weapon_feather = set_item_meta_lore(weapon_feather, lore);
		
		return weapon_feather;
	}
	
	
	// MARK: debug cmd
	public static ItemStack[] get_all_potion_feather()
	{
		String[] list = get_potion_effects_list();
		int size = list.length;
		
		ItemStack[] feathers = new ItemStack[size];
		for (int i = 0; i < feathers.length; i++) {
			feathers[i] = get_feather_item();
		}
		
		int index = 0;
		for(ItemStack potion_feather : feathers)
		{ 
			potion_feather = set_item_meta_display_name(potion_feather, "Potion");

			String[] lore = {
				list[index++]
			};
			potion_feather = set_item_meta_lore(potion_feather, lore);
		}
		
		return feathers;
	}
	
	
	// MARK: debug cmd
	public static ItemStack[] get_all_skill_feather()
	{
		String[] list = get_skills_list();
		int size = list.length;
		
		ItemStack[] feathers = new ItemStack[size];
		for (int i = 0; i < feathers.length; i++) {
			feathers[i] = get_feather_item();
		}
		
		int index = 0;
		for(ItemStack skill_feather : feathers)
		{ 
			skill_feather = set_item_meta_display_name(skill_feather, "Skill");

			String[] lore = {
				list[index++]
			};
			skill_feather = set_item_meta_lore(skill_feather, lore);
		}
		
		return feathers;
	}
}




































