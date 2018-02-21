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
	JPanel jpList = new JPanel();
	JLabel jlMessage = new JLabel("Clients' Messages");
	JLabel jlClients = new JLabel("Clients");
	JButton jbGod = new JButton("God mode");
	Vector<String> v = new Vector<String>();
	JList<String> list = new JList<String>(v);
	JScrollPane scroll = null;
	ServerSocket ss = null;
	Socket s = null;
	Thread t = null;
	Scanner in = null;
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
		jpList.setLayout(new BorderLayout());
		
		jtaMessage.setLineWrap(true);
		scroll = new JScrollPane(jtaMessage);
		jlMessage.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		jlClients.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		
		jpList.add(list, BorderLayout.CENTER);
		jpCenter.add(jlMessage, BorderLayout.NORTH);
		jpCenter.add(scroll, BorderLayout.CENTER);
		jpEast.add(jlClients, BorderLayout.NORTH);
		jpEast.add(jbGod, BorderLayout.SOUTH);
		jpEast.add(jpList, BorderLayout.CENTER);
		
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
//System.out.println(in.nextLine());
				Clients cs = new Clients(s, v);
				al.add(cs);
				cs.start();
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
	
	class Clients extends Thread {
		
		Socket s = null;
		Scanner in = null;
		boolean flag = false;
		Vector<String> v = null;
		PrintStream ps = null;
		
		Clients(Socket s, Vector<String> v) {
			this.s = s;
			this.v = v;
			flag = true;
			try {
				in = new Scanner(s.getInputStream());
				ps = new PrintStream(s.getOutputStream());
				String str = in.nextLine();
				setName(str);
				jpList.remove(list);
				jpList.setVisible(false);
				v.addElement(getName());
				list = new JList<String>(v);
				jpList.add(list, BorderLayout.CENTER);
				jpList.setVisible(true);
				jpList.repaint();
//System.out.println(str + getName() + v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(in.hasNext()) {
//				System.out.println(in.nextLine());
				String str = getName() + ": " + in.nextLine();
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