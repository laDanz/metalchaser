package mechPeck.equipment;

import java.awt.Dimension;
import java.io.IOException;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.Equipable;
import mechPeck.RechtsKlickAble;
import Classes.Texture;

/**
 * Class to generate health-package-drops
 * 
 * @author danzi
 * 
 */
public class HealthPackage implements Equipable, RechtsKlickAble {
	int value;
	transient Texture tex;

	/**
	 * Simple constructor to generate a healph-package of the specified
	 * health-value
	 * 
	 * @param value
	 *            of health
	 */
	public HealthPackage(int value) {
		this.value = value;

		initTexture();

	}

	/**
	 * Loads and initiates the specific texture to represent the item in the bag
	 */
	public void initTexture() {
		try {
			tex = SuperMain.loadTex("img/hud/equip/first_aid.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
	}

	/**
	 * Returns the item-type (5)
	 */
	public int getType() {
		return 5;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Logic to heal the player when a rightclick on the health-package is
	 * performed.<BR>
	 * Will only heal if the player health is not already full and will only
	 * heal until the health is full, or the health-package-value is zero.<BR>
	 * In the latter case the health-package will be removed from the bag.
	 */
	public void doRechtsKlickAction() {
		if (value <= 0) {
			removeMe();
			return;
		}

		int ueber = LevelPlay.p.hurt(-value);

		value = ueber;
		if (value <= 0) {
			removeMe();
		}

	}

	/**
	 * Removes the health-package from the bag
	 */
	private void removeMe() {
		SuperMain.profil.mecha.bag.remove(this);

	}

	/**
	 * Get item Dimension (1,1)
	 * 
	 * @return item-dimension
	 */
	public Dimension getEquipDimension() {
		// TODO Auto-generated method stub
		return new Dimension(1, 1);
	}

	/**
	 * Get the texture that represents the item in the bag.
	 * 
	 * @return item-texture
	 */
	public Texture getEquipTexture() {

		return tex;
	}

	/**
	 * Get the item-description on mouseover
	 * 
	 * @return <current value> + " Health"
	 */
	public String getMouseOverText() {
		// TODO Auto-generated method stub
		return value + " Health";
	}

	//@override
	public String toString() {
		// TODO Auto-generated method stub
		// return value + "er HPackage";
		return "+ Health";
	}

}
