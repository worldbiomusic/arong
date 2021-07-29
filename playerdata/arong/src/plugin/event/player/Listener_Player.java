package plugin.event.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import db.DB_Rank;
import db.PlayerData;
import plugin.main.Main;
import setting.Setting;


/*
 * team spawn location
 * red: 7, 3, -344
 * blue: 45, 3, -344
 */

@SuppressWarnings("deprecation")
public class Listener_Player implements Listener{
	
	Player_Helper helper;
	Feather_Helper feather_helper;
	HashMap<UUID, PlayerData> db;
	
	public Listener_Player(HashMap<UUID, PlayerData> db) {
		this.db = db;
		helper = new Player_Helper(db);
		feather_helper = new Feather_Helper(db);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		// MARK: put PlayerData to db 
		db.put(p.getUniqueId(), new PlayerData(p));
		PlayerData p_data = db.get(p.getUniqueId());
		
		// MARK: setup DB
		setup_db(p, p_data);
		
		// MARK: clear inventory
		helper.clear_inventory(p);
		
		// MARK: give tools
		helper.give_tools(p, p_data);
		
		// MARK: teleport team spawn location
		helper.teleport_team_spawn_location(p, p_data);
		
		// MARK: NOT hurt for n seconds
		helper.add_spawn_protect(p, p_data);
		
		// MARK: announce player join
		String join_msg = helper.get_join_message(p, p_data);
		e.setJoinMessage(join_msg);
		
		// MARK: update kill,death rank sign
		DB_Rank.update_sign("kill");
		DB_Rank.update_sign("death");
		
		// MARK: customize tab list
		
		// MARK: check PLAYER_KILL_TOKEN
		if(Setting.DEBUG)
			p.sendMessage("PLAYER_KILL_TOKEN: " + Setting.PLAYER_KILL_TOKEN());
	}
	
	void setup_db(Player p, PlayerData p_data)
	{
		// MARK: register team
		helper.register_team(p, p_data);
		
		// MARK: give token
		helper.manage_token_on_join(p, p_data);
		
		// MARK: make config keys on first join
		helper.make_DB_Kill_Death_keys_on_first_join(p);
		
		// MARK: set 0 feather purchase count
		p_data.reset_shop_used_count();
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		PlayerData p_data = db.get(p.getUniqueId());
		
		// MARK: when quit after reload
		if(p_data == null)
			return;
		
		db.remove(p.getUniqueId());
		
		// MARK: clear inventory
		helper.clear_inventory(p);
		
		// MARK: annouce player quit
		String quit_msg = helper.get_quit_message(p, p_data);
		e.setQuitMessage(quit_msg);
		
		// MARK: unregister team
		helper.unregister_team(p, p_data);
		
		// MARK: distribute tokens (1/2: random user, 1/2: server)
		helper.manage_token_on_quit(p, p_data);
	
		// MARK: update kill,death rank sign
		DB_Rank.update_sign("kill");
		DB_Rank.update_sign("death");
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent e)
	{
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		
		// 교훈: 처음부터 타입 캐스팅 해서 체크하지 말고 본래 캐스팅(또는 최상위 상속 타입의 변수설정)을 가지고 와서 타입 체크하기
		// (타입 캐스팅 먼저하고 검사하면 캐스팅에서 오류날 수도 있음)
		
		if(victim instanceof Player && (damager instanceof Player || damager instanceof Arrow))
		{
			Player p_victim = (Player) victim;
			Player p_damager;
			
			if(damager instanceof Arrow)
			{
				p_damager = (Player) ( ((Arrow) damager).getShooter() );
			}
			else // Player
			{
				p_damager = (Player) damager;
			}
			
			PlayerData p_data = db.get(p_victim.getUniqueId());
			
			// MARK: check spawn protecting
			if(p_data.is_spawn_protecting())
			{
				if(Setting.DEBUG)
					p_damager.sendMessage("you hit in spawn protecting state");
				e.setCancelled(true);
			}
				
			
			// MARK: check victim & attacker is same team 
			if(helper.is_same_team_attack(p_victim, p_damager))
			{
				e.setCancelled(true);
			}
			
		}
	}
	
	
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player victim = e.getEntity();
		
