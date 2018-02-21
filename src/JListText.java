import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class JListText extends JFrame {
	
	private static final long serialVersionUID = 1L;
//	JList<String> jl = new JList<String>();
	Vector<String> v = new Vector<String>();
	JList<String> jl = new JList<String>(v);
	JPanel jp = new JPanel();
	JButton jb = new JButton("SS");
	
	public static void main(String args[]) {
		new JListText();
	}
	
	JListText() {
		super("sdsss");
		init();
	}
	
	private void init() {
		setSize(200, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		jp.setLayout(new BorderLayout());
		v.addElement("A");
		v.addElement("D");
		jp.add(jl, BorderLayout.CENTER);
		jp.add(jb, BorderLayout.NORTH);
//		add(jl, BorderLayout.CENTER);
		add(jp, BorderLayout.CENTER);
		System.out.println(v);
		setVisible(true);
		new Temp(v).start();
	}

	class Temp extends Thread {
		
		Vector<String> v = null;
		String name = null;
		
		Temp(Vector<String> v) {

			this.v = v;
			v.addElement("C");
			
		}
		
		public void run() {
//			jl = new JList<String>(v);
			v.addElement("B");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//******************************************	
			jp.setVisible(false);
			jp.remove(jl);
			v.addElement("E");
			jl = new JList<String>(v);
			jp.add(jl, BorderLayout.CENTER);
			jp.setVisible(true);
			//jp.repaint();
//******************************************
			System.out.println(v);
		}
		
	}
	
}
