package db;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import plugin.main.Main;

public class DB_Kill_Death {
	private static Plugin plugin = Main.get_instance();
	private static FileConfiguration config = plugin.getConfig();
	
	public static void register_all_keys(Player p)
	{
		String[] keys = {"kill", "death"};
		for(String key : keys)
		{
			String key_str = p.getName() + "." + key;
			config.set(key_str, 0);
		}
		
		save();
	}
	
	public static Set<String> get_all_keys()
	{
		return (config.getKeys(false));
	}
	
	public static boolean is_key_exist(Player p)
	{
		return (config.contains(p.getName()));
	}
	
	public static void add_kill_point(Player p)
	{
		String path = p.getName() + ".kill";
		int kill_point = config.getInt(path);
		kill_point += 1;
		
		config.set(path, kill_point);
		
		DB_Rank.update_sign("kill");
		
		save();
	}
	
	
	public static int get_kill_point(Player p)
	{
		String path = p.getName() + ".kill";
		int kill_point = config.getInt(path);
		
		return kill_point;
	}
	
	public static int get_kill_point(String p)
	{
		String path = p + ".kill";
		int kill_point = config.getInt(path);
		
		return kill_point;
	}
	
	public static void add_death_point(Player p)
	{
		String path = p.getName() + ".death";
		int death_point = config.getInt(path);
		death_point += 1;
		
		config.set(path, death_point);
		
		DB_Rank.update_sign("death");
		
		save();
	}
	
	public static int get_death_point(Player p)
	{
		String path = p.getName() + ".death";
		int death_point = config.getInt(path);
		
		return death_point;
	}
	
	public static int get_death_point(String p)
	{
		String path = p + ".death";
		int death_point = config.getInt(path);
		
		return death_point;
	}
	
	public static void save()
	{
		plugin.saveConfig();
	}
}
