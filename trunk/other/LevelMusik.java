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

	@Override
	public String getDescription() {
		return "Bistimmt den track der beim Level gespielt wird.";
	}

	@Override
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Musikfile";

		default:
			return "fehler";
		}
	}

	@Override
	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.STRING;

		default:
			return 0;
		}
	}

	@Override
	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return musik;

		default:
			return null;
		}
	}

	@Override
	public boolean isInWertebereich(int i, Object value) {

		return true;
	}

	@Override
	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			musik = (String) value;
			break;

		default:
			break;
		}
	}

	@Override
	public String toString() {
		return "LevelMusikObjekt";
	}
}
