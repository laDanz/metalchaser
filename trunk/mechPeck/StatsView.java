package mechPeck;

import java.util.Collection;
import java.util.LinkedList;

import main.SuperMain;
import Classes.OGL;
import Classes.Profil;
import Classes.Vektor3D;
import Classes.myButton;
import Classes.myColor;
import Classes.myText;
import Classes.v3;
/**
 * Class that handles viewing of the stats
 * @author danzi
 *
 */
public class StatsView {

	myButton[] stats_b = new myButton[5];

	myButton[] stats_b_plus = new myButton[5];

	myButton[] stats_b_minus = new myButton[5];

	myButton verfugbar;

	LinkedList<myButton> buttons;

	/**
	 * Initialises a button, that increase the stats
	 * @param einheit
	 */
	public StatsView(double einheit) {
		buttons = new LinkedList<myButton>();
		initButtons(einheit);

	}

	public StatsView() {
		this(1);
	}

	private void initButtons(double einheit) {
		for (int i = 0; i < stats_b.length; i++) {
			stats_b[i] = new myButton(null, -1 * einheit, -(i + 1) * einheit, 4 * einheit, 1 * einheit, onStatsButton(
					i, 0), getText(i));
			stats_b[i].setVisible(false);
			stats_b_plus[i] = new myButton(null, 6.5 * einheit, -(i + 1) * einheit, 1 * einheit, 1 * einheit,
					onStatsButton(i, 1), "+");
			stats_b_minus[i] = new myButton(null, 8.5 * einheit, -(i + 1) * einheit, 1 * einheit, 1 * einheit,
					onStatsButton(i, -1), "-");
			buttons.add(stats_b[i]);
			buttons.add(stats_b_plus[i]);
			// buttons.add(stats_b_minus[i]);
		}
		verfugbar = new myButton("img/button/button", 0 * einheit, -8 * einheit, 4 * einheit, 1 * einheit, null,
				"avaiable:");

	}

