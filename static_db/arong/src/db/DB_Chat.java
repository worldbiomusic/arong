package db;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class DB_Chat {
	static ArrayList<Player> data = new ArrayList<Player>();
	
	public static void add_player(Player p)
	{
		data.add(p);
	}
	
	public static void remove_player(Player p)
	{
		if(is_player_exist(p))
		{
			data.remove(p);
		}
	}
	
	public static boolean is_player_exist(Player p)
	{
		return data.contains(p);
	}
}
