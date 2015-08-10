import java.net.*;
import java.util.*;
import java.awt.*; 
import java.awt.event.*;
import java.nio.*;

class ARDrone extends Frame implements KeyListener {
    InetAddress inet_addr;
    DatagramSocket socket;
    int seq = 1;
    float speed = (float)0.1;
    boolean shift = false;
    FloatBuffer fb;
    IntBuffer ib;

    public ARDrone(String name, String args[]) throws Exception {
        super(name);

    	String ip = "192.168.1.1";

    	if (args.length >= 1) {
    	    ip = args[0];
    	}

    	StringTokenizer st = new StringTokenizer(ip, ".");

    	byte[] ip_bytes = new byte[4];
    	if (st.countTokens() == 4){
     	    for (int i = 0; i < 4; i++){
    		ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
    	    }
    	}
    	else {
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

    	if (args.length == 2) {
    	    send_at_cmd(args[1]);
    	    System.exit(0);
    	}

    	addKeyListener(this); 
    	setSize(320, 160);
    	setVisible(true);
    	addWindowListener(new WindowAdapter() {
    	    public void windowClosing(WindowEvent e) {
    		System.exit(0);
    	    }
      	});
    }

    public static void main(String args[]) throws Exception {
        new ARDrone("ARDrone", args);
    }

    public void keyTyped(KeyEvent e) {
        ;
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        System.out.println("Key: " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")");

        try {
            control(keyCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 49 && keyCode <= 57) speed = (float)0.1;
        if (keyCode == 16) shift = false;
    }

    public int intOfFloat(float f) {
        fb.put(0, f);
        return ib.get(0);
    }
    
    public void send_at_cmd(String at_cmd) throws Exception {
    	System.out.println("AT command: " + at_cmd);    	
    	byte[] buffer = (at_cmd + "\r").getBytes();
    	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
    	socket.send(packet);
	
    }

    public void control(int keyCode) throws Exception {
    	String at_cmd = "";
    	String action = "";
    	
    	switch (keyCode) {
     	    case 49:	//1
    	        speed = (float)0.05;
    	    	break;
    	    case 50:	//2
    	        speed = (float)0.1;
    	    	break;
    	    case 51:	//3
    	        speed = (float)0.15;
    	    	break;
    	    case 52:	//4
    	        speed = (float)0.25;
    	    	break;
    	    case 53:	//5
    	        speed = (float)0.35;
    	    	break;
    	    case 54:	//6
    	        speed = (float)0.45;
    	    	break;
    	    case 55:	//7
    	        speed = (float)0.6;
    	    	break;
    	    case 56:	//8
    	        speed = (float)0.8;
    	    	break;
    	    case 57:	//9
    	        speed = (float)0.99;
    	    	break;
    	    case 16:	//Shift
    	        shift = true;
    	    	break;
    	    case 38:	//Up
    	    	if (shift) {
    	    	    action = "Go Up (gaz+)";
    	    	    at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed) + ",0";
    	    	} else {
    	    	    action = "Go Forward (pitch+)";
    	    	    at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed) + ",0,0,0";
    	    	}
    	    	break;
    	    case 40:	//Down
    	    	if (shift) {
    	    	    action = "Go Down (gaz-)";
    	    	    at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed) + ",0";
    	    	} else {
    	    	    action = "Go Backward (pitch-)";
    	    	    at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed) + ",0,0,0";
    	    	}
       	    	break;
    	    case 37:	//Left 
    	        if (shift) {
    	            action = "Rotate Left (yaw-)";
		    at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(-speed);
		} else {
		    action = "Go Left (roll-)";
		    at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed) + ",0,0";
		}
    	    	break;
    	    case 39:	//Right
    		if (shift) {
    		    action = "Rotate Right (yaw+)";
		    at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(speed);
		} else {
		    action = "Go Right (roll+)";
		    at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed) + ",0,0";
		}
    	    	break;
    	    case 32:	//SpaceBar
    	    	action = "Hovering";
    	    	at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,0";
    	    	break;
    	    case 33:	//PageUp
    	    	action = "Takeoff";
    	    	at_cmd = "AT*REF=" + (seq++) + ",290718208";
    	    	break;
    	    case 34:	//PageDown
    	    	action = "Landing";
    	    	at_cmd = "AT*REF=" + (seq++) + ",290717696";
    	    	break;
    	    default:
    	    	break;
    	}

    	System.out.println("Speed: " + speed);    	
    	System.out.println("Action: " + action);    	
	send_at_cmd(at_cmd);
    }
}
