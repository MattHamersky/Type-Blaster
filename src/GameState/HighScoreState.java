package GameState;

import Main.GamePanel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class HighScoreState extends GameState{

	private HighScore[][] highscores = new HighScore[4][10];
	
	private Font highScoreFont; //font for the "highscore" title
	private Font highScoresFont; //font for the individual highscores
	
	private static Long newestHighScore = 0L; //the last player's score to get a highscore
	private static String newestHighName = ""; //the last player's gamertag to get a highscore
	private int highscoreIndex = -1;
	
	private boolean isTyping = false;
	
	public HighScoreState(GameStateManager gsm) {
		super(gsm);
		init();
	}

	@Override
	public void init() {
		
		//load previous highscores from files
		//read in easy highscores
		try {
			BufferedReader reader = new BufferedReader(new FileReader("highscores/Easy.hs"));
			readInHighScores(reader, MainMenuState.EASY);
			
		} catch(FileNotFoundException e) {
			createFile("highscores/easy.hs"); //create the missing file
			populateWithNewScores(MainMenuState.EASY);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//read in medium highscores
		try {
			BufferedReader reader = new BufferedReader(new FileReader("highscores/Medium.hs"));
			readInHighScores(reader, MainMenuState.MEDIUM);
		} catch(FileNotFoundException e) {
			createFile("highscores/medium.hs");
			populateWithNewScores(MainMenuState.MEDIUM);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//read in hard highscores
		try {
			BufferedReader reader = new BufferedReader(new FileReader("highscores/Hard.hs"));
			readInHighScores(reader, MainMenuState.HARD);
		} catch(FileNotFoundException e) {
			createFile("highscores/hard.hs"); //create the missing file
			populateWithNewScores(MainMenuState.HARD);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//read in insane highscores
		try {
			BufferedReader reader = new BufferedReader(new FileReader("highscores/Insane.hs"));
			readInHighScores(reader, MainMenuState.INSANE);
		} catch(FileNotFoundException e) {
			createFile("highscores/insane.hs"); //create the missing file
			populateWithNewScores(MainMenuState.INSANE);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//sanity check to fill in empty highscore spots
		for(int i = 0; i < highscores.length; i++) {
			for(int k = 0; k < highscores[i].length; k++) {
				if(highscores[i][k] == null) {
					highscores[i][k] = new HighScore("-", 0);
				}
			}
		}
		
		//re sort the highscores in case there was an error in decryption, making sure there are no zero scores above actual scores
		for(int i = 0; i < highscores.length; i++) {
			sortArray(highscores[i]);
		}
		
		//create fonts
		highScoreFont = new Font("Arial", Font.BOLD, 42);
		highScoresFont = new Font("Arial", Font.PLAIN, 36);
	}
	
	private void sortArray(HighScore[] scores) {
		for(int i = 0; i < scores.length - 1; i++) {
			if(scores[i].getScore() < scores[i+1].getScore()) {
				HighScore temp = scores[i];
				scores[i] = scores[i+1];
				scores[i+1] = temp;
				i--;
			}
		}
	}
	
	private void createFile(String path) {
		File file = new File(path); //create the missing file
		try {
			if (file.createNewFile() ) {
				System.out.println("Success!");
			}
			else {
				System.out.println("Failure!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readInHighScores(BufferedReader reader, int difficultyIndex) {
		try {
			String line;
			//if the first line contains white space assume the whole file is bad
			if(containsWhitespace(line = reader.readLine())) {
				populateWithNewScores(difficultyIndex);
				return;
			}
			
			//if the file is empty or someone was messing with it, we scrub that data and start over
			for(int i = 0; line != null; i++) {
				if(i == highscores[difficultyIndex].length) {
					break;
				}
				//if an individual line is missing or contains white space override it with new data
				if(containsLotsOfWhitespace(line)) {
					populateWithNewScore(difficultyIndex, i);
					continue;
				}
				highscores[difficultyIndex][i] = Cryptographer.decrypt(line);
				line = reader.readLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void populateWithNewScores(int difficultyIndex) {
		for(int i = 0; i < highscores[difficultyIndex].length; i++) {
			highscores[difficultyIndex][i] = new HighScore("-", 0);
		}
	}
	
	private void populateWithNewScore(int difficultyIndex, int rank) {
		highscores[difficultyIndex][rank] = new HighScore("-", 0);
	}
	
	private boolean containsWhitespace(String str) {
		if(str == null) {
			return true;
		}
		for(int i = 0; i < str.length(); i++) {
			if(Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsLotsOfWhitespace(String str) {
		if(str == null) {
			return true;
		}
		int counter = 0;
		for(int i = 0; i < str.length(); i++) {
			if(Character.isWhitespace(str.charAt(i))) {
				counter++;
			}
		}
		//if there is a suitable amount of whitespace such that it seems they just repeatedly hit the spacebar
		if(counter > (str.length() / 3)) {
			return true;
		}
		return false;
	}
	
	public static void setNewestHighScore(long score) {
		newestHighScore = score;
	}

	@Override
	public void update() {
		if(newestHighScore != 0) {
			for(int i = 0; i < highscores[MainMenuState.difficulty].length; i++) {
				if(newestHighScore > highscores[MainMenuState.difficulty][i].getScore()) {
					shiftScoresDown(i);
					highscores[MainMenuState.difficulty][i] = new HighScore("[Begin Typing Name]", newestHighScore);
					isTyping = true;
					highscoreIndex = i;
					break;
				}
			}
			newestHighScore = 0L;
		}
		
		if(highscoreIndex != -1 && !newestHighName.equals("")) {
			highscores[MainMenuState.difficulty][highscoreIndex].setName(newestHighName);
		}
	}
	
	private void shiftScoresDown(int index) {
		for(int i = highscores[MainMenuState.difficulty].length - 1; i > index; i--) {
			highscores[MainMenuState.difficulty][i] = highscores[MainMenuState.difficulty][i-1];
		}
	}

	@Override
	public void draw(Graphics2D g, double percentBetweenUpdates) {
		FontMetrics fm;
		Rectangle2D rect;
		
		//draw highscore title
		g.setFont(highScoreFont);
		g.setColor(Color.WHITE);
		fm = g.getFontMetrics();
		rect = fm.getStringBounds("Highscores - " + MainMenuState.getDifficulty(), g);
		g.drawString(
				"Highscores - " + MainMenuState.getDifficulty(),
				(int) ((GamePanel.WIDTH / 2) - (rect.getWidth() / 2)),
				(int) (40)
		);
		
		//draw individual ranks and names
		g.setFont(highScoresFont);
		for(int i = 0; i < 10; i++) {
			String line = (i+1) + ". \t\t\t" + highscores[MainMenuState.difficulty][i].getName();
			drawtabString(g, line, 20, 110 + i * 60);
		}
		
		//draw individual scores
		for(int i = 0; i < 10; i++) {
			g.drawString(
					"" + highscores[MainMenuState.difficulty][i].getScore(),
					(GamePanel.WIDTH / 2) + (GamePanel.WIDTH / 5),
					110 + i * 60
			);
		}
	}
	
	/*
	 * I have no idea why this works, but it does.  Found it online, credit to: Rupok on stackoverflow
	 * http://stackoverflow.com/questions/8676691/break-lines-in-g-drawstring-in-java-se
	 */
	private void drawtabString(Graphics2D g, String text, int x, int y) {
        for (String line : text.split("\t"))
            g.drawString(line, x += g.getFontMetrics().getHeight(), y);
    }
	
	public static void setHighScore(Long highscore) {
		newestHighScore = highscore;
	}
	
	private void outputHighScores() {
		PrintWriter pw = getAndClearPrintWriter();
		//someone was screwing around with the file or something happened during opening
		if(pw == null) {
			return;
		}
		for(int i = 0; i < highscores[MainMenuState.difficulty].length; i++) {
			//if the score equals 0 that means it's a placeholder until an actual player gets a score, no need to encrypt that data
			if(highscores[MainMenuState.difficulty][i].getScore() == 0) {
				continue;
			}
			pw.println(Cryptographer.encrypt(highscores[MainMenuState.difficulty][i]));
			
		}
		pw.close();
	}
	
	private PrintWriter getAndClearPrintWriter() {
		try {
			//open the file first so we can clear the contents, and then open and return it for writing to
			PrintWriter writer = new PrintWriter("highscores/"+MainMenuState.getDifficulty()+".hs");
			writer.close();
			return new PrintWriter("highscores/"+MainMenuState.getDifficulty()+".hs");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void keyPressed(int code) {
		
	}

	@Override
	public void keyReleased(int code) {
		if(!isTyping) {
			if(code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
				gsm.changeState(GameStateManager.MAINMENUSTATE);
			}
		}
		if(isTyping) {
			if(code == KeyEvent.VK_ENTER) {
				if(newestHighName.length() < 2) {
					return;
				}
				highscores[MainMenuState.difficulty][highscoreIndex].setName(newestHighName.substring(0, newestHighName.length()-1));
				newestHighName = "";
				highscoreIndex = -1;
				isTyping = false;
				outputHighScores();
			}
		}
	}
	
	@Override
	public void keyTyped(char letter) {
		if(isTyping) {
			//delete a char from the string if they hit backspace
			if(letter == '\b') {
				if(newestHighName.length() > 1) {
					newestHighName = newestHighName.substring(0, newestHighName.length() - 2);
					newestHighName += "_";
				}
				return;
			}
			//don't let them type more than 40 characters
			if(newestHighName.length() > 40) {
				return;
			}
			
			//don't let them put spaces at the beginning of the their name
			if(letter == ' ' && newestHighName.length() < 2) {
				return;
			}
			
			//eliminate all non letter/digits or spaces and underscores
			if(Character.isLetterOrDigit(letter) || letter == ' ' || letter == '_') {
				if(!newestHighName.equals("")) {
					newestHighName = newestHighName.substring(0, newestHighName.length() - 1);
				}
				newestHighName += (letter + "_");
			}
		}
	}
	
}
