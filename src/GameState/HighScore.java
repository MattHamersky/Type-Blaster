package GameState;

public class HighScore {

	private String name;
	private long score;
	
	public HighScore(String name, long score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public long getScore() {
		return score;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
