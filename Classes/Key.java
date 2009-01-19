package Classes;

/**
 * Utility class for keybindings.
 * 
 * @author laDanz
 * 
 */
public class Key {
	private boolean isTastatur;
	private int id;

	/**
	 * Creates a new key.
	 * 
	 * @param isTastatur
	 *            true if it is on the keyboard, false if it is the mouse.
	 * @param id
	 *            ID for the key, from
	 *            <code>org.lwjgl.input.Keyboard.KEY_XXX</code>
	 */
	public Key(boolean isTastatur, int id) {

		this.isTastatur = isTastatur;
		this.id = id;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return Is it on the keyboard?
	 */
	public boolean isTastatur() {
		return isTastatur;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return <code>org.lwjgl.input.Keyboard.KEY_XXX</code> Key-code.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get a String representing this key.
	 */
	public String toString() {

		return (isTastatur ? "T:" : "M:")
				+ (isTastatur ? org.lwjgl.input.Keyboard.getKeyName(id) : (id == 0 ? "Li" : (id == 1 ? "Re" : "An")));
	}

}
