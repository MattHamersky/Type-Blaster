package GameState;

import Main.GamePanel;
import WordTypes.ClearWord;
import WordTypes.Word;
import WordTypes.WordFinder;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;


public class LevelState extends GameState {

	//hard coded constants to line up with the spaceship's top and bottom and edge
	public static final int ROWS_START = 90;
	public static final int ROWS_END = 660;
	public static final int ROW_HEIGHT = 30;
	public static final int SPACESHIP_EDGE = 40; //where the words collide with the ship
	public static final double START_NEW_WORD_EDGE = 0.30; //how close a word can be to the ship before we start a new word on that row
	
	private Font uiFont;
	private Font gameoverFont;
	private Font gameoverScoreFont;
	private Font wordFont;
	
	private long startTime = 0;
	private static int[] DELAY;  //lower bound delay for adding now words, so we don't get flooded right away
	private static int[] DELAY_UB; //upper bound delay
	private static int LOWER_UB_DELAY = 1000; //the upper bound cannot go lower than this number (in milliseconds)
	private static long DECREASE_DELAY_TIME = 120000; //how many milliseconds before we decrease the spawn delay upper bound
	private static long DECREASE_SPAWN_DELAY_AMOUNT = 100; //decrease the upper bound delay by this much each time the DECREASE_DELAY_TIME has been reached
	
	private static long gameTimeStart;
	
	public static final int[] HEALTH = {100, 200, 500, 1000}; //health depending on the difficulty
	public static int[] HEALTH_MODIFIER = {10, 5, 2, 1}; //number we multiply our health by so it covers the same number of pixels
	
	private int specialWordProb; //percent that a special word will spawn in
	public static final int MAX_SPECIAL_WORD_PROB = 30;
	public static final double PROB_SPECIAL_WORDS_INCREASE = 2;
	public static final int INCREASE_PROB_AT_SCORE = 250; //continually increase prob after this many points has been reached
	private long nextSpecialWordIncrease; //score value at which the next increase takes place;
	
	private volatile static boolean shouldClear = false; //once the last letter of a word has been typed, clear current progress on all other words so there's no confusion
	
	private BufferedImage[] sky;
	private int currSky;
	
	private BufferedImage spaceShip;
	public static final int MAX_HEALTH = 1000;
	private static int currHealth;
	
	private static long score = 0;
	private volatile static List<ScoreEffect> scoreEffects;
	private static int scoreWidth;
	private static int scoreXStart;
	private static int scoreYStart;
	private static Random randScoreEffects;
	
	private volatile boolean gameover = false;
	
	private volatile static List<Word> words;
	private volatile static List<Word> specialWords;
	
	private Random rand;
	private Random specialWordRand;
	
	public LevelState(GameStateManager gsm) {
		super(gsm);
		init();
	}

