package sock;

import java.io.Serializable;

import Classes.Objekt;

/**
 * For Multiplayer.<br>
 * Indicates which object schould be removed
 * 
 * @author ladanz
 * 
 */
public class ObjRemover implements Serializable {

	private static final long serialVersionUID = 1L;

	public Objekt obj;

	public ObjRemover(Objekt obj) {
		this.obj = obj;
	}

}
