package sock;

import java.io.Serializable;

/**
 * Placeholder for level sending.
 * 
 * @author ladanz
 * 
 */
public class LevelString implements Serializable {

	private static final long serialVersionUID = 1L;

	String s;
	int id;

	public LevelString(String s, int id) {
		this.s = s;
		this.id = id;
	}

}
