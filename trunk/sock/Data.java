package sock;

import java.io.Serializable;

import Classes.Objekt;
import Classes.Player;
import Classes.RotateAble;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Class wich provides in short form all necessary data about one object.
 * 
 * @author ladanz 12.08.2008
 */

public class Data implements Serializable {

	private static final long serialVersionUID = 1L;

	public int id;
	public Vektor3D pos;
	public double drehwinkel;
	public double huftwinkel;
	public int life;

	/**
	 * Default Constructor.
	 */
	public Data(Objekt o) {
		this.id = o.getID();
		this.pos = new v3(o.getPosition());
		if (o instanceof RotateAble) {
			drehwinkel = ((RotateAble) o).getDrehwinkel();
		} else if (o instanceof Player) {
			drehwinkel = ((Player) o).blickrichtung;
		} else {
			drehwinkel = 0;
		}
		if (o instanceof Player) {
			huftwinkel = ((Player) o).huftwinkel;
		} else {
			huftwinkel = 0;
		}
		if (o instanceof monster.master) {
			this.life = ((monster.master) o).Health;
		} else {
			this.life = 0;
		}
	}

	public Data(Objekt p, int id2) {
		this(p);
		this.id = id2;
	}

	//@override
	public boolean equals(Object obj) {
		if (obj instanceof Data) {
			Data other = ((Data) obj);
			if (this.drehwinkel == other.drehwinkel && this.life == other.life && this.id == other.id
					&& this.pos.equals(other.pos)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
