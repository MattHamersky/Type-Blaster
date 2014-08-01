package WordTypes;

import GameState.Animation;
import GameState.LevelState;
import GameState.MainMenuState;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.Color;

public class Word {

	protected Animation animation;
	protected BufferedImage[] destroyedExplosion;
	protected BufferedImage[] explosion;
	
	protected double speed = ((MainMenuState.difficulty + 1) * 3) / 2;
	protected double x = 1300; //start slightly offscreen on the right
	protected double adjustedXValue = 1300;
	protected double y;
	protected int row;
	protected Color wordColor = Color.WHITE;
	
	protected boolean isExploding = false; //stop x direction movement and begin explosion animation
	protected boolean isBeingDestroyed = false;
	protected boolean toBeDeleted = false;
	
	protected boolean isSpecialWord = false;
	
	protected String word;
	protected int correctLetters = 0; //how many letters the player has correctly typed in this word
	
	protected FontMetrics fm = null;

	public Word(String word, int row, BufferedImage[] explosion, BufferedImage[] destroyedExplosion) {
		this.word = word;
		this.y = LevelState.ROWS_START + (row * LevelState.ROW_HEIGHT);
		this.row = row;
		this.destroyedExplosion = destroyedExplosion;
		this.explosion = explosion;
		
		animation = new Animation();
		animation.setDelay(100);
	}
	
	public void update() {
		//if we aren't exploding then continue movement
		if(!isExploding && !isBeingDestroyed) {
			x -= speed;
		}
		else {
			animation.update();
		}
	}
	
	public void draw(Graphics2D g, double percentBetweenUpdates, boolean gameover) {
		if(!isExploding && !isBeingDestroyed) {
			drawWordProgress(g, percentBetweenUpdates, gameover);
		}
		else {
			drawExplosionProgress(g);
		}
	}
	
	public void drawWordProgress(Graphics2D g, double percentBetweenUpdates, boolean gameover) {
		if(fm == null) {
			fm = g.getFontMetrics();
		}
		Rectangle2D rect = null;
		
		if(!gameover) {
			adjustedXValue = x - Math.round(speed * percentBetweenUpdates);
		}
		else {
			adjustedXValue = x;
		}
		
		//if the player has typed at least one correct letter so far
		if(correctLetters != 0) {
			rect = fm.getStringBounds(word.substring(0, correctLetters), g);
			
			//draw typed letters first (in red)
			g.setColor(Color.RED);
			g.drawString(
					word.substring(0, correctLetters),
					(int) adjustedXValue,
					(int) y
			);
		}
		
		//draw the remaining letters that need to be typed (in white)
		double tempx = adjustedXValue;
		if(rect != null) {
			tempx += rect.getWidth();
		}
		g.setColor(wordColor);
		g.drawString(
				word.substring(correctLetters, word.length()),
				(int) tempx,
				(int) y
		);
	}
	
	public void drawExplosionProgress(Graphics2D g) {
		//play explosion animations here
		BufferedImage sprite = animation.getCurrentSprite();
		if(sprite == null) {
			toBeDeleted = true;
		}
		else {
			g.drawImage(
					sprite,
					(int) x - sprite.getWidth() / 2,
					(int) y - sprite.getHeight() / 2,
					null
			);
		}
	}
	
	public int getX() { return (int) adjustedXValue; }
	public int getRow() { return row; }
	public boolean isExploding() { return isExploding; }
	public boolean isBeingDestroyed() { return isBeingDestroyed; }
	public void setCorrectLetters(int numCorrect) {
		if(numCorrect < 0)
			return;
		correctLetters = numCorrect;
	}
	
	public synchronized void explode() {
		isExploding = true;
		animation.setSprites(explosion, true);
	}
	public synchronized void destroyed() {
		isBeingDestroyed = true;
		animation.setSprites(destroyedExplosion, true);
	}
	
	public int getWordLength() { return word.length(); }
	public boolean toBeDeleted() { return toBeDeleted; }
	public int getHealthReduction() {
		return word.length() * 2;
	}
	
	public void nextLetter(char letterTyped) {
		if(!isExploding) {
			char letter = word.charAt(correctLetters);
			if(letterTyped == letter) {
				correctLetters++;
				if(correctLetters >= word.length()) {
					destroyed();
					if(isSpecialWord) {
						LevelState.increaseScore(word.length() * WordFinder.SPECIAL_WORD_SCORE_MODIFIER);
					}
					else {
						LevelState.increaseScore(word.length());
					}
					LevelState.setClearCorrectLettersFlag(true);
				}
			}
			else {
				correctLetters = 0;
			}
		}
	}	
}
