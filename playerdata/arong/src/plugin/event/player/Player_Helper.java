package plugin.event.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import db.DB;
import db.DB_CoolDown;
import db.DB_Rank;
import db.PlayerData;
import setting.Setting;

@SuppressWarnings("deprecation")
public class Player_Helper {
	
	DB_CoolDown chat_cooldown;
	HashMap<UUID, PlayerData> db;
	
	// MARK: constructor
	public Player_Helper(HashMap<UUID, PlayerData> db)
	{
		chat_cooldown = new DB_CoolDown();
		this.db = db;
	}

	// ========================== onPlayerJoin ==========================
	
	// MARK: player register to small team 
	void register_team(Player p, PlayerData p_data)
	{
		// MARK: count red, blue players
		int red = PlayerData.get_RED_count(), blue = PlayerData.get_BLUE_count();
		
		// MARK: add player to less team
		int player_team = get_small_team(red, blue);
	
		// MARK: add player
		p_data.register_team(player_team);
		
		// MARK: notify count of RED & BLUE to server
		notify_RED_BLUE_count_to_server(p_data);
	}
	
	// MARK: get samll team
	int get_small_team(int red, int blue)
	{
		if(red < blue)
		{
			return Setting.RED_TEAM;
		}
		
		else if(red > blue)
		{
			return Setting.BLUE_TEAM;			
		}
		
		else // (red == blue)
		{
			int r = (int)(Math.random() * 2);
			if(r == 0)
				return Setting.RED_TEAM;
			else
				return Setting.BLUE_TEAM;
		}
	}
	
