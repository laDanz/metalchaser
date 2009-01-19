package anim;

import java.io.File;

import main.SuperMain;
import Classes.Object3D;

/**
 * The skeleton management for the shocker.
 * 
 * @author laDanz
 * 
 */
public class SkelettShocker extends SkelettPusher {

	/**
	 * Default constructor.<br>
	 * Initializes the 3D models and loads the animations.
	 * 
	 * 
	 * @param id
	 *            The parent id.
	 */
	public SkelettShocker(int id) {
		super(id);
		this.name = "Shocker";

		// Modells laden
		setNieten(new Object3D(main.SuperMain.ordner + "models/data4/sub0/001.mco", "img/shocker/nieten.jpg"));

		body = new Object3D(main.SuperMain.ordner + "models/data4/sub0/003.mco", "img/shocker/rsmile.jpg");

		propellor = new Object3D(main.SuperMain.ordner + "models/data4/sub0/002.mco", "img/shocker/propeller.jpg");

		lufter = new Object3D(main.SuperMain.ordner + "models/data4/sub0/000.mco", "img/shocker/lufter.jpg");

		// kalib file vorhanden??? ---> laden

		File f = new File(SuperMain.ordner + "anim/" + name + "/kalib.xml");
		if (f.exists()) {
			loadAll(f.getAbsolutePath());
		}
	}
}
