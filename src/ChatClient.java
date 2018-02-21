import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	Socket s = null;
	boolean bConnect = false;
	Scanner in = null;
	PrintStream ps = null;
	
	public static void main(String args[]) {
		new ChatClient();
	}
	
	ChatClient() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		try {
			s = new Socket("localhost", 1289);
			bConnect = true;
			in = new Scanner(System.in);
			ps = new PrintStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(bConnect) {
			String str = in.nextLine();
			ps.println(str);
		}
		
		add(jtaMessage, BorderLayout.CENTER);
		add(jtf, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
}