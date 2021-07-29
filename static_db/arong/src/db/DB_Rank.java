package db;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import plugin.main.Main;

public class DB_Rank {
	
	static File file = new File(Main.get_instance().getDataFolder() 
			+ File.separator + "rank_sign_location.yml");
	
	static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	public static void add_sign_location(Location loc, String kind)
	{
		// MARK: Location 타입 그대로 추가해서 사용 가능!!!
		add_kind_loc(kind, loc);
	}
	
	public static void update_sign(String kind)
	{
		// MARK: update all signs
		iterate_all_signs(kind);
	}
	
	
	static boolean is_sign_block(Block b)
	{
		Material type = b.getType();
		
		// MARK: no block or, not sign
		if(b == null || 
				( ! (type == Material.WALL_SIGN ||
				type == Material.SIGN_POST || type == Material.SIGN)))
		{
			return true;
		}
		
		return false;
	}
	
	static Vector<Player> get_players_sorted(String kind)
	{
		Vector<Player> v = new Vector<Player>();
		
		switch(kind)
		{
			case "token":
				Player[] all = DB_Token.get_all_players();
				
				// MARK: add all player to vector
				for(Player p : all)
					v.add(p);
				
				// MARK: sort vector
				v.sort(new Token_Comparator());
				
				return v;
				
			case "kill":
				for(Player p : Bukkit.getOnlinePlayers())
					v.add(p);
				
				v.sort(new Kill_Comparator());
				
				return v;
				
			default: // "death":
				for(Player p : Bukkit.getOnlinePlayers())
					v.add(p);
				
				v.sort(new Death_Comparator());
				
				return v;
		}
	}
	
	static String get_kind_path(String kind)
	{
		return ("rank_sign_loc" + "." + kind); 
	}
	
	static void add_kind_num(String kind)
	{
		String num_path = get_kind_path(kind) + ".num";
		int count = config.getInt(num_path);
		
		config.set(num_path, count + 1);
		
		save();
	}
	
	static int get_kind_num(String kind)
	{
		String num_path = get_kind_path(kind) + ".num";
		int count = config.getInt(num_path);
		
		return count;
	}

	static void add_kind_loc(String kind, Location loc)
	{
		int kind_num = get_kind_num(kind);
		String loc_path = get_kind_path(kind) + "." + kind_num; 
		
		config.set(loc_path, loc);
		
		add_kind_num(kind);
		
		save();
	}
	
	static Location get_kind_loc(String kind, int index)
	{
		String loc_path = get_kind_path(kind) + "." + index;
		
		return (Location) config.get(loc_path);
	}
	
	static int get_print_line_count(int v_size)
	{
		int print_line = 3;
		
		if(v_size < print_line)
		{
			return v_size;
		}
		
		return print_line;
	}
	
	static void iterate_all_signs(String kind)
	{
		// MARK: get signs count
		int num = get_kind_num(kind);
		
		for(int i = 0; i < num; i++)
		{
			// MARK: 타입 그대로 추출해서 사용 가능!!!
			Location loc = get_kind_loc(kind, i);
			
			Block b = loc.getBlock();
			if(is_sign_block(b))
			{
				continue;
			}
			
			Sign sign = (Sign) (b.getState());
			
			// MARK: write top 3 players to sign
			write_lines(kind, sign);
			
			// MARK: update sign
			sign.update();
		}
	}
	
	static void write_lines(String kind, Sign sign)
	{
		// MARK: set sign title
		write_line_0(kind, sign);
		
		// TODO: 이름에 팀 색깔 추가하기
		// MARK: write 1 ~ 3 rank
		write_line_1_2_3(kind, sign);
	}
	
	static void write_line_0(String kind, Sign sign)
	{
		String title = "[" + kind.toUpperCase() + "]";
		sign.setLine(0, title);
		
	}
	
	static void write_line_1_2_3(String kind,Sign sign)
	{
		// MARK: get player sorted
		Vector<Player> players = get_players_sorted(kind);
		
		// MARK: get print line count
		int print_line = get_print_line_count(players.size());
		
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		
		for (int x = 0; x < print_line; x++) {
			
			Player p = players.get(x);
			
			int count;
			switch(kind)
			{
				case "token":
					count = DB_Token.get_player_token(p);

					break;
				case "kill":
					count = DB_Kill_Death.get_kill_point(p);
					break;
				default: // "death":
					count = DB_Kill_Death.get_death_point(p);
					break;
			}
			
			String player_name = DB_Team.get_string_player_name_colored(p, ChatColor.BLACK);
			
			String text = String.format("%d.%.10s(%d)", 
					x + 1, player_name, count);
			
			sign.setLine(x + 1, text);
		}
	}
	
	
	
	
	public static void setup()
	{
		String[] kinds = {
				"token",
				"kill",
				"death"
		};
		
		for(String kind : kinds)
		{
			String token_key = get_kind_path(kind) + ".num";
			if( ! config.contains(token_key))
			{
				config.set(token_key, 0);
			}
		}
		
		save();
	}
	
	public static void save()
	{
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}

class Token_Comparator implements Comparator<Player> 
{

	@Override
	public int compare(Player p1, Player p2) {
		
		int p1_token =DB_Token.get_player_token(p1);
		int p2_token =DB_Token.get_player_token(p2);
		
		if(p1_token < p2_token)
		{
			return 1;
		}
		else if(p1_token > p2_token)
		{
			return -1;
		}
		return 0;
	}
	
}

class Kill_Comparator implements Comparator<Player> 
{

	@Override
	public int compare(Player p1, Player p2) {
		
		int p1_kill = DB_Kill_Death.get_kill_point(p1);
		int p2_kill = DB_Kill_Death.get_kill_point(p2);
		
		if(p1_kill < p2_kill)
		{
			return 1;
		}
		else if(p1_kill > p2_kill)
		{
			return -1;
		}
		return 0;
	}
	
}


class Death_Comparator implements Comparator<Player> 
{

	@Override
	public int compare(Player p1, Player p2) {
		
		int p1_death = DB_Kill_Death.get_death_point(p1);
		int p2_death = DB_Kill_Death.get_death_point(p2);
		
		if(p1_death < p2_death)
		{
			return 1;
		}
		else if(p1_death > p2_death)
		{
			return -1;
		}
		return 0;
	}
	
}







































