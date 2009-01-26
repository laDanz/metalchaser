package mechPeck.equipment;

import java.awt.Dimension;
import java.io.IOException;

import main.SuperMain;
import mechPeck.Equipment;
import Classes.Profil;
import Classes.Texture;

/**
 * Dynamic generation of equipment-drops using enemystats to calculate
 * 
 * @author incredibleJan
 * 
 */

public class Loot implements Equipment {

	private static final long serialVersionUID = 1L;
	transient Texture tex;
	public int type;

	int arms; // type 0
	private double Damage = 0;
	private double Piercing = 0;
	private double CriticalDamage = 0;

	int electronics; // type 1
	private double Sensors = 0;
	private double RocketLauncherAccuracy = 0;
	private double Stealth = 0;

	int hydraulics; // type 2
	private double Strength = 0;
	private double GatlingAccuracy = 0;
	private double Speed = 0;

	int mechanics; // type 3
	private double ReloadingTime = 0;
	private double Agility = 0;
	private double RateOfFire = 0;

	int protection; // type 4
	private double Armor = 0;
	private double CriticalHitProtection = 0;
	private int Health = 0;

	/**
	 * Random Equipment-drop Constructor 
	 * 
	 * @param Arms-Value of killed enemy
	 * @param Electronics-Value of killed enemy
	 * @param Hydraulics-Value of killed enemy
	 * @param Mechanics-Value of killed enemy
	 * @param Protection-Value of killed enemy
	 */
	public Loot(int Arms, int Electronics, int Hydraulics, int Mechanics, int Protection) {

		arms = Arms;
		electronics = Electronics;
		hydraulics = Hydraulics;
		mechanics = Mechanics;
		protection = Protection;

		type = findType();
		initEffect(type);

		initTexture();
	}
	
	/**
	 * Equipment-drop Constructor 
	 * 
	 * @param the Equipment-Type (e.g. Arms)
	 * @param option1 is the raise of the first skill of the defined Skill-Type (e.g. Damage)
	 * @param option2 is the raise of the second skill of the defined Skill-Type (e.g. Piercing)
	 * @param option3 is the raise of the third skill of the defined Skill-Type (e.g. CriticalDamage)
	 */
	public Loot(int Type, int option1, int option2, int option3) {

		type = Type;
		
		//Init Effect
		// type 0
		if(type == 0)
		{
		Damage = option1;
		Piercing = option2;
		CriticalDamage = option3;
		}
		
		if(type == 1)
		{
		// type 1
		Sensors = option1;
		RocketLauncherAccuracy = option2;
		Stealth = option3;
		}
		
		if(type == 2)
		{
		// type 2
		Strength = option1;
		GatlingAccuracy = option2;
		Speed = option3;
		}
		
		if(type == 3)
		{
		// type 3
		ReloadingTime = option1;
		Agility = option2;
		RateOfFire = option3;
		}
		
		if(type == 4)
		{
		// type 4
		Armor = option1;
		CriticalHitProtection = option2;
		Health = option3;
		}

		initTexture();
	}

