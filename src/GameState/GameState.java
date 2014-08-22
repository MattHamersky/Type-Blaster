package GameState;

import GameState.GameStateManager;

public abstract class GameState {

	protected GameStateManager gsm;
	
	public GameState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(java.awt.Graphics2D g, double percentBetweenUpdates);
	public abstract void reloadState();
	public abstract void keyPressed(int code);
	public abstract void keyReleased(int code);
	public void keyTyped(char code){}
	
}
