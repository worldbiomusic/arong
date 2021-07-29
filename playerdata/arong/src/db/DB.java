package db;

import java.util.HashMap;
import java.util.UUID;

import setting.Setting;

// MARK: singleton pattern!
public class DB {
	HashMap<UUID, PlayerData> online_players = new HashMap<UUID, PlayerData>();
	static DB instance;
	
	private DB() {}
	
	public static DB get_instance()
	{
		if(instance == null)
		{
			instance = new DB();
		}
		return instance;
	}
	
	public HashMap<UUID, PlayerData> get_data()
	{
		return online_players;
	}
	
	
	public PlayerData[] get_all_players()
	{
		PlayerData[] list = new PlayerData[online_players.size()];
		int index = 0;
		
		for(PlayerData p_data : online_players.values())
		{
			list[index] = p_data;
			index++;
		}
		
		return list;
	}
	
	public PlayerData[] get_RED_players()
	{
		PlayerData[] ret_p_datas = new PlayerData[PlayerData.RED_TEAM_count];
		int index = 0;
		
		for(PlayerData p_data : online_players.values())
		{
			if(p_data.get_team() == Setting.RED_TEAM)
			{
				ret_p_datas[index] = p_data;
				index++;
			}
		}
		
		return ret_p_datas;
	}
	
	public PlayerData[] get_BLUE_players()
	{
//		Set<UUID> set = online_players.keySet();
//		Iterator<UUID> it = set.iterator();
//		
//		PlayerData[] list = new PlayerData[PlayerData.get_BLUE_count()];
//		
//		for (int i = 0; i < list.length;  ) {
//			PlayerData p_data = online_players.get(it.next());
//			if(p_data.get_team() == Setting.BLUE_TEAM)
//			{
//				list[i] = p_data;
//				i++;
//			}
//		}
//		
//		return list;
		
		PlayerData[] ret_p_datas = new PlayerData[PlayerData.BLUE_TEAM_count];
		int index = 0;

		for(PlayerData p_data : online_players.values())
		{
			if(p_data.get_team() == Setting.BLUE_TEAM)
			{
				ret_p_datas[index] = p_data;
				index++;
			}
		}
		
		return ret_p_datas;
	}
}






































