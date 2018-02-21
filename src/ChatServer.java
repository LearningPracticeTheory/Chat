import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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
//System.out.println("A client connect");
				jtaMessage.append("A client connect\n");
				Clients cs = new Clients(s);
				new Thread(cs).start();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class Clients implements Runnable {
		
		Socket s = null;
		Scanner in = null;
		boolean flag = false;
		
		Clients(Socket s) {
			this.s = s;
			flag = true;
			try {
				in = new Scanner(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(in.hasNext()) {
//				System.out.println(in.nextLine());
				String str = in.nextLine();
				jtaMessage.append(str + "\n");
			}
		}
		
	}
	
}