	/**
	 * Loads and initiates the specific texture to represent the item in the bag
	 */
	public void initTexture() {
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					switch (type) {
					case 0:
						tex = SuperMain.loadTex("img/hud/equip/arms.jpg");
						break;
					case 1:
						tex = SuperMain.loadTex("img/hud/equip/electronics.jpg");
						break;
					case 2:
						tex = SuperMain.loadTex("img/hud/equip/hydraulics.jpg");
						break;
					case 3:
						tex = SuperMain.loadTex("img/hud/equip/mechanics.jpg");
						break;
					case 4:
						tex = SuperMain.loadTex("img/hud/equip/armor.jpg");
						break;
					default:
						tex = SuperMain.loadTex("img/hud/equip/no-pic.jpg");
					}
				} catch (IOException e) {

					e.printStackTrace();
					SuperMain.out(e);
				}
			}
		});

	}

	/**
	 * Calculates which item type will be generated
	 * 
	 * @return type of item
	 */
	private int findType() {

		int t = 0;
		boolean searching = true;

		while (searching) {
			if (arms > Math.random() * 100) {
				t = 0;
				searching = false;
			}
			if (electronics > Math.random() * 100) {
				t = 1;
				searching = false;
			}
			if (hydraulics > Math.random() * 100) {
				t = 2;
				searching = false;
			}
			if (mechanics > Math.random() * 100) {
				t = 3;
				searching = false;
			}
			if (protection > Math.random() * 100) {
				t = 4;
				searching = false;
			}
		}

		return t;

	}

	/**
	 * Calculates the bonuses given by the item of the specified type
	 * 
	 * @param itemtype t
	 */
	private void initEffect(int t) {

		switch (type) {
		case 0:
			for (int i = arms; i > 0; i--) {
				switch ((int) Math.round(Math.random() * 3 - 0.5)) {
				case 0:
					Damage++;
					break;
				case 1:
					Piercing++;
					break;
				case 2:
					CriticalDamage++;
				}
			}
			break;

		case 1:
			for (int i = electronics; i > 0; i--) {
				switch ((int) Math.round(Math.random() * 3 - 0.5)) {
				case 0:
					Sensors++;
					break;
				case 1:
					RocketLauncherAccuracy++;
					break;
				case 2:
					Stealth++;
				}
			}
			break;
		case 2:
			for (int i = hydraulics; i > 0; i--) {
				switch ((int) Math.round(Math.random() * 3 - 0.5)) {
				case 0:
					Strength++;
					break;
				case 1:
					GatlingAccuracy++;
					break;
				case 2:
					Speed++;
				}
			}
			break;
		case 3:
			for (int i = mechanics; i > 0; i--) {
				switch ((int) Math.round(Math.random() * 3 - 0.5)) {
				case 0:
					ReloadingTime++;
					break;
				case 1:
					Agility++;
					break;
				case 2:
					RateOfFire++;
				}
			}
			break;
		case 4:
			for (int i = protection; i > 0; i--) {
				switch ((int) Math.round(Math.random() * 3 - 0.5)) {
				case 0:
					Armor++;
					break;
				case 1:
					Health++;
					break;
				case 2:
					CriticalHitProtection++;
				}
			}
			break;
		}

	}

	/**
	 * Actual function that will give the upgrade-bonus to the mecha 
	 */
	public void doEffekt() {

		Profil.mecha.setTDamage(Profil.mecha.getTDamage() + Damage);
		Profil.mecha.setTPiercing(Profil.mecha.getTPiercing() + (double) Piercing);
		Profil.mecha.setTCriticalDamage(Profil.mecha.getTCriticalDamage() + CriticalDamage);

		Profil.mecha.setTSensors(Profil.mecha.getTSensors() + (int) Sensors);
		Profil.mecha.setTRocketLauncherAccuracy(Profil.mecha.getTRocketLauncherAccuracy() + RocketLauncherAccuracy);
		Profil.mecha.setTStealth(Profil.mecha.getTStealth() + Stealth);

		Profil.mecha.setTStrength(Profil.mecha.getTStrength() + (int) Strength);
		Profil.mecha.setTGatlingAccuracy(Profil.mecha.getTGatlingAccuracy() + GatlingAccuracy);
		Profil.mecha.setTSpeed(Profil.mecha.getTSpeed() + (int) Speed);
		
		Profil.mecha.setTReloadingTime(Profil.mecha.getTReloadingTime() + ReloadingTime);
		Profil.mecha.setTAgility(Profil.mecha.getTAgility() + (double) Agility);
		Profil.mecha.setTRateOfFire(Profil.mecha.getTRateOfFire() + (double) RateOfFire);

		Profil.mecha.setTHealth(Profil.mecha.getTHealth() + Health);
		Profil.mecha.setTArmor(Profil.mecha.getTArmor() + Armor);
		Profil.mecha.setTCriticalHitProtection(Profil.mecha.getTCriticalHitProtection() + CriticalHitProtection);

	}

	/**
	 * Get Item dimension
	 * @return standard dimension: 1,1
	 */
	public Dimension getEquipDimension() {

		return new Dimension(1, 1);
	}

	/**
	 * Get Texture
	 * @return equipment-texture
	 */
	public Texture getEquipTexture() {
		return tex;
	}

	/**
	 * Get equipment type
	 * @return equipment-type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Handles item-description on mouseover
	 * @return item description
	 */
	public String getMouseOverText() {
		String info;
		
		switch (type) {
		case 0: 
			info = "Arms Upgrade +" + arms;
			if (Damage > 0) info += "/n Damage +"+Damage;
			if (Piercing > 0) info += "/n Piercing +"+Piercing;
			if (CriticalDamage > 0) info += "/n Critical Damage +"+CriticalDamage;
			return info;
		case 1:
			info = "Electronics Upgrade +" + electronics;
			if (Sensors > 0) info += "/n Sensors +"+Sensors;
			if (RocketLauncherAccuracy > 0) info += "/n Rocketlauncher Accuracy +"+RocketLauncherAccuracy;
			if (Stealth > 0) info += "/n Stealth +"+Stealth;
			return info;
		case 2:
			info = "Hydraulics Upgrade +" + hydraulics;
			if (Strength > 0) info += "/n Strength +"+Strength;
			if (GatlingAccuracy > 0) info += "/n Gatling Accuracy +"+GatlingAccuracy;
			if (Speed > 0) info += "/n Speed +"+Speed;
			return info;
		case 3:
			info = "Mechanics Upgrade +" + mechanics;
			if (ReloadingTime > 0) info += "/n ReloadingTime +"+ReloadingTime;
			if (Agility > 0) info += "/n Agility +"+Agility;
			if (RateOfFire > 0) info += "/n Rate of Fire +"+RateOfFire;
			return info;
		case 4:
			info = "Protection Upgrade +" + protection;
			if (Health > 0) info += "/n Health +"+Health;
			if (Armor > 0) info += "/n Armor +"+Armor;
			if (CriticalHitProtection > 0) info += "/n Critical Hit Protection +"+CriticalHitProtection;
			return info;
		default:
			return "Unknonw Upgrade";
		}
	}

	/**
	 * Checks if the requirements to equip the equipment are fullfilled.<BR>
	 * Required is that the player has no item of the same type already equipped.
	 * @return fullfilled? 
	 */
	public boolean RequirementsFullfiled() {

		// aufpassen dass man sich nicht selbst mitzählt
		//int typeEquip = 0;

		for (Equipment eq : SuperMain.profil.mecha.bag.getEquippedItems()) {
			if (eq instanceof Loot && eq.getType() == type && eq != this)
				return false;
		}

		return true;

	}

	/**
	 * Returns the correct type name of the item, or "Unknown Upgrade"
	 */
	//@override
	public String toString() {
		switch (type) {
		case 0:
			return "Arms Upgr";
		case 1:
			return "Elec Upgr";
		case 2:
			return "Hydr Upgr";
		case 3:
			return "Mech Upgr";
		case 4:
			return "Prot Upgr";
		default:
			return "Unknown Upgr";
		}
	}

}
