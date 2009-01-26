package other;

import main.SuperMain;
import mechPeck.equipment.Loot;
import mechPeck.munition.MunPack;
import mechPeck.munition.Rockets;
import Classes.Objekt;
import drops.EquipDrop;

/**
 * Handles Logic for Equipment-Drops
 * @author Jan
 *
 */
public class EquipmentDrop extends master {

	// Option 0
	int type;
	// Option 1
	int option1;
	// Option 2
	int option2;
	// Option 3
	int option3;

	
	boolean putted;

	/**
	 * Constructor
	 */
	public EquipmentDrop() {
		super();
		type = 0;
		option1 = 0;
		option2 = 0;
		option3 = 0;

		
		putted = false;
	}

	//@override
	public String getDescription() {
		return "Equipment Drop";
	}

	//@override
	public int getOptionCount() {
		return 3;
	}

	//@override
	public String getOptionDescription(int i) {
		switch(i){
			case 0: return "EquipType";
			case 1: return "Option 1";
			case 2: return "Option 2";
			case 3: return "Option 3";

			default: return "Unknown Option";
		}
	}

	//@override
	public int getOptionType(int i) {
		return Objekt.INT;
	}

	//@override
	public Object getOptionValue(int i) {
		switch(i){
			case 0: return type;
			case 1:	return option1;
			case 2: return option2;
			case 3: return option3;

			default: return 0;
		}
	}

	//@override
	public void logic() {
		super.logic();

		if (!putted) {
			putted = 			SuperMain.addDrop(new EquipDrop(position, new Loot(type, option1, option2, option3)));
		}
	}

	//@override
	public void render() {
		super.render();

	}

	//@override
	public void setOptionValue(int i, Object value) {
		switch(i){
			case 0: type = (Integer) value; break;
			case 1: option1 = (Integer) value; break;
			case 2: option2 = (Integer) value; break;
			case 3: option3 = (Integer) value; break;

			default : break;
		}
	}

	//@override
	public boolean isInWertebereich(int i, Object value) {
		return ((Integer) value) > -1;
	}

}


