package Main;

import javax.swing.JFrame;

public class Init {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Type Blaster");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new GamePanel());
		frame.pack();
		frame.setVisible(true);
		
	}

}
