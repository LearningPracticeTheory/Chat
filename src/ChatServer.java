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
	JPanel jpCenter =  new JPanel();
	JPanel jEast = new JPanel();
	JScrollPane scroll = null;
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
		jtaMessage.setLineWrap(true);
		scroll = new JScrollPane(jtaMessage);
		add(scroll);
		setVisible(true);
		
		try {
			ss = new ServerSocket(1289);
			bConnect = true;
		} catch(IOException e) {
			System.out.println("Server has already ran.");
			System.exit(0);
		}
		
		try {
			while(bConnect) {
				s = ss.accept();
//System.out.println("A client connect");
				jtaMessage.append("A client connect\n");
				Clients cs = new Clients(s);
				al.add(cs);
				new Thread(cs).start();
			}
		} catch(IOException e) {
			bConnect = false;
			e.printStackTrace();
		} finally {
			try {
				if(ss != null) ss.close();
				if(s != null) s.close();
			} catch (IOException e) {
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
				jtaMessage.selectAll();
				jtaMessage.setCaretPosition(jtaMessage.getSelectedText().length() - 1);
				for(Clients cs : al) {
					cs.send(str);
				}
			}
		}
		
		public void send(String str) {
			ps.println(str);
			ps.flush();
		}
		
	}
	
}