	// MARK: print RED, BLUE player count to console
	void notify_RED_BLUE_count_to_server(PlayerData p_data)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(
				String.format("RED: %d | BLUE: %d", 
						PlayerData.get_RED_count(), PlayerData.get_BLUE_count()));
	}
	
	// MARK: deposit token when join server
	void manage_token_on_join(Player p, PlayerData p_data)
	{
		{
			if( ! p_data.add_token(Setting.SERVER_JOIN_TOKEN))
			{
				@SuppressWarnings("static-access")
				int remain_server_token = p_data.get_server_token();
				p_data.add_token(remain_server_token);
			}
			
			// MARK: notify token to server
			notify_player_and_server_token_amount(p, p_data);
		}
	}
	
	// MARK: print server_token & quiting_player_token to console
	void notify_player_and_server_token_amount(Player p, PlayerData p_data)
	{
		{
			@SuppressWarnings("static-access")
			int server_token = p_data.get_server_token();
			int player_token = p_data.get_token();
			String player_name = p.getName();
			
			Bukkit.getServer().getConsoleSender().sendMessage(
					String.format("server: %d | %s: %d",
							server_token, player_name, player_token));
		}
	}

	// MARK: make key(kill, death) in config file
	void make_DB_Kill_Death_keys_on_first_join(Player p)
	{
		if( ! p.hasPlayedBefore())
		{
			PlayerData p_data = db.get(p.getUniqueId());
			p_data.register_kill_death_keys();
		}
	}

	// MARK: clear player inventory
	void clear_inventory(Player p)
	{
		p.getInventory().clear();
	}

	// MARK: give basic weapon to player
	void give_tools(Player p, PlayerData p_data)
	{
		PlayerInventory inv = p.getInventory();
		
		// MARK: add 
		ItemStack sword = new ItemStack(Material.STONE_SWORD); 
//		sword.addEnchantment(Enchantment.DURABILITY, 3);
		inv.addItem(sword);
		
		inv.addItem(new ItemStack(Material.BOW));
		
		inv.addItem(new ItemStack(Material.ARROW, 10));
		
		// MARK: dye armor
		ItemStack chest_plate = new ItemStack(Material.LEATHER_CHESTPLATE);
		
		LeatherArmorMeta meta = (LeatherArmorMeta) chest_plate.getItemMeta();
		if(p_data.get_team() == Setting.RED_TEAM)
		{
			meta.setColor(Color.RED);
		}
		else if(p_data.get_team() == Setting.BLUE_TEAM)
		{
			meta.setColor(Color.BLUE);
		}
		chest_plate.setItemMeta(meta);
		
		inv.setChestplate(chest_plate);
	}

	// MARK: player teleport to their team location
	void teleport_team_spawn_location(Player p, PlayerData p_data)
	{
		int team = p_data.get_team();
		
		Location loc = Setting.GET_SPAWN_LOCATION(team);
		
		p.teleport(loc);
	}

	// MARK: check player is in spawn protect
	void add_spawn_protect(Player p, PlayerData p_data)
	{
		p_data.add_spawn_protecting();
	}

	// MARK: get changed join message
	String get_join_message(Player p, PlayerData p_data)
	{
		String team_str = p_data.get_string_team_colored(ChatColor.WHITE);
		
		String msg = String.format("%s join to %s team", p.getName(), team_str);
		
		return msg;
	}

	// ========================== onPlayerQuit ==========================
	
	// MARK: get changed quit message
	String get_quit_message(Player p, PlayerData p_data)
	{
		String team_str = p_data.get_string_team_colored(ChatColor.WHITE);
		
		String msg = String.format("%s quit from %s team", p.getName(), team_str);
		
		return msg;
	}

	// MARK: player unregister team
	void unregister_team(Player p, PlayerData p_data)
	{
		p_data.unregister_team();
		
		// MARK: notify count of RED & BLUE to server
		notify_RED_BLUE_count_to_server(p_data);
	}
	
	// MARK: withdraw token when quit server
	void manage_token_on_quit(Player p, PlayerData p_data)
	{
		{
			// MARK: notify token to server
			notify_player_and_server_token_amount(p, p_data);
			
			// MARK: unregister player
			int player_token = p_data.get_token();
			p_data.remove_token(player_token);
			db.remove(p.getUniqueId());
			
			// MARK: half of token
			int token_to_players = player_token / 2;
			
			// MARK: give half to other players
			PlayerData[] other_players = DB.get_instance().get_all_players();
			int other_players_size = other_players.length; 
			
			if(other_players_size != 0)
			{
				for(int i = 0; i < token_to_players; i++)
				{
					// MARK: choose random user
					int index = (int) (Math.random() * other_players_size);
					PlayerData r_p_data = other_players[index];
					
					r_p_data.add_token(1);
				}
			}
			
			// MARK: notify token to server
			
		}
		notify_player_and_server_token_amount(p, p_data);
	}

	// ========================== onPlayerDamaged ==========================
	
	// MARK: check same team
	boolean is_same_team(Player victim, Player attacker)
	{
		PlayerData victim_data = db.get(victim.getUniqueId());
		PlayerData attacker_data = db.get(attacker.getUniqueId());
		
		
		int attacker_team = victim_data.get_team();
		int victim_team = attacker_data.get_team();
		
		// MARK: same team
		if(attacker_team == victim_team)
		{
			if(Setting.DEBUG)
			{
				attacker.sendMessage("you hit same team");
				victim.sendMessage("you hit by same team");
			}
			return true;
		}
		// MARK: other team
		else // if(attacker_team != victim_team)
		{
			if(Setting.DEBUG)
			{
				attacker.sendMessage("you hit other team");
				victim.sendMessage("you hit by other team");
			}
			return false;
		}
		
	}


	// ========================== onPlayerDeath ==========================
	
	// MARK: manage token with victim and killer
	void manage_token_when_player_death(Player victim, Player killer)
	{
		PlayerData victim_data = db.get(victim.getUniqueId());
		PlayerData killer_data = db.get(killer.getUniqueId());
		{
			// MARK: give victim's half token to killer
			int victim_token_lost = victim_data.get_token() / 2;
			victim_data.remove_token(victim_token_lost);
			
			killer_data.add_token(victim_token_lost);
			
			// MARK: 
			// TODO: 유저 2명이 있다고 가정, feather사서 둘다 토큰이 0 개이면 신입유저가 들어올 때까지 토큰 못모으기 떄문에
			// kill 했을시 토큰 줌
			killer_data.add_token(Setting.PLAYER_KILL_TOKEN());
			
			if(Setting.DEBUG)
				killer.sendMessage("PLAYER_KILL_TOKEN: " + Setting.PLAYER_KILL_TOKEN());
		}
	}
	
	// ========================== onPlayerChat ==========================
	
	// MARK: change message on chat
	boolean change_msg(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		
		String index = e.getMessage();
		
		// MARK: change chatting simple
		String msg;
		if((msg = get_msg_at_index(index)) == null)
		{
			e.setCancelled(true);
			return true;
		}
		
		// MARK: set cooldown system
		if(chat_cooldown.check_remain_and_start(p, Setting.CHAT_COOLDOWN_TIME))
		{
			p.sendMessage("you can chat in " + 
		chat_cooldown.get_remain_time(p) + " seocnds");
			e.setCancelled(true);
			return true;
		}
		
		e.setMessage(msg);
		
		// MARK: set colored name
		PlayerData p_data = db.get(p.getUniqueId());
		String name = p_data.get_string_name_colored(ChatColor.WHITE);
		p.setDisplayName(name);
		
		return true;
	}
	
	// MARK: get message(index) and return macro text
	String get_msg_at_index(String index)
	{
		switch(index)
		{
			case "1":
				return "Good luck!";
			case "2":
				return "FXXX XXX!";
			case "3":
				return "Oh, I'm sorry~";
			case "4":
				return "LOL";
			case "5":
				return "Come on";
			default:
				return null;
		}
	}

	// ========================== onPlayerRespawn ==========================
	
	// MARK: set player respawn location with their team
	Location teleport_team_respawn_location(Player p, PlayerData p_data)
	{
		int team = p_data.get_team();
		
		Location respawn_loc = Setting.GET_SPAWN_LOCATION(team);
		
		return respawn_loc;
	}
	

	// ========================== onPlayerChangeSign ==========================
	
	// MARK: onPlayerChangeSign ===================================================
	boolean check_block_is_sign(SignChangeEvent e)
	{
		Material type = e.getBlock().getType();
		if(type == Material.WALL_SIGN ||
				type == Material.SIGN_POST|| type == Material.SIGN)
		{
			if(e.getPlayer().isOp())
			{
				String sign_text = e.getLine(0);
				
				switch(sign_text)
				{
				case "feather_shop":
					make_feather_shop(e);
					break;
				case "token_rank":
					add_rank_sign_location(e, "token");
					break;
				case "kill_rank":
					add_rank_sign_location(e, "kill");	
					break;
				case "death_rank":
					add_rank_sign_location(e, "death");
					break;
				}
				return true;
			}
		}
		
		return false;
	}
	
	// MARK: 
	void add_rank_sign_location(SignChangeEvent e, String kind)
	{
		Location loc = e.getBlock().getLocation();
		
		DB_Rank.add_sign_location(loc, kind);
	}
	
	// MARK: set feather shop when player make sign with '[Feather]' text in line 0
	void make_feather_shop(SignChangeEvent e)
	{
		e.setLine(0, "[Feather]");
		
		int base_token = Setting.FEATHER_PRICE;
		int incremenet = Setting.FEATHER_PRICE_INCREMENT_AMOUNT;
		
		for(int i = 0; i < 3; i++)
		{
			String line = String.format
					("%d: %d token",i + 1 , base_token + (i * incremenet));
			e.setLine(i+1, line);
		}
	}
	
	
	
	// ##########onPlayerChat###########
	
	boolean is_same_team_attack(Player victim, Player damager)
	{
		boolean same_team = false;
		
		// MARK: check same team
		same_team = is_same_team(victim, damager);
			
		return same_team;
		
	}
	
	
}










































