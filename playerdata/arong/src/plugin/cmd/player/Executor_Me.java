package plugin.cmd.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import db.PlayerData;

public class Executor_Me implements CommandExecutor
{

	HashMap<UUID, PlayerData> db;
	
	public Executor_Me(HashMap<UUID, PlayerData> db) {
		this.db = db;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		// MARK: check sender is player
		if( ! (sender instanceof Player))
		{
			sender.sendMessage("Only Player");
			return true;
		}
		
		Player p = (Player) sender;
		
		PlayerData p_data = db.get(p.getUniqueId());
		
		int length = args.length;
		
		switch(length)
		{
		case 0:
			// MARK: print token amount
			int token = p_data.get_token();
			
			// MARK: print kill point
			int kill = p_data.get_kill_point();
			
			// MARK: print death point
			int death = p_data.get_death_point();;
			
			String msg = "[Info]";
			msg += "\ntoken: " + token;
			msg += "\nkill: " + kill;
			msg += "\ndeath: " + death;
			
			p.sendMessage(msg);
			
			return true;
		}
		
		return false;
	}
	
}







































