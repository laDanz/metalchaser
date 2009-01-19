package Classes;

/**
 * Interface which must be applied by all entities
 * 
 * @author laDanz
 * 
 */
public interface Objekt {

	public static final int BOOL = 0;
	public static final int INT = 1;
	public static final int DOUBLE = 2;
	public static final int STRING = 3;
	public static final int FILE = 4;

	Vektor3D getPosition();

	Vektor3D getDimension();

	int hurt(int by);

	/**
	 * set position of object.<br>
	 * in contrast to<code>setPositionDirectly</code> the hight value of level can be checked and applied correctly
	 * 
	 * @param v
	 */

	void setPosition(Vektor3D v);

	/**
	 * Set position of Objekt<br>
	 * In contrast to<code>setPosition</code>the Vektor3D is inherited directly
	 * 
	 * @param v
	 */

	void setPositionDirectly(Vektor3D v);

	/**
	 * Description for Editor
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Standard method for logical operations like Waypointcalculation and adjournment
	 */
	void logic();

	/**
	 * For Rendering of the object. For working with lists only.
	 */
	void render();

	/**
	 * How many options does a Objekt have?
	 * 
	 * @return amount of options
	 */
	int getOptionCount();

	/**
	 * Description of option 'i'
	 * 
	 * @param i
	 *            Option
	 * @return Description
	 */
	String getOptionDescription(int i);

	/**
	 * Returns type of option
	 * 
	 * @param i
	 *            Option
	 * @return Datatype
	 */
	int getOptionType(int i);

	/**
	 * Range of value<i>value</i> bei der <i>i.</i> Check option.
	 * 
	 * @param i
	 * @param value
	 * @return Is in Range?
	 */
	boolean isInWertebereich(int i, Object value);

	/**
	 * Returns value of option 'i'
	 * 
	 * @param i
	 * @return
	 */
	Object getOptionValue(int i);

	/**
	 * Set value of option 'i'
	 * 
	 * @param i
	 * @param value
	 */

	void setOptionValue(int i, Object value);

	String toString();

	/**
	 * Return ID<br>
	 * Every Objekt has its global ID<br>
	 * Realise by<br>
	 * <code>int id=SuperMain.genID()</code><br>
	 * in the Constructor
	 * 
	 * @return
	 */
	int getID();

	/**
	 * Collides the given point with my coordinates<br>
	 * For Ellipsoids there is a implementation under 'Geschoss'
	 * 
	 * @param pos
	 *            bullet-position
	 * @return doesIdCollide?
	 */
	boolean checkCollisionforObjekt(Vektor3D pos);
}
