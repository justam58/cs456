import java.awt.EventQueue;

import javax.swing.JFrame;


public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(
			new Runnable() {
				public void run() {
					JFrame window = new MainFrame();
					window.setVisible(true);
				}
			}
			
		);
	}

}
