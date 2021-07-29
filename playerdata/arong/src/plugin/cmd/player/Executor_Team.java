package plugin.cmd.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import db.PlayerData;
import setting.Setting;

public class Executor_Team implements CommandExecutor
{
	HashMap<UUID, PlayerData> db;
	
	public Executor_Team(HashMap<UUID, PlayerData> db) {
		this.db = db;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		// MARK: check op
		if( ! sender.isOp())
		{
			sender.sendMessage("Only OP");
			return true;
		}
		
		int length = args.length;
		
		switch(length)
		{
			case 0:
				return length_0(sender);
			case 1:
				return length_1(sender, args);
			case 3:
				return length_3(sender, args);
		}
		
		return false;
	}

	
	
	// MARK: /team
	boolean length_0(CommandSender sender)
	{
		// MARK: check sender is player
		if( ! ( sender instanceof Player))
		{
			send_MSG(sender, "Only Player");
			return true;
		}
				
		Player player = (Player) sender;
		PlayerData p_data = db.get(player.getUniqueId());
		
		send_MSG(player, "you are " + p_data.get_string_team_colored(ChatColor.WHITE) + " team");
		
		return true;
	}
	
	// MARK: /team [player]
	boolean length_1(CommandSender sender, String[] args)
	{
		String player_name = args[0];
		
		Player target_player = Bukkit.getPlayer(player_name);

		
		if(target_player == null)
		{
			send_MSG(sender, "not exist player");
			return true;
		}
		
		PlayerData p_data = db.get(target_player.getUniqueId());
		
		Player player = (Player) sender;
		send_MSG(player, player_name + " is " + p_data.get_string_team_colored(ChatColor.WHITE) + " team");
		return true;
	}
	
	// MARK: /team setspawnloc [red | blue]
	boolean length_2(CommandSender sender, String[] args)
	{
		return false;
	}
	
	// MARK: team [register | remove] [player] [amount] 
	boolean length_3(CommandSender sender, String[] args)
	{
		String cmd = args[0];
		
		if(cmd.equalsIgnoreCase("change"))
		{
			String player_name = args[1];
			String team_str = args[2];
			int team;
			
			switch(team_str)
			{
				case "RED":
					team = Setting.RED_TEAM;
					break;
				case "BLUE":
					team = Setting.BLUE_TEAM;
					break;
				case "BLACK":
					team = Setting.BLACK_TEAM;
					break;
				default:
					return false;
			}
			
			Player p = Bukkit.getPlayer(player_name);
			if(p == null)
			{
				send_MSG(p, "is not exist");
				return true;
			}
			
			PlayerData p_data = db.get(p.getUniqueId());
			// change
			p_data.change_team(team);
			
			return true;
		}
		
		
		return false;
	}
	
	void send_MSG(CommandSender sender, String msg)
	{
		String text = "[DB_Team] ";
		text += msg;
		
		sender.sendMessage(text);
	}
}











































