package WordTypes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;


/*
 * Word where each letter is an alternating color as it goes across the screen
 */

public class RainbowWord extends Word {

	public static final long SWITCH_COLORS_TIME = 700; //milliseconds between letter color switches
	private long startTime = System.currentTimeMillis();
	
	private Color[] letterColors;
	private Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK};
	private Random rand = new Random(System.currentTimeMillis());
	
	public RainbowWord(String word, int row, BufferedImage[] explosions, BufferedImage[] destroyedExplosion) {
		super(word, row, explosions, destroyedExplosion);
		letterColors = new Color[word.length()]; //holds the color of each letter in the word
		changeColors(); //start by assigning each letter a color
		isSpecialWord = true;
	}
	
	@Override
	public void update() {
		super.update();
		if(System.currentTimeMillis() - startTime > SWITCH_COLORS_TIME) {
			startTime = System.currentTimeMillis(); //remove this line to have the colors switch like crazy
			changeColors(); //assign each letter a new color
		}
	}
	
	private void changeColors() {
		for(int i = 0; i < letterColors.length; i++) {
			letterColors[i] = colors[rand.nextInt(colors.length)];
		}
	}
	
	@Override
	public void drawWordProgress(Graphics2D g, double percentBetweenUpdates, boolean gameover) {
		FontMetrics fm = g.getFontMetrics();
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
		
		double tempx = adjustedXValue; //the relative x coordinate for drawing the remaining letters
		
		//draw the remaining letters that need to be typed (in their respective color)
		for(int i = correctLetters; i < word.length(); i++) {
			if(rect != null) {
				tempx += rect.getWidth();
			}
			rect = fm.getStringBounds(word.substring(i, i+1), g);
			g.setColor(letterColors[i]);
			g.drawString(
					word.substring(i, i+1),
					(int) tempx,
					(int) y
			);
		}
	}

}
