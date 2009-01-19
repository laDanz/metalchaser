package mechPeck.munition;

import mechPeck.Munition;
import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Class to generate rockets with specified damage and piercing values.
 * 
 * @author jan
 * 
 */
public class Rockets implements Munition {

	static Object3D obj = null;
	private int dmg;
	private int pierc;

	/**
	 * Generates a rocket with damage 10 and piercing 1.
	 */
	public Rockets() {

		this(10, 1);
	}

	/**
	 * Generates custom rocket
	 * 
	 * @param damage
	 * @param piercing
	 */
	public Rockets(int damage, int piercing) {

		if (obj == null)
			obj = new Object3D("models/data2/000.mco", "img/rakete/rakete.png");

		setSchaden(damage);
		setPiercing(piercing);

	}

	@Override
	public void setPiercing(int value) {
		this.pierc = value;
	}

	@Override
	public void setSchaden(int value) {
		this.dmg = value;

	}

	/**
	 * Simple Getter
	 * 
	 * @return caliber: Rockets
	 */
	public String getCaliber() {
		// TODO Auto-generated method stub
		return "Rockets";
	}

	/**
	 * Simple Getter
	 * 
	 * @return damage
	 */
	public int getSchaden() {
		return dmg;
	}

	/**
	 * Simple Getter
	 * 
	 * @return piercing
	 */
	public int getPiercing() {
		return pierc;
	}

	Vektor3D scale = new v3(1 / 10., 1 / 10., 1 / 10.);

	/**
	 * rendering utility-function
	 */
	public void render() {
		render(0);
	}

	/**
	 * rendering function, regards the angle of rotation
	 * 
	 * @param drehwinkel_in_rad
	 */
	public void render(double drehwinkel_in_rad) {
		double drehwinkel = Math.toDegrees(drehwinkel_in_rad) - 90;
		OGL.skaliere(scale);
		OGL.rot(drehwinkel, v3.y_axis);
		obj.render();
		OGL.rot(-drehwinkel, v3.y_axis);
		OGL.skaliere(scale.reziproke());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Rockets";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rockets) {
			Rockets other = (Rockets) obj;
			if (other.dmg == this.dmg && other.pierc == this.pierc) {
				return true;
			}
		}
		return false;
	}
}
