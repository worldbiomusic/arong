package db;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import plugin.main.Main;

public class DB_Rank {
	
	static File file = new File(Main.get_instance().getDataFolder() 
			+ File.separator + "rank_sign_location.yml");
	
	static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	static DB db = DB.get_instance();
	static HashMap<UUID, PlayerData> map = db.get_data();
	
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
	
	static Vector<PlayerData> get_online_players_sorted()
	{
		Vector<PlayerData> v = new Vector<PlayerData>();
		
		PlayerData[] all = db.get_all_players();
		
		// MARK: add all player to vector
		for(PlayerData p : all)
			v.add(p);
		
		// MARK: sort vector
		v.sort(new Token_Comparator());
		
		return v;

	}
	
	static Vector<String> get_config_players_sorted(String kind)
	{
		Vector<String> v = new Vector<String>();
		
		Set<String> all_player_name = DB_Kill_Death.get_all_keys();
		Iterator<String> it = all_player_name.iterator();
		
		switch(kind)
		{		
			case "kill":
				
				while(it.hasNext())
				{
					v.add(it.next());
				}
				
				v.sort(new Kill_Comparator());
				
				return v;
				
			default: // "death":

				while(it.hasNext())
				{
					v.add(it.next());
				}
				
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
		switch(kind)
		{
		case "token":
			write_line_1_2_3_online(kind, sign);
			break;
		case "kill":
		case "death":
			write_line_1_2_3_config(kind, sign);
			break;
		}
	}
	
	static void write_line_0(String kind, Sign sign)
	{
		String title = "[" + kind.toUpperCase() + "]";
		sign.setLine(0, title);
		
	}
	
	static void write_line_1_2_3_online(String kind,Sign sign)
	{
		Vector<PlayerData> players = get_online_players_sorted();
		
		// MARK: get print line count
		int print_line = get_print_line_count(players.size());
		
		
		init_sign_lines(sign);
		
		for (int x = 0; x < print_line; x++) {
			
			// TODO:kill, death는 오프라인 플레이도 불러오기 때문에 오류남
			// 이름만 가지고 해야함 
			
			PlayerData p_data = players.get(x);
			
			String count;
			count = p_data.get_token() + "";

			
			String player_name = p_data.get_string_name_colored(ChatColor.BLACK);
			
			// MARK: 12로 해도 앞에 chatcolor가 2자리를 잡아먹기떄문에 10자리로 됨
			String text = String.format("%d.%.12s" + ChatColor.BLACK + "(%s)", 
					x + 1, player_name, count);
			
			sign.setLine(x + 1, text);
		}
	}
	
	static void write_line_1_2_3_config(String kind,Sign sign)
	{
		Vector<String> players;
		
		// MARK: get player sorted
		switch(kind)
		{
		case "kill":
			players = get_config_players_sorted(kind);
			break;
		default: // "death":
			players = get_config_players_sorted(kind);
			break;
		}
		
		
		// MARK: get print line count
		int print_line = get_print_line_count(players.size());
		
		
		init_sign_lines(sign);
		
		for (int x = 0; x < print_line; x++) {
			
			String p = players.get(x);
			
			int count;
			switch(kind)
			{
				case "kill":
					count = DB_Kill_Death.get_kill_point(p);
					break;
				default: // "death":
					count = DB_Kill_Death.get_death_point(p);
					break;
			}
			
			String player_name = p;
			
			String text = String.format("%d.%.10s(%d)", 
					x + 1, player_name, count);
			
			sign.setLine(x + 1, text);
		}
	}
	
	public static void init_sign_lines(Sign sign)
	{
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
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


class Token_Comparator implements Comparator<PlayerData> 
{
	DB db = DB.get_instance();
	HashMap<UUID, PlayerData> map = db.get_data();
	
	@Override
	public int compare(PlayerData p1_data, PlayerData p2_data) {
		
		int p1_token =p1_data.get_token();
		int p2_token =p2_data.get_token();
		
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

class Kill_Comparator implements Comparator<String> 
{

	@Override
	public int compare(String p1, String p2) {
		
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


class Death_Comparator implements Comparator<String> 
{

	@Override
	public int compare(String p1, String p2) {
		
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






































