package mechPeck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import main.SuperMain;
import mechPeck.equipment.HealthPackage;
import mechPeck.munition.Ammo;
import mechPeck.munition.MunPack;
import mechPeck.munition.Rockets;
import mechPeck.weapons.MachineGun;
import mechPeck.weapons.RocketLauncher;
import Classes.OpenAlClip;
import anim.SkelettZ300;

/**
 * Class Handling Stats, Stat-Points, Health
 * 
 * @author danzi & jan
 * 
 */
public class Mecha implements Serializable {

	// objecte die transient werden nicht gespeichert
	private static final long serialVersionUID = 1L;

	transient public Waffe linkeWaffe;
	transient public Waffe rechteWaffe;

	public Bag bag;

	transient JetPack Jetpack;

	transient public SkelettZ300 skellet;

	/* Stats */
	private int verteilbare_punkte = 0;

	// Arms
	private double Damage, Piercing, CriticalDamage;
	// Electronics
	private double Sensors, RocketLauncherAccuracy, Stealth;
	// Hydraulics
	private double Strength, GatlingAccuracy, Speed;
	// Mechanics
	private double ReloadingTime, Agility, RateOfFire;
	// Protection
	private double armor, CriticalHitProtection;
	private int health;

	// Variablen fuer temporaere Werte
	private double TPiercing, TDamage, TCriticalDamage;

	private int TSensors;
	private double TRocketLauncherAccuracy, TStealth;

	private int TStrength;
	private double TGatlingAccuracy, TReloadingTime;

	private int TSpeed;
	private double TAgility, TRateOfFire;

	private int Thealth;
	private double Tarmor, TCriticalHitProtection;

	public static String new_state;

	public int gesamt_punkt_zahl;

	static OpenAlClip reload, heal;

	/**
	 * Initialises Mecha with weapons, bag, stats and animations
	 */
	public Mecha() {
		// erstmal initialisierung mit standard ausrstung
		// FIXME er ldt gleich waffen und damit mun und da sind projektile
		// schweine und das dauert lange
		linkeWaffe = new RocketLauncher();
		rechteWaffe = new MachineGun();

		skellet = new SkelettZ300("Z300");
		bag = new Bag();

		initStats();
		deleteTempValues();

		if (reload == null)
			reload = new OpenAlClip("sound/reload.ogg");

		if (heal == null)
			heal = new OpenAlClip("sound/heal.ogg");
	}

	/**
	 * Initialises "nonloaded" objects, meaning weapons and animations
	 */
	public void initNonLoadedObjects() {
		linkeWaffe = new RocketLauncher();
		rechteWaffe = new MachineGun();

		skellet = new SkelettZ300("Z300");
	}

	/**
	 * Checks if weapon w is on the left side, which would be the rocketlauncher
	 * 
	 * @param w
	 * @return is left?
	 */
	public boolean isLinkeWaffe(Waffe w) {
		return (w == linkeWaffe);
	}

	/**
	 * Simple Getter
	 * 
	 * @return number of slots
	 */
	public int getEquipSlots() {
		return 5;
	}

	/**
	 * Increase Arms
	 * 
	 * @param by
	 */
	public void incArms(int by) {
		Damage += by;
		Piercing += by;
		CriticalDamage += by;
	}

	/**
	 * Decrease Arms
	 * 
	 * @param by
	 */
	public void decArms(int by) {
		Damage -= by;
		Piercing -= by;
		CriticalDamage -= by;
	}

	/**
	 * Set Arms to value
	 * 
	 * @param value
	 */
	public void setArms(int value) {
		Damage = value;
		Piercing = value;
		CriticalDamage = value;
	}

	/**
	 * Sets Additional Arms Points to value
	 * 
	 * @param value
	 */
	public void setAdditionalArmsTemporary(int value) {
		TDamage = value;
		TPiercing = value;
		TCriticalDamage = value;
	}

