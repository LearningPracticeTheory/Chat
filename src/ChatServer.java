import javax.swing.*;

public class ChatServer extends JFrame {
	
	public static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	
	public static void main(String args[]) {
		new ChatServer();
	}
	
	ChatServer() {
		setSize(500, 400);
//		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		add(jtaMessage);
		setVisible(true);
	}
	
}