package db;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DB_Team {
	
	public static final int OFFLINE = -1;
	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int BLACK = 2; // manager
	
	static int RED_count = 0;
	static int BLUE_count = 0;
	
	public static HashMap<Player, Integer> team = new HashMap<Player, Integer>();
	
	public static void add_player(Player p, int team)
	{
		DB_Team.team.put(p, team);
		
		plus_team_count(team);
	}
	
	public static void remove_player(Player p)
	{
		if(team.containsKey(p))
		{
			int player_team = get_player_team(p);
			team.remove(p);
			
			minus_team_count(player_team);
		}
	}
	
	public static void change_team(Player p, int new_team)
	{
		int player_team = get_player_team(p);
		if(new_team == player_team)
			return;
		
		remove_player(p);
		add_player(p, new_team);
		
		// print each team count
		String msg = 
				String.format("RED: %d | BLUE: %d", 
						DB_Team.get_RED_count(), DB_Team.get_BLUE_count());
		
		Bukkit.getServer().broadcastMessage(msg);
	}
	
	static void plus_team_count(int team)
	{
		if(team == RED)
		{
			RED_count += 1;
		}
		else if(team == BLUE)
		{
			BLUE_count += 1;
		}
	}
	
	static void minus_team_count(int team)
	{
		if(team == RED)
		{
			RED_count -= 1;
		}
		else if(team == BLUE)
		{
			BLUE_count -= 1;
		}
	}
	
	public static int get_player_team(Player p)
	{
		if(team.containsKey(p))
		{
			return team.get(p);
		}
		else
		{
			return OFFLINE;
		}
	}
	
	public static String get_player_team_string(Player p)
	{
		int team = get_player_team(p);
		
		switch(team)
		{
		case RED:
			return "RED";
		case BLUE:
			return "BLUE";
		case BLACK:
			return "BLACK";
		default: // OFFLINE:
			return "OFFLINE";
		}
	}
	
	public static int get_RED_count()
	{
		return RED_count;
	}
	
	public static int get_BLUE_count()
	{
		return BLUE_count;
	}
	
	public static ChatColor get_team_color(int team)
	{
		switch(team)
		{
			case RED:
				return ChatColor.RED;
			case BLUE:
				return ChatColor.BLUE;
			case BLACK:
				return ChatColor.BLACK;
			default: // OFFLINE:
				return ChatColor.GRAY;
		}
	}
	
	public static String get_string_player_team_colored(Player p, ChatColor next_color)
	{
		int team = get_player_team(p);
		ChatColor team_color = get_team_color(team);
		
		String team_str = get_player_team_string(p);
		
		String str = team_color + team_str + next_color;
		return str;
	}
	
	public static String get_string_player_name_colored(Player p, ChatColor next_color)
	{
		int team = get_player_team(p);
		ChatColor team_color = get_team_color(team);
		
		String str = team_color + p.getName() + next_color;
		return str;
	}
}






































