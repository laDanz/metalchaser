import Classes.HighscoreEintrag;

public class another_test {

	public static void main(String[] args) throws InterruptedException {

		boolean b = HighscoreEintrag.writeToOnlineHighscore("ladanz", "1511");

		System.out.println((b ? "true" : "false"));
	}

}
