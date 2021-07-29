package setting;

import org.bukkit.Bukkit;

public class Setting {
	
	// MARK: server
	public static final int SPAWN_PROTECT_TIME = 3;
	
	// MARK: token
	public static final int SERVER_JOIN_TOKEN = 5;
	public static final int TOKEN_SIZE_PER_HALF_HEART = 5;
	private static final int PLAYER_KILL_TOKEN_STANDARD = 10;
	public static int PLAYER_KILL_TOKEN()
	{
		// MARK: 사람이 적을 땐 kill이 적게 발생하므로 보상을 높이고
		// 사람이 많을 땐 kill이 많이 발생하므로 보상을 줄임
		int online_count = Bukkit.getOnlinePlayers().size();
		int count = PLAYER_KILL_TOKEN_STANDARD / online_count;
		return count;
	}
	
	// MARK: cooldown time
	public static final int CHAT_COOLDOWN_TIME = 5;
	public static final int POTION_DURABILITY_TIME = 5;
	public static final int POTION_FEATHER_COOLDOWN_TIME = 10;
	
	
	// MARK: feather shop
	public static final int FEATHER_PRICE = 10;
	public static final int FEATHER_LIMIT_COUNT = 3;
	public static final int FEATHER_PRICE_INCREMENT_AMOUNT = 5;
	

	// MARK: skill cooldown time
	public static final int SKILL_HIDE_COOLDOWN_TIME = 10;
	public static final int SKILL_HEAL_COOLDOWN_TIME = 10;
	public static final int SKILL_CHAT_COOLDOWN_TIME = 10;
	public static final int SKILL_SWAP_TOKEN_COOLDOWN_TIME = 10;
	public static final int SKILL_KILLER_TP_COOLDOWN_TIME = 10;
	public static final int SKILL_FISHING_TP_COOLDOWN_TIME = 10;
	public static final int SKILL_THANOS_COOLDOWN_TIME = 10;
	
	
	// MARK: skill duration time
	public static final int SKILL_HIDE_DURATION_TIME = 5;
	public static final int SKILL_CHAT_DURATION_TIME = 5;
	
	
	// MARK: debug mode
	public static final boolean DEBUG = false; 
	
	
	
	
	
	
	
	
	
	
}







































