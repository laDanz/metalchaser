package other;

import main.SuperMain;
import mechPeck.munition.MunPack;
import mechPeck.munition.Rockets;
import Classes.Objekt;
import drops.EquipDrop;

/**
 * Handles Logic for Drops that are from the Type Rocket
 * @author ladanz
 *
 */
public class RocketDrop extends master {

	// Option 1
	int amount;
	// Option 2
	int damage;
	// Option 3
	int piercing;
	
	boolean putted;

	/**
	 * Constructor
	 */
	public RocketDrop() {
		super();
		amount = 10;
		damage = 10;
		piercing = 1;
		
		putted = false;
	}

	@Override
	public String getDescription() {
		return "Ein Packet Raketen";
	}

	@Override
	public int getOptionCount() {
		return 3;
	}

	@Override
	public String getOptionDescription(int i) {
		switch(i){
			case 0: return "Amount";
			case 1: return "Damage";
			case 2: return "Piercing";
			default: return "Unknown Option";
		}
	}

	@Override
	public int getOptionType(int i) {
		return Objekt.INT;
	}

	@Override
	public Object getOptionValue(int i) {
		switch(i){
			case 0:	return amount;
			case 1: return damage;
			case 2: return piercing;
			default: return 0;
		}
	}

	@Override
	public void logic() {
		super.logic();

		if (!putted) {
			putted = SuperMain.addDrop(new EquipDrop(getPosition(), new MunPack(amount, new Rockets(damage, piercing))));
		}
	}

	@Override
	public void render() {
		super.render();

	}

	@Override
	public void setOptionValue(int i, Object value) {
		switch(i){
			case 0: amount = (Integer) value; break;
			case 1: damage = (Integer) value; break;
			case 2: piercing = (Integer) value; break;
			default : break;
		}
	}

	@Override
	public boolean isInWertebereich(int i, Object value) {
		return ((Integer) value) > 0;
	}

}
