
public class Test {
	
	public static void main(String[] args) {
		Bank b = new Bank();
		PersonThread1 th1 = new PersonThread1(b);
		PersonThread2 th2 = new PersonThread2(b);
		
		th1.run();
		th2.run();
		
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
	
	Bank()
	{
		this.money = 0;
	}
	
	void deposit(int m)
	{
		int t = money + m;
		System.out.print("+");
		this.money = t; 
	}
	
	void withdraw(int m)
	{
		int t = money - m;
		System.out.print("-");
		this.money = t; 
	}
	
	void print_money()
	{
		System.out.println(this.money);
	}
}

class PersonThread1 extends Thread
{
	Bank b;
	PersonThread1(Bank b)
	{
		this.b = b;
	}
	
	@Override
	public void run() 
	{
		for(int i = 0; i < 100000; i++)
		{
			b.deposit(1000);
		}
	}
}

class PersonThread2 extends Thread
{
	Bank b;
	PersonThread2(Bank b)
	{
		this.b = b;
	}
	
	@Override
	public void run() 
	{
		for(int i = 0; i < 100000; i++)
		{
			b.withdraw(1000);
		}
	}
}




































