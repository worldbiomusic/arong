package plugin.cmd.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import db.DB_Feather;

// MARK: debug
public class Executor_Debug implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		// MARK: check sender is player
		if( ! (sender instanceof Player))
		{
			sender.sendMessage("Only Player");
			return true;
		}
		
		// MARK: check op
		if( ! sender.isOp())
		{
			sender.sendMessage("Only OP");
			return true;
		}
		
		Player p = (Player) sender;
		int length = args.length;
		
		switch(length)
		{
			case 2:
				return length_2(p, args);
		}
		
		return false;
	}
	
	boolean length_2(Player p, String[] args)
	{
		String kind = args[0];
		
		switch(kind)
		{
			case "feather":
				return kind_feather(p, args[1]); 
		}
		
		return false;
	}
	
	boolean kind_feather(Player p, String item)
	{
		switch(item)
		{
		case "weapon":
			ItemStack weapon_feather = DB_Feather.get_all_weapon_feather();
			p.getInventory().addItem(weapon_feather);
			
			return true;
			
		case "potion":
			ItemStack[] potion_feathers = DB_Feather.get_all_potion_feather();

			for(ItemStack feather : potion_feathers)
			{
				p.getInventory().addItem(feather);
			}
			return true;
					
		case "skill":
			ItemStack[] skill_feathers = DB_Feather.get_all_skill_feather();

			for(ItemStack feather : skill_feathers)
			{
				p.getInventory().addItem(feather);
			}
			
			return true;
			
		}
		return false;
	}
}






































