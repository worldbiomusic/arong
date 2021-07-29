package db;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import plugin.main.Main;
import setting.Setting;

public class DB_Spawn_Protect {
	private static HashMap<Player, Boolean> players = new HashMap<Player, Boolean>();
	
	public static void add_player_for_time(Player p, int time)
	{
		players.put(p, true);
		if(Setting.DEBUG)
			p.sendMessage("start protect");
		
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {

			@Override
			public void run() {
				if(Setting.DEBUG)
					p.sendMessage("end protect");
				remove_player(p);
			}
			
		}, Setting.SPAWN_PROTECT_TIME * 20);
		
	}
	
	public static void remove_player(Player p)
	{
		players.remove(p);
	}
	
	public static boolean is_player_exist(Player p)
	{
		return players.containsKey(p);
	}
}
