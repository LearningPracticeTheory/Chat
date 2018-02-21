import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class ChatServer extends JFrame {
	
	public static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	ServerSocket ss = null;
	Socket s = null;
	boolean bConnect = false;
	ArrayList<Clients> al = new ArrayList<Clients>();
	
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
				al.add(cs);
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
		PrintStream ps = null;
		
		Clients(Socket s) {
			this.s = s;
			flag = true;
			try {
				in = new Scanner(s.getInputStream());
				ps = new PrintStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(in.hasNext()) {
//				System.out.println(in.nextLine());
				String str = in.nextLine();
				jtaMessage.append(str + "\n");
				for(Clients cs : al) {
					cs.send(str);
				}
			}
		}
		
		public void send(String str) {
			ps.println(str);
		}
		
	}
	
}