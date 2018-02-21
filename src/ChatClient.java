import java.awt.*;
import javax.swing.*;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	
	public static void main(String args[]) {
		new ChatClient();
	}
	
	ChatClient() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		add(jtaMessage, BorderLayout.CENTER);
		add(jtf, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
}