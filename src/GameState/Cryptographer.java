package GameState;

public class Cryptographer {

	private static int hexOffset = 10; //add this number to the hex representation of each character to encrypt, subtract to decrypt
	
	public static String encrypt(HighScore highscore) {
		StringBuilder encryptedString = new StringBuilder();
		char[] name = highscore.getName().toCharArray();
		
		//encrypt name
		for(int i = 0; i < name.length; i++) {
			String hex = Integer.toHexString(name[i]+hexOffset);
			encryptedString.append(hex);
		}
		
		//add dash between name and score
		encryptedString.append(Integer.toHexString('-'));
		
		//encrypt score
		encryptedString.append(Long.toHexString(highscore.getScore()+hexOffset));
		
		return encryptedString.toString();
		
	}
	
	public static HighScore decrypt(String encryptedString) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < encryptedString.length(); i+=2) {
			String hexNumber = encryptedString.substring(i, (i+2));
			int number = 10;
			try {
				number = Integer.parseInt(hexNumber, 16);
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			//we've reached the end of the name and now need to decrypt the score
			if((char)number == '-') {
				builder.append((char)number);
				String score = encryptedString.substring(i+2, encryptedString.length());
				long parsedScore = 10;
				try {
					parsedScore = Long.parseLong(score, 16);
					builder.append(Long.toString(parsedScore-hexOffset));
				} catch(Exception e) {
					e.printStackTrace();
					return null;
				}
				break;
			}
			builder.append((char)((char)(number)-hexOffset));
		}
		
		String nameAndScore = builder.toString();
		String[] splitString = nameAndScore.split("-");		
		
		return new HighScore(splitString[0], Long.parseLong(splitString[1]));
	}
	
}
