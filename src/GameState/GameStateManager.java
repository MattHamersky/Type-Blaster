package GameState;

import java.awt.Graphics2D;


public class GameStateManager {
	
	private GameState[] gameStates;
	private int currentState = 0;
	
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
			gameStates[currentState].reloadState();
		}
		gameStates[currentState].update();
	}
	
	public void draw(Graphics2D g, double percentBetweenUpdates) {
		gameStates[currentState].draw(g, percentBetweenUpdates);
	}
	
	public void keyPressed(int code) {
		if(gameStates[currentState] != null) {
			gameStates[currentState].keyPressed(code);
		}
	}
	
	public void keyReleased(int code) {
		if(gameStates[currentState] != null) {
			gameStates[currentState].keyReleased(code);
		}
	}
	
	public void keyTyped(char letter) {
		if(gameStates[currentState] != null) {
			gameStates[currentState].keyTyped(letter);
		}
	}

}
