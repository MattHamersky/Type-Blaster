package WordTypes;

import GameState.MainMenuState;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WordFinder {
	
	private static ArrayList<String> easyWords = new ArrayList<String>();
	private static ArrayList<String> mediumWords = new ArrayList<String>();
	private static ArrayList<String> hardWords = new ArrayList<String>();
	private static ArrayList<String> insaneWords = new ArrayList<String>();
	
	private static ArrayList<String> currentWordList;
	private static int counter = 0;
	
	public static BufferedImage[] explosion; //regular explosion for contacting the ship
	public static BufferedImage[] destroyedExplosion; //regular explosion for when a word is fully typed, smaller than "explosion"
	public static BufferedImage[] rainbowExplosion; //explosion for the rainbow colored words
	public static BufferedImage[] nuclearExplosion; //explosion for the flashy word "nuclear"
	public static BufferedImage[] smokeExplosion; // explosion made of smoke for the flashy word "smoke"
	public static BufferedImage[] fireworksExplosion; //explosion of fireworks for the flashy word "fireworks"
	
	private static Random rand;
	
	public static final int SPECIAL_WORD_SCORE_MODIFIER = 3;
	
	static {
		try {
			
			//read in easy words
			BufferedReader reader = new BufferedReader(new InputStreamReader(WordFinder.class.getResourceAsStream("/words/easy_words.txt")));
			String word = reader.readLine();
			while(word != null) {
				easyWords.add(word);
				word = reader.readLine();
			}
			
			//read in medium words
			reader = new BufferedReader(new InputStreamReader(WordFinder.class.getResourceAsStream("/words/medium_words.txt")));
			word = reader.readLine();
			while(word != null) {
				mediumWords.add(word);
				word = reader.readLine();
			}
			
			//read in hard words
			reader = new BufferedReader(new InputStreamReader(WordFinder.class.getResourceAsStream("/words/hard_words.txt")));
			word = reader.readLine();
			while(word != null) {
				hardWords.add(word);
				word = reader.readLine();
			}
			
			//read in insane words
			reader = new BufferedReader(new InputStreamReader(WordFinder.class.getResourceAsStream("/words/insane_words.txt")));
			word = reader.readLine();
			while(word != null) {
				insaneWords.add(word);
				word = reader.readLine();
			}
			
			//read in explosion sprites for the "normal" words
			explosion = new BufferedImage[12];
			BufferedImage temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/explosion.png"));
			for(int i = 0; i < explosion.length; i++) {
				explosion[i] = temp.getSubimage(i * 134, 0, 134, temp.getHeight());
			}
			
			//read in rainbow explosions sprites for the "rainbow" words
			rainbowExplosion = new BufferedImage[11];
			temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/rainbow_explosion.png"));
			for(int i = 0; i < rainbowExplosion.length; i++) {
				rainbowExplosion[i] = temp.getSubimage(i * 128, 0, 128, temp.getHeight());
			}
			
			//puff of smoke and a little fire for the regular destruction of words
			destroyedExplosion = new BufferedImage[20];
			temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/destroyed_explosion.png"));
			for(int i = 0; i < 5; i++) {
				for(int k = 0; k < 4; k++) {
					destroyedExplosion[(i*4)+k] = temp.getSubimage((k+1) * 128, i * 128, 128, 128);
				}
			}
			
			//smoke explosion for the flashy word "smoke"
			smokeExplosion = new BufferedImage[15];
			temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/smoke_explosion.png"));
			for(int i = 0; i < 3; i++) {
				for(int k = 0; k < 5; k++) {
					smokeExplosion[(i*3)+k] = temp.getSubimage(k * 256, i * 256, 256, 256);
				}
			}
			
			//fireworks explosion for the flashy word "fireworks"
			fireworksExplosion = new BufferedImage[12];
			temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/fireworks_explosion.png"));
			for(int i = 0; i < 3; i++) {
				for(int k = 0; k < 4; k++) {
					fireworksExplosion[(i*4)+k] = temp.getSubimage(k * 256, i * 256, 256, 256);
				}
			}
			
			//nuclear explosion for the flashy word "nuclear"
			nuclearExplosion = new BufferedImage[12];
			temp = ImageIO.read(WordFinder.class.getResourceAsStream("/sprites/nuclear_explosion.png"));
			for(int i = 0; i < 4; i++) {
				for(int k = 0; k < 3; k++) {
					nuclearExplosion[(i*3)+k] = temp.getSubimage(k * 512, i * 256, 512, 256);
				}
			}
			
			//initialize RNG
			rand = new Random(System.currentTimeMillis());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> getDifficultyWordList() {
		switch(MainMenuState.difficulty) {
			case 0:
				return easyWords;
				
			case 1:
				return mediumWords;
				
			case 2:
				return hardWords;
				
			case 3:
				return insaneWords;
			
			default:
				return easyWords;
		}
	}
	
	public static void randomizeWords() {
		currentWordList = getDifficultyWordList();
		counter = 0;
		Collections.shuffle(currentWordList, new Random(System.nanoTime()));
	}
	
	private static int getCurrCounter() {
		int tempCounter = counter;
		counter++;
		if(counter >= currentWordList.size()) {
			counter = 0;
		}
		return tempCounter;
	}
	
	public static Word getWord(int row) {
		return new Word(currentWordList.get(getCurrCounter()), row, explosion, destroyedExplosion);
	}
	
	public static String getWord() {
		return currentWordList.get(getCurrCounter());
	}
	
	public static Word getSpecialWord(int row) {
		//originally 13
		int specialWord = rand.nextInt(13); //random int to decide which of the special words we spawn in
		//fast words
		//if(specialWord >= 0 && specialWord <= 3) {
		//	return new FastWord(getWord(), row, explosion, destroyedExplosion);
		//}
		//rainbow words
		//else if(specialWord >= 4 && specialWord <= 7) {
		//	return new RainbowWord(getWord(), row, rainbowExplosion, rainbowExplosion);
		//}
		//flashy words
		//else {
			return getFlashyWord(row);
		//}
	}
	
	private static FlashyWord getFlashyWord(int row) {
		//int newSpecialWord = rand.nextInt(7); //number of "flashy" words i have
		int newSpecialWord = rand.nextInt(1)+2;
		switch(newSpecialWord) {
			case 0:
				return new NuclearWord("nuclear", row, nuclearExplosion, nuclearExplosion);
				
			case 1:
				return new FlashyWord("smoke", row, smokeExplosion, smokeExplosion);
				
			case 2:
				return new StrobeWord("strobe", row);
				
			case 3:
				return new FlashWord("flash", row);
			
			case 4:
				return new FlashyWord("fireworks", row, fireworksExplosion, fireworksExplosion);
				
			case 5:
				return new HealthWord("health", row, explosion);
				
			case 6:
				return new ClearWord("clearance", row, explosion);
				
			default:
				return null;
				
			
		}
	}
	
}
