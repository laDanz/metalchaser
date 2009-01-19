package other;

import Classes.Objekt;

/**
 * Provides Information for the levels like Descriptions etc.<br>
 * Will be displayed on the start of the Level
 * @author paule
 *
 */
public class LevelInfo extends master implements Objekt {

	// Option 1
	String text;

	/**
	 * Constructor
	 */
	public LevelInfo() {
		super();
		text = "";
	}

	public String getText() {
		return text;
	}

	@Override
	public String getDescription() {
		return "Bestimmt die Informationen, die angezeigt werden.";
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
			return "Text";

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
			return text;

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
			text = (String) value;
			break;

		default:
			break;
		}
	}

	@Override
	public String toString() {
		return "LevelInfoObjekt";
	}
}
