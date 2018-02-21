import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatServer extends JFrame {
	
	public static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	ServerSocket ss = null;
	Socket s = null;
	boolean bConnect = false;
	
	public static void main(String args[]) {
		new ChatServer();
	}
	
	ChatServer() {
		setSize(500, 400);
//		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		add(jtaMessage);
		setVisible(true);
		
		try {
			ss = new ServerSocket(1289);
			bConnect = true;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		while(bConnect) {
			try {
				s = ss.accept();
System.out.println("A client connect");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}