	private Runnable onStatsButton(final int i, final int change) {
		return new Runnable() {
			public void run() {
				if (change > SuperMain.profil.mecha.getVerteilbare_punkte()) {
					return;
				}
				SuperMain.toRun.add(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						Profil.mecha.deleteTempValues();
						SuperMain.profil.mecha.setVerteilbare_punkte(-change
								+ SuperMain.profil.mecha.getVerteilbare_punkte());
						switch (i) {
						case 0:
							SuperMain.profil.mecha.incArms(change);
							break;
						case 1:
							SuperMain.profil.mecha.incElectronics(change);
							break;
						case 2:
							SuperMain.profil.mecha.incHydraulics(change);
							break;
						case 3:
							SuperMain.profil.mecha.incMechanics(change);
							break;
						case 4:
							SuperMain.profil.mecha.incProtection(change);
							break;

						default:

						}
					}
				});
			}
		};
	}

	/**
	 * Returns the Stats-Name
	 * @param i
	 * @return stats-name
	 */
	public static String getText(int i) {
		switch (i) {
		case 0:
			return "Arms";
		case 1:
			return "Electronics";
		case 2:
			return "Hydraulics";
		case 3:
			return "Mechanics";
		case 4:
			return "Protection";

		default:
			return "fehler";
		}
	}

	/**
	 * Handles stat-descriptions on mouseover. /n produces new lines
	 * @param i
	 * @return stat-description
	 */
	public static String getDiscription(int i) {
		switch (i) {
		case 0: // Arms
			return "Damage: +" + round(Profil.mecha.getDamage() - Profil.mecha.getTDamage()) * 5 + "% (+"
					+ Profil.mecha.getTDamage()*5 + "%)/n" + 
					"Piercing: +" + round(Profil.mecha.getPiercing() - Profil.mecha.getTPiercing()) + " (+"
					+ Profil.mecha.getTPiercing() + ")/n" + 
					"CriticalDamage: +"	+ round(Profil.mecha.getCriticalDamage() - Profil.mecha.getTCriticalDamage())*2 + "% (+"
					+ Profil.mecha.getTCriticalDamage()*2 + "%)";
		case 1: // Electronics
			return "Sensors: +" + round(Profil.mecha.getSensors() - Profil.mecha.getTSensors()) + " (+"
					+ Profil.mecha.getTSensors() + ")/n" + 
					"Stealth: +" + round(Profil.mecha.getStealth() - Profil.mecha.getTStealth()) + " (+"
					+ Profil.mecha.getTStealth() + ")/n" + 
					"Rocket Acc.: +" + round(Profil.mecha.getRocketLauncherAccuracy() - Profil.mecha.getTRocketLauncherAccuracy())*2
					+ "% (+" + Profil.mecha.getTRocketLauncherAccuracy()*2 + "%)";

		case 2: // Hydraulics
			return "Strength: +" + round(Profil.mecha.getStrength() - Profil.mecha.getTStrength()) + " (+"
					+ Profil.mecha.getTStrength() + ")/n" + 
					"Speed: +" + round(5 + Profil.mecha.getSpeed() - Profil.mecha.getTSpeed()) + "km/h (+"
					+ Profil.mecha.getTSpeed() + "km/h)/n" + 
					"Gatling Acc.: +" + round(Profil.mecha.getGatlingAccuracy() - Profil.mecha.getTGatlingAccuracy())*5 + "% (+"
					+ Profil.mecha.getTGatlingAccuracy()*5 + "%)";
		case 3: // Mechanics
			return "Reloading Time: -" + round(Profil.mecha.getReloadingTime() - Profil.mecha.getTReloadingTime()) + "% (-"
					+ Profil.mecha.getTReloadingTime() + "%)/n" + 
					"Agility: +" + round(Profil.mecha.getAgility() - Profil.mecha.getTAgility()) + " (+"
					+ Profil.mecha.getTAgility() + ")/n" + 
					"Rate of Fire: +" + round(Profil.mecha.getRateOfFire() - Profil.mecha.getTRateOfFire())*2 + "% (+"
					+ Profil.mecha.getTRateOfFire()*2 + "%)";
		case 4: // Protection
			return "Health: +" + round(Profil.mecha.getHealth() - Profil.mecha.getTHealth()) + " (+"
					+ Profil.mecha.getTHealth() + ")/n" + 
					"Armor:" + round(Profil.mecha.getArmor() - Profil.mecha.getTArmor()) + " (+" 
					+ Profil.mecha.getTArmor() + ")/n" + 
					"Crit.-Protection: +" + round(Profil.mecha.getCriticalHitProtection() - Profil.mecha.getTCriticalHitProtection()) + "%"
					+ " (+" + Profil.mecha.getTCriticalHitProtection() + "%)";

		default:
			return "fehler";
		}
	}

	/**
	 * Rounts to one decimal place
	 * @param d number to round
	 * @return rounded number
	 */
	private static double round(double d) {

		return ((int) (d * 10)) / 10.;
	}

	Vektor3D versch = new v3(6, -1.5, 0);

	public void render() {
		render(1);
	}
	
	/**
	 * Renders specified text
	 * @param einheit
	 */
	public void render(double einheit) {
		myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
		myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);

		/*
		 * OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0)); myText.out("SCORE", new
		 * v3(1 * cm, 1 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm),
		 * myColor.BGREY, 1.1, 0); myText.out("Time: " +
		 * SuperMain.statistics.getTimeUsedString(), new v3(1 * cm, -1 * cm, 0),
		 * new v3( 0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0); int
		 * aim = SuperMain.statistics.getAim(); myText.out("Aim (hits/all): " +
		 * SuperMain.statistics.getHitsGiven() + "/" +
		 * (SuperMain.statistics.getShootsFired()) + " (" + aim + "%)", new v3(1 *
		 * cm, -2 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE,
		 * 1.1, 0);
		 */

		OGL.verschieb(versch.mal(einheit));

		myText.out("MECH STATS", new v3(1 * einheit, 1 * einheit, 0), new v3(0.4 * einheit, 0.5 * einheit,
				0.5 * einheit), myColor.BGREY, 1.1, 0);

		for (int i = 0; i < stats_b.length; i++) {
			// stats_b[i].render();
			myText.out(getText(i), new v3(stats_b[i].getX() + 2 * einheit, stats_b[i].getY(), 0), new v3(0.5 * einheit,
					0.5 * einheit, 0.5 * einheit), new v3(1, 1, 1), 1.1, 0);

			stats_b_plus[i].render();
			Vektor3D color = myColor.WHITE;
			if (stats_b_plus[i].getId() == SuperMain.selection) {
				color = myColor.BGREY;
			}
			myText.out("+", new v3(stats_b_plus[i].getX() + 2.5 * einheit, stats_b_plus[i].getY(), 0), new v3(
					0.5 * einheit, 0.5 * einheit, 0.5 * einheit), color, 1.1, 0);

			// stats_b_minus[i].render();
			color = myColor.WHITE;
			// if (stats_b_minus[i].getId() == SuperMain.selection)
			// color = myColor.RED;
			// myText.out("-", new v3(stats_b_minus[i].getX() + 0.5 * einheit,
			// stats_b_minus[i].getY(), 0), new v3(
			// 0.5 * einheit, 0.5 * einheit, 0.5 * einheit), color, 1.1, 0);
			myText.out(getValue(i), new v3(8 * einheit, -(i + 1) * einheit, 0), new v3(0.5 * einheit, 0.5 * einheit,
					0.5 * einheit), new v3(1, 1, 1), 1.1, 0);
			OGL.setColor(myColor.WHITE);
			if (stats_b_plus[i].getId() == SuperMain.selection || stats_b_minus[i].getId() == SuperMain.selection
					|| stats_b[i].getId() == SuperMain.selection) {
				Profil.mecha.bag.bagtext = getDiscription(i);
			}
		}
		// verfugbar.render();
		myText.out(SuperMain.profil.mecha.getVerteilbare_punkte() + "", new v3(8 * einheit, -8 * einheit, 0), new v3(
				0.5 * einheit, 0.5 * einheit, 0.5 * einheit), new v3(1, 1, 1), 1.1, 0);
		OGL.verschieb(versch.mal(einheit).negiere());
	}

	/**
	 * Returns the value of the specified attribute
	 * @param i
	 * @return
	 */
	private String getValue(int i) {
		switch (i) {
		case 0:
			return SuperMain.profil.mecha.getArms() + "";
		case 1:
			return SuperMain.profil.mecha.getElectronics() + "";
		case 2:
			return SuperMain.profil.mecha.getHydraulics() + "";
		case 3:
			return SuperMain.profil.mecha.getMechanics() + "";
		case 4:
			return SuperMain.profil.mecha.getProtection() + "";
		default:
			return "fehler";
		}
	}

	/**
	 * Returns all buttons
	 * @return all buttons
	 */
	public Collection<myButton> getAllButtons() {

		return buttons;
	}

}
