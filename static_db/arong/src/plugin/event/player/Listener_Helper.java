package plugin.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import db.DB_Chat;
import db.DB_CoolDown;
import db.DB_Kill_Death;
import db.DB_Spawn_Protect;
import db.DB_Rank;
import db.DB_Team;
import db.DB_Token;
import setting.Setting;

@SuppressWarnings("deprecation")
public class Listener_Helper {
	
	DB_CoolDown chat_cooldown;
	
	public Listener_Helper()
	{
		chat_cooldown = new DB_CoolDown();
	}
	
	void clear_inventory(Player p)
	{
		p.getInventory().clear();
	}
	
	void give_tools(Player p)
	{
		PlayerInventory inv = p.getInventory();
		
		// MARK: add 
		ItemStack sword = new ItemStack(Material.STONE_SWORD); 
		sword.addEnchantment(Enchantment.DURABILITY, 3);
		inv.addItem(sword);
		
		inv.addItem(new ItemStack(Material.BOW));
		
		
		inv.addItem(new ItemStack(Material.ARROW, 10));
		
		// dye armor
		ItemStack chest_plate = new ItemStack(Material.LEATHER_CHESTPLATE);
		
		LeatherArmorMeta meta = (LeatherArmorMeta) chest_plate.getItemMeta();
		if(DB_Team.get_player_team(p) == DB_Team.RED)
		{
			meta.setColor(Color.RED);
		}
		else if(DB_Team.get_player_team(p) == DB_Team.BLUE)
		{
			meta.setColor(Color.BLUE);
		}
		chest_plate.setItemMeta(meta);
		
		inv.setChestplate(chest_plate);
	}
	
	void register_team(Player p)
	{
		// MARK: count red, blue players
		int red = DB_Team.get_RED_count(), blue = DB_Team.get_BLUE_count();
		
		// MARK: add player to less team
		int player_team = get_small_team(red, blue);
	
		// MARK: add player
		DB_Team.add_player(p, player_team);
		
		// MARK: notify count of RED & BLUE to server
		notify_RED_BLUE_count_to_server();
	}
	
	void unregister_team(Player p)
	{
		DB_Team.remove_player(p);
		
		// MARK: notify count of RED & BLUE to server
		notify_RED_BLUE_count_to_server();
	}
	
