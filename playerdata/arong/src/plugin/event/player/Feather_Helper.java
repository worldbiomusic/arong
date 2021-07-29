package plugin.event.player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import db.DB;
import db.DB_CoolDown;
import db.DB_Feather;
import db.PlayerData;
import plugin.main.Main;
import setting.Setting;

public class Feather_Helper {
	
	DB_CoolDown potion_cooldown;
	DB_CoolDown skill_cooldown;
	HashMap<UUID, PlayerData> db;
	
	public Feather_Helper(HashMap<UUID, PlayerData> db)
	{
		potion_cooldown = new DB_CoolDown();
		skill_cooldown = new DB_CoolDown();
		this.db = db;
	}
	
	
	// MARK: help when right_click feather
	public boolean use_feahter(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		Action action = e.getAction();
		ItemStack item = e.getItem();
		
		if(item != null && item.getType() != Material.AIR && 
				item.hasItemMeta() && item.getType() == Material.FEATHER)
		{
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				return check_feather_kind(item, p);
			}
		}
		return false;
	}
	
	boolean check_feather_kind(ItemStack item, Player p)
	{
		ItemMeta meta = item.getItemMeta();
		String kind = meta.getDisplayName();
		if(Setting.DEBUG)
			p.sendMessage("feather kind: " + kind);
		
		switch(kind)
		{
		case "Weapon":
			give_weapon(p);
			return true;
		case "Potion":
			List<String> lore = meta.getLore();
			String potion = lore.get(0);
			
			give_potion_effect(p, potion);
			
			return true;
		case "Skill":
			List<String> lore2 = meta.getLore();
			String skill = lore2.get(0);
			
			use_skill(p, skill);
			
			return true;
		default:
			return false;
		}
	}
	
	void give_weapon(Player p)
	{
		ItemStack weapon = DB_Feather.random_weapon();
		p.getInventory().addItem(weapon);
		
		// MARK: let player know about random weapon kind
		String kind = weapon.getType().toString();
		p.sendTitle(kind, "random weapon", 20, 40, 20);
		
		if(Setting.DEBUG)
			p.sendMessage("random weapon: " + kind);
		
		// MARK: remove weapon feather
		remove_weapon_feather(p);
	}
	
	void remove_weapon_feather(Player p)
	{
		ItemStack weapon_feahter = p.getInventory().getItemInMainHand();
		
		int feather_amount = weapon_feahter.getAmount();
		weapon_feahter.setAmount(feather_amount - 1);
	}
	
	/*
	 *  "speed",
		"increase_damage",
		"regeneration",
		"damage_resistance",
		"absorption",		
		"fire_resistance"
	 */
	
	void give_potion_effect(Player p, String potion)
	{
		String cooldown_key = p + potion;
		if(potion_cooldown.check_remain_and_start(cooldown_key, Setting.POTION_FEATHER_COOLDOWN_TIME))
		{
			p.sendMessage("you can use potion in "+ potion_cooldown.get_remain_time(cooldown_key));
			return;
		}
		
		p.sendTitle(potion, "potion effect", 20, 40, 20);
		
		if(Setting.DEBUG)
			p.sendMessage(potion);
		
		PotionEffectType type = PotionEffectType.getByName(potion);
		p.addPotionEffect(new PotionEffect(type, 20 * Setting.POTION_DURATION_TIME, 0));
	}
	
	/*
	 *  [skill list]
	 * 	"hide",
		"heal",
		"chat",
		"swap_token",
		"killer_tp",
		"fishing_tp",
		"thanos"
	 */
	void use_skill(Player p, String skill)
	{
		if(Setting.DEBUG)
			p.sendMessage(skill);

		// MARK: check skill cooldown time
		if(check_remain_cooldown_time(p, skill))
		{
			p.sendMessage("you can use skill in " + skill_cooldown.get_remain_time(p + skill) + " seconds");
			return;
		}
		
		switch(skill)
		{
		case "hide":
			hide(p);
			break;
		case "heal":
			heal(p);
			break;
		case "chat":
			chat(p);
			break;
		case "swap_token":
			swap_token(p);
			break;
		case "killer_tp":
			killer_tp(p);
			break;
		case "fishing_tp":
			fishing_tp(p);
			break;
		case "thanos":
			thanos(p);
			break;
		case "superstar":
			super_star(p);
			break;
		}
		
	}
	
	boolean check_remain_cooldown_time(Player p, String skill)
	{
		String cooldown_key = p + skill;
		
		int cooldown_time;
		
		switch(skill)
		{
			case "hide":
				cooldown_time = Setting.SKILL_HIDE_COOLDOWN_TIME;	break;
			case "heal":
				cooldown_time = Setting.SKILL_HEAL_COOLDOWN_TIME;	break;
			case "chat":
				cooldown_time = Setting.SKILL_CHAT_COOLDOWN_TIME;	break;
			case "swap_token":
				cooldown_time = Setting.SKILL_SWAP_TOKEN_COOLDOWN_TIME;	break;
			case "killer_tp":
				cooldown_time = Setting.SKILL_KILLER_TP_COOLDOWN_TIME;	break;
			case "fishing_tp":
				cooldown_time = Setting.SKILL_FISHING_TP_COOLDOWN_TIME;	break;
			case "thanos":
				cooldown_time = Setting.SKILL_THANOS_COOLDOWN_TIME;	break;
			default: //super_star
				cooldown_time = Setting.SKILL_SUPER_STAR_COOLDOWN_TIME;	break;
					
		}
		
		return skill_cooldown.check_remain_and_start(cooldown_key, cooldown_time);
	}
	
	void hide(Player p)
	{
		// MARK: hide player
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.hidePlayer(Main.get_instance(), p);
		}
		p.sendTitle("Hide", "hided for " + Setting.SKILL_HIDE_DURATION_TIME + " sec", 20, 40, 20);
		
		
		// MARK: unhide player in Setting.~ seconds
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {

			@Override
			public void run() {
				// MARK: unhide player
				for(Player all : Bukkit.getOnlinePlayers())
				{
					all.showPlayer(Main.get_instance(), p);
					p.sendTitle("Hide end", "you're now shown to other players", 20, 40, 20);
				}
			}
			
		}, 20 * Setting.SKILL_HIDE_DURATION_TIME);
	}
	
	@SuppressWarnings("deprecation")
	void heal(Player p)
	{
		double max_health = p.getMaxHealth(); 
		p.sendTitle("Heal", "you healed", 20, 40, 20);
		p.setHealth(max_health);
	}
	
	void chat(Player p)
	{
		// MARK: make player can chat
		PlayerData p_data = db.get(p.getUniqueId());
		
		p_data.set_on_chat();
		p.sendTitle("Chat", "you can chat for " + Setting.SKILL_CHAT_DURATION_TIME + " sec", 20, 40, 20);
		
		// MARK: make player can not chat
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {
	
			@Override
			public void run() {
				p_data.set_off_chat();
				p.sendTitle("Chat end", "you can't chat from now", 20, 40, 20);
			}
			
		}, 20 * Setting.SKILL_CHAT_DURATION_TIME);
	}
	
	
	void swap_token(Player p)
	{
		PlayerData p_data = db.get(p.getUniqueId());
		PlayerData[] opposite_team_players;
		
		if(p_data.get_team() == Setting.RED_TEAM)
		{
			opposite_team_players = DB.get_instance().get_BLUE_players();
		}
		else
		{
			opposite_team_players = DB.get_instance().get_RED_players();
		}
		
		int opposite_players_size = opposite_team_players.length;
		
		// MARK: return it when only 1 player
		if(opposite_players_size <= 0)
		{
			p.sendMessage("need more than 2 players");
			return;
		}
		
		// MARK: get random player
		int index = (int) (Math.random() * opposite_players_size);
		PlayerData random_p_data = opposite_team_players[index];
		
		// MARK: swap token
		swap_token_each_other(p_data, random_p_data);

		// MARK: notify players that swaping tokens
		p.sendTitle("Swap_Token", "your token is swaped with " + random_p_data.get_name(), 20, 40, 20);
		
		(random_p_data.get_player()).sendTitle("Swap_Token", "your token is swaped with " + p.getName(), 20, 40, 20);
	}
	
	void swap_token_each_other(PlayerData p1_data, PlayerData p2_data)
	{
		int p1_token = p1_data.get_token();
		int p2_token = p2_data.get_token();
		
		p1_data.remove_token(p1_token);
		p2_data.remove_token(p2_token);
		
		p1_data.add_token(p2_token);
		p2_data.add_token(p1_token);
	}
	
	void killer_tp(Player p)
	{
//		ArrayList<Player> all = get_all_players();
//		
//		// MARK: return it when only 1 player
//		if(all.size() <= 1)
//		{
//			p.sendMessage("need more than 2 players");
//			return;
//		}
//			
//		int r = (int) (Math.random() * all.size());
//		Player target_player = all.get(r);
//		
//		run_killer_tp(p, target_player);
	}
	
	void run_killer_tp(Player killer, Player victim)
	{
//		Location killer_back_loc = killer.getLocation();
//		
//		Location victim_loc = victim.getEyeLocation();
//		Vector direction = victim_loc.getDirection();
//		
//		killer.teleport(victim_loc);
	}
	
	void fishing_tp(Player p) 
	{
//		ArrayList<Player> all = get_all_players();
//		
//		// MARK: return it when only 1 player
//		if(all.size() <= 1)
//		{
//			p.sendMessage("need more than 2 players");
//			return;
//		}
//		
//		
	}
	
	// 유일하게 대상이 아군, 적군 구분없음 
	// MARK: kill random player
	void thanos(Player p)
	{
		PlayerData[] all_players = DB.get_instance().get_all_players();
		
		int all_count = all_players.length;
		
		// MARK: return it when only 1 player
		if(all_count < 2)
		{
			p.sendMessage("need more than 2 players");
			return;
		}
		
		int r = (int) (Math.random() * all_count);
		PlayerData target_player = all_players[r];
		
		// MARK: kill player
		target_player.get_player().setHealth(0);
		
		for(Player all : Bukkit.getOnlinePlayers())
		{
			all.sendTitle("thanos", target_player.get_name() + " is dead with finger", 20, 40, 20);
		}
	}

	// MARK: give player potionEffect Glowing
	void super_star(Player p)
	{
		PlayerData p_data = db.get(p.getUniqueId());
		
		PlayerData[] enemy_player_datas;
		
		if(p_data.get_team() == Setting.RED_TEAM)
			enemy_player_datas = DB.get_instance().get_BLUE_players();
		else if(p_data.get_team() == Setting.BLUE_TEAM)
			enemy_player_datas = DB.get_instance().get_RED_players();
		else
			return;

		int all_count = enemy_player_datas.length;
		
		if(all_count < 1)
		{
			p.sendMessage("need enemy player");
			return;
		}
		
		
		int r = (int) (Math.random() * all_count);
		PlayerData target_player_data = enemy_player_datas[r];
		
		Player target_p = target_player_data.get_player();
		
		target_p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * Setting.POTION_DURATION_TIME, 0));
		
		p.sendTitle("Super_Star", target_p.getName() + " is super star", 20, 40, 20);
		target_p.sendTitle("Super_Star", "you're now super star", 20, 40, 20);
	}
	
	
	
	
	
	
	// MARK: help when buying feather
	public boolean buy_feahter(PlayerInteractEvent e)
	{
		Block block = e.getClickedBlock();
		Action action = e.getAction();
		Player p = e.getPlayer();

		if(block != null && block.getType() == Material.WALL_SIGN)
		{
			if(action == Action.RIGHT_CLICK_BLOCK)
			{
				Sign sign = (Sign) e.getClickedBlock().getState(); // TODO: sign은 block.getState()해야함
				if(sign.getLine(0).equals("[Feather]")) // TODO: equalsIgnoreCase 로 하면 안됨 (왜그런지 모름)
				{
					click_shop(p);
					
					return true;
				}
			}
		}

		return false;
	}
	
	void click_shop(Player p)
	{
		PlayerData p_data = db.get(p.getUniqueId());
		
		// MARK: check purchase try count
		if(p_data.can_purchase())
		{
			int purchased_count = p_data.get_shop_used_count();
			int price = Setting.FEATHER_PRICE + 
					(Setting.FEATHER_PRICE_INCREMENT_AMOUNT * purchased_count);
			
			{
				// MARK: check player token amount
				if(p_data.remove_token(price))
				{
					
					// MARK: check distinct random item 
					ItemStack random_item = get_distinct_random_item(p);
					
					
					// MARK: give random item
					p.getInventory().addItem(random_item);
					
					// MARK: add player purchased count
					p_data.add_shop_used_count();
					
					int limit = Setting.SHOP_LIMIT_COUNT;
					int used = p_data.get_shop_used_count();
					
					p.sendMessage(String.format("chance: %d / %d", used, limit));
				}
				else
				{
					p.sendMessage("you need more token");
				}
			}
		}
		else
		{
			p.sendMessage("you already ran out " + Setting.SHOP_LIMIT_COUNT + " chances");
		}
	}
	
	ItemStack get_distinct_random_item(Player p)
	{
		while(true)
		{
			ItemStack item = DB_Feather.get_random_item();
			if( ( ! (p.getInventory().contains(item))) || 
					item.getItemMeta().getDisplayName().equals("Weapon"))
			{
				return item;
			}
		}
	}
}










































