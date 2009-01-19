package monster;

import java.util.LinkedList;

import main.LevelPlay;
import main.SuperMain;
import Classes.Geschoss;
import Classes.OGL;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.RadarAble;
import Classes.RotateAble;
import Classes.Vektor3D;
import Classes.v3;
import anim.SkelettPusher;

/**
 * A enemy guarding a specific area<br>
 * Does the Player enter this area he will pushed out of it<br>
 * This enemy can also call friends to help him
 * 
 * @author ladanz 22.05.2008
 */

public class Blocker extends monster.master implements RotateAble, RadarAble {
	/**
	 * Range for friends which can be called
	 */
	private int call_radius = 40;

	/**
	 * range of guarded area
	 */
	private int bewachungs_radius = (int) Sensors * 10;

	/**
	 * ammount of called friends with info, who called them
	 */
	private static LinkedList<EnemyCaller> gecallte = new LinkedList<EnemyCaller>();

	/**
	 * 3D Modell for drawing
	 */
	// private static Object3D object;
	SkelettPusher skelett;

	/**
	 * angle in degree, wich direction the enemy faces
	 */
	private int rotation = 0;

	/**
	 * Utilityvar to await reaction time
	 */
	private long first_time_called = 0;

	/**
	 * Utilityvar to reaction after reaction time expired
	 */
	private Runnable RunAfterReaction;

	/**
	 * how many health the enemy has left
	 */
	// private int life;
	/**
	 * Der bewachte Punkt.
	 */
	private Vektor3D waypoint = null;

	/**
	 * is enemy in defense mode?
	 */
	private boolean attacking = false;

	/**
	 * Utilityvar to detect if defense mode recently switched
	 */
	private boolean first_attack = false;

	/**
	 * marks scale of modell. 1 means it is as big as the model
	 */
	double streck = 0.5;

	private long last_critical_dmg;

	/**
	 * Constructor
	 */
	public Blocker() {
		super();

		// Keine ReaktionsAktion
		RunAfterReaction = null;
		// 3D Modell laden.
		if (skelett == null)
			skelett = new SkelettPusher(this.id);
		// Start-Leben nach den Stats bestimmen.
		initStats();
		
		if (crit == null)
			crit = new OpenAlClip("sound/critical.ogg");

	}

	/**
	 * Monster attack-routines
	 */
	public void doAttackRoutine() {
		// Ist es der erste Tick der Attacke ?
		if (first_attack) {

			callAllInRadius(this.getID());

		}// \FirstAtack

		if (System.currentTimeMillis() - last_critical_dmg < 1000) {
			// wackeln

		} else {
			// In Richtung des Players bewegen.
			move_to(LevelPlay.p.getPosition());
		}

	}

	//@override
	public void setDrehwinkel(int dreh) {
		this.rotation = dreh;

	}

	/**
	 * Call all monster in radius<br>
	 * Exception: Monster itself and the Monster which calls initial
	 * 
	 * @param by_id
	 *            Who calls the monster
	 */
	public void callAllInRadius(int by_id) {
		// Alle LevelObjekte
		for (Objekt o : SuperMain.level.objekte) {
			// Nur Blocker
			if (o instanceof monster.master && o.getClass().toString().equals(this.getClass().toString())) {

				// nur wenn in call radius
				if (o.getPosition().add(getPosition().mal(-1)).length() <= call_radius)
					call(o, by_id);

			}
		}
	}

	/**
	 * action that calls all Monster in <code>enemy</code>s radius<br>
	 * Realises the chain reaction
	 * 
	 * @param enemy
	 *            Which Blocker calls other Monster
	 * @param by_id
	 *            Who was the initial caller
	 * @return executable action
	 */
	private Runnable CallThemAfterReactionTime(final Objekt enemy, final int by_id) {
		return new Runnable() {
			public void run() {
				if (enemy instanceof Blocker) {
					Blocker en = (Blocker) enemy;
					en.callAllInRadius(by_id);
				}
			}
		};
	}

