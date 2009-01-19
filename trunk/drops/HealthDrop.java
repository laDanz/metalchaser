package drops;

import main.LevelPlay;
import main.SuperMain;
import Classes.OpenAlClip;
import Classes.Vektor3D;

/**
 * Functions for drops of Healthpackages
 * 
 * @author ladanz
 * 
 */
public class HealthDrop extends SchrottDrop {

	int value;
	boolean tryed_it = false;

	public HealthDrop(int how_much, Vektor3D pos) {
		super(pos);
		value = how_much;
		
		if (pickup == null)
			pickup = new OpenAlClip("sound/pickup.ogg");
	}

	/**
	 * Reaction that follows a drop
	 */
	public void logic() {
		Vektor3D diff = position.add(LevelPlay.p.getPosition().mal(-1));

		if (diff.length() < 3) {
			if (!tryed_it) {
				aufsammeln();
				tryed_it = true;
			}
		} else {
			tryed_it = false;
		}

	}

	/**
	 * Pick up a item
	 */
	private void aufsammeln() {
		if (value <= 0)
			return;
		int ueber = LevelPlay.p.hurt(-value);
		if ((value - ueber) > 0)
			// LevelPlay.InGameConsole.addText("Health: "+(value-ueber)+"
			// aufgesammelt");
			value = ueber;
		if (value <= 0) {
			SuperMain.removeDrop(this);
			pickup.play();
		}

	}
}
