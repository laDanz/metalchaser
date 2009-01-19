package mechPeck.munition;

import mechPeck.Munition;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Class to generate Gatling-Ammunition with specified damage and piercing
 * values.
 * 
 * @author jan
 * 
 */
public class Ammo implements Munition {

	static Object3D obj = null;
	private int dmg;
	private int pierc;

	/**
	 * Simple constructor. Generates gatling-ammo with damage and piercing 1
	 */
	public Ammo() {

		this(2, 1);

	}

	/**
	 * Generates Ammo with custom damage and piercing
	 * 
	 * @param damage
	 * @param piercing
	 */
	public Ammo(int damage, int piercing) {

		setSchaden(damage);
		setPiercing(piercing);

	}

	//@override
	public void setPiercing(int value) {
		this.pierc = value;
	}

	//@override
	public void setSchaden(int value) {
		this.dmg = value;

	}

	/**
	 * Simple Getter
	 * 
	 * @return caliber
	 */
	public String getCaliber() {
		// TODO Auto-generated method stub
		return "9mm";
	}

	/**
	 * Simple Getter
	 * 
	 * @return damage
	 */
	public int getSchaden() {
		// TODO Auto-generated method stub
		return dmg;
	}

	/**
	 * Simple Getter
	 * 
	 * @return piercing
	 */
	public int getPiercing() {
		// TODO Auto-generated method stub
		return pierc;
	}

	Vektor3D scale = new v3(1 / 10., 1 / 10., 1 / 10.);

	public void render() {

	}

	//@override
	public String toString() {
		// TODO Auto-generated method stub
		return "Gatling Ammo";
	}

	//@override
	public boolean equals(Object obj) {
		if (obj instanceof Ammo) {
			Ammo other = (Ammo) obj;
			if (other.dmg == this.dmg && other.pierc == this.pierc) {
				return true;
			}
		}
		return false;
	}

}
