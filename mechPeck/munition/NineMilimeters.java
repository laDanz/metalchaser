package mechPeck.munition;

import mechPeck.Munition;
import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.v3;

public class NineMilimeters implements Munition {

	static Object3D obj = null;

	public NineMilimeters() {

		if (obj == null)
			obj = new Object3D("models/more_models/pig.obj", "img/test.bmp");// "images/marble.jpg"
	}

	public String getCaliber() {
		// TODO Auto-generated method stub
		return "9mm";
	}

	public int getSchaden() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void setPiercing(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSchaden(int value) {
		// TODO Auto-generated method stub

	}

	public int getPiercing() {
		// TODO Auto-generated method stub
		return 3;
	}

	Vektor3D scale = new v3(1 / 10., 1 / 10., 1 / 10.);

	public void render() {
		OGL.skaliere(scale);
		obj.render();
		OGL.skaliere(scale.reziproke());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "9mm Mun";
	}

	/**
	 * Checks for equality.<br>
	 * Returns true, when Piercing, Damage and Caliber is equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NineMilimeters) {
			NineMilimeters other = (NineMilimeters) obj;

			if (other.getPiercing() == this.getPiercing() && other.getSchaden() == this.getSchaden()
					&& other.getCaliber().equals(this.getCaliber())) {
				return true;
			}

		}
		return false;
	}
}