	void notify_RED_BLUE_count_to_server()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(
				String.format("RED: %d | BLUE: %d", 
						DB_Team.get_RED_count(), DB_Team.get_BLUE_count()));
	}
	
	private int get_small_team(int red, int blue)
	{
		int small_team;
		
		if(red < blue)
		{
			small_team = DB_Team.RED;
		}
		
		else if(red > blue)
		{
			small_team = DB_Team.BLUE;			
		}
		
		else // (red == blue)
		{
			int r = (int)(Math.random() * 2);
			if(r == 0)
				small_team = DB_Team.RED;
			else
				small_team = DB_Team.BLUE;
		}
		
		return small_team;
	}
	
	// MARK: when join server
	void teleport_team_spawn_location(Player p)
	{
		int team = DB_Team.get_player_team(p);
		
		if(team == DB_Team.RED)
		{
			Location loc = new Location(p.getWorld(), 7.5, 3, -344.5);
			p.teleport(loc);
		}
		else if(team == DB_Team.BLUE)
		{
			Location loc = new Location(p.getWorld(), 45.5, 3, -344.5);
			p.teleport(loc);
		}
	}
	
	// MARK: when respawn
	Location teleport_team_respawn_location(Player p)
	{
		int team = DB_Team.get_player_team(p);
		
		Location respawn_loc;
		if(team == DB_Team.RED)
		{
			respawn_loc = new Location(p.getWorld(), 7.5, 3, -344.5);
		}
		else if(team == DB_Team.BLUE)
		{
			respawn_loc = new Location(p.getWorld(), 45.5, 3, -344.5);
		}
		else // team == DB_Team.BLACK
		{
			respawn_loc = new Location(p.getWorld(), -16 ,13 ,-312);
		}
		
		return respawn_loc;
	}
	
	void manage_token_when_player_death(Player victim, Player killer)
	{
		synchronized(DB_Token.class)
		{
			// MARK: give victim's half token to killer
			int victim_token_lost = DB_Token.get_player_token(victim) / 2;
			DB_Token.remove_token(victim, victim_token_lost);
			
			DB_Token.add_token(killer, victim_token_lost);
			
			// MARK: 
			// TODO: 유저 2명이 있다고 가정, feather사서 둘다 토큰이 0 개이면 신입유저가 들어올 때까지 토큰 못모으기 떄문에
			// kill 했을시 토큰 줌
			DB_Token.add_token(killer, Setting.PLAYER_KILL_TOKEN());
			
			if(Setting.DEBUG)
				killer.sendMessage("PLAYER_KILL_TOKEN: " + Setting.PLAYER_KILL_TOKEN());
			
			DB_Kill_Death.add_kill_point(killer);
			
		}
	}
	
	// MARK: deposit token when join server
	void manage_token_on_join(Player p)
	{
		synchronized(DB_Token.class)
		{
			DB_Token.register_player(p);
			
			if( ! (DB_Token.add_token(p, Setting.SERVER_JOIN_TOKEN)))
			{
				int server_token = DB_Token.get_server_token();
				DB_Token.add_token(p, server_token);
			}
			
			// MARK: notify token to server
			notify_player_and_server_token_amount(p);
			
		}
	}
	
	// MARK: withdraw token when quit server
	void manage_token_on_quit(Player p)
	{
		synchronized(DB_Token.class)
		{
			// MARK: notify token to server
			notify_player_and_server_token_amount(p);
			
			// MARK: unregister player
			int player_token = DB_Token.get_player_token(p);
			DB_Token.remove_token(p, player_token);
			DB_Token.unregister_player(p);
			
			// MARK: half of token
			int token_to_players = player_token / 2;
			
			// MARK: give half to other players
			Player[] other_players = DB_Token.get_all_players();
			int other_players_size = other_players.length; 
			
			if(other_players_size != 0)
			{
				for(int i = 0; i < token_to_players; i++)
				{
					// MARK: choose random user
					int index = (int) (Math.random() * other_players_size);
					DB_Token.add_token(other_players[index], 1);
				}
			}
			
			// MARK: notify token to server
			
		}
		notify_player_and_server_token_amount(p);
	}
	
	void notify_player_and_server_token_amount(Player p)
	{
		synchronized(DB_Token.class)
		{
			int server_token = DB_Token.get_server_token();
			int player_token = DB_Token.get_player_token(p);
			String player_name = p.getName();
			
			Bukkit.getServer().getConsoleSender().sendMessage(
					String.format("server: %d | %s: %d",
							server_token, player_name, player_token));
			
		}
	}
	
	boolean is_same_team(Player victim, Player attacker)
	{
		int attacker_team = DB_Team.get_player_team(attacker);
		int victim_team = DB_Team.get_player_team(victim);
		
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
	
	void not_damaged(Player p)
	{
		DB_Spawn_Protect.add_player_for_time(p, Setting.SPAWN_PROTECT_TIME);
	}
	
	String get_join_message(Player p)
	{
		String team_str = DB_Team.get_player_team_string(p);
		
		ChatColor team_color = 
				((DB_Team.get_player_team(p) == DB_Team.RED) ? ChatColor.RED : ChatColor.BLUE);
		
		team_str = team_color + team_str + ChatColor.WHITE; 
		
		String msg = String.format("%s join to %s team", p.getName(), team_str);
		
		return msg;
	}
	
	void make_keys_on_first_join(Player p)
	{
		if( ! p.hasPlayedBefore())
		{
			DB_Kill_Death.register_all_keys(p);
		}
	}
	
	String get_quit_message(Player p)
	{
		String team_str = DB_Team.get_string_player_team_colored(p, ChatColor.WHITE);
		
		String msg = String.format("%s quit from %s team", p.getName(), team_str);
		
		return msg;
	}
	
	// MARK: ##########onPlayerChat###########
	
	boolean change_msg(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		String index = e.getMessage();
		
		// MARK: change chatting simple
		String msg;
		if((msg = get_msg_at_index(index)) == null) // MARK: check is index message
		{
			e.setCancelled(true);
			return false;
		}
		
		// MARK: set cooldown system
		if(chat_cooldown.check_remain_and_start(p, Setting.CHAT_COOLDOWN_TIME))
		{
			p.sendMessage("you can chat in " + chat_cooldown.get_remain_time(p) + " seocnds");
			e.setCancelled(true);
			return true;
		}
		
		e.setMessage(msg);
		
		// MARK: set colored name
		String name = DB_Team.get_string_player_name_colored(p, ChatColor.WHITE);
		p.setDisplayName(name);
		
		return true;
	}
	
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

	boolean check_DB_Chat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		
		if(DB_Chat.is_player_exist(p))
		{
			return true;
		}
		
		return false;
	}
	
	boolean is_same_team_attack(Entity victim, Entity damager)
	{
		if(victim instanceof Player && (damager instanceof Player || damager instanceof Arrow))
		{
			boolean same_team = false, protect_state;
			
			if(damager instanceof Arrow)
			{
				damager = (Player) ( ((Arrow) damager).getShooter() );
			}
			
			// MARK: check same team
			same_team = is_same_team((Player)victim, (Player)damager);
				
			// MARK: check player is state of protection
			protect_state = DB_Spawn_Protect.is_player_exist((Player)victim);
			
			return (same_team || protect_state);
		}
		
		return false;
	}
	
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
						make_token_rank(e);
						break;
					case "kill_rank":
						make_kill_rank(e);	
						break;
					case "death_rank":
						make_death_rank(e);
						break;
				}
				return true;
			}
		}
		
		return false;
	}
	
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
	
	void make_token_rank(SignChangeEvent e)
	{
		Location loc = e.getBlock().getLocation();
		
		DB_Rank.add_sign_location(loc, "token");
	}
	
	void make_kill_rank(SignChangeEvent e)
	{
		Location loc = e.getBlock().getLocation();
		
		DB_Rank.add_sign_location(loc, "kill");
	}
	
	void make_death_rank(SignChangeEvent e)
	{
		Location loc = e.getBlock().getLocation();
		
		DB_Rank.add_sign_location(loc, "death");
	}
}










































