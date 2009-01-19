package other;

import Classes.Objekt;

/**
 * Handels Logic that implements the Music into the Level
 * @author paule
 *
 */
public class LevelMusik extends master implements Objekt {

	// Option 1
	String musik;

	/**
	 * Constructor
	 */
	public LevelMusik() {
		super();
		musik = "";

	}

	/**
	 * Simple Getter
	 * @return
	 */
	public String getMusikFile() {
		return musik;
	}

	//@override
	public String getDescription() {
		return "Bistimmt den track der beim Level gespielt wird.";
	}

	//@override
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	//@override
	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Musikfile";

		default:
			return "fehler";
		}
	}

	//@override
	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.STRING;

		default:
			return 0;
		}
	}

	//@override
	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return musik;

		default:
			return null;
		}
	}

	//@override
	public boolean isInWertebereich(int i, Object value) {

		return true;
	}

	//@override
	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			musik = (String) value;
			break;

		default:
			break;
		}
	}

	//@override
	public String toString() {
		return "LevelMusikObjekt";
	}
}
