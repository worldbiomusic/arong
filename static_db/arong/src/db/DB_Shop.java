package db;

import java.util.HashMap;

import org.bukkit.entity.Player;

import setting.Setting;

public class DB_Shop {
	static HashMap<Player, Integer> data = new HashMap<Player, Integer>();
	
	public static void reset_count(Player p)
	{
		data.put(p, 0);
	}
	
	public static void add_count(Player p)
	{
		int count = get_player_count(p);
		
		data.put(p, count + 1);
	}
	
	public static int get_player_count(Player p)
	{
		return data.get(p);
	}
	
	public static boolean is_exist(Player p)
	{
		return data.containsKey(p);
	}
	
	public static boolean can_purchase(Player p)
	{
		int count = get_player_count(p);
		
		if(count < Setting.FEATHER_LIMIT_COUNT)
		{
			return true;
		}
		
		return false;
	}
}
