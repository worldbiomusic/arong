import java.util.HashMap;

public class Test {
	
	public static void main(String[] args) {
		Bank b = new Bank();
		PersonThread1 th1 = new PersonThread1("1", b);
		PersonThread2 th2 = new PersonThread2("2", b);
		
		th1.start();
		th2.start();
		
		try {
			th1.join();
			th2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		b.print_money();
	}
}

class Bank
{
	int money;
	HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
	Bank()
	{
		this.money = 0;
	}
	
	void deposit(int m)
	{
		int remain = this.money;
		Thread.yield();
		int t = remain + m;
//		System.out.print("+");
		this.money = t;
		System.out.println(Thread.currentThread().getName() + ": " + this.money);
	}
	
	void withdraw(int m)
	{
		int remain = this.money;
		Thread.yield();
		int t = remain - m;
//		System.out.print("+");
		this.money = t;
		System.out.println(Thread.currentThread().getName() + ": " + this.money);
	}
	
	void print_money()
	{
		System.out.println(this.money);
	}
}

class PersonThread1 extends Thread
{
	Bank b;
	PersonThread1(String name, Bank b)
	{
		super(name);
		this.b = b;
	}
	
	@Override
	public void run() 
	{
		for(int i = 0; i < 10000; i++)
		{
			synchronized(b)
			{
				b.deposit(1);
			}
		}
	}
}

class PersonThread2 extends Thread
{
	Bank b;
	PersonThread2(String name, Bank b)
	{
		super(name);
		this.b = b;
	}
	
	@Override
	public void run() 
	{
		for(int i = 0; i < 10000; i++)
		{
			synchronized(b)
			{
				b.withdraw(1);
			}
		}
	}
}