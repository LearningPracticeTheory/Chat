import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	JLabel jlMessage = new JLabel("Messages");
	JLabel jlClients = new JLabel("Friends");
	JPanel jpCenter = new JPanel();
	JPanel jpSouth = new JPanel();
	JPanel centerL = new JPanel();
	JPanel centerR = new JPanel();
	JPanel jpList = new JPanel();
	Vector<String> v = new Vector<String>();
	JList<String> list = new JList<String>(v);
	JScrollPane scrollList = null;
	
	String name = null;
	Socket s = null;
	boolean bConnect = false;
	PrintStream ps = null;
	final String clientIdentify = "#@$";
	final String clientQuit = "$@#";
	
	public static void main(String args[]) {
		new ChatClient();
	}
	
	ChatClient() {
		setName();
		setSize(500, 400);
		setLocationRelativeTo(null);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ps.println(clientQuit);
				ps.flush();
				if(ps != null) ps.close();
					try {
						if(s != null) s.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				System.exit(0);
			}
		});
		
		setLayout(new BorderLayout());
		jpCenter.setLayout(new BorderLayout());
		jpSouth.setLayout(new BorderLayout());
		centerL.setLayout(new BorderLayout());
		centerR.setLayout(new BorderLayout());
		jpList.setLayout(new BorderLayout());
		
		jlMessage.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		jlClients.setFont(new Font("¿¬Ìå", Font.BOLD, 16));
		
		centerL.add(jlMessage, BorderLayout.NORTH);
		centerL.add(new JScrollPane(jtaMessage), BorderLayout.CENTER);
		centerR.add(jlClients, BorderLayout.NORTH);
		scrollList = new JScrollPane(list);
		jpList.add(scrollList, BorderLayout.CENTER);
		centerR.add(jpList, BorderLayout.CENTER);
//		jpList.add(list, BorderLayout.CENTER);
//		centerR.add(jpList, BorderLayout.CENTER);
		jpCenter.add(centerL, BorderLayout.CENTER);
		jpCenter.add(centerR, BorderLayout.EAST);
		jpSouth.add(jtf, BorderLayout.SOUTH);
		
		add(jpCenter, BorderLayout.CENTER);
		add(jpSouth, BorderLayout.SOUTH);
		setVisible(true);
		
		try {
			s = new Socket("localhost", 1289);
			bConnect = true;
			ps = new PrintStream(s.getOutputStream());
			ps.println(getName());
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
//System.out.println("jtf: " + str);
				ps.println(str);
				ps.flush();
				jtf.setText("");
			}
		});

//			String str = in.nextLine();
//			ps.println(str);
		
	}

	public void setName() {
		name = JOptionPane.showInputDialog(null);
	}
	
	public String getName() {
		return name;
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
				if(str.startsWith(clientIdentify)) {
//System.out.println("if " + str);
					str = str.substring(clientIdentify.length());
					addClients(str);
				} else {
					jtaMessage.append(str + "\n");
//System.out.println("else " + str);
					jtaMessage.selectAll();
					jtaMessage.setCaretPosition(jtaMessage.getSelectedText().length() - 1);
				}
			}
		}
		
		public void addClients(String str) {		
			jpList.setVisible(false);
			jpList.remove(scrollList);
			v.addElement(str);
//System.out.println(v + str);
			list = new JList<String>(v);
			scrollList = new JScrollPane(list);
			jpList.add(scrollList, BorderLayout.CENTER);
			jpList.setVisible(true);
			jpList.repaint();
		}
		
	}
	
}