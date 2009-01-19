package mechPeck;

import Classes.Vektor3D;

/**
 * Weapon Interface
 * 
 * @author ladanz
 * 
 */
public interface Waffe {

	/**
	 * in Weight-element.
	 * 
	 * @return
	 */
	double getGewicht();

	/**
	 * In percent.<br>
	 * 100% hit the target accurate. X% Allotted by accident on Cone from
	 * -45*[(100-X)/100] to 45䰰*[(100-X)/100] .
	 * 
	 * @return
	 */
	double getGenauigkeit();

	/**
	 * Maximum amount of Munition that can be stored
	 * 
	 * @return
	 */
	int getMaxMunition();

	/**
	 * Current amount of Munition that can be stored
	 * 
	 * @return
	 */
	int getAktuellMunitionCount();

	/**
	 * the point in space where the Bullets start
	 * 
	 * @return
	 */
	Vektor3D getAustrittsPkt();

	/**
	 * Calibre in Stringform.<br>
	 * getter: Munition.getCaliber.equals(Waffe.getCaliber)<br>
	 * Seperate more Calibre with comma
	 * 
	 * @return
	 */
	String getCaliber();

	boolean isFeuerbereit();

	/**
	 * 
	 * @return Was shot successfull?
	 */
	boolean feuer();

	// void render();

	double getPreis();

	/**
	 * In percent;
	 * 
	 * @return
	 */
	double getZustand();

	/**
	 * How many Ammunition-Slots does the Weapon have
	 * 
	 * @return
	 */
	int getSlotCount();

	/**
	 * Amount of Ammunition per Slot
	 * 
	 * @return
	 */
	int getAmountperSlot();

	/**
	 * Simple Getter
	 */
	public double getMechRoF();

	/**
	 * Simple Setter
	 */
	public void setMechRoF(double RoF);

	/**
	 * Simple Getter
	 */
	public double getMechTRoF();

	/**
	 * Simple Setter
	 */
	public void setMechTRoF(double RoF);

	long getLastTimeFeuered();
}
