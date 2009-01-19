package Classes;

import java.io.Serializable;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.Munition;
import mechPeck.munition.Rockets;
import anim.SkelettTeil;

/**
 * Represents bullet physics.
 * 
 * @author laDanz
 * 
 */
public class Geschoss implements Serializable {
	Vektor3D pos;
	Vektor3D richtung;
	double abnahme;
	Munition mun;
	long borntime;
	int sender_id;
	static OpenAlClip sound = null;

	/**
	 * Creates a new bullet.
	 * 
	 * @param start_pos
	 *            From where is the bullet launched?
	 * @param richtungsvektor
	 *            What direction is the bullet flying.
	 * @param abnahme
	 *            By what decreases the speed of the bullet per tick (in
	 *            Percent). 0% means no decrease.
	 * @param speed
	 *            Measured in meters per tick.
	 * @param mun
	 *            The kind of munition that is fired.
	 * @param senderID
	 *            Which entity launched this bullet?
	 */
	public Geschoss(Vektor3D start_pos, Vektor3D richtungsvektor, Munition mun, double abnahme, double speed,
			int senderID) {
		this(start_pos, richtungsvektor.normierter().mal(speed), mun, abnahme, senderID);
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Munition getMun() {
		return mun;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getPos() {
		return pos;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getRichtung() {
		return richtung;
	}

	/**
	 * Creates a new bullet.
	 * 
	 * @param start_pos
	 *            From where is the bullet launched?
	 * @param richtungsvektor
	 *            What direction is the bullet flying.
	 * @param abnahme
	 *            By what decreases the speed of the bullet per tick (in
	 *            Percent). 0% means no decrease.
	 * @param mun
	 *            The kind of munition that is fired.
	 * @param senderID
	 *            Which entity launched this bullet?
	 */
	public Geschoss(Vektor3D start_pos, Vektor3D richtungsvektor, Munition mun, double abnahme, int senderID) {
		borntime = System.currentTimeMillis();
		pos = new v3(start_pos);
		this.sender_id = senderID;
		richtung = new v3(richtungsvektor);
		this.abnahme = abnahme;
		this.mun = mun;
		if (sound == null) {
			sound = new OpenAlClip(SuperMain.ordner + "sound/hit.ogg");
		}

	}

	/**
	 * Creates a new bullet.
	 * 
	 * @param start_pos
	 *            From where is the bullet launched?
	 * @param richtungsvektor
	 *            What direction is the bullet flying.
	 * @param mun
	 *            The kind of munition that is fired.
	 * @param senderID
	 *            Which entity launched this bullet?
	 */
	public Geschoss(Vektor3D start_pos, Vektor3D richtungsvektor, Munition mun, int senderID) {
		this(start_pos, richtungsvektor, mun, 0, senderID);
	}

	/**
	 * Move the bullet and make collision calculations.
	 */
	public void tick() {
		// weiter setzten
		Vektor3D newpos = pos.add(richtung);
		// Kollisionsabfrage
		double delta = 0;
		// im level rahmen???
		if (main.SuperMain.level.isOutta(newpos)) {
			collision();
			setAlive(false);
			return;
		}
		// Untergrund
		double height = main.SuperMain.level.getHeight(newpos);
		if ((newpos.getX2() + delta) <= height) {
			collision();
			if (this.mun instanceof Rockets) {
				SuperMain.level.addObjekt(new ExplodingParticles(getPos(), 20, 1, 1, myColor.YELLOW));
			} else {
				SuperMain.level.addObjekt(new ExplodingParticles(getPos(), 10, 0.5, 0.5, myColor.YELLOW));
			}

			setAlive(SuperMain.dev);
			return;
		}
		// mit objekt???
		for (Objekt o : SuperMain.level.objekte.toArray(new Objekt[0])) {
			if (o.checkCollisionforObjekt(pos)) {
				collisionObj(o);
				if (this.mun instanceof Rockets) {
					SuperMain.level.addObjekt(new ExplodingParticles(getPos(), 20, 1, 1, myColor.GREY));
				} else {
					SuperMain.level.addObjekt(new ExplodingParticles(getPos(), 10, 0.5, 0.5, myColor.GREY));
				}
				setAlive(SuperMain.dev);
				return;

			}
		}
		// mit player???
		if (main.LevelPlay.p.checkCollisionforObjekt(pos)) {
			collisionObj(main.LevelPlay.p);
			sound.play();
			setAlive(false);
			return;
		}
		// keine kollision
		pos = newpos;
	}

	/**
	 * Checks whether the given Object collides with the bullet position.
	 * 
	 * @param o
	 *            An <code>Objekt</code> to check collision for.
	 * @param geschoss_pos
	 *            The position of the bullet.
	 * @param mittelpkt
	 *            The center of the object.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforObjektAsEllipse(Objekt o, Vektor3D geschoss_pos, Vektor3D mittelpkt) {
		if (o.getDimension() != null) {
			Vektor3D ellips = geschoss_pos.add(o.getPosition().mal(-1));
			if (mittelpkt != null) {
				ellips = ellips.add(new v3(0, -o.getDimension().getX2() / 2., 0));
				// ellips = ellips.add(new v3(0, mittelpkt.getX2(), 0));
			}
			// wenn objekt drehbar verndert sich der bezugs vektor
			if (o instanceof RotateAble) {
				RotateAble new_name = (RotateAble) o;
				double wink = new_name.getDrehwinkel();
				double newx1 = ellips.getX1() * Math.cos(Math.toRadians(wink)) - ellips.getX3()
						* Math.sin(Math.toRadians(wink));
				double newx2 = ellips.getX2();
				double newx3 = ellips.getX1() * Math.sin(Math.toRadians(wink)) + ellips.getX3()
						* Math.cos(Math.toRadians(wink));
				ellips = new v3(newx1, newx2, newx3);
			}
			// is in ellipse???
			double fac1 = ellips.getX1() * ellips.getX1() / (o.getDimension().getX1() * o.getDimension().getX1() / 4);
			double fac2 = ellips.getX2() * ellips.getX2() / (o.getDimension().getX2() * o.getDimension().getX2() / 4);
			double fac3 = ellips.getX3() * ellips.getX3() / (o.getDimension().getX3() * o.getDimension().getX3() / 4);

			return ((fac1 + fac2 + fac3) < 1);
		}
		return false;
	}

	/**
	 * Checks the collision for a <code>SkelettTeil</code> with a bullet.
	 * 
	 * @param sk
	 *            A <code>SkelettTeil</code>.
	 * @param geschoss_pos_relative
	 *            The position of the bullet relative to the
	 *            <code>SkelettTeil</code>.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforSkelettTeilAsEllipse(SkelettTeil sk, Vektor3D geschoss_pos_relative) {
		if (sk.getObject().getDimension() != null) {
			Vektor3D ellips = geschoss_pos_relative;
			if (sk.getObject().getMittelPkt() != null) {
				ellips = ellips.add(new v3(0, -sk.getObject().getDimension().getX2() / 2., 0));
				// ellips = ellips.add(new v3(0, mittelpkt.getX2(), 0));
			}
			// wenn objekt drehbar verndert sich der bezugs vektor
			if (sk.getRotation().getX2() != 0) {

				double wink = sk.getRotation().getX2();
				double newx1 = ellips.getX1() * Math.cos(Math.toRadians(wink)) - ellips.getX3()
						* Math.sin(Math.toRadians(wink));
				double newx2 = ellips.getX2();
				double newx3 = ellips.getX1() * Math.sin(Math.toRadians(wink)) + ellips.getX3()
						* Math.cos(Math.toRadians(wink));
				ellips = new v3(newx1, newx2, newx3);
			}
			// is in ellipse???
			double fac1 = ellips.getX1() * ellips.getX1()
					/ (sk.getObject().getDimension().getX1() * sk.getObject().getDimension().getX1() / 4);
			double fac2 = ellips.getX2() * ellips.getX2()
					/ (sk.getObject().getDimension().getX2() * sk.getObject().getDimension().getX2() / 4);
			double fac3 = ellips.getX3() * ellips.getX3()
					/ (sk.getObject().getDimension().getX3() * sk.getObject().getDimension().getX3() / 4);

			return ((fac1 + fac2 + fac3) < 1);
		}
		return false;
	}

	/**
	 * Checks collision with square interpolation.
	 * 
	 * @param sk
	 *            A <code>SkelettTeil</code>.
	 * @param geschoss_pos_relative
	 *            The position of the bullet relative to the
	 *            <code>SkelettTeil</code>.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforSkelettTeilAsKasten(SkelettTeil sk, Vektor3D geschoss_pos_relative) {
		if (sk.getObject().getDimension() == null)
			return false;
		Vektor3D kasten = getDimensionUnderDrehung(sk.getObject().getDimension(), sk.getRotation());
		if (geschoss_pos_relative.getX1() > kasten.getX1() / 2.)
			return false;
		if (geschoss_pos_relative.getX1() < -kasten.getX1() / 2.)
			return false;
		if (geschoss_pos_relative.getX2() > kasten.getX2() / 2.)
			return false;
		if (geschoss_pos_relative.getX2() < -kasten.getX2() / 2.)
			return false;
		if (geschoss_pos_relative.getX3() > kasten.getX3() / 2.)
			return false;
		if (geschoss_pos_relative.getX3() < -kasten.getX3() / 2.)
			return false;

		return true;
	}

	/**
	 * Checks whether the given Object collides with the bullet position.<br>
	 * Uses square interpolation.
	 * 
	 * @param sk
	 *            An <code>Objekt</code> to check collision for.
	 * @param geschoss_pos_relative
	 *            The position of the bullet.
	 * @param obj_mittelpkt
	 *            The center of the object.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforObjektAsKasten(Objekt sk, Vektor3D geschoss_pos_relative,
			Vektor3D obj_mittelpkt) {
		if (sk.getDimension() == null)
			return false;
		double rot = 0;
		if (sk instanceof RotateAble) {
			rot = ((RotateAble) sk).getDrehwinkel();
		}
		Vektor3D kasten = sk.getDimension();
		geschoss_pos_relative = geschoss_pos_relative.sub(sk.getPosition());
		kasten = getDimensionUnderDrehung(sk.getDimension(), new v3(0, rot, 0));
		if (geschoss_pos_relative.getX1() > kasten.getX1() / 2.)
			return false;
		if (geschoss_pos_relative.getX1() < -kasten.getX1() / 2.)
			return false;
		if (geschoss_pos_relative.getX2() > kasten.getX2() / 2.)
			return false;
		if (geschoss_pos_relative.getX2() < -kasten.getX2() / 2.)
			return false;
		if (geschoss_pos_relative.getX3() > kasten.getX3() / 2.)
			return false;
		if (geschoss_pos_relative.getX3() < -kasten.getX3() / 2.)
			return false;

		return true;
	}

	/**
	 * Calculates a dimension under rotation.
	 * 
	 * @param dimension
	 *            The original dimension.
	 * @param rotation
	 *            A set with angles for each dimension.
	 * @return The dimension under rotation.
	 */
	public static Vektor3D getDimensionUnderDrehung(Vektor3D dimension, Vektor3D rotation) {
		Vektor3D x_dim = new v3(dimension.getX1() * Math.cos(Math.toRadians(rotation.getX2())), 0, dimension.getX1()
				* Math.sin(Math.toRadians(rotation.getX2())));

		Vektor3D z_dim = new v3(dimension.getX3() * Math.sin(Math.toRadians(rotation.getX2())), 0, dimension.getX3()
				* Math.cos(Math.toRadians(rotation.getX2())));

		double maxx = Math.max(Math.abs(x_dim.getX1()), Math.abs(z_dim.getX1()));
		double maxz = Math.max(Math.abs(x_dim.getX3()), Math.abs(z_dim.getX3()));

		return new v3(Math.abs(maxx), dimension.getX2(), Math.abs(maxz));
	}

	/**
	 * Checks for collision with a given object dimension and a bullet position.
	 * 
	 * @param dim
	 *            The dimension of the object.
	 * @param pos
	 *            The position of the object.
	 * @param geschoss_pos
	 *            The position of the bullet.
	 * @param mittelpkt
	 *            The center of the object.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforObjektAsEllipse(Vektor3D dim, Vektor3D pos, Vektor3D geschoss_pos,
			Vektor3D mittelpkt) {
		if (dim != null) {
			Vektor3D ellips = geschoss_pos.add(pos.mal(-1));
			if (mittelpkt != null) {
				ellips = ellips.add(new v3(0, -dim.getX2() / 2., 0));
				// ellips = ellips.add(new v3(0, mittelpkt.getX2(), 0));
			}

			// is in ellipse???
			double fac1 = ellips.getX1() * ellips.getX1() / (dim.getX1() * dim.getX1() / 4);
			double fac2 = ellips.getX2() * ellips.getX2() / (dim.getX2() * dim.getX2() / 4);
			double fac3 = ellips.getX3() * ellips.getX3() / (dim.getX3() * dim.getX3() / 4);

			return ((fac1 + fac2 + fac3) < 1);
		}
		return false;
	}

	/**
	 * Checks for collision with a given object and a bullet position.
	 * 
	 * @param o
	 *            The object.
	 * @param geschoss_pos
	 *            The position of the bullet.
	 * @return true if it collides, false else.
	 */
	public static boolean checkCollisionforObjektAsEllipse(Objekt o, Vektor3D geschoss_pos) {
		return checkCollisionforObjektAsEllipse(o, geschoss_pos, null);
	}

	/**
	 * This method is called when the bullet collides with an object.<br>
	 * It calculates the damage and increases some related statistics.<br>
	 * It stops the bullet.
	 * 
	 * @param o
	 *            The object collided with.
	 */
	private void collisionObj(Objekt obj) {

		// bewegung aussetzten
		richtung = new v3();
		// FIXME manchmal kommt hier ein null mun an!
		if (mun == null)
			return;

		int schaden = 1;

		if (obj instanceof monster.master) {
			monster.master o = (monster.master) obj;
			double dmg = mun.getSchaden() + (mun.getSchaden() * SuperMain.profil.mecha.getDamage() * 5 / 100);
			double pier = mun.getPiercing() + (mun.getPiercing() * SuperMain.profil.mecha.getPiercing() / 100);

			double armor = (o.getArmor() - pier) * dmg / 100;
			if (armor < 0)
				armor = 0;

			schaden = (int) Math.round(dmg - armor);
			// System.out.println("Armor("+armor+") - Damage("+dmg+") = Schaden:
			// " +schaden);

			if (SuperMain.profil.mecha.getCriticalDamage() * 2 - o.getCriticalHitProtection() > Math.random() * 100) {
				schaden *= 2;
				((monster.master) obj).doCriticalDamage();

			}
		}

		int ueber = obj.hurt(schaden);
		schaden -= ueber;
		if (obj.getClass().toString().equals(LevelPlay.p.getClass().toString())) {
			SuperMain.statistics.incHitsTaken();
			schaden = Math.max(0, schaden);
			SuperMain.statistics.incDamageTaken(schaden);
		} else if (this.sender_id == LevelPlay.p.getID()) {
			if (obj instanceof monster.master) {
				if (((monster.master) obj).Health <= 0) {
					SuperMain.statistics.IncTargets_killed();
				}
			}
			SuperMain.statistics.incHitsGiven();
			SuperMain.statistics.incDamageGiven(schaden);
		}

	}

	/**
	 * This method should be called when the bullet collides but not with an
	 * object.<br>
	 * It stops the bullet.
	 */
	private void collision() {
		// bewegung aussetzten
		richtung = new v3();
	}

	/**
	 * Perform bullet calculations like moving and colliding.<br>
	 * After that: render the <code>Munition</code>.
	 */
	public void render() {
		tick();

		if (mun != null && (mun.toString().toString()=="Pig Shells" || mun.getCaliber() != "9mm" || SuperMain.dev)) {
			OGL.verschieb(pos);

			OGL.setColor(myColor.WHITE);
			if (mun instanceof Rockets) {
				((Rockets) mun).render(Math.atan2(richtung.getX1(), richtung.getX3()));
			} else {
				mun.render();
			}
			OGL.verschieb(pos.negiere());

		}
		else {
			OGL.setColor(myColor.RED);
			OGL.line(4, pos, pos.add(richtung));
		}

	}

	/**
	 * Checks whether the bullet is still 'alive'.
	 * 
	 * @return true if its alive.
	 */
	public boolean isAlive() {
		return (System.currentTimeMillis() < borntime + 15 * 1000);
	}

	/**
	 * Can only kill the bullet by <code>setAlive(false)</code>.<br>
	 * Cannot revive the bullet.
	 * 
	 * @param b
	 */
	public void setAlive(boolean b) {
		if (!b)
			borntime = 0;
	}

}