	/**
	 * Get Arms value
	 * 
	 * @return Arms value
	 */
	public int getArms() {
		return (int) ((Damage + Piercing + CriticalDamage + TDamage + TPiercing + TCriticalDamage) / 3.);
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setElectronics(int value) {
		Sensors = value;
		RocketLauncherAccuracy = value;
		Stealth = value;
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setAdditionalElectronicsTemporary(int value) {
		TSensors = value;
		TRocketLauncherAccuracy = value;
		TStealth = value;
	}

	/**
	 * Increase electronics
	 * 
	 * @param by
	 */
	public void incElectronics(int by) {
		Sensors += by;
		RocketLauncherAccuracy += by;
		Stealth += by;
	}

	/**
	 * Decrease electronics
	 * 
	 * @param by
	 */
	public void decElectronics(int by) {
		Sensors -= by;
		RocketLauncherAccuracy -= by;
		Stealth -= by;
	}

	/**
	 * Simple Getter
	 * 
	 * @return electronics-value
	 */
	public int getElectronics() {
		return (int) ((Sensors + RocketLauncherAccuracy + Stealth + TSensors + TRocketLauncherAccuracy + TStealth) / 3.);
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setHydraulics(int value) {
		Strength = value;
		GatlingAccuracy = value;
		Speed = value;
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setAdditionalHydraulicsTemporary(int value) {
		TStrength = value;
		TGatlingAccuracy = value;
		TSpeed = value;
	}

	/**
	 * Increase hydraulics
	 * 
	 * @param by
	 */
	public void incHydraulics(int by) {
		Strength += by;
		GatlingAccuracy += by;
		Speed += by;
	}

	/**
	 * Decrease hydraulics
	 * 
	 * @param by
	 */
	public void decHydraulics(int by) {
		Strength -= by;
		GatlingAccuracy -= by;
		Speed -= by;
	}

	/**
	 * Simple Getter
	 * 
	 * @return hydraulics-value
	 */
	public int getHydraulics() {

		return (int) ((Strength + GatlingAccuracy + Speed + TStrength + TGatlingAccuracy + TSpeed) / 3.);
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setMechanics(int value) {
		ReloadingTime = value;
		Agility = value;
		RateOfFire = value;
		linkeWaffe.setMechRoF(value);
		rechteWaffe.setMechRoF(value);
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setAdditionalMechanicsTemporary(int value) {
		TReloadingTime = value;
		TAgility = value;
		TRateOfFire = value;
		linkeWaffe.setMechTRoF(value);
		rechteWaffe.setMechTRoF(value);
	}

	/**
	 * Increase mechanics
	 * 
	 * @param by
	 */
	public void incMechanics(int by) {
		ReloadingTime += by;
		Agility += by;
		RateOfFire += by;
		linkeWaffe.setMechRoF(linkeWaffe.getMechRoF() + by);
		rechteWaffe.setMechRoF(linkeWaffe.getMechRoF() + by);
	}

	/**
	 * Decrease mechanics
	 * 
	 * @param by
	 */
	public void decMechanics(int by) {
		ReloadingTime -= by;
		Agility -= by;
		RateOfFire -= by;
		linkeWaffe.setMechRoF(linkeWaffe.getMechRoF() - by);
		rechteWaffe.setMechRoF(linkeWaffe.getMechRoF() - by);
	}

	/**
	 * Simple Getter
	 * 
	 * @return mechanics-value
	 */
	public int getMechanics() {
		return (int) ((ReloadingTime + Agility + RateOfFire + TSpeed + TAgility + TRateOfFire) / 3.);
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setProtection(int value) {
		health = value * 5;
		armor = value;
		CriticalHitProtection = value;
	}

	/**
	 * Simple Setter
	 * 
	 * @param value
	 */
	public void setAdditionalProtectionTemporary(int value) {
		Thealth = value * 5;
		Tarmor = value;
		TCriticalHitProtection = value;
	}

	/**
	 * Increase protection
	 * 
	 * @param by
	 */
	public void incProtection(int by) {
		health += by * 5;
		armor += by;
		CriticalHitProtection += by;
	}

	/**
	 * Decrease protection
	 * 
	 * @param by
	 */
	public void decProtection(int by) {
		health -= by * 5;
		armor -= by;
		CriticalHitProtection -= by;
	}

	/**
	 * Simple Getter
	 * 
	 * @return protection-value
	 */
	public int getProtection() {
		return (int) ((health / 5 + armor + CriticalHitProtection + Thealth + Tarmor + TCriticalHitProtection) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return health-value
	 */
	public int getHealth() {
		return health + Thealth;
	}

	/**
	 * Simple Getter
	 * 
	 * @return damage-value
	 */
	public double getDamage() {
		return Damage + TDamage;
	}

	/**
	 * Simple Getter
	 * 
	 * @return piercing-value
	 */
	public double getPiercing() {
		return Piercing + TPiercing;
	}

	/**
	 * Simple Getter
	 * 
	 * @return CriticalDamage-value
	 */
	public double getCriticalDamage() {
		return CriticalDamage + TCriticalDamage;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Sensors-value
	 */
	public double getSensors() {
		return Sensors + TSensors;
	}

	/**
	 * Simple Getter
	 * 
	 * @return RocketLauncherAccuracy-value
	 */
	public double getRocketLauncherAccuracy() {
		return RocketLauncherAccuracy + TRocketLauncherAccuracy;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Stealth-value
	 */
	public double getStealth() {
		return Stealth + TStealth;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Strength-value
	 */
	public double getStrength() {
		return Strength + TStrength;
	}

	/**
	 * Simple Getter
	 * 
	 * @return GatlingAccuracy-value
	 */
	public double getGatlingAccuracy() {

		return GatlingAccuracy + TGatlingAccuracy;
	}

	/**
	 * Simple Getter
	 * 
	 * @return ReducedFallingDamage-value
	 */
	public double getReloadingTime() {
		return ReloadingTime + TReloadingTime;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Speed-value
	 */
	public double getSpeed() {
		return Speed + TSpeed;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Agility-value
	 */
	public double getAgility() {
		return Agility + TAgility;
	}

	/**
	 * Simple Getter
	 * 
	 * @return RateOfFire-value
	 */
	public double getRateOfFire() {
		return RateOfFire + TRateOfFire;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Armor-value
	 */
	public double getArmor() {
		return armor + Tarmor;
	}

	/**
	 * Simple Getter
	 * 
	 * @return CriticalHitProtection-value
	 */
	public double getCriticalHitProtection() {
		return CriticalHitProtection + TCriticalDamage;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Piercing-value
	 */
	public double getTPiercing() {
		return TPiercing;
	}

	/**
	 * Simple Setter
	 * 
	 * @param piercing
	 */
	public void setTPiercing(double piercing) {
		TPiercing = piercing;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Damage-value
	 */
	public double getTDamage() {
		return TDamage;
	}

	/**
	 * Simple Setter
	 * 
	 * @param damage
	 */
	public void setTDamage(double damage) {
		TDamage = damage;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional CriticalDamage-value
	 */
	public double getTCriticalDamage() {
		return TCriticalDamage;
	}

	/**
	 * Simple Setter
	 * 
	 * @param criticalDamage
	 */
	public void setTCriticalDamage(double criticalDamage) {
		TCriticalDamage = criticalDamage;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Sensors-value
	 */
	public int getTSensors() {
		return TSensors;
	}

	/**
	 * Simple Setter
	 * 
	 * @param sensors
	 */
	public void setTSensors(int sensors) {
		TSensors = sensors;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional RocketLauncherAccuracy-value
	 */
	public double getTRocketLauncherAccuracy() {
		return TRocketLauncherAccuracy;
	}

	/**
	 * Simple Setter
	 * 
	 * @param rocketLauncherAccuracy
	 */
	public void setTRocketLauncherAccuracy(double rocketLauncherAccuracy) {
		TRocketLauncherAccuracy = rocketLauncherAccuracy;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Stealth-value
	 */
	public double getTStealth() {
		return TStealth;
	}

	/**
	 * Simple Setter
	 * 
	 * @param stealth
	 */
	public void setTStealth(double stealth) {
		TStealth = stealth;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Strength-value
	 */
	public int getTStrength() {
		return TStrength;
	}

	/**
	 * Simple Setter
	 * 
	 * @param strength
	 */
	public void setTStrength(int strength) {
		TStrength = strength;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional GatlingAccuracy-value
	 */
	public double getTGatlingAccuracy() {
		return TGatlingAccuracy;
	}

	/**
	 * Simple Setter
	 * 
	 * @param gatlingAccuracy
	 */
	public void setTGatlingAccuracy(double gatlingAccuracy) {
		TGatlingAccuracy = gatlingAccuracy;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional ReloadingTime-value
	 */
	public double getTReloadingTime() {
		return TReloadingTime;
	}

	/**
	 * Simple Setter
	 * 
	 * @param ReloadingTime
	 */
	public void setTReloadingTime(double reloadingTime) {
		TReloadingTime = reloadingTime;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Speed-value
	 */
	public int getTSpeed() {
		return TSpeed;
	}

	/**
	 * Simple Setter
	 * 
	 * @param speed
	 */
	public void setTSpeed(int speed) {
		TSpeed = speed;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Agility-value
	 */
	public double getTAgility() {
		return TAgility;
	}

	/**
	 * Simple Setter
	 * 
	 * @param agility
	 */
	public void setTAgility(double agility) {
		TAgility = agility;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional RateOfFire-value
	 */
	public double getTRateOfFire() {
		return TRateOfFire;
	}

	/**
	 * Simple Setter
	 * 
	 * @param rateOfFire
	 */
	public void setTRateOfFire(double rateOfFire) {
		TRateOfFire = rateOfFire;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Health-value
	 */
	public int getTHealth() {
		return Thealth;
	}

	/**
	 * Simple Setter
	 * 
	 * @param thealth
	 */
	public void setTHealth(int thealth) {
		Thealth = thealth;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional Armor-value
	 */
	public double getTArmor() {
		return Tarmor;
	}

	/**
	 * Simple Setter
	 * 
	 * @param tarmor
	 */
	public void setTArmor(double tarmor) {
		Tarmor = tarmor;
	}

	/**
	 * Simple Getter
	 * 
	 * @return Additional CriticalHitProtection-value
	 */
	public double getTCriticalHitProtection() {
		return TCriticalHitProtection;
	}

	/**
	 * Simple Setter
	 * 
	 * @param criticalHitProtection
	 */
	public void setTCriticalHitProtection(double criticalHitProtection) {
		TCriticalHitProtection = criticalHitProtection;
	}

	/**
	 * Deletes Temporary Stat-Values
	 */
	public void deleteTempValues() {
		setAdditionalArmsTemporary(0);
		setAdditionalElectronicsTemporary(0);
		setAdditionalHydraulicsTemporary(0);
		setAdditionalMechanicsTemporary(0);
		setAdditionalProtectionTemporary(0);

	}

	/**
	 * Initialises starting mecha stats
	 */
	public void initStats() {
		gesamt_punkt_zahl = 0;
		verteilbare_punkte = 10;
		deleteTempValues();
		setArms(3);
		setElectronics(4);
		setHydraulics(5);
		setMechanics(4);
		setProtection(5);
	}

	/**
	 * Simple Getter
	 * 
	 * @return verteilbare_punkte -value
	 */
	public int getVerteilbare_punkte() {
		return verteilbare_punkte;
	}

	/**
	 * Simple Setter
	 * 
	 * @param verteilbare_punkte
	 */
	public void setVerteilbare_punkte(int verteilbare_punkte) {
		this.verteilbare_punkte = verteilbare_punkte;
	}

	/**
	 * Simple Getter
	 * 
	 * @return gewicht -value
	 */
	double getGewicht() {
		return (linkeWaffe.getGewicht() + rechteWaffe.getGewicht() + Jetpack.getGewicht());

	}

	/**
	 * Renders Animations
	 */
	public void render() {
		skellet.render();
		// FIXME gerendert wird über player klasse ??
	}

	/**
	 * Loads Mecha from current profile
	 * 
	 * @return Mecha-Configuration
	 */
	public static Mecha loadAsObject() {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + "mecha.cfgo");
		ObjectInputStream bw;
		try {
			if (!f.exists())
				f.createNewFile();
			bw = new ObjectInputStream(new FileInputStream(f));
			Mecha mecha = (Mecha) bw.readObject();
			new_state = (String) bw.readObject();
			bw.close();
			return mecha;
		} catch (IOException e) {
			System.err.println("Mecha konfig (Object) laden fehlgeschlagen: " + e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
			return null;
		}
	}

	/**
	 * Deletes Mecha-Save
	 * 
	 * @return is deleted?
	 */
	public static boolean deleteMechaSave() {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/mecha.cfgo");
		boolean b = f.delete();
		return b;
	}

	/**
	 * Saves current Mecha as object in profile folder.<br>
	 * Also writes the next state for the LevelPlay module.
	 * 
	 * @return is saved?
	 */
	public boolean saveAsObject(String new_state) {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + "mecha.cfgo");
		ObjectOutputStream bw;
		try {
			if (!f.exists())
				f.createNewFile();
			bw = new ObjectOutputStream(new FileOutputStream(f));
			bw.writeObject(this);
			bw.writeObject(new_state);
			bw.close();
			// Nötig?
			// bag.saveAsObject();
		} catch (IOException e) {
			System.err.println("Mecha konfig (object) speichern fehlgeschlagen: " + e.getMessage());
			e.printStackTrace();
			SuperMain.out(e);
			return false;
		}
		return true;

	}

	/**
	 * Saves current mecha-configuration as config-file in profile folder
	 * 
	 * @return is saved?
	 */
	public boolean saveConfig() {

		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + "mecha.cfg");
		BufferedWriter bw;
		try {
			if (!f.exists())
				f.createNewFile();
			bw = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			System.err.println("Mecha konfig speichern fehlgeschlagen: " + e.getMessage());
			return false;
		}

		try {
			bw.write("<MechaConfig>");

			bw.newLine();
			bw.write("<VerfugbarePunkte>");
			bw.write(getVerteilbare_punkte() + "");
			bw.write("</VerfugbarePunkte>");
			bw.newLine();
			bw.write("<Arms>");
			bw.write(getArms() + "");
			bw.write("</Arms>");
			bw.newLine();
			bw.write("<Electronics>");
			bw.write(getElectronics() + "");
			bw.write("</Electronics>");
			bw.newLine();
			bw.write("<Hydraulics>");
			bw.write(getHydraulics() + "");
			bw.write("</Hydraulics>");
			bw.newLine();
			bw.write("<Mechanics>");
			bw.write(getMechanics() + "");
			bw.write("</Mechanics>");
			bw.newLine();
			bw.write("<Protection>");
			bw.write(getProtection() + "");
			bw.write("</Protection>");
			bw.newLine();

			bag.save(bw);

			bw.write("</MechaConfig>");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
		return true;
	}

	public void health_anwenden() {
		// bag durchgehen und nach health suchen
		for (Equipable e : bag.getItems()) {
			if (e instanceof HealthPackage) {
				((HealthPackage) e).doRechtsKlickAction();
				heal.play();
			}
		}

	}

	public void nachladen() {
		nachladen(true, true);
	}

	public void nachladen(final boolean waitingtime, final boolean playsound) {

		new Thread() {

			// @override
			public void run() {
				super.run();
				// warten in Abhängigkeit von skill
				if (playsound) {
					reload.play();
				}
				try {
					if (waitingtime) {
						this.sleep((int) (10000 / getReloadingTime()));
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				reload.stop();

				// machine gun
				Equipable mun = null;
				int damage = 0;
				// stärkste raussuchen
				for (Equipable e : bag.getItems()) {
					if (e instanceof MunPack) {
						if (((MunPack) e).mun.getSchaden() > damage && ((MunPack) e).mun instanceof Ammo) {
							damage = ((MunPack) e).mun.getSchaden();
							mun = e;
						}
					}
				}
				if (mun != null)
					bag.tryToPutInWeaponSlot(mun);

				// rocket launcha
				mun = null;
				damage = 0;
				// stärkste raussuchen
				for (Equipable e : bag.getItems()) {
					if (e instanceof MunPack) {
						if (((MunPack) e).mun.getSchaden() > damage && ((MunPack) e).mun instanceof Rockets) {
							damage = ((MunPack) e).mun.getSchaden();
							mun = e;
						}
					}
				}
				if (mun != null)
					bag.tryToPutInWeaponSlot(mun);

			}
		}.start();
	}
}
