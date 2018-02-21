import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.*;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	Socket s = null;
	boolean bConnect = false;
	PrintStream ps = null;
	
	public static void main(String args[]) {
		new ChatClient();
	}
	
	ChatClient() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		add(new JScrollPane(jtaMessage), BorderLayout.CENTER);
		add(jtf, BorderLayout.SOUTH);
		setVisible(true);

		try {
			s = new Socket("localhost", 1289);
			bConnect = true;
			ps = new PrintStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No Server, System exit");
			System.exit(0);
		}
		
		ServerInfo sInfo = new ServerInfo(s);
		new Thread(sInfo).start();
		
		jtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = jtf.getText();
//System.out.println(str);
				ps.println(str);
				ps.flush();
				jtf.setText("");
			}
		});

//			String str = in.nextLine();
//			ps.println(str);
	
	}

	class ServerInfo implements Runnable {
		
		Socket s = null;
		Scanner in = null;
		
		ServerInfo(Socket s) {
			this.s = s;
			try {
				in = new Scanner(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(in.hasNext()) {
//System.out.println(in.nextLine());
				String str = in.nextLine();
				jtaMessage.append(str + "\n");
				jtaMessage.selectAll();
				jtaMessage.setCaretPosition(jtaMessage.getSelectedText().length() - 1);
			}
		}
		
	}
	
}