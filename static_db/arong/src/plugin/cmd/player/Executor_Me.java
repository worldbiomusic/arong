package plugin.cmd.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import db.DB_Kill_Death;
import db.DB_Token;

public class Executor_Me implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		// MARK: check sender is player
		if( ! (sender instanceof Player))
		{
			sender.sendMessage("Only Player");
			return true;
		}
		
		Player p = (Player) sender;
		
		int length = args.length;
		
		switch(length)
		{
		case 0:
			// MARK: print token amount
			int token = DB_Token.get_player_token(p);
			
			// MARK: print kill point
			int kill = DB_Kill_Death.get_kill_point(p);
			
			// MARK: print death point
			int death = DB_Kill_Death.get_death_point(p);
			
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







































