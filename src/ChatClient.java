import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
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

public class ChatClient extends JFrame 
implements ActionListener{

	private static final long serialVersionUID = 1L;
	JTextArea jtaMessage = new JTextArea();
	JTextField jtf = new JTextField();
	JLabel jlName = null;
	JLabel jlClients = new JLabel("Friends");	
	JPanel jpCenter = new JPanel();
	JPanel centerL = new JPanel();
	JPanel centerR = new JPanel();
	JPanel jpList = new JPanel();
	JPanel jpSouth = new JPanel();
	JPanel southR = new JPanel();
	JScrollPane scrollList = null;
	JButton send = new JButton(" Send ");
	JButton cancel = new JButton("Cancel");

	Vector<String> v = new Vector<String>();
	JList<String> list = new JList<String>(v);
	
	String name = null;
	Socket s = null;
	PrintStream ps = null;
	boolean bConnect = false;
	final String clientIdentify = "#@$";
	final String clientQuit = "$@#";
	final String shutDownIdentify = "@#$";
	
	public static void main(String args[]) {
		new ChatClient();
	}
	
	ChatClient() {
		setName();
		setSize(500, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		jpCenter.setLayout(new BorderLayout());
		centerL.setLayout(new BorderLayout());
		centerR.setLayout(new BorderLayout());
		jpList.setLayout(new BorderLayout());
		jpSouth.setLayout(new BorderLayout());
		southR.setLayout(new GridLayout(1, 2));
		
		jlClients.setFont(new Font("楷体", Font.BOLD, 16));
		jlName.setFont(new Font("楷体", Font.PLAIN, 16));
		send.setFont(new Font("楷体", Font.PLAIN, 14));
		cancel.setFont(new Font("楷体", Font.PLAIN, 14));
		
		centerL.add(jlName, BorderLayout.NORTH);
		centerL.add(new JScrollPane(jtaMessage), BorderLayout.CENTER);
		centerR.add(jlClients, BorderLayout.NORTH);		
		scrollList = new JScrollPane(list);
		jpList.add(scrollList, BorderLayout.CENTER);
		centerR.add(jpList, BorderLayout.CENTER);
		
		southR.add(send);
		southR.add(cancel);
		jpSouth.add(southR, BorderLayout.EAST);
		jpSouth.add(jtf, BorderLayout.CENTER);
		
		jpCenter.add(centerL, BorderLayout.CENTER);
		jpCenter.add(centerR, BorderLayout.EAST);
		add(jpCenter, BorderLayout.CENTER);
		add(jpSouth, BorderLayout.SOUTH);

		jtf.requestFocus(true);
		setVisible(true);
		
		jtf.addActionListener(this);
		send.addActionListener(this);
		cancel.addActionListener(this);
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
		
	}

	public void setName() {
		name = JOptionPane.showInputDialog(null, "Enter Name", 
				"Enter", JOptionPane.CLOSED_OPTION);
		while(name.matches("\\s*")) {
			name = JOptionPane.showInputDialog(null, "Name cannot be null",  
					"Enter again", JOptionPane.CLOSED_OPTION);
		}
		jlName = new JLabel(name);
	}
	
	public void send() {
		String l = list.getSelectedValue();
		if(l != null) {
			jtf.setText(jtf.getText() + "@" + l + " ");
		}
		String str = jtf.getText();
		if(!str.matches("\\s*")) {
			ps.println(str);
			ps.flush();
			jtf.setText("");		
		}
		list.clearSelection();
	}
	
	public String getName() {
		return name;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(send)) {
			send();
		} else if(e.getSource().equals(jtf)) {
			send();
		} else if(e.getSource().equals(cancel)) {
			jtf.setText("");
		}
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
				String str = in.nextLine();
				identify(str);
			}
		}

		public void identify(String str) {
			if(str.startsWith(clientIdentify)) {
				str = str.substring(clientIdentify.length());
				if(v.isEmpty() || !v.contains(str)) {
					addClients(str);					
				}
			} else if(str.startsWith(clientQuit)) {
				str = str.substring(clientQuit.length());
				deleteClient(str);
			} else if(str.equals(shutDownIdentify)) {
				jtaMessage.append("***Server Shutdown***\n");
				jtaMessage.append("Can not provice services");
				jtaMessage.append("Quit After 3 Second\n");
				try {
					Thread.sleep(1000);
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				jtaMessage.append(str + "\n");
				jtaMessage.selectAll();
				jtaMessage.setCaretPosition(jtaMessage.getSelectedText().length() - 1);
			}
		}
		
		public void addClients(String str) {	
			jpList.setVisible(false);
			jpList.remove(scrollList);
			v.addElement(str);
			list = new JList<String>(v);
			scrollList = new JScrollPane(list);
			jpList.add(scrollList, BorderLayout.CENTER);
			jpList.setVisible(true);
			jpList.repaint();
		}
		
		public void deleteClient(String str) {
			v.remove(str);
			repaint();
		}
		
	}
	
}