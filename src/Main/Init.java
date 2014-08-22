package Main;

import javax.swing.JFrame;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.GraphicsConfiguration;
import java.awt.BufferCapabilities;

public class Init {
	
	public static boolean fullscreen = false;
	public static boolean restart = true;
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static JFrame frame;
	public static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice gd = ge.getDefaultScreenDevice();
	
	public static DisplayMode display = gd.getDisplayMode();
	public static DisplayMode[] displays = gd.getDisplayModes();
	
	public static DisplayMode oldDisplay;
	
	public static BufferStrategy bufferStrat;
	public static GraphicsConfiguration config = gd.getDefaultConfiguration();
	public static BufferCapabilities bufferCap = config.getBufferCapabilities();
	
	public static boolean isBufferStrat = false;
	
	public static void main(String[] args) {
		
		for(int i = 0; i < displays.length; i++) {
			//System.out.println("width - " + displays[i].getWidth() + " : height - " + displays[i].getHeight() + " : bit depth - " + displays[i].getBitDepth() + " : refresh rate " + displays[i].getRefreshRate());
		}
		
		
		properties();
		
		doesScreenChange();
		
	}
	
	public static void setFullscreen() {
		oldDisplay = gd.getDisplayMode();
		changeResolution();
		frame.setVisible(false);
		frame.dispose();
		frame.setUndecorated(true);
		gd.setFullScreenWindow(frame);
		frame.setVisible(true);
		frame.validate();
		System.out.println(bufferCap.isPageFlipping());
		if(bufferCap.isMultiBufferAvailable()) {
			frame.createBufferStrategy(2);
		}
		else {
			frame.createBufferStrategy(1);
		}
		bufferStrat = frame.getBufferStrategy();
	}
	
	public static void setWindowed() {
		changeResolution();
		frame.setVisible(false);
		frame.dispose();
		frame.setUndecorated(false);
		gd.setFullScreenWindow(null);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.pack();
		frame.setVisible(true);
		frame.validate();
	}
	
	public static void properties() {
		frame = new JFrame("Type Blaster");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		changeResolution();
		frame.getContentPane().add(new GamePanel());
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
	}
	
	public static void changeResolution() {
		if(fullscreen) {
			WIDTH = display.getWidth();
			HEIGHT = display.getHeight();
		}
		else {
			WIDTH = 1280;
			HEIGHT = 720;
		}
	}
	
	public static boolean doesScreenChange() {
		if(restart) {
			if(fullscreen) {
				if(gd.isFullScreenSupported()) {
					setFullscreen();
				}
				else {
					setWindowed();
				}
			}
			else {
				setWindowed();
			}
			restart = false;
			return true;
		}
		return false;
	}

}
