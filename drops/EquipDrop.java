package drops;

import java.util.LinkedList;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.Equipable;
import Classes.Vektor3D;

/**
 * Class packages EquipDrop
 * 
 * @author ladanz
 * 
 */
public class EquipDrop extends SchrottDrop {

	Equipable[] loot;
	boolean tryed_it = false;

	/**
	 * 
	 * @param pos
	 *            3D position
	 * @param equip
	 *            equipment
	 */
	public EquipDrop(Vektor3D pos, Equipable... equip) {
		super(pos);
		loot = new Equipable[0];

		LinkedList<Equipable> res = new LinkedList<Equipable>();
		for (Equipable o : equip) {
			res.add(o);
		}
		loot = res.toArray(new Equipable[0]);

		// DUDE masterfile already does this!
		// if (pickup == null)
		// pickup = new OpenAlClip("sound/pickup.ogg");

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
		// FIXME effekt bewirken
		LinkedList<Equipable> rest = new LinkedList<Equipable>();
		if (loot.length > 0)
			// LevelPlay.InGameConsole.addText("Equipment aufgesammelt");
			for (Equipable o : loot) {
				if (SuperMain.profil.mecha.bag.put(o)) {
					LevelPlay.InGameConsole.addText("" + o.toString());
				} else {
					LevelPlay.InGameConsole.addText("EQUIP VOLL");
					rest.add(o);
				}

			}
		LevelPlay.InGameConsole.addText("----------");

		loot = rest.toArray(new Equipable[0]);

		if (loot.length == 0) {
			SuperMain.removeDrop(this);
			pickup.play();
		}
	}
}
