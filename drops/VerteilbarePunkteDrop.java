package drops;

import main.LevelPlay;
import main.SuperMain;
import Classes.OpenAlClip;
import Classes.Vektor3D;

/**
 * Drop which contains status points
 * 
 * @author ladanz
 * 
 */
public class VerteilbarePunkteDrop extends SchrottDrop {

	int value;

	/**
	 * ammount of statuspoints
	 * 
	 * @param pos
	 *            position of drop
	 * @param anzahl_punkte
	 *            ammount of points
	 */
	public VerteilbarePunkteDrop(Vektor3D pos, int anzahl_punkte) {
		super(pos);
		value = anzahl_punkte;
		if (pickup == null)
			pickup = new OpenAlClip("sound/pickup.ogg");
	}

	public void logic() {
		Vektor3D diff = position.add(LevelPlay.p.getPosition().mal(-1));

		if (diff.length() < 1) {
			aufsammeln();
		}

	}

	/**
	 * logic of what happens when the drop is picked up
	 */
	private void aufsammeln() {
		// effekt wirken
		SuperMain.profil.mecha.setVerteilbare_punkte(value + SuperMain.profil.mecha.getVerteilbare_punkte());

		if (value > 0)
			// LevelPlay.InGameConsole.addText(value+" neue Punkte verfügbar");
			LevelPlay.InGameConsole.addText(value + " Stat-Pts");
		value = 0;
		SuperMain.removeDrop(this);
		pickup.play();

	}

}
