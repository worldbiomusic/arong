package db;

import java.util.HashMap;

public class DB_CoolDown {
	private HashMap<Object, Long> data = new HashMap<Object, Long>();

	public void start_cooldown(Object key, int delay)
	{
		long current = System.currentTimeMillis();
		long waiting = 1000 * delay;
		
		long cooldown_time = current + waiting;
		
		data.put(key, cooldown_time);
	}
	
	public int get_remain_time(Object key)
	{
		int remain = (int) ((data.get(key) - System.currentTimeMillis()) / 1000);
		return remain;
	}
	
	public boolean is_remain_time(Object key)
	{
		if(data.containsKey(key))
		{
			long remain = get_remain_time(key);
			
			return (remain > 0) ? true : false;
		}
		
		// MARK: not register cooldown time data
		return false;
	}
	
	public boolean check_remain_and_start(Object key, int delay)
	{
		if(is_remain_time(key))
		{
			return true;
		}
		
		start_cooldown(key, delay);
		return false;
	}
	
}
