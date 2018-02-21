import java.awt.*;
import java.io.IOException;
import java.net.*;

import javax.swing.*;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	Socket s = null;
	
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		add(jtaMessage, BorderLayout.CENTER);
		add(jtf, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
}