	/**
	 * Set a "after-discharge-of-chain-reaction"
	 * 
	 * @param run
	 */
	private void AddAfterReactionTimeRunnable(Runnable run) {
		this.RunAfterReaction = run;
	}

	/**
	 * Call another Blocker for support
	 * 
	 * @param enemy
	 *            Call whom
	 * @param by_id
	 *            Called from whom - identification over ID
	 */
	private void call(Objekt enemy, int by_id) {
		// mich nicht zufügen; Initial Caller auch nicht!
		if (enemy == this || enemy.getID() == by_id)
			return;
		// Keine Gerufener/Rufer Paar doppelt zufuegen.
		EnemyCaller ec = new EnemyCaller(enemy.getID(), by_id);
		if (gecallte.contains(ec))
			return;

		// Das Paar letztendlich zufuegen.
		gecallte.add(ec);
		// rekursiver aufruf; erst nach Ablauf der reaktionszeit
		if (enemy instanceof Blocker) {
			Blocker en = (Blocker) enemy;
			en.AddAfterReactionTimeRunnable(CallThemAfterReactionTime(enemy, by_id));
		}

	}

	/**
	 * Internal class for management of called Pairs<br>
	 * Identification over ID
	 * 
	 * @author ladanz
	 * 
	 */
	class EnemyCaller {
		int enemy_id;

		int caller_id;

		public EnemyCaller(int enemy_id, int caller_id) {
			this.enemy_id = enemy_id;
			this.caller_id = caller_id;
		}

		//@override
		public boolean equals(Object obj) {
			if (obj instanceof EnemyCaller) {
				EnemyCaller new_name = (EnemyCaller) obj;
				return (this.caller_id == new_name.caller_id && this.enemy_id == new_name.enemy_id);
			}
			return false;
		}
	}

	/**
	 * Moves the monster with according speed to the point<br>
	 * Speed is calculated by the speed Stat.
	 * 
	 * @param In
	 *            In which direction I will move
	 */

	private void move_to(Vektor3D punkt) {
		// Abstand zum Ziel.
		Vektor3D delta = punkt.add(getPosition().mal(-1));
		// Wenn ich schon bis auf 0.3 Meter ran bin --> nicht weiter bewegen
		// Grund: Stack-Overflow.
		if (Math.abs(delta.length()) <= 0.3)
			return;
		// Den Vektor um den Bewegt werden soll der geschwindigkeit anpassen.
		double geschwindigkeit = 1 + Speed / 5;
		delta = delta.normierter().mal(OGL.fps_anpassung / 60. * geschwindigkeit);
		// Darf ich mich auf den Punkt bewegen ? --> kollisionsabfrage
		if (canGo(getPosition().add(delta), delta)) {
			setPosition(getPosition().add(delta));
		}
		// Rotationsrichtung bestimmt sich aus bewegungsrichtung
		rotation = (int) Math.toDegrees((Math.atan2(delta.getX1(), delta.getX3()))) - 90;

	}

