package db;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import plugin.main.Main;
import setting.Setting;

public class PlayerData {
	
	static int server_token = 100;
	
	static int RED_TEAM_count = 0;
	static int BLUE_TEAM_count = 0;
	
	// MARK: fields
	Player p;
	int token;
	int team;
	boolean spawn_protecting;
	boolean chat;
	int shop_used;
	
	// MARK: constructor
	public PlayerData(Player p)
	{
		this(p, 0, Setting.OFFLINE_TEAM, false, false, 0);
	}
	
	public PlayerData(Player p, int token, int team, boolean spawn_protecting, boolean chat, int shop_left)
	{
		this.p = p;
		this.token = token;
		this.team = team;
		this.spawn_protecting = spawn_protecting;
		this.chat = chat;
		this.shop_used = shop_left;
	}
	
	
	// MARK: methods
	
	public Player get_player()
	{
		return p;
	}
	
	public String get_name()
	{
		return p.getName();
	}
	
	// ============================== token ==============================
	
	public static int get_server_token()
	{
		return server_token;
	}
	
	public void plus_server_token(int amount)
	{
		server_token += amount;
	}
	
	public boolean minus_server_token(int amount)
	{
		int sum = server_token - amount;
		
		if(sum >= 0)
		{
			server_token = sum;
			return true;
		}
		
		return false;
	}
	
	
	
	public int get_token()
	{
		return token;
	}
	
	public boolean add_token(int amount)
	{
		if(minus_server_token(amount))
		{
			token += amount;
			
			print_token_to_player();	
			
			set_player_health_scale_with_token();
			
			DB_Rank.update_sign("token");

			return true;
		}
		
		return false;
	}
	
	public boolean remove_token(int amount)
	{
		int sum = token - amount;
		if(sum >= 0)
		{
			token -= amount;
			
			plus_server_token(amount);
			
			set_player_health_scale_with_token();
			
			DB_Rank.update_sign("token");

			return true;
		}
		
		return false;
	}
	
	void set_player_health_scale_with_token()
	{
		int health_plus = token / Setting.TOKEN_SIZE_PER_HALF_HEART;
		
		if(Setting.DEBUG)
			p.sendMessage("health plus: " + health_plus);
		
		int health = 20 + health_plus;
		
		p.setHealthScale(health);
		
		if(Setting.DEBUG)
			p.sendMessage("health set to " + health);
	}
	
	public void print_token_to_player()
	{
		p.sendMessage("token: " + token);
	}
	// ============================== team ==============================
	public void register_team(int team)
	{
		this.team = team;
		plus_team_count(team);
	}
	
	public void unregister_team()
	{
		minus_team_count(team);
	}
	
	public int get_team()
	{
		return team;
	}
	
	public void plus_team_count(int team)
	{
		if(team == Setting.RED_TEAM)
		{
			RED_TEAM_count++;
		}
		else if(team == Setting.BLUE_TEAM)
		{
			BLUE_TEAM_count++;
		}
	}
	
	public void minus_team_count(int team)
	{
		if(team == Setting.RED_TEAM)
		{
			RED_TEAM_count--;
		}
		else if(team == Setting.BLUE_TEAM)
		{
			BLUE_TEAM_count--;
		}
	}
	
	public String get_player_team_string()
	{
		switch(team)
		{
			case Setting.RED_TEAM:
				return "RED";
			case Setting.BLUE_TEAM:
				return "BLUE";
			case Setting.BLACK_TEAM:
				return "BLACK";
			default: // OFFLINE:
				return "OFFLINE";
		}
	}
	
	public static int get_RED_count()
	{
		return RED_TEAM_count;
	}
	
	public static int get_BLUE_count()
	{
		return BLUE_TEAM_count;
	}
	
	public ChatColor get_team_color()
	{
		switch(team)
		{
			case Setting.RED_TEAM:
				return ChatColor.RED;
			case Setting.BLUE_TEAM:
				return ChatColor.BLUE;
			case Setting.BLACK_TEAM:
				return ChatColor.BLACK;
			default: // OFFLINE:
				return ChatColor.GRAY;
		}
	}
	
	public String get_string_team_colored(ChatColor next_color)
	{
		ChatColor team_color = get_team_color();
		
		String team_str = get_player_team_string();
		
		String str = team_color + team_str + next_color;
		
		return str;
	}
	
	public String get_string_name_colored(ChatColor next_color)
	{
		ChatColor team_color = get_team_color();
		
		String str = team_color + p.getName() + next_color;
		return str;
	}
	
	public void change_team(int new_team)
	{
		if(new_team == team)
			return;

		team = new_team;
		
		// print each team count
		String msg = 
				String.format("RED: %d | BLUE: %d", 
						RED_TEAM_count, BLUE_TEAM_count);
		
		Bukkit.getServer().broadcastMessage(msg);
	}
	// ============================== shop_left ==============================
	public void reset_shop_used_count()
	{
		shop_used = 0;
	}
	
	public void add_shop_used_count()
	{
		shop_used++;
	}
	
	public int get_shop_used_count()
	{
		return shop_used;
	}
	
	public boolean can_purchase()
	{
		if(shop_used < Setting.SHOP_LIMIT_COUNT)
		{
			return true;
		}
		
		return false;
	}
	
	
	// ============================== spawn protecting ==============================
	
	public boolean is_spawn_protecting()
	{
		return spawn_protecting;
	}
	
	public void add_spawn_protecting()
	{
		spawn_protecting = true;
		
		if(Setting.DEBUG)
			p.sendMessage("start protect");
		
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {

			@Override
			public void run() {
				if(Setting.DEBUG)
					p.sendMessage("end protect");
				
				spawn_protecting = false;
			}
			
		}, Setting.RESPAWN_PROTECT_DURATION_TIME * 20);
	}
	
	
	
	
	// ============================== can_chat ==============================
	public void set_on_chat()
	{
		chat = true;
	}
	
	public void set_off_chat()
	{
		chat = false;
	}
	
	public boolean get_state_of_chat()
	{
		return chat;
	}
	
	
	
	// ============================== kill_death ==============================
	/*
	 * register_kill_death
	 * get_all_keys
	 * get_kill_point
	 * get_death_point
	 * 
	 */
	
	public void register_kill_death_keys()
	{
		DB_Kill_Death.register_all_keys(p); // TODO: register_keys 로 바꾸기
	}
	
	public Set<String> get_kill_death_all_keys()
	{
		return DB_Kill_Death.get_all_keys();
	}
	
	public int get_kill_point()
	{
		return DB_Kill_Death.get_kill_point(p);
	}
	
	public int get_death_point()
	{
		return DB_Kill_Death.get_death_point(p);
	}
	
	public void add_kill_point()
	{
		DB_Kill_Death.add_kill_point(p);
	}
	
	public void add_death_point()
	{
		DB_Kill_Death.add_death_point(p);
	}
}

