	@Override
	public void init() {
		try {
			
			uiFont = new Font("Arial", Font.PLAIN, 18);
			gameoverFont = new Font("Arial", Font.BOLD, 26);
			gameoverScoreFont = new Font("Arial", Font.BOLD, 22);
			wordFont = new Font("Arial", Font.PLAIN, 22);
			
			rand = new Random(System.currentTimeMillis());
			specialWordRand = new Random(System.currentTimeMillis()+System.currentTimeMillis());
			randScoreEffects = new Random(System.currentTimeMillis() / 2);
			
			spaceShip = ImageIO.read(getClass().getResourceAsStream("/sprites/spaceship.png"));
			
			sky = new BufferedImage[3];
			sky[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/sky/sky1.png"));
			sky[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/sky/sky2.png"));
			sky[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/sky/sky3.png"));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		//if we are starting a new game, reset variables and words
		if(MainMenuState.isNewGame) {
			MainMenuState.isNewGame = false;
			currSky = rand.nextInt(sky.length);
			
			words = new LinkedList<Word>();
			specialWords = new LinkedList<Word>();
			
			scoreEffects = new LinkedList<ScoreEffect>();
			
			gameover = false;
			currHealth = HEALTH[MainMenuState.difficulty];
			score = 0;
			
			DELAY = new int[]{400, 600, 800, 1000};
			DELAY_UB = new int[]{1600, 1700, 1800, 2000};
			
			gameTimeStart = System.currentTimeMillis();
			
			specialWordProb = 0;
			nextSpecialWordIncrease = 250;
			
			WordFinder.randomizeWords();
		}
		
		//bump up the probability of a special word spawning after a certain score
		if(score >= nextSpecialWordIncrease) {
			specialWordProb += PROB_SPECIAL_WORDS_INCREASE;
			if(specialWordProb > MAX_SPECIAL_WORD_PROB) {
				specialWordProb = MAX_SPECIAL_WORD_PROB;
			}
			nextSpecialWordIncrease += INCREASE_PROB_AT_SCORE;
		}
		
		//if the game has gone on for a certain time we lower the time restrictions on when a new word can spawn
		if(System.currentTimeMillis() - gameTimeStart > DECREASE_DELAY_TIME) {
			gameTimeStart = System.currentTimeMillis();
			DELAY_UB[MainMenuState.difficulty] -= DECREASE_SPAWN_DELAY_AMOUNT;
			if(DELAY_UB[MainMenuState.difficulty] < LOWER_UB_DELAY) {
				DELAY_UB[MainMenuState.difficulty] = LOWER_UB_DELAY;
			}
		}
		
		if(!gameover) {
			//update number of words scrolling
			if(startTime == 0) {
				startTime = System.currentTimeMillis();
				words.add(WordFinder.getWord(rand.nextInt((ROWS_END - ROWS_START)/ ROW_HEIGHT))); //add a word to get the ball rolling
			}
			int counter = 0;
			int specialWordRandNum = specialWordRand.nextInt(100)+1;
			int delay = rand.nextInt(DELAY_UB[MainMenuState.difficulty] - DELAY[MainMenuState.difficulty]) + DELAY[MainMenuState.difficulty]; //add randomness to our delay using a lower and upper bound
			if((System.currentTimeMillis() - startTime) > delay) {
				int newRow = rand.nextInt((ROWS_END - ROWS_START)/ ROW_HEIGHT);
				//check the normal words to see if there is a row match
				counter += spawnWords(newRow, words);
				
				//check the special words to see if there is a row match
				counter += spawnWords(newRow, specialWords);
				
				//if no word is on the new row then go ahead and add the word to that row
				if(counter == (words.size() + specialWords.size())) {
					if(specialWordRandNum <= specialWordProb) {
						specialWords.add(WordFinder.getSpecialWord(newRow));
					}
					else {
						words.add(WordFinder.getWord(newRow));
					}
				}
				startTime = System.currentTimeMillis(); //reset the clock timer
			}
			
			//remove any null words that we acquired for whatever reason
			for(int i = 0; i < words.size(); i++) {
				if(words.get(i) == null) {
					words.remove(i);
					i--;
				}
			}
			for(int i = 0; i < specialWords.size(); i++) {
				if(specialWords.get(i) == null) {
					specialWords.remove(i);
					i--;
				}
			}
			
			//update each word
			updateWords(words);
			
			//update each special word
			updateWords(specialWords);
			
			//update score effects
			for(int i = 0; i < scoreEffects.size(); i++) {
				scoreEffects.get(i).update();
				if(scoreEffects.get(i).shouldBeDeleted()) {
					scoreEffects.remove(i);
					i--;
				}
			}
		}
		if(shouldClear) {
			clearCorrectLetters();
		}
	}
	
	/*
	 * Try to spawn new words based on if there is a free row or if the word is at least a certain way down the line
	 */
	private int spawnWords(int newRow, List<Word> checkList) {
		int counter = 0;
		for(int i = 0; i < checkList.size(); i++) {
			if(checkList.get(i).getRow() == newRow) { //if the new row is already in use
				if((checkList.get(i).getX() < (GamePanel.WIDTH * START_NEW_WORD_EDGE))) { //check that the word is sufficiently close to the ship to start a new word on the same line
					break;
				}
			}
			else {
				counter++;
			}
		}
		return counter;
	}
	
	/*
	 * Update the words from the ArrayList, checking if the words need to be removed or health taken away, etc
	 */
	private void updateWords(List<Word> wordsToUpdate) {
		for(int i = 0; i < wordsToUpdate.size(); i++) {
			wordsToUpdate.get(i).update();
			if(wordsToUpdate.get(i).toBeDeleted()) {
				wordsToUpdate.remove(i);
				i--;
				continue;
			}
			if(wordsToUpdate.get(i).getX() <= SPACESHIP_EDGE  && !wordsToUpdate.get(i).isExploding() && !wordsToUpdate.get(i).isBeingDestroyed()) {
				wordsToUpdate.get(i).explode();
				currHealth -= wordsToUpdate.get(i).getHealthReduction();
				if(currHealth <= 0) {
					currHealth = 0;
					gameover = true;
				}
				else if(currHealth > HEALTH[MainMenuState.difficulty]) {
					currHealth = HEALTH[MainMenuState.difficulty];
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g, double percentBetweenUpdates) {
		g.setFont(uiFont);
		
		//draw background sky
		g.drawImage(
				sky[currSky],
				0,
				0,
				null
		);
		
		//draw health bar
		g.setColor(Color.RED);
		g.fillRect(
				140,
				10,
				currHealth * HEALTH_MODIFIER[MainMenuState.difficulty],
				50
		);
		
		//draw health string (?/1000)
		g.setColor(Color.WHITE);
		g.drawString(
				currHealth + "/" + HEALTH[MainMenuState.difficulty],
				GamePanel.WIDTH / 2,
				40
		);
		
		//draw space ship
		g.drawImage(
				spaceShip,
				-115,
				55,
				null
		);
		
		//draw score
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(Long.toString(score), g);
		scoreWidth = (int) rect.getWidth();
		int scoreX = (int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2));
		int scoreY = (int) (GamePanel.HEIGHT - rect.getHeight() - 10);
		scoreXStart = scoreX;
		scoreYStart = scoreY;
		g.setColor(Color.WHITE);
		g.drawString(
				Long.toString(score),
				scoreX,
				scoreY
		);
		
		//draw score effects
		for(int i = 0; i < scoreEffects.size(); i++) {
			scoreEffects.get(i).draw(g);
		}
		
		//draw words
		g.setFont(wordFont);
		for(int i = 0; i < words.size(); i++) {
			words.get(i).draw(g, percentBetweenUpdates, gameover);
		}
		
		//draw special words
		for(int i = 0; i < specialWords.size(); i++) {
			specialWords.get(i).draw(g, percentBetweenUpdates, gameover);
		}
				
		
		//draw game over overlay
		if(gameover) {
			
			//draw black background
			g.setColor(Color.BLACK);
			g.fillRect(
					GamePanel.WIDTH / 2 - 200,
					GamePanel.HEIGHT / 2 - 200,
					400,
					200
			);
			
			//draw "Game Over - DIFFICULTY"
			g.setFont(gameoverFont);
			fm = g.getFontMetrics();
			rect = fm.getStringBounds("GAME OVER - " + MainMenuState.getDifficulty(), g);
			g.setColor(Color.WHITE);
			g.drawString(
					"GAME OVER - " + MainMenuState.getDifficulty(),
					(int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2)),
					(int) (GamePanel.HEIGHT / 2 - 150)
			);
			
			//draw score
			g.setFont(gameoverScoreFont);
			fm = g.getFontMetrics();
			rect = fm.getStringBounds("Score: " + Long.toString(score), g);
			g.drawString(
					"Score: " + Long.toString(score),
					(int) ((GamePanel.WIDTH / 2 - (rect.getWidth() / 2))),
					(int) ((GamePanel.HEIGHT / 2) - 100)
			);
			
			//draw "highscores" button
			g.setColor(Color.RED);
			rect = fm.getStringBounds("Hit 'Enter' for Highscores", g);
			g.drawString(
					"Hit 'Enter' for Highscores",
					(int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2)),
					(int) ((GamePanel.HEIGHT / 2) - 50)
			);
		}
	}
	
	/*
	 * Wipe out all words except the word doing the wiping
	 */
	public static void clearAllWords(ClearWord word) {
		for(int i = 0; i < words.size(); i++) {
			if(!words.get(i).isExploding() && !words.get(i).isBeingDestroyed()) {
				words.get(i).destroyed();
			}
		}
		for(int i = 0; i < specialWords.size(); i++) {
			if(specialWords.get(i) != word && !specialWords.get(i).isExploding() && !specialWords.get(i).isBeingDestroyed()) {
				specialWords.get(i).destroyed();
			}
		}
	}
	
	/*
	 * Certain words restore health, method to handle that properly
	 */
	public static void addHealth(int healthToAdd) {
		currHealth += healthToAdd;
		if(currHealth > HEALTH[MainMenuState.difficulty]) {
			currHealth = HEALTH[MainMenuState.difficulty];
		}
		
	}
	
	public static void setClearCorrectLettersFlag(boolean toBeCleared) {
		shouldClear = toBeCleared;
	}
	
	/*
	 * Once a word is fully typed we erase the current progress on all the other words so as not to confuse the player
	 */
	public void clearCorrectLetters() {
		for(int i = 0; i < words.size(); i++) {
			words.get(i).setCorrectLetters(0);
		}
		for(int i = 0; i < specialWords.size(); i++) {
			specialWords.get(i).setCorrectLetters(0);
		}
		shouldClear = false;
	}
	
	public static void increaseScore(int wordLength) {
		long points = ((wordLength * 3) / 2) * (MainMenuState.difficulty + 1);
		score += points;
		scoreEffects.add(new ScoreEffect(
								points,
								scoreWidth + scoreXStart,
								scoreYStart,
								randScoreEffects.nextInt(10),
								randScoreEffects.nextInt(6) - 6
						)
		);
								
		
	}

	@Override
	public void keyPressed(int code) {
		
	}

	@Override
	public void keyReleased(int code) {
		//if player hits "escape" go back to main menu
		if(code == KeyEvent.VK_ESCAPE) {
			gsm.changeState(GameStateManager.MAINMENUSTATE);
		}
		//if player hits "backspace" restart level
		else if(code == KeyEvent.VK_BACK_SPACE) {
			MainMenuState.isNewGame = true;
		}
		
		if(gameover) {
			//take them to the highscore page
			if(code == KeyEvent.VK_ENTER) {
				HighScoreState.setNewestHighScore(score);
				gsm.changeState(GameStateManager.HIGHSCORESTATE);
			}
		}
	}
	
	@Override
	public void keyTyped(char letter) {
		if(!gameover) {
			for(int i = 0; i < words.size(); i++) {
				words.get(i).nextLetter(letter);
			}
			for(int i = 0; i < specialWords.size(); i++) {
				specialWords.get(i).nextLetter(letter);
			}
		}
	}
	
	
}
