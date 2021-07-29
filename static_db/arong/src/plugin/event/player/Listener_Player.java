package plugin.event.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

import db.DB_Kill_Death;
import db.DB_Rank;
import db.DB_Shop;
import setting.Setting;


/*
 * team spawn location
 * red: 7, 3, -344
 * blue: 45, 3, -344
 */

@SuppressWarnings("deprecation")
public class Listener_Player implements Listener{
	
	Listener_Helper helper;
	Feather_Helper feather_helper;
	
	public Listener_Player() {
		helper = new Listener_Helper();
		feather_helper = new Feather_Helper();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		// MARK: clear inventory
		helper.clear_inventory(p);
		
		// MARK: register team
		helper.register_team(p);
				
		// MARK: give tools
		helper.give_tools(p);
		
		// MARK: teleport team spawn location
		helper.teleport_team_spawn_location(p);
		
		// MARK: give token
		helper.manage_token_on_join(p);
		
		// MARK: NOT hurt for n seconds
		helper.not_damaged(p);
		
		// MARK: announce player join
		String join_msg = helper.get_join_message(p);
		e.setJoinMessage(join_msg);
		
		// MARK: make config keys on first join
		helper.make_keys_on_first_join(p);
		
		// MARK: set 0 feather purchase count
		DB_Shop.reset_count(p);
		
		// MARK: customize tab list
		
		// MARK: update kill,death rank sign
		DB_Rank.update_sign("kill");
		DB_Rank.update_sign("death");
		
		// MARK: check PLAYER_KILL_TOKEN
		if(Setting.DEBUG)
			p.sendMessage("PLAYER_KILL_TOKEN: " + Setting.PLAYER_KILL_TOKEN());
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		
		// MARK: clear inventory
		helper.clear_inventory(p);
		
		// MARK: annouce player quit
		String quit_msg = helper.get_quit_message(p);
		e.setQuitMessage(quit_msg);
		
		// MARK: unregister team
		helper.unregister_team(p);
		
		// MARK: distribute tokens (1/2: random user, 1/2: server)
		helper.manage_token_on_quit(p);
	
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
		
		// MARK: check victim & attacker is Player 
		if(helper.is_same_team_attack(victim, damager))
		{
			e.setCancelled(true);
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
		}
		
		DB_Kill_Death.add_death_point(victim);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e)
	{
		if(helper.check_DB_Chat(e))
			return;
		
		if(helper.change_msg(e))
			return;
	}
	
	@EventHandler
	public void onPlayerThrowItem(PlayerDropItemEvent e)
	{
		// MARK: block drop item
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		// MARK: respawn in team point
		Player p = e.getPlayer();
		Location respawn_location = helper.teleport_team_respawn_location(p); 
		e.setRespawnLocation(respawn_location);
		
		// MARK: give tools
		helper.give_tools(p);
		
		// MARK: NOT hurt for n seconds
		helper.not_damaged(p);
		
		// MARK: reset 0 feather purchase count
		DB_Shop.reset_count(p);
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






































