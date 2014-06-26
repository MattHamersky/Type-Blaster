package GameState;

import java.awt.Graphics2D;


public class GameStateManager {
	
	private GameState[] gameStates;
	private int currentState = 1;
	
	public static final int OPENINGSTATE = 0;
	public static final int MAINMENUSTATE = 1;
	public static final int LEVELSTATE = 2;
	public static final int HIGHSCORESTATE = 3;
	
	public static final int NUMSTATES = 4;
	
	private boolean changeState;
	private int stateToChangeTo;
	
	public GameStateManager() {
		gameStates = new GameState[NUMSTATES];
		gameStates[OPENINGSTATE] = new OpeningState(this);
		gameStates[MAINMENUSTATE] = new MainMenuState(this);
		gameStates[LEVELSTATE] = new LevelState(this);
		gameStates[HIGHSCORESTATE] = new HighScoreState(this);
	}
	
	public void changeState(int newState) {
		stateToChangeTo = newState;
		changeState = true;
	}
	
	public int getCurrentState() {
		return currentState;
	}
	
	public void update() {
		if(changeState) {
			changeState = false;
			currentState = stateToChangeTo;
		}
		gameStates[currentState].update();
	}
	
	public void draw(Graphics2D g) {
		gameStates[currentState].draw(g);
	}
	
	public void keyPressed(int code) {
		gameStates[currentState].keyPressed(code);
	}
	
	public void keyReleased(int code) {
		gameStates[currentState].keyReleased(code);
	}
	
	public void keyTyped(char letter) {
		gameStates[currentState].keyTyped(letter);
	}

}
