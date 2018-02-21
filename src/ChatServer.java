import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.*;

public class ChatServer extends JFrame 
implements ActionListener {
	
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
	JScrollPane scrollList = null;
	ServerSocket ss = null;
	Socket s = null;
	Scanner in = null;
	Calendar cal = null;
	PrintStream ps = null;
	BufferedReader br = null;
	BufferedWriter bw = null;
	File f = new File("log.txt");
	ArrayList<Clients> al = new ArrayList<Clients>();
	String time = null;
	final String clientIdentify = "#@$";
	final String clientQuit = "$@#";
	final String shutDownIdentify = "@#$";
	boolean bConnect = false;
	
	public static void main(String args[]) {
		new ChatServer();
	}
	
	ChatServer() {
		init();
	}
	
	public void init() {
		setSize(600, 400);
		setLayout(new BorderLayout());
		jpCenter.setLayout(new BorderLayout());
		jpEast.setLayout(new BorderLayout());
		jpList.setLayout(new BorderLayout());
		jtaMessage.setLineWrap(true);
		scroll = new JScrollPane(jtaMessage);
		jlMessage.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		jlClients.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		
		jpCenter.add(jlMessage, BorderLayout.NORTH);
		jpCenter.add(scroll, BorderLayout.CENTER);
		jpEast.add(jlClients, BorderLayout.NORTH);
		jpEast.add(jbGod, BorderLayout.SOUTH);
		scrollList = new JScrollPane(list);
		jpList.add(scrollList, BorderLayout.CENTER);
		jpEast.add(jpList, BorderLayout.CENTER);
		
		jbGod.addActionListener(this);
		add(jpCenter, BorderLayout.CENTER);
		add(jpEast, BorderLayout.EAST);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});
		
		try {
			if(!f.exists()) {
				f.createNewFile();
			}
			br = new BufferedReader(new FileReader(f));
			bw = new BufferedWriter(new FileWriter(f, true));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
				ps = new PrintStream(s.getOutputStream());
				Clients cs = new Clients(s);
				al.add(cs);
				
				for(int i = 0; i < v.size(); i++) {
					for(Clients cd : al) {
						cd.send(clientIdentify + v.get(i));
					}
				}
				
				cs.start();
			}
		} catch(IOException e) {
			bConnect = false;
			write("");
			write("---* Server ShutDown at " + getTimes() + "*---");
		} finally {
			shutdown();
		}
		
	}
	
	public void shutdown() {
		for(Clients cs : al) {
			cs.ps.println(shutDownIdentify);
		}
		try {
			if(in != null) in.close();
			if(ps != null) ps.close();
			if(ss != null) ss.close();
			if(s != null) s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String str) {
		try {
			bw.write(str);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getTimes() {
		cal = Calendar.getInstance();
		time = cal.getTime().toString();
		return time;
	}
	
	public void actionPerformed(ActionEvent e) {
		new GodDialog(this, "God mode", true);
	}
	
	class GodDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		JTextArea text = new JTextArea();
		String log = null;
		
		GodDialog(JFrame jf, String title, boolean flag) {
			super(jf, title, flag);
			text.setFont(new Font("¿¬Ìå", Font.PLAIN, 14));
			setLayout(new BorderLayout());
			add(new JScrollPane(text), BorderLayout.CENTER);
			try {
				while((log = br.readLine()) != null) {
					text.append(log + "\n");
					text.selectAll();
					text.setCaretPosition(text.getSelectedText().length() - 1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			setSize(500, 400);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setVisible(true);
		}
		
	}
	
	class Clients extends Thread {
		
		Socket s = null;
		Scanner sin = null;
		PrintStream ps = null;
		
		Clients(Socket s) {
			this.s = s;
			try {
				sin = new Scanner(s.getInputStream());
				ps = new PrintStream(s.getOutputStream());
				String str = sin.nextLine();
				setName(str);
				jtaMessage.append(getName() + " connected!\n");
				write("+ " + getName() + " connected at " + getTimes());
				addClients(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(sin.hasNext()) {
				String getStr = sin.nextLine();
				if(getStr.startsWith(clientQuit)) {
					deleteClient(getName());
					for(Clients cs : al) {
						cs.send(clientQuit + getName());
					}
				} else {
					String sendStr = getName() + ": " + getStr;
					write(sendStr);
					write("\t# " + getTimes());
					jtaMessage.selectAll();
					jtaMessage.setCaretPosition(jtaMessage.getSelectedText().length() - 1);
					for(Clients cs : al) {
						cs.send(sendStr);
					}
				}
			}
		}
		
		public void send(String str) {
			ps.println(str);
			ps.flush();
		}
		
		public void addClients(String str) {
			jpList.setVisible(false);
			jpList.remove(scrollList);
			v.addElement(str);
			list = new JList<String>(v);
			scrollList = new JScrollPane(list);
			jpList.add(scrollList, BorderLayout.CENTER);
			jpList.setVisible(true);
			repaint();
		}
		
		public void deleteClient(String str) {
			jtaMessage.append(getName() + " Quit!\n");
			write("- " + getName() + " quited at " + getTimes());
			v.remove(getName());
			repaint();
		}
		
	}
	
}