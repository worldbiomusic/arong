package plugin.cmd.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import db.DB_Token;

public class Executor_Token implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
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
	
	
	
	// MARK: length 0
	boolean length_0(CommandSender sender)
	{
		if(sender instanceof Player)
		{
			DB_Token.print_token_to_player((Player)sender);
		}
		
		return true;
	}
	
	// MARK: length 1
	boolean length_1(CommandSender sender, String[] args)
	{
		String player_name = args[0];
		
		// MARK: check op
		if( ! sender.isOp())
		{
			send_MSG(sender, "Only OP");
			return true;
		}
		
		if(player_name.equals("server"))
		{
			send_MSG(sender, "server token: " + DB_Token.get_server_token());
		}
		else // player name
		{
			// MARK: print player token amount
			Player player = Bukkit.getPlayer(player_name);
			
			if(player == null)
			{
				send_MSG(sender, "not exist player");
				return true;
			}
			
			int token = DB_Token.get_player_token(player);
			send_MSG(sender, player_name + " token: " + token);
		}
		
		return true;
	}

	// MARK: length 3
	boolean length_3(CommandSender sender, String[] args)
	{
		// MARK: check op
		if( ! sender.isOp())
		{
			send_MSG(sender, "Only OP");
			return true;
		}
		
		String cmd = args[0];
		String player_name = args[1];
		int amount = Integer.parseInt(args[2]);
		
		// MARK: execute cmd
		Player player = Bukkit.getPlayer(player_name);
		
		if(player == null)
		{
			send_MSG(sender, "not exist player");
			return true;
		}
		
		switch(cmd)
		{
			case "add":
				DB_Token.add_token(player, amount);
				break;
			case "remove":
				DB_Token.remove_token(player, amount);
				break;
		}
		
		return true;
	}
	
	void send_MSG(CommandSender sender, String msg)
	{
		String text = "[DB_Token] ";
		text += msg;
		
		sender.sendMessage(text);
	}
}










