		// MARK: remove dropped items
		e.getDrops().clear();

		// MARK: set token, item, config point
		if(victim.getKiller() instanceof Player)
		{
			Player killer = victim.getKiller();
			helper.manage_token_when_player_death(victim, killer);
			
			// MARK: manage death, kill point
			PlayerData p_data = db.get(killer.getUniqueId());
			p_data.add_kill_point();
			
		}
		
		PlayerData p_data = db.get(victim.getUniqueId());
		p_data.add_death_point();
		if(Setting.DEBUG)
			victim.sendMessage("you died");
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		PlayerData p_data = db.get(p.getUniqueId());
		
//		if(p.getName().equals("LLLJH"))
//			return;
		
		// MARK: check player can chat
		if(p_data.get_state_of_chat())
		{
			String name = p_data.get_string_name_colored(ChatColor.WHITE);
			p.setDisplayName(name);
			return;
		}
		
		// MARK: change player message to macro
		if(helper.change_msg(e))
		{
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		// MARK: block drop item
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		
		Player p = e.getPlayer();
		
		// MARK: PlayerData
		PlayerData p_data = db.get(p.getUniqueId());
		
		// MARK: respawn in team location
		Location respawn_location = helper.teleport_team_respawn_location(p, p_data); 
		e.setRespawnLocation(respawn_location);
		
		// MARK: give tools
		helper.give_tools(p, p_data);
		
		// MARK: NOT hurt for n seconds
		helper.add_spawn_protect(p, p_data);
		
		// MARK: reset 0 feather purchase count
		p_data.reset_shop_used_count();
		
		// MARK: give speed effect for 10 sec
		if(Setting.DEBUG)
			p.sendMessage("respawn");
		
		// MARK: run after 1 second
		new BukkitRunnable()
		{
			@Override
			public void run() {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * Setting.RESPAWN_POTION_DURATION_TIME, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * Setting.RESPAWN_POTION_DURATION_TIME, 0));
			}
			
		}.runTaskLater(Main.get_instance(), (20 * 1));
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		
		if( ! p.isOp())
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if( ! p.isOp())
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBreakHanging(HangingBreakEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerChangeFoodLevel(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	// TODO: 계속 선택하면 선택되면서 안막아짐, 복사버그 가능함, 다른 인벤토리 이벤트들도 캔슬해야 하는건가
	// TODO: 신기하게 그냥 선택한 아이템부터 비교하면 잘 안되는데, 선택한 인벤토리를 비교하고 나서 하니까 다 잘됨
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e)
	{
//		Player p = (Player) e.getWhoClicked();
		
		Inventory inv = e.getClickedInventory();
//		ClickType click_type = e.getClick();
		ItemStack item = e.getCurrentItem();
		
		// MARK: verify inventory type is PlayerInventory
		if(inv instanceof PlayerInventory)
		{
			if(item.getType() == Material.LEATHER_CHESTPLATE)
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerChangeSign(SignChangeEvent e)
	{
		if(helper.check_block_is_sign(e))
		{
			return;
		}
	}
	
	
	@EventHandler
	public void onPlayerClickRelativeWithFeather(PlayerInteractEvent e)
	{
//		if(action == Action.RIGHT_CLICK_AIR)
//		{
//			ItemStack item = e.getItem();
//			if(item != null && item.getType() != Material.AIR)
//			{
//				if(item.getType() == Material.COMPASS)
//				{
//					p.sendMessage("compass");
//				}
//			}
//		}
		
		if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL)
		{
			e.setCancelled(true);
		}
		
		// MARK: use feather
		if(feather_helper.use_feahter(e))
		{
			return;
		}
		
		// MARK: buy feather
		if(feather_helper.buy_feahter(e))
		{
			return;
		}
	}
	
}






































