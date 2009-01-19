package monster;

import main.LevelPlay;
import main.SuperMain;
import Classes.OpenAlClip;
import anim.SkelettShocker;

public class Shocker extends Blocker {

	long last_shock = 0;
	OpenAlClip electric;

	/**
	 * Constructor
	 */
	public Shocker() {
		super();
		skelett = new SkelettShocker(id);
		last_shock = 0;
		if (electric == null)
			electric = new OpenAlClip(SuperMain.ordner + "sound/electricFlashLong.ogg");
		
		if (crit == null)
			crit = new OpenAlClip("sound/critical.ogg");
		initStats();
	}

	//@override
	public void initStats() {
		// Arms
		Damage = 0;
		Piercing = 0;
		CriticalDamage = 0;

		// Electronics
		Sensors = 3;
		RocketLauncherAccuracy = 0;
		Stealth = 2;

		// Hydraulics
		Strength = 2;
		GatlingAccuracy = 0;
		Speed = 2;

		// Mechanics
		ReloadTime = 1;
		Agility = 1;
		RateOfFire = 0;

		// Protection
		Armor = 1;
		CriticalHitProtection = 0;
		Health = 15; // 1 Punkt = +5
		Max_Health = Health;
	}

	/**
	 * Attack of the Monster
	 */
	//@override
	public void doAttackRoutine() {
		// TODO Auto-generated method stub
		super.doAttackRoutine();

		// alle 2 Sekunden+Nur wenn in der Naehe vom Player
		if (last_shock == 0) {
			last_shock = System.currentTimeMillis();
			// zusaetzlich noch schocken
			int r = (int) (getPosition().add(LevelPlay.p.getPosition().mal(-1)).length());
			int schaden = (getElectronics() * 5 - (2 * r));
			schaden = Math.max(0, schaden);
			if (schaden == 0)
				return;
			// System.out.println("Shockausgelöst: " +
			// System.currentTimeMillis());
			// new myClip(SuperMain.ordner +
			// "sound/electricFlashLong.ogg").play();
			electric.play();
			SuperMain.statistics.incDamageTaken(schaden);
			LevelPlay.p.hurt(schaden);

		} else {
			if (last_shock + 5000/ReloadTime < System.currentTimeMillis()) {
				last_shock = 0;
			}
		}
	}

	//@override
	public String toString() {

		return "Shocker";
	}

}