	/**
	 * Can a object move to this place without colliding with other objects?
	 * 
	 * @param vektor3D
	 *            Requestet place
	 * @return Can he walk?
	 */
	private boolean canGo(Vektor3D vektor3D, Vektor3D delta) {
		// Wenn fuer mich kein Wert bei Dimension angegeben ist kann ich immer
		// gehen.
		if (getDimension() == null)
			return true;
		// Der von mir eingenommene Platz.
		// Groestmoeglicher Kreis.
		double my_needed_platz = Math.max(Math.abs(getDimension().getX1()), Math.abs(getDimension().getX3())) / 2.;

		LinkedList<Objekt> alle_objekte_von_interesse = new LinkedList<Objekt>();
		alle_objekte_von_interesse.addAll(SuperMain.level.objekte);
		alle_objekte_von_interesse.add(LevelPlay.p);
		// Durch alle levelObjekte iterieren.
		for (Objekt o : alle_objekte_von_interesse) {
			// Nicht mit mir selbst oder etwas ohne Dimension kollidieren
			if (o.getDimension() == null || o == this)
				continue;
			// beweg ich mich drauf zu oder weg?
			double myangle = (Math.atan2(delta.getX1(), delta.getX3()));
			Vektor3D obj_delta = o.getPosition().add(vektor3D.add(delta.mal(-1)).mal(-1));

			double hisangle = (Math.atan2(obj_delta.getX1(), obj_delta.getX3()));
			double delta_angel = Math.abs(Math.toDegrees(myangle - hisangle));
			// wenn ich mich davon wegbeweg kann ich gehen.
			if (delta_angel > 90 && delta_angel < 270)
				continue;

			// Jetzt kommt noch der Vom gegenüber benötigte Platz hinzu.
			double neededplatz = my_needed_platz
					+ Math.max(Math.abs(o.getDimension().getX1()), Math.abs(o.getDimension().getX3())) * 0.5;

			// zu nah dran??? --> Kollision!
			if (Math.abs(getPosition().add(o.getPosition().mal(-1)).length()) < neededplatz)
				return false;

		}
		// Alles glatt gelaufen --> darf mich bewegen
		return true;
	}

	/**
	 * Simple Getter
	 * 
	 * @return strength
	 */
	public int getStrength() {

		int basis_strength = (int) Strength;
		int strength = basis_strength;
		// Wenn sich mehrere Blocker zusammen tun--> Staerker
		for (Objekt o : SuperMain.level.objekte) {
			if (o == this || o.getDimension() == null)
				continue;
			if (o instanceof monster.master && o.getClass().toString().equals(this.getClass().toString())) {
				monster.master enemy = (monster.master) o;
				// staerke addiert sich wenn sie mit ner tolleranz von einer
				// halben laenge zusammen sind
				double neededplatz = 1.5 * Math.max(Math.abs(o.getDimension().getX1()), Math.abs(o.getDimension()
						.getX3()));
				if (Math.abs(getPosition().add(o.getPosition().mal(-1)).length()) < neededplatz) {
					strength += basis_strength;
				}

			} else {
				continue;
			}

		}
		// zu nah dran??? --> Kollision!

		return strength;
	}

	/**
	 * Simple Getter
	 * 
	 * @return
	 */
	public Vektor3D[] getWayPoints() {

		return new Vektor3D[] { waypoint };
	}

	// Für die Geschoss-Kollision vorgefertigte ellipsoide Annaeherung
	// verwenden.
	//@override
	public boolean checkCollisionforObjekt(Vektor3D pos) {

		// return skelett.checkCollisionforObjekt(pos.sub(getPosition()));
		// Streck!
		return Geschoss.checkCollisionforObjektAsEllipse(this, pos.sub(new v3(0, skelett.getHeight() * streck, 0)));
	}

	// Aus dem RadarAble Interface
	public int getRadarAppearance() {
		if (Health > 0)
			return RadarAble.MONSTER;
		else
			return RadarAble.INVISABLE;
	}

	// Aus dem RotateAble Interface
	public double getDrehwinkel() {

		return rotation;
	}

	public Vektor3D getDimension() {
		if (Health > 0) {

			try {
				return skelett.getNieten().getDimension().mal(streck);
			} catch (NullPointerException e) {

				return null;
			}
		}
		return null;
	}

	public String toString() {

		return "Blocker";
	}

	public void render() {
		super.render();
		OGL.verschieb(position);
		OGL.rot(rotation - 90, new Vektor3D(0, 1, 0));

		// Dimension auf Normierten Vektor reduzieren

		if (skelett.getNieten() != null && skelett.getNieten().getDimension() != null) {
			OGL.skaliere(new v3(streck, streck, streck));
		}

		if (isAlive()) {
			skelett.render();
		}
		if (skelett.getNieten() != null && skelett.getNieten().getDimension() != null) {
			OGL.skaliere(new v3(streck, streck, streck).reziproke());
		}

		OGL.rot(-rotation + 90, new Vektor3D(0, 1, 0));
		OGL.verschieb(position.negiere());
	}

