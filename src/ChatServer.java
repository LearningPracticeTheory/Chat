import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;

public class ChatServer extends JFrame {
	
	public static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JPanel jpCenter =  new JPanel();
	JPanel jpEast = new JPanel();
	JLabel jlMessage = new JLabel("Clients' Messages");
	JLabel jlClients = new JLabel("Clients");
	JButton jbGod = new JButton("God mode");
	JList<String> list = null;
	Vector<String> v = new Vector<String>();
	JScrollPane scroll = null;
	ServerSocket ss = null;
	Socket s = null;
	Thread t = null;
	boolean bConnect = false;
	ArrayList<Clients> al = new ArrayList<Clients>();
	
	public static void main(String args[]) {
		new ChatServer();
	}
	
	ChatServer() {
		setSize(600, 400);
//		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		jpCenter.setLayout(new BorderLayout());
		jpEast.setLayout(new BorderLayout());
		
		jtaMessage.setLineWrap(true);
		scroll = new JScrollPane(jtaMessage);
		jlMessage.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		jlClients.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		list = new JList<String>(v);
		
		jpCenter.add(jlMessage, BorderLayout.NORTH);
		jpCenter.add(scroll, BorderLayout.CENTER);
		jpEast.add(jlClients, BorderLayout.NORTH);
		jpEast.add(jbGod, BorderLayout.SOUTH);
		jpEast.add(new JScrollPane(list), BorderLayout.CENTER);
		
		add(jpCenter, BorderLayout.CENTER);
		add(jpEast, BorderLayout.EAST);
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
				t = new Thread(cs);
				t.start();
				v.addElement(t.getName());
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