//package db;
//
//import org.bukkit.Bukkit;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.Plugin;
//
//public class DB_Token {
//	
//	public static FileConfiguration config;
//	
//	public static Plugin plugin;
//	
//	private static int server_token;
//
//	public static void init(Plugin p)
//	{
//		config = p.getConfig();
//		plugin = p;
//		server_token = 100;
//	}
//	
//	// MARK: plus server token
//	public static void plus(int amount)
//	{
//		server_token += amount;
//	}
//	
//	// MARK: minus server token
//	public static void minus(int amount)
//	{
//		if(server_token < amount)
//		{
//			Bukkit.getServer().getConsoleSender().sendMessage("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\nserver_token is not fit to 100\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//			Bukkit.getServer().getConsoleSender().sendMessage("server_token: " + server_token);
//			Bukkit.getServer().getConsoleSender().sendMessage("amount: " + amount);
//			return;
//		}
//		server_token -= amount;
//	}
//	
//	public static int get_server_token()
//	{
//		return server_token;
//	}
//	
//	public static void register_player(Player p)
//	{
//		config.set(p.getName(), 0);
//	}
//	
//	public static void add_token(Player p, int amount)
//	{
//		if(!is_player_exist(p))
//		{
//			register_player(p);
//		}
//		
//		int token = get_player_token(p);
//		config.set(name(p), token + amount);
//		
//		// MARK: server token
//		minus(amount);
//		
//		save();
//		
//		print_token(p);
//	}
//	
//	public static void remove_token(Player p, int amount)
//	{
//		if(is_player_exist(p))
//		{
//			int token = get_player_token(p);
//			
//			if(token - amount < 0)
//			{
//				p.sendMessage("tokens are not enough to remove" + amount + " tokens");
//				print_token(p);
//			}
//			else
//			{
//				config.set(name(p), token - amount);
//			}
//			
//			// MARK: server token
//			plus(amount);
//			
//			save();
//		}
//		
//		print_token(p);
//	}
//	
//	public static int get_player_token(Player p)
//	{
//		if(! is_player_exist(p))
//		{
//			return -1;
//		}
//		else
//		{
//			return config.getInt(name(p));
//		}
//	}
//	
//	public static boolean is_player_exist(Player p)
//	{
//		return config.contains(p.getName());
//	}
//	
//	public static boolean is_token_zero(Player p)
//	{
//		int token = get_player_token(p);
//		return (token == 0);
//	}
//	
//	public static void save()
//	{
//		plugin.saveConfig();
//	}
//	
//	public static void print_token(Player p)
//	{
//		p.sendMessage("you have " + get_player_token(p) + " now");
//	}
//	
//	public static String name(Player p)
//	{
//		return p.getName();
//	}
//}






































/*
 * [RULE]
 * 1.server token must manage all token
 * 2.token must go through the server when exchanging token with players
 */

package db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;

import setting.Setting;

public class DB_Token {
	
	private static HashMap<Player, Integer> data = new HashMap<Player, Integer>();
	
	private static int server_token = 100;

	// MARK: plus server token
	static void plus_server_token(int amount)
	{
		server_token += amount;
	}
	
	// MARK: minus server token
	static boolean minus_server_token(int amount)
	{
		if(server_token >= amount)
		{
			server_token -= amount;
			return true;
		}
		return false;
	}
	
	public synchronized static int get_server_token()
	{
		return server_token;
	}
	
	public static void register_player(Player p)
	{
		data.put(p, 0);
	}
	
	public static void unregister_player(Player p)
	{
		data.remove(p);
	}
	
	// MARK: return true or false
	// true = server token is enough
	// false = (server token is lack) or player is not exist 
	public static synchronized boolean add_token(Player p, int amount)
	{
		if(is_player_exist(p))
		{
			int token = get_player_token(p);
			data.put(p, token + amount);
		
			// MARK: server token minus
			if(minus_server_token(amount))
			{
				print_token_to_player(p);	
				
				set_player_health_scale_with_token(p);
				
				DB_Rank.update_sign("token");

				return true;
			}
		}
		
		return false;
	}
	
	public static synchronized boolean remove_token(Player p, int amount)
	{
		if(is_player_exist(p))
		{
			int token = get_player_token(p);
			
			if(token - amount < 0)
			{
				p.sendMessage("need more token");
				print_token_to_player(p);
				return false;
			}
			
			data.put(p, token - amount);
			
			// MARK: server token plus
			plus_server_token(amount);
			
			set_player_health_scale_with_token(p);
			
			DB_Rank.update_sign("token");
			return true;
		}
		
		return false;
	}
	
	private static void set_player_health_scale_with_token(Player p)
	{
		int token = get_player_token(p);
		
		int health_plus = token / Setting.TOKEN_SIZE_PER_HALF_HEART;
		
		if(Setting.DEBUG)
			p.sendMessage("health plus: " + health_plus);
		
		int health = 20 + health_plus;
		
		p.setHealthScale(health);
		
		if(Setting.DEBUG)
			p.sendMessage("health set to " + health);
	}
	
	public static synchronized int get_player_token(Player p)
	{
		if(! is_player_exist(p))
		{
			return 0;
		}
		else
		{
			return data.get(p);
		}
	}
	
	public static boolean is_player_exist(Player p)
	{
		return data.containsKey(p);
	}
	
	public static synchronized boolean is_token_zero(Player p)
	{
		int token = get_player_token(p);
		return (token == 0);
	}
	
	public static Player[] get_all_players()
	{
		Set<Player> set = data.keySet();
		Iterator<Player> iterator = set.iterator();
		
		Player[] all = new Player[set.size()];
		
		for (int i = 0; iterator.hasNext(); i++) {
			Player p = iterator.next();
			all[i] = p;
		}
		
		return all;
	}
	
	public static synchronized void print_token_to_player(Player p)
	{
		p.sendMessage("token: " + get_player_token(p));
	}
	
}
