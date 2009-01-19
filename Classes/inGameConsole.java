package Classes;

import java.util.LinkedList;

import main.SuperMain;

/**
 * Represents the ingame console.
 * 
 * @author ladanz
 * 
 */
public class inGameConsole {
	LinkedList<String> text;
	String[] display_text;

	/**
	 * Initialize the displayed text.
	 */
	public inGameConsole() {
		text = new LinkedList<String>();
		display_text = new String[0];
	}

	/**
	 * Adds a line to the console.<br>
	 * Only 15 characters are allowed.<br>
	 * After 8 lines the console will cut the text.
	 * 
	 * @param text
	 *            The Text to append.
	 */
	public void addText(String text) {
		// nur 15 zeichen pro Zeile erlaubt
		if (text.length() > 15) {
			LinkedList<String> res = new LinkedList<String>();

			while (text.length() > 11) {
				String teil = "" + text.substring(0, 10);
				res.add(teil);
				text = text.substring(10);
			}
			if (text.length() > 0)
				res.add(text);
			this.text.addAll(res);

		} else {
			this.text.addLast(text);
		}

		if (this.text.size() > 8) {
			this.text.removeFirst();
		}
		display_text = this.text.toArray(new String[0]);
	}

	/**
	 * Render the console.
	 * 
	 * @param alpha
	 */
	public void render(double alpha) {
		myText.setSelected_text(SuperMain.TEXT_CONSOLE_BIG);
		myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);

		int count = 0;

		for (String s : display_text) {
			myText.out(s, new v3(0, count--, 0), new v3(1.5, .75, 1), new v3(1, 1, 1), alpha, 0);
		}

	}

}
