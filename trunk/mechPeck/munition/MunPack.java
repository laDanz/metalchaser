package mechPeck.munition;

import java.awt.Dimension;
import java.io.IOException;

import main.SuperMain;
import mechPeck.Equipable;
import mechPeck.Munition;
import Classes.Texture;

/**
 * Generates an ammo-box containing gatling or rocketlauncher munition
 * @author danzi
 */
public class MunPack implements Equipable {

	private static final long serialVersionUID = 2L;
	public int amount = 0;
	public Munition mun;

	static Texture Ninemm = null;
	static Texture rock = null;

	/**
	 * Simple Constructor
	 * @param amount of munition
	 * @param type of munition (mun)
	 */
	public MunPack(int amount, Munition mun) {
		this.amount = amount;
		this.mun = mun;
		int i = 0;
		if (Ninemm == null)
			SuperMain.toRun.add(new Runnable() {
				public void run() {
					try {
						Ninemm = SuperMain.loadTex("img/hud/equip/mun.jpg");
						rock = SuperMain.loadTex("img/hud/equip/rocket.jpg");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();SuperMain.out(e);
					}

				}
			});

	}

	/**
	 * Simple Getter
	 * @return item-dimension (1,1)
	 */
	public Dimension getEquipDimension() {

		return new Dimension(1, 1);
	}

	/**
	 * Simple Getter
	 * @return item-type (6) 
	 */
	public int getType() {
		return 6;
	}

	/**
	 * Get description on mouseover:
	 * @return mouseover description
	 */
	public String getMouseOverText() {
		// TODO Auto-generated method stub
		return this.amount + "x" + mun.toString() + "/n  Damage: " + mun.getSchaden() + "/n  Piercing: "
				+ mun.getPiercing();
	}

	/**
	 * Simple Getter
	 * @return texture 
	 */
	public Texture getEquipTexture() {
		return (mun.getCaliber().equals("9mm") ? Ninemm : rock);

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// return this.amount + " " + mun.toString();
		//return "+ 9mm Mun";
		return (mun.getCaliber().equals("9mm") ? "+ Ammo" : "+ Rockets");
	}

}