	// Aus dem Objekt Interface
	public void logic() {
		super.logic();
		// Waypoint einmalig bestimmen.
		if (waypoint == null) {
			waypoint = getPosition();
		}
		// Nur solange das Monster lebt.
		if (isAlive()) {
			// Hoechste Prioritaet: Ich wurde gerufen
			if (isgecallt()) {

				// ich bin in diesem tick gecallt worden
				if (first_time_called == 0) {
					first_time_called = System.currentTimeMillis();
				}
				// reaktionszeit abwarten
				if ((first_time_called + Agility * 1000) < System.currentTimeMillis()) {
					// danach Angreifen und NachReaktionsZeitAktion ausfuehren,
					// in diesem Fall andere in meinem radius noch zusaetzlich
					// rufen.
					doAttackRoutine();
					if (RunAfterReaction != null) {
						RunAfterReaction.run();
						// Aktion nur einmalig ausfuehren
						RunAfterReaction = null;
					}
				}
			} else {
				// ich bin nicht gecallt
				// wert zur ermittlung der reaktionszeit wieder initialisieren.
				first_time_called = 0;
				// Player in meinem bewachungsradius?
				if (waypoint.add(LevelPlay.p.getPosition().mal(-1)).length() <= 30 + (int) Sensors*2 - SuperMain.profil.mecha.getStealth()) {

					// Player rausschieben
					if (!attacking)
						first_attack = true;
					else
						first_attack = false;

					attacking = true;
					doAttackRoutine();
				} else {
					// Player ist nicht in meinem Bewachungsradius
					// erster tick nach dem angreifen --> von mir gecallte
					// entfernen
					if (attacking) {
						removeGecallte();
					}
					attacking = false;
					// auf standpunkt zurück
					move_to(waypoint);
				}
			}// \nicht gecallt

		}

	}

	//@override
	public void doCriticalDamage() {
		super.doCriticalDamage();
		crit.play();
		last_critical_dmg = System.currentTimeMillis();
	}

	/**
	 * Uncall all called Monster
	 * 
	 */
	private void removeGecallte() {
		LinkedList<Integer> merk = new LinkedList<Integer>();
		int i = 0;
		for (EnemyCaller v : gecallte) {
			if (v.caller_id == (this.getID()))
				merk.addFirst(i);
			i++;
		}

		for (Integer j : merk) {
			gecallte.remove((int) j);
		}
	}

	/**
	 * Am I called
	 * 
	 * @return
	 */
	private boolean isgecallt() {
		for (EnemyCaller v : gecallte) {
			if (v.enemy_id == (this.getID()))
				return true;
		}
		return false;
	}

	public String getDescription() {

		return "Ruft Artgenossen (" + call_radius
				+ "m Radius) und bildet zusammen mit ihnen eine Mauer\nund schiebt Eindringling aus Bewachungsradius ("
				+ bewachungs_radius + "m Radius von Standpunkt) ";
	}

	// Editor-Optionen
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Blickrichtung";
		case 1:
			return "Bewachungs-Radius";
		case 2:
			return "Monster Level";

		default:
			return "";
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
			return rotation;
		case 1:
			return bewachungs_radius;
		case 2:
			return this.getLevel();
		default:
			return null;
		}
	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			rotation = j;
			break;
		case 1:
			j = (Integer) value;
			bewachungs_radius = j;
			break;
		case 2:
			j = (Integer) value;
			this.setLevel(j);
			break;

		default:
			break;
		}
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			return (j >= 0 && j <= 360);
		case 1:
			int k = (Integer) value;
			return (k > 0 && k <= 100);
		case 2:
			int l = (Integer) value;
			return (l > 0);
		default:
			return false;
		}
	}

}
