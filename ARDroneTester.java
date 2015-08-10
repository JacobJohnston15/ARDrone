import java.net.*;
import java.util.*;
import java.awt.*; 
import java.awt.event.*;
import java.nio.*;

public class ARDroneTester extends Frame
{
	InetAddress inet_addr;
	DatagramSocket socket;
	int seq = 1;
	float speed = (float)0.1;
	boolean shift = false;
	FloatBuffer fb;
	IntBuffer ib;
	
	public ARDroneTester(String name, String args[]) throws Exception
	{
		super(name);

		String ip = "192.168.1.1";

		if (args.length >= 1)
		{
			ip = args[0];
		}

		StringTokenizer st = new StringTokenizer(ip, ".");

		byte[] ip_bytes = new byte[4];
		if (st.countTokens() == 4)
		{
			for (int i = 0; i < 4; i++)
			{
				ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
			}
		}
		else
		{
			System.out.println("Incorrect IP address format: " + ip);
			System.exit(-1);
		}
		
		System.out.println("IP: " + ip);
			System.out.println("Speed: " + speed);    	

			ByteBuffer bb = ByteBuffer.allocate(4);
			fb = bb.asFloatBuffer();
			ib = bb.asIntBuffer();

			inet_addr = InetAddress.getByAddress(ip_bytes);
		socket = new DatagramSocket();
		socket.setSoTimeout(3000);
		
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"");

		if (args.length == 2)
		{
			send_at_cmd(args[1]);
			System.exit(0);
		}

		//addKeyListener(this);
		setSize(320, 160);
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	
	public int intOfFloat(float f)
	{
		fb.put(0, f);
		return ib.get(0);
	}
	
	public void send_at_cmd(String at_cmd) throws Exception
	{
		System.out.println("AT command: " + at_cmd);    	
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
		socket.send(packet);
	}
	
	public void square() throws Exception
	{
		this.takeOff();
		Thread.sleep(6000);
		
		this.moveForward(5);
		Thread.sleep(5500);
		
		this.moveRight(5);
		Thread.sleep(5500);

		this.moveBack(5);
		Thread.sleep(5500);

		this.moveLeft(5);
		Thread.sleep(5500);

		this.land();
		Thread.sleep(10000);
		
		System.exit(0);
	}
	
	public void line(int seconds) throws Exception
	{
		this.moveForward(seconds);
	}
	
	
	
	private void takeOff() throws Exception
	{
		send_at_cmd("AT*REF=" + (seq++) + ",290718208");
	}
	
	private void moveForward(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed) + ",0,0,0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void moveLeft(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed) + ",0,0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void moveBack(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed) + ",0,0,0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void moveRight(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed) + ",0,0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void land() throws Exception
	{
		send_at_cmd("AT*REF=" + (seq++) + ",290717696");
	}
	
	private void lower(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed) + ",0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void raise(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed) + ",0");
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void rotateLeft(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(-speed));
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	private void rotateRight(int seconds) throws Exception
	{
		long beginTime = System.nanoTime();
		while(true)
		{
			send_at_cmd("AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(speed));
			if(System.nanoTime() >= beginTime + seconds*(1E9)) break;
		}
	}
	
	public static void main(String[]args) throws Exception
	{
		ARDroneTester a = new ARDroneTester("Fucker", args);
		a.square();
	}
}
