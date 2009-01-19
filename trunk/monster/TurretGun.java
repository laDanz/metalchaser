package monster;

import java.util.Random;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.Munition;
import mechPeck.munition.Ammo;
import Classes.Geschoss;
import Classes.OGL;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.RadarAble;
import Classes.RotateAble;
import Classes.Vektor3D;
import Classes.v3;
import anim.SkelettSentry;

/**
 * Defines TurretGun Monster
 * 
 * @author ladanz
 * 
 */
public class TurretGun extends master implements RotateAble, RadarAble {

	int drehwinkel = 0;
	double y_drehwinkel = 0;
	int drehwinkel_init = 0; // Option 1 Hauptrichtung
	int max_schwenk = 90; // Option 2, Maximal schwenk in eine Richtung
	double salve = 0;

	// Object3D model;
	SkelettSentry skelett;
	Munition mun;
	int dmg = (int) Math.round(Math.random() * getLevel() * 2 + 0.5);
	int pierc = (int) Math.round(Math.random() * getLevel() + 0.5);

	private long abklingzeit = 0;
	
	// Normalverteilte Zufallszahlen generieren
	Random random = new Random();
	
	OpenAlClip sound;

	/**
	 * Constructor
	 */
	public TurretGun() {
		super();
		initStats();
		setLevel(2);
		skelett = new SkelettSentry(id);
		mun = new Ammo(dmg, pierc);
		if (sound == null)
			sound = new OpenAlClip("sound/shoot1.ogg");
		
		if (crit == null)
			crit = new OpenAlClip("sound/critical.ogg");
	}

	//@override
	public void initStats() {
		// Arms
		Damage = 2;
		Piercing = 2;
		CriticalDamage = 1;

		// Electronics
		Sensors = 3;
		RocketLauncherAccuracy = 0;
		Stealth = 0;

		// Hydraulics
		Strength = 0;
		GatlingAccuracy = 1;
		Speed = 0;

		// Mechanics
		ReloadTime = 0;
		Agility = 1;
		RateOfFire = 1;

		// Protection
		Armor = 1;
		CriticalHitProtection = 0;
		Health = 15; // 1 Punkt = +5
		Max_Health = Health;
	}

	/**
	 * Simple Getter
	 */
	public double getDrehwinkel() {
		// TODO Auto-generated method stub
		return drehwinkel;
	}

	/**
	 * Simple Setter
	 */
	//@override
	public void setDrehwinkel(int dreh) {
		this.drehwinkel = dreh;

	}

	//@override
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		if (isAlive())
			return skelett.checkCollisionforObjekt(pos, getPosition());
		else
			return false;
		// Geschoss.checkCollisionforObjektAsEllipse(this, pos);
	}

	public int getRadarAppearance() {
		if (isAlive())
			return RadarAble.MONSTER;
		else
			return RadarAble.INVISABLE;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Sentry Gun";
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		// if(isAlive())
		// return model.getDimension();
		return null;
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Hauptrichtung";
		case 1:
			return "Schwenkbereich";
		case 2:
			return "Monster Level";
		default:
			return "Unknown Option";
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.INT;
		case 1:
			return Objekt.INT;
		case 2:
			return Objekt.INT;
		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return drehwinkel_init;
		case 1:
			return max_schwenk * 2;
		case 2:
			return this.getLevel();
		default:
			return null;
		}
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			return (j >= 0 && j <= 360);
		case 1:
			int k = (Integer) value;
			return (k >= 0 && k <= 360);
		case 2:
			int l = (Integer) value;
			return (l > 0);
		default:
			return false;
		}
	}

	public void logic() {
		if (!isAlive())
			return;
		// 2 states
		// state #0: hin und her drehen
		// wenn player nicht im sichtbereich
		double distance = LevelPlay.p.getPosition().add(this.getPosition().mal(-1)).length();
		if (distance > 30 + Sensors*2 - SuperMain.profil.mecha.getStealth()) {
			// bei geradem blickwinkel nach rechts
			if (drehwinkel % 2 == 0) {
				drehwinkel += 2;
			} else {
				drehwinkel -= 2;
			}
			int diff = drehwinkel - drehwinkel_init;
			if (diff < -max_schwenk || diff > max_schwenk) {
				drehwinkel++;
			}
		} else {
			// zum player drhen
			double y = LevelPlay.p.getPosition().getX3() - this.getPosition().getX3();
			double x = LevelPlay.p.getPosition().getX1() - this.getPosition().getX1();
			double hoehenunterschied = getPosition().getX2() + 1. - LevelPlay.p.getPosition().getX2();
			y_drehwinkel = -Math.toDegrees(Math.atan(hoehenunterschied / distance));
			int winkel = 360 - (int) Math.round(Math.toDegrees(Math.atan2(y, x)));

			// nicht sofort hinspringen
			int diff = drehwinkel - winkel;

			if (diff > -5 && diff < 5) {
				drehwinkel = winkel;

				// schießen
				if (abklingzeit < System.currentTimeMillis()) {
					float gain = (float) (70 - distance);
					gain = gain / 70;
					if (gain < 0) {
						gain = 0;
					}
					sound.setMyGain(gain);
					sound.play();

					// Ungenauigkeit
					double dx = 0.5 + salve/100 + random.nextGaussian() / (1 + GatlingAccuracy/30 );
					double dy = 0.5 + salve/100 + random.nextGaussian() / (1 + GatlingAccuracy/30 );

					Vektor3D richtung = new v3(-1 * Math.sin(Math.toRadians(drehwinkel - 90 + dx)), 1 * Math.sin(Math
							.toRadians(y_drehwinkel + dy)), -1 * Math.cos(Math.toRadians(drehwinkel - 90 + dx)));

					double speed = ((500 / 3.6) / (60)) * OGL.fps_anpassung;
					Vektor3D geschospos = position.add(new v3(0, 3, 0)).add(richtung.normierter().mal(2.5));
					Geschoss g = new Geschoss(geschospos, richtung, mun, speed, this.id);
					LevelPlay.bullets.add(g);
					
					//abklingzeit = (long) (System.currentTimeMillis() + (1 / RateOfFire) * 1000);
					abklingzeit = (long) (System.currentTimeMillis() + 300);
					
					if( salve < 200)
						salve++;
				}
			} else {
				salve = 0;
				// weiterbewegen
				drehwinkel -= 2 * diff / Math.abs(diff);
				if (drehwinkel < 0 || drehwinkel > 360) {
					drehwinkel -= 360 * drehwinkel / Math.abs(drehwinkel);

				}
			}
		}

	}

	public void render() {
		if (!isAlive())
			return;
		super.render();
		OGL.verschieb(position);
		// OGL.rot(drehwinkel, new v3(0, 1, 0));
		// model.render();
		skelett.render(0, drehwinkel, y_drehwinkel);

		// OGL.rot(-drehwinkel, new v3(0, 1, 0));
		OGL.verschieb(position.negiere());

	}

	public void setOptionValue(int i, Object value) {

		int j = (Integer) value;

		switch (i) {
		case 0:
			drehwinkel_init = j;
			drehwinkel = j;
			break;
		case 1:
			max_schwenk = j / 2;
			break;
		case 2:
			this.setLevel(j);
			break;

		default:
			break;
		}
	}

}
