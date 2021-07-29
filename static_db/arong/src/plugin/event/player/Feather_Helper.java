package plugin.event.player;

import java.util.List;

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

import db.DB_Chat;
import db.DB_CoolDown;
import db.DB_Feather;
import db.DB_Shop;
import db.DB_Token;
import plugin.main.Main;
import setting.Setting;

public class Feather_Helper {
	
	DB_CoolDown potion_cooldown;
	DB_CoolDown skill_cooldown;
	
	public Feather_Helper()
	{
		potion_cooldown = new DB_CoolDown();
		skill_cooldown = new DB_CoolDown();
	}
	
	
	// MARK: help when using feather
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
				return check_feather(item, p);
			}
		}
		return false;
	}
	
	boolean check_feather(ItemStack item, Player p)
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
	
	void give_potion_effect(Player p, String potion)
	{
		String cooldown_key = p + potion;
		if(potion_cooldown.check_remain_and_start(cooldown_key, Setting.POTION_FEATHER_COOLDOWN_TIME))
		{
			p.sendMessage("you can use potion in "+ potion_cooldown.get_remain_time(cooldown_key));
			return;
		}
		
		if(Setting.DEBUG)
			p.sendMessage(potion);
		
		PotionEffectType type = PotionEffectType.getByName(potion);
		p.addPotionEffect((new PotionEffect(type, 20 * Setting.POTION_DURABILITY_TIME, 0)));
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
			
		}
		
	}
	
	boolean check_remain_cooldown_time(Player p, String skill)
	{
		String cooldown_key = p + skill;
		
		int cooldown_time;
		
		switch(skill)
		{
			case "hide":
				cooldown_time = Setting.SKILL_HIDE_COOLDOWN_TIME;
			case "heal":
				cooldown_time = Setting.SKILL_HEAL_COOLDOWN_TIME;
			case "chat":
				cooldown_time = Setting.SKILL_CHAT_COOLDOWN_TIME;
			case "swap_token":
				cooldown_time = Setting.SKILL_SWAP_TOKEN_COOLDOWN_TIME;
			case "killer_tp":
				cooldown_time = Setting.SKILL_KILLER_TP_COOLDOWN_TIME;
			case "fishing_tp":
				cooldown_time = Setting.SKILL_FISHING_TP_COOLDOWN_TIME;
			default: //thanos:
				cooldown_time = Setting.SKILL_THANOS_COOLDOWN_TIME;
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
		p.sendMessage("hided for" + Setting.SKILL_HIDE_DURATION_TIME + " seconds");
		
		// MARK: unhide player in Setting.~ seconds
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {

			@Override
			public void run() {
				// MARK: unhide player
				for(Player all : Bukkit.getOnlinePlayers())
				{
					all.showPlayer(Main.get_instance(), p);
					p.sendMessage("unhided");
				}
			}
			
		}, 20 * Setting.SKILL_HIDE_DURATION_TIME);
	}
	
	@SuppressWarnings("deprecation")
	void heal(Player p)
	{
		double max_health = p.getMaxHealth(); 
		p.setHealth(max_health);
	}
	
	void chat(Player p)
	{
		// MARK: make player can chat
		DB_Chat.add_player(p);
		p.sendMessage("can chat for" + Setting.SKILL_CHAT_DURATION_TIME + " seconds");

		
		// MARK: make player can not chat
		Bukkit.getScheduler().runTaskLater(Main.get_instance(), new Runnable() {
	
			@Override
			public void run() {
				DB_Chat.remove_player(p);
				p.sendMessage("can not chat");
			}
			
		}, 20 * Setting.SKILL_CHAT_DURATION_TIME);
	}
	
	
	void swap_token(Player p)
	{
		List<Player> all = get_all_players();
		
		// MARK: return it when only 1 player
		if(all.size() <= 1)
		{
			p.sendMessage("need more than 2 players");
			return;
		}
		
		// MARK: get random player
		int index = (int) (Math.random() * all.size());
		Player random_p = all.get(index);
		
		// MARK: swap token
		swap_token_each_other(p, random_p);

	}
	
	void swap_token_each_other(Player p1, Player p2)
	{
		synchronized(DB_Token.class)
		{
			int p1_token = DB_Token.get_player_token(p1);
			int p2_token = DB_Token.get_player_token(p2);
			
			DB_Token.remove_token(p1, p1_token);
			DB_Token.remove_token(p2, p2_token);
			
			DB_Token.add_token(p1, p2_token);
			DB_Token.add_token(p2, p1_token);
		}
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
	
	void thanos(Player p)
	{
		List<Player> all = get_all_players();
		
		// MARK: return it when only 1 player
		if(all.size() <= 1)
		{
			p.sendMessage("need more than 2 players");
			return;
		}
		
		int r = (int) (Math.random() * all.size());
		Player target_player = all.get(r);
		
		target_player.setHealth(0);
	}
	
	List<Player> get_all_players()
	{
		@SuppressWarnings("unchecked")
		List<Player> all = (List<Player>) Bukkit.getOnlinePlayers();
		return all;
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
		// MARK: check purchase try count
		if(DB_Shop.can_purchase(p))
		{
			int purchased_count = DB_Shop.get_player_count(p);
			int price = Setting.FEATHER_PRICE + 
					(Setting.FEATHER_PRICE_INCREMENT_AMOUNT * purchased_count);
			
			synchronized(DB_Token.class)
			{
				// MARK: check player token amount
				if(DB_Token.remove_token(p, price))
				{
					
					// MARK: check distinct random item 
					ItemStack random_item = get_distinct_random_item(p);
					
					
					// MARK: give random item
					p.getInventory().addItem(random_item);
					
					// MARK: add player purchased count
					DB_Shop.add_count(p);
					
					int limit = Setting.FEATHER_LIMIT_COUNT;
					int used = DB_Shop.get_player_count(p);
					
					p.sendMessage(String.format("chance: %d / %d", used, limit));
				}
			}
		}
		else
		{
			p.sendMessage("you already ran out " + Setting.FEATHER_LIMIT_COUNT + " chances");
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










































