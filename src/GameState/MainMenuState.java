package GameState;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

import Main.GamePanel;

/*
 * ToDo:
 * 
 * Add points that flash onto the screen when a word is destroyed.  Similar to arcade games
 * 
 * Change java icon to game logo
 * 
 * credit Kevin MacLeod for music in readme
 */

public class MainMenuState extends GameState {
	
	public static final String TITLE = "Type Blaster";
	
	private SoundHandler sound = new SoundHandler();
	
	private Font titleFont;
	private Font menuOptionsFont;
	
	private int selectedOption = 0;
	private String[] menuOptions = {"Start", "Difficulty: ","Music: ", "Highscores", "Quit"};
	public static int EASY = 0;
	public static int MEDIUM = 1;
	public static int HARD = 2;
	public static int INSANE = 3;
	
	public static int difficulty = 0; //0 = easy, 1 = medium, 2 = hard, 3 = insane
	private static String[] difficulties = {"Easy", "Medium", "Hard", "Insane"};
	
	public static int soundState = 1;
	private static String[] soundStates = {"Off", "On"};
	
	public static boolean isNewGame = true;
	
	public MainMenuState(GameStateManager gsm) {
		super(gsm);
		this.gsm = gsm;
		init();
		
	}
	
	@Override
	public void init() {
		titleFont = new Font("Arial", Font.BOLD, 22);
		menuOptionsFont = new Font("Arial", Font.PLAIN, 18);
		sound.start();
	}
	
	@Override
	public void update() {
		isNewGame = true;
		menuOptions[1] = "Difficulty: " + difficulties[difficulty];
		menuOptions[2] = "Music: " + soundStates[soundState];
		if(soundState == 0)
			sound.stop();
		else
			sound.start();
	}
	
	@Override
	public void draw(Graphics2D g, double percentBetweenUpdates) {
		g.setFont(titleFont);
		
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect;
		
		//draw game title
		rect = fm.getStringBounds(TITLE, g);
		g.setColor(Color.WHITE);
		g.drawString(
				TITLE,
				(int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2)),
				(int) GamePanel.HEIGHT / 2
		);
		
		//draw menu options
		g.setFont(menuOptionsFont);
		fm = g.getFontMetrics();
		for(int i = 0; i < menuOptions.length; i++) {
			rect = fm.getStringBounds(menuOptions[i], g);
			if(i == selectedOption) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.WHITE);
			}
			g.drawString(
					menuOptions[i],
					(int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2)),
					(int) GamePanel.HEIGHT / 2 + ((i+1) * 20)
			);
		}
	}
	
	public void chooseMenuOption() {
		switch(selectedOption) {
			case 0:
				gsm.changeState(GameStateManager.LEVELSTATE);
				break;
			case 1:
				difficulty++;
				if(difficulty == difficulties.length)
					difficulty = 0;
				break;
			case 2:
				soundState++;
				if(soundState == soundStates.length)
					soundState = 0;
				break;
			case 3:
				gsm.changeState(GameStateManager.HIGHSCORESTATE);
				break;
			case 4:
				System.exit(0);
				break;
			default:
				break;
		}
	}
	
	public static String getDifficulty() {
		return difficulties[difficulty];
	}
	
	@Override
	public void keyPressed(int code) {
		if(code == KeyEvent.VK_UP) {
			selectedOption--;
			if(selectedOption < 0) {
				selectedOption = menuOptions.length - 1;
			}
		}
		if(code == KeyEvent.VK_DOWN) {
			selectedOption++;
			if(selectedOption == menuOptions.length) {
				selectedOption = 0;
			}
		}
	}
	
	@Override
	public void keyReleased(int code) {
		if(code == KeyEvent.VK_ENTER) {
			chooseMenuOption();
		}
	}
	
}
