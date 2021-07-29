package plugin.event.easter_egg;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EA_Listener_Player implements Listener{
	
	@EventHandler
	public void onPlayerMineIron(BlockBreakEvent e)
	{
		Block b = e.getBlock();

		if(b.getType() == Material.IRON_ORE)
		{
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item != null && item.getType() == Material.IRON_PICKAXE)
			{
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 0));
				p.sendMessage("easter_egg!");
			}
		}
	}
}
