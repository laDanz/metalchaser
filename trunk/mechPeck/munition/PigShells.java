package mechPeck.munition;

import mechPeck.Munition;
import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Generates funky pig-shell Ammunition for the rocketlauncher
 * 
 * @author jan
 * 
 */
public class PigShells implements Munition {

	static Object3D obj = null;

	/**
	 * Simple constructor
	 */
	public PigShells() {

		if (obj == null)
			obj = new Object3D("models/more_models/pig.obj", "img/textures/skin-pink.jpg");
	}

	/**
	 * Simple Getter
	 * 
	 * @return caliber: pigs
	 */
	public String getCaliber() {
		// TODO Auto-generated method stub
		return "9mm";
	}

	/**
	 * Simple Getter
	 * 
	 * @return damage: 100
	 */
	public int getSchaden() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public void setPiercing(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSchaden(int value) {
		// TODO Auto-generated method stub

	}

	/**
	 * Simple Getter
	 * 
	 * @return piercing: 10
	 */
	public int getPiercing() {
		// TODO Auto-generated method stub
		return 10;
	}

	Vektor3D scale = new v3(1 / 10., 1 / 10., 1 / 10.);

	/**
	 * Renderfunction
	 */
	public void render() {
		OGL.skaliere(scale);
		obj.render();
		OGL.skaliere(scale.reziproke());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Pig Shells";
	}

}
