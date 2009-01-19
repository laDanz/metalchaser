package Classes;

import java.io.BufferedWriter;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Represents the key bindings.
 * 
 * @author laDanz
 * 
 */
public class Steuerung {

	private HashMap<Integer, Key> belegung;

	public static final int MOVE_VOR = 1;
	public static final int MOVE_ZURUCK = 2;
	public static final int STRAVE_LINKS = 3;
	public static final int STRAVE_RECHTS = 4;
	public static final int DREH_LINKS = 5;
	public static final int DREH_RECHTS = 6;
	public static final int ROCKETL = 7;
	public static final int GATLING = 8;
	public static final int HEALTH_ANWENDEN = 9;
	public static final int NACHLADEN = 10;

	/**
	 * Constructor.<br>
	 * Sets the default keybinding.
	 */
	public Steuerung() {
		belegung = new HashMap<Integer, Key>();
		initBelegung();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param steuerung
	 */
	public Steuerung(Steuerung steuerung) {
		belegung = new HashMap<Integer, Key>();
		for (int option : new Integer[] { MOVE_VOR, MOVE_ZURUCK, STRAVE_LINKS, STRAVE_RECHTS, DREH_LINKS, DREH_RECHTS,
				ROCKETL, GATLING, HEALTH_ANWENDEN, NACHLADEN }) {
			setBelegung(option, steuerung.getBelegung(option));
		}
	}

	/**
	 * Get the key for a certain action.
	 * 
	 * @param identifier
	 *            The action identifier.
	 * @return A <code>Key</code>
	 */
	public Key getBelegung(int identifier) {
		return belegung.get(identifier);
	}

	/**
	 * Checks whether the key associated to a special action is pressed.
	 * 
	 * @param identifier
	 *            The action identifier.
	 * @return true if the key is pressed.
	 */
	public boolean isDown(int identifier) {
		Key k = belegung.get(identifier);
		if (k.isTastatur()) {
			return Keyboard.isKeyDown(k.getId());
		} else {
			return Mouse.isButtonDown(k.getId());
		}
	}

	/**
	 * Sets a key binding for a special action.
	 * 
	 * @param identifier
	 *            A action identifier.
	 * @param key
	 *            A <code>Key</code>
	 * @return The old <code>Key</code> associated with that action.
	 */
	public Key setBelegung(int identifier, Key key) {
		Key old = belegung.get(identifier);
		belegung.put(identifier, key);
		return old;
	}

	private void initBelegung() {
		setBelegung(Steuerung.DREH_LINKS, new Key(true, org.lwjgl.input.Keyboard.KEY_LEFT));
		setBelegung(Steuerung.DREH_RECHTS, new Key(true, org.lwjgl.input.Keyboard.KEY_RIGHT));

		setBelegung(Steuerung.GATLING, new Key(false, 0));
		setBelegung(Steuerung.ROCKETL, new Key(false, 1));

		setBelegung(Steuerung.MOVE_VOR, new Key(true, org.lwjgl.input.Keyboard.KEY_W));
		setBelegung(Steuerung.MOVE_ZURUCK, new Key(true, org.lwjgl.input.Keyboard.KEY_S));

		setBelegung(Steuerung.STRAVE_LINKS, new Key(true, org.lwjgl.input.Keyboard.KEY_A));
		setBelegung(Steuerung.STRAVE_RECHTS, new Key(true, org.lwjgl.input.Keyboard.KEY_D));

		setBelegung(Steuerung.HEALTH_ANWENDEN, new Key(true, org.lwjgl.input.Keyboard.KEY_Q));
		setBelegung(Steuerung.NACHLADEN, new Key(true, org.lwjgl.input.Keyboard.KEY_R));

	}

	/**
	 * Save the key bindings to a file.
	 * 
	 * @param bwriter
	 *            A <code>BuferedWriter</code> of the destination file.
	 */
	public void save(BufferedWriter bwriter) {
		try {
			bwriter.write("<Steuerung>");
			bwriter.newLine();

			for (int option : new Integer[] { MOVE_VOR, MOVE_ZURUCK, STRAVE_LINKS, STRAVE_RECHTS, DREH_LINKS,
					DREH_RECHTS, ROCKETL, GATLING }) {
				bwriter.write("<Taste" + option + ">");
				Key k = getBelegung(option);
				bwriter.write((k.isTastatur() ? "T" : "M") + "," + k.getId());
				bwriter.write("</Taste" + option + ">");
				bwriter.newLine();

			}
			bwriter.write("</Steuerung>");
			bwriter.newLine();
		} catch (Exception e) {

		}
	}

	/**
	 * Returns a human readable interpretation of an action identifier.
	 * 
	 * @param i
	 *            An action identifier.
	 * @return The description of the action.
	 */
	public static String getDescription(int i) {
		switch (i) {
		case STRAVE_LINKS:
			return "StrafeL";
		case STRAVE_RECHTS:
			return "StrafeR";
		case MOVE_VOR:
			return "Forwar.";
		case MOVE_ZURUCK:
			return "Backwa.";
		case ROCKETL:
			return "RocketL";
		case GATLING:
			return "Gatling";
		case DREH_LINKS:
			return "TurnL";
		case DREH_RECHTS:
			return "TurnR";
		case NACHLADEN:
			return "reload";
		case HEALTH_ANWENDEN:
			return "use health";
		default:
			return "Fehler";
		}
	}

	/**
	 * Binds an action to a key by parsing the key.toString() method.
	 * 
	 * @param id
	 *            An action identifier.
	 * @param s
	 *            The string from a <code>Key</code>s toString() method.
	 */
	public void setBelegung(int id, String s) {
		boolean isT = s.split(",")[0].contains("T");
		int id_ = Integer.parseInt(s.split(",")[1]);
		setBelegung(id, new Key(isT, id_));

	}

}
