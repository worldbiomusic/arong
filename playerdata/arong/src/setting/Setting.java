package setting;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import db.PlayerData;

public class Setting {
	
	
	
	
	// MARK: debug mode
	public static final boolean DEBUG = false; 
	
	
	
	
	
	// MARK: server
	public static final int RESPAWN_PROTECT_DURATION_TIME = 15;
	public static final int RESPAWN_POTION_DURATION_TIME = 15;
	
	// MARK: token
	public static final int SERVER_JOIN_TOKEN = 5;
	public static final int TOKEN_SIZE_PER_HALF_HEART = 5;
	private static final int PLAYER_KILL_TOKEN_STANDARD = 10;
	
	public static int PLAYER_KILL_TOKEN()
	{
		// MARK: 사람이 적을 땐 kill이 적게 발생하므로 보상을 높이고
		// 사람이 많을 땐 kill이 많이 발생하므로 보상을 줄임
		int count = PlayerData.get_server_token() / PLAYER_KILL_TOKEN_STANDARD;
		return count;
	}
	
	// MARK: cooldown time
	public static final int CHAT_COOLDOWN_TIME = 5;
	public static final int POTION_DURATION_TIME = 10;
	public static final int POTION_FEATHER_COOLDOWN_TIME = 20;
	
	
	// MARK: feather shop
	public static final int FEATHER_PRICE = 10;
	public static final int SHOP_LIMIT_COUNT = 3;
	public static final int FEATHER_PRICE_INCREMENT_AMOUNT = 5;
	

	// MARK: skill cooldown time
	public static final int SKILL_HIDE_COOLDOWN_TIME = 20;
	public static final int SKILL_HEAL_COOLDOWN_TIME = 30;
	public static final int SKILL_CHAT_COOLDOWN_TIME = 20;
	public static final int SKILL_SWAP_TOKEN_COOLDOWN_TIME = 40;
	public static final int SKILL_KILLER_TP_COOLDOWN_TIME = 20;
	public static final int SKILL_FISHING_TP_COOLDOWN_TIME = 20;
	public static final int SKILL_THANOS_COOLDOWN_TIME = 60;
	public static final int SKILL_SUPER_STAR_COOLDOWN_TIME = 20;
	
	// MARK: skill duration time
	public static final int SKILL_HIDE_DURATION_TIME = 10;
	public static final int SKILL_CHAT_DURATION_TIME = 5;
	
	
	
	// MARK: team
	public static final int OFFLINE_TEAM = -1;
	public static final int RED_TEAM = 0;
	public static final int BLUE_TEAM = 1;
	public static final int BLACK_TEAM = 2;
	
	
	
	
	// MARK: map
	/*
	 * 시멘트
	 * red: 7, 3, -344
	 * blue: 45, 3, -344
	 * 
	 * 초원
	 * red: 7, 3, -396
	 * blue: 49, 3, -396
	 * 
	 * 
	 */
	
	static final int MAP_AMOUNT = 3;
	
	static final int concrete = 0;
	static final int grass = 1;
	static final int deserted_island = 2;
	
	static final World world = Bukkit.getWorld("world");
	
	static final Location NEED_REJOIN_LOCATION = new Location(world, -15, 13, -314);
	
	static final Location[] RED_SPAWN_LOCATIONS = {
			new Location(world, 8, 3, -354),
			new Location(world, 32.5, 16, -406.5),
			new Location(world, 13.5, 5, -470.5),
	};
	
	static final Location[] BLUE_SPAWN_LOCATIONS = {
			new Location(world, 45, 3, -355),
			new Location(world, 32.5, 16, -410.5),
			new Location(world, 50.5, 5, -471.5),
	};
	
	static int GET_TODAY_MAP()
	{
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		int map_index = day % MAP_AMOUNT;
		return map_index;
	}
	
	public static Location GET_SPAWN_LOCATION(int team)
	{
		int map_index = GET_TODAY_MAP();
//		int map_index = deserted_island;
		
		Location loc;
		
		if(team == RED_TEAM)
		{
			loc = RED_SPAWN_LOCATIONS[map_index];
		}
		else if(team == BLUE_TEAM)
		{
			loc = BLUE_SPAWN_LOCATIONS[map_index];
		}
		else
		{
			loc = NEED_REJOIN_LOCATION;
		}
		
		return loc;
	}
}







































