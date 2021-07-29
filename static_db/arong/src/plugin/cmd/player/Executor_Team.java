package plugin.cmd.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import db.DB_Team;

public class Executor_Team implements CommandExecutor
{

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

	
	
	
	boolean length_0(CommandSender sender)
	{
		// MARK: check sender is player
		if( ! ( sender instanceof Player))
		{
			send_MSG(sender, "Only Player");
			return true;
		}
				
		Player player = (Player) sender;
		send_MSG(player, "you are " + DB_Team.get_player_team_string(player) + " team");
		return true;
	}
	
	boolean length_1(CommandSender sender, String[] args)
	{
		String player_name = args[0];
		
		Player target_player = Bukkit.getPlayer(player_name);
		if(target_player == null)
		{
			send_MSG(sender, "not exist player");
			return true;
		}
		
		Player player = (Player) sender;
		send_MSG(player, player_name + " is " + DB_Team.get_player_team_string(target_player) + " team");
		return true;
	}
	
	// team change player team
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
					team = DB_Team.RED;
					break;
				case "BLUE":
					team = DB_Team.BLUE;
					break;
				case "BLACK":
					team = DB_Team.BLACK;
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
			
			// change
			DB_Team.change_team(p, team);
			
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











































