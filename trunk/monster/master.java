package monster;

import java.io.IOException;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.equipment.HealthPackage;
import mechPeck.equipment.Loot;
import mechPeck.munition.Ammo;
import mechPeck.munition.MunPack;
import mechPeck.munition.Rockets;

import org.lwjgl.opengl.GL11;

import Classes.ExplodingParticles;
import Classes.OGL;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;
import drops.EquipDrop;

/**
 * Monster-Master-Class. Specifies all needed functions and variables for an
 * enemy.
 * 
 * @author jan
 * 
 */
public class master implements Objekt {

	int id;
	Vektor3D position;
	Texture ico, healthbar;
	long last_time_hurt = 0;
	OpenAlClip death, crit;

	/* Stats */
	public double Damage, Piercing, CriticalDamage; // Arms
	public double Sensors, RocketLauncherAccuracy, Stealth; // Electronics
	public double Strength, GatlingAccuracy, Speed; // Hydraulics
	public double ReloadTime, Agility, RateOfFire; // Mechanics
	public double Armor, CriticalHitProtection; // Protection
	public int Health, Max_Health;

	/**
	 * Constructor
	 */
	public master() {

		id = SuperMain.genId();
		position = new v3(0, 0, 0);
		initStats();
		try {
			ico = SuperMain.loadTex("img/test.bmp");
			healthbar = SuperMain.loadTex("img/hud/healthbar.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
	}

	/**
	 * Simple Getter
	 * 
	 * @return arms-value
	 */
	public int getArms() {
		return (int) Math.round((Damage + Piercing + CriticalDamage) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return electronics-value
	 */
	public int getElectronics() {
		return (int) Math.round((Sensors + RocketLauncherAccuracy + Stealth) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return hydraulics-value
	 */
	public int getHydraulics() {
		return (int) Math.round((Strength + GatlingAccuracy + Speed) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return mechanics-value
	 */
	public int getMechanics() {
		return (int) Math.round((ReloadTime + Agility + RateOfFire) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return protection-value
	 */
	public int getProtection() {
		return (int) Math.round((Max_Health / 5 + Armor + CriticalHitProtection) / 3.);
	}

	/**
	 * Simple Getter
	 * 
	 * @return Armor-value
	 */
	public int getArmor() {
		return (int) Armor;
	}

	/**
	 * Simple Getter
	 * 
	 * @return CriticalHitProtection
	 */
	public int getCriticalHitProtection() {
		return (int) CriticalHitProtection;
	}

	/**
	 * Simple Getter
	 * 
	 * @return monster-level
	 */
	public int getLevel() {
		double level = (Damage + Piercing + CriticalDamage) + (Sensors + RocketLauncherAccuracy + Stealth)
				+ (Strength + GatlingAccuracy + Speed) + (ReloadTime + Agility + RateOfFire)
				+ (Max_Health / 5 + Armor + CriticalHitProtection);

		return (int) level / 15;
	}

	/**
	 * Initializes a Level 1 Monster<br>
	 * 15 points will be allotted to 15 categories (not more than 15!)
	 */
	public void initStats() {
		// Arms
		Damage = 0;
		Piercing = 0;
		CriticalDamage = 0;

		// Electronics
		Sensors = 2;
		RocketLauncherAccuracy = 0;
		Stealth = 1;

		// Hydraulics
		Strength = 3;
		GatlingAccuracy = 0;
		Speed = 0;

		// Mechanics
		ReloadTime = 2;
		Agility = 1;
		RateOfFire = 0;

		// Protection
		Armor = 2;
		CriticalHitProtection = 1;
		Health = 15; // 1 Punkt = +5
		Max_Health = Health;
	}

	/**
	 * Set Level of the monster
	 * 
	 * @param i >
	 *            0
	 */
	public void setLevel(int level) {

		// Level zurück setzen
		initStats();

		// Arms
		Damage *= level;
		Piercing *= level;
		CriticalDamage *= level;

		// Electronics
		Sensors *= level;
		RocketLauncherAccuracy *= level;
		Stealth *= level;

		// Hydraulics
		Strength *= level;
		GatlingAccuracy *= level;
		Speed *= level;

		// Mechanics
		ReloadTime *= level;
		Agility *= level;
		RateOfFire *= level;

		// Protection
		Armor *= level;
		CriticalHitProtection *= level;
		Health *= level;
		Max_Health = Health;
	}

	/**
	 * Simple Getter
	 */
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Simple Getter
	 */
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Method that calculates the damage the enemy recieves with every hit.
	 */
	public int hurt(int dmg) {

		last_time_hurt = System.currentTimeMillis();

		if (Health > 0 && dmg >= Health) {
			onDeath();
			last_time_hurt = 0;
		}

		Health -= dmg;

		return 0;
	}

	/**
	 * Is Monster alive?
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return Health > 0;
	}

	/**
	 * OnDeath event (drops, etc...)
	 */
	public void onDeath() {
		// TODO Death Animation
		// ExplodingSphere s = new ExplodingSphere(getDimension().mal(0.4),
		// getPosition().add(
		// new Vektor3D(0, getDimension().getX2() / 2., 0)), 2, 2);
		
		if (death == null)
			death = new OpenAlClip("sound/explosion2.ogg");
		
		
		double b = 0;
		try {
			b = getDimension().getX2() / 2.;
		} catch (NullPointerException e) {
			b = 1;
		}
		ExplodingParticles s = new ExplodingParticles(getPosition().add(new Vektor3D(0, b, 0)));
		death.play();
		SuperMain.level.addObjekt(s);
		doDrop();

	}

	/**
	 * Item Drop routines
	 */
	private void doDrop() {

		if ((int) Math.round(Math.random() * 6 - 0.5) == 1) { // Equipment
			// drop?
			SuperMain.addDrop(new EquipDrop(position, new Loot(getArms(), getElectronics(), getHydraulics(),
					getMechanics(), getProtection())));
		}

		if (GatlingAccuracy > 0) { // Gatling Munition Drop?
			int dmg = (int) getLevel();
			int pierc = (int) Math.round(getLevel() * 0.5);
			int amount = (int) Math.round(Math.random() * 100 + 50);
			SuperMain.addDrop(new EquipDrop(position, new MunPack(amount, new Ammo(dmg, pierc))));
		}

		if (RocketLauncherAccuracy > 0) { // Rocket Drop?
			int dmg = (int) Math.round(getLevel() * 10 + 5);
			int pierc = (int) Math.round(getLevel() * 0.5);
			int amount = (int) Math.round(Math.random() * 30 + 2);
			SuperMain.addDrop(new EquipDrop(position, new MunPack(amount, new Rockets(dmg, pierc))));
		}

		if (Math.max(getArms(), Math.max(getElectronics(), Math.max(getHydraulics(), getMechanics()))) <= getProtection()
				&& (int) Math.round(Math.random() * 6 - 0.5) == 1) { // Health
			// Drop?
			SuperMain.addDrop(new EquipDrop(position, new HealthPackage((int) (getProtection() * 5 + getProtection()
					* Math.random() * 5))));
		}

	}

	public boolean isInWertebereich(int i, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void render() {
		// OGL.wuerfel(position, new v3(0.5, 0.5, 0.5), ico, id);
		GL11.glDisable(GL11.GL_LIGHTING);

		// Healthbar
		if (System.currentTimeMillis() < last_time_hurt + 3 * 1000) {
			double bias = 1;
			Vektor3D dimension = new v3();
			if (getDimension() != null) {
				dimension = getDimension();
			}
			double breite = 0;
			if (this instanceof TurretGun) {
				bias = 4;
				breite = 2;

			}
			Vektor3D pos = getPosition().add(new v3(0, dimension.getX2() + bias, 0));
			OGL.verschieb(pos);
			OGL.rot(LevelPlay.p.blickrichtung, v3.y_axis);
			OGL.setColor(myColor.BLACK);
			OGL.viereck(new v3(-dimension.getX1() / 2. - breite, 0, 0), new v3(dimension.getX1() + 2 * breite, 0.2, 0),
					healthbar, 0, 1.1, 0, 0, 0);
			OGL.setColor(myColor.WHITE);
			OGL.viereck(new v3(-dimension.getX1() / 2. - breite, 0, 0.01), new v3((dimension.getX1() + 2 * breite)
					* ((Health + 0.0) / Max_Health), 0.2, 0), healthbar);
			OGL.rot(-LevelPlay.p.blickrichtung, v3.y_axis);
			OGL.verschieb(pos.negiere());
		}
		GL11.glEnable(GL11.GL_LIGHTING);

	}

	public void setPosition(Vektor3D v) {
		this.position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());

	}

	public void setPositionDirectly(Vektor3D v) {
		this.position = v;

	}

	public String getDescription() {
		return "Monsters";
	}

	public String getOptionDescription(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOptionType(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public Object getOptionValue(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOptionValue(int i, Object value) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "Monster";
	}

	public void doCriticalDamage() {
		if (crit == null)
			crit = new OpenAlClip("sound/critical.ogg");
		// TODO Auto-generated method stub

	}

}
