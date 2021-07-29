package plugin.recipe.easter_egg;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class EasterEgg_Recipe {
	
	Server server;
	
	public EasterEgg_Recipe(Server server)
	{
		this.server = server;
	}
	
	public void add()
	{
		//
		ItemStack punch_bow = new ItemStack(Material.BOW);
		punch_bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		
		add_shapeless_recipe(punch_bow, Material.BOW, Material.ARROW, Material.ARROW, Material.ARROW);
		
		//
		ItemStack knockback_sword = new ItemStack(Material.STONE_SWORD);
		knockback_sword.addEnchantment(Enchantment.KNOCKBACK, 1);
		
		add_shapeless_recipe(knockback_sword, Material.STONE_SWORD, Material.ARROW, Material.ARROW, Material.ARROW);
	}
	
	@SuppressWarnings("deprecation")
	void add_shapeless_recipe(ItemStack result, Material...materials)
	{
		ShapelessRecipe recipe = new ShapelessRecipe(result);
		
		for(Material m : materials)
		{
			recipe.addIngredient(m);
		}
		
		server.addRecipe(recipe);
	}
	
}
