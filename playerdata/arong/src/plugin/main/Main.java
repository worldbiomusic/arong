package plugin.main;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import db.DB;
import db.DB_Rank;
import db.PlayerData;
import plugin.cmd.player.Executor_Debug;
import plugin.cmd.player.Executor_Me;
import plugin.cmd.player.Executor_Team;
import plugin.cmd.player.Executor_Token;
import plugin.event.easter_egg.EA_Listener_Player;
import plugin.event.player.Listener_Player;
import plugin.recipe.easter_egg.EasterEgg_Recipe;

public class Main extends JavaPlugin{
	Server server;
	
	static Plugin plugin;
	HashMap<UUID, PlayerData> db;
	
	// MARK: singleton pattern method
	public static Plugin get_instance()
	{
		return plugin;
	}
	
	@Override
	public void onEnable() {
		setup_onEnable();
		
		add_command();
		register_event_listener();
	} 
	
	@Override
	public void onDisable() {
		setup_onDisable();
	}
	
	void setup_onEnable()
	{
		plugin = this;
		server = getServer();
		server.getConsoleSender().sendMessage("on@@@@@@@@@@@@@@@@@@");
		
		// MARK: get PlayerData db 
		DB db_instance = DB.get_instance();
		db = db_instance.get_data();
		
		// MARK: make config file
		saveConfig();
		DB_Rank.setup();
		DB_Rank.save();
		
		// MARK: add easter egg (recipe)
		EasterEgg_Recipe easter_egg_recipe = new EasterEgg_Recipe(server);
		easter_egg_recipe.add();
	}
	
	void setup_onDisable()
	{
		
	}
	
	void add_command()
	{
		getCommand("token").setExecutor(new Executor_Token(db));
		getCommand("team").setExecutor(new Executor_Team(db));
		getCommand("me").setExecutor(new Executor_Me(db));
		getCommand("debug").setExecutor(new Executor_Debug());
	}
	
	
	void register_event_listener()
	{
		PluginManager m = server.getPluginManager();
		m.registerEvents(new Listener_Player(db), this);
		m.registerEvents(new EA_Listener_Player(), this);
	}
	
	void make_config()
	{
		
	}
}



























