package plugin.main;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import db.DB_Rank;
import plugin.cmd.player.Executor_Debug;
import plugin.cmd.player.Executor_Me;
import plugin.cmd.player.Executor_Team;
import plugin.cmd.player.Executor_Token;
import plugin.event.player.Listener_Player;

public class Main extends JavaPlugin{
	Server server;
	
	static Plugin plugin;
	
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
		
		// MARK: make config file
		saveConfig();
		DB_Rank.setup();
		DB_Rank.save();
	}
	
	void setup_onDisable()
	{
		
	}
	
	void add_command()
	{
		getCommand("token").setExecutor(new Executor_Token());
		getCommand("team").setExecutor(new Executor_Team());
		getCommand("me").setExecutor(new Executor_Me());
		getCommand("debug").setExecutor(new Executor_Debug());
	}
	
	void register_event_listener()
	{
		PluginManager m = server.getPluginManager();
		m.registerEvents(new Listener_Player(), this);
		
	}
	
	void make_config()
	{
		
	}
}



























