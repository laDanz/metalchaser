package Classes;

import java.io.Serializable;

/**
 * A utility class for variable-triplets.<br>
 * Implements general functions.
 * 
 * @author laDanz
 * 
 */
public class Vektor3D implements Comparable<Vektor3D>, Serializable {
	private double x1, x2, x3;

	public static Vektor3D x_axis = new Vektor3D(1, 0, 0);
	public static Vektor3D y_axis = new Vektor3D(0, 1, 0);
	public static Vektor3D z_axis = new Vektor3D(0, 0, 1);

	/**
	 * Constructor.<br>
	 * 
	 * Takes three <code>double</code>s as values.
	 * 
	 * @param x1
	 * @param x2
	 * @param x3
	 */
	public Vektor3D(double x1, double x2, double x3) {
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
	}

	/**
	 * Constructor.<br>
	 * Using a <code>Vektor2D</code> and a double value to create a
	 * <code>Vektor3D</code>.
	 * 
	 * @param v
	 *            A <code>Vektor2D</code>
	 * @param x3
	 *            A <code>double</code> value.
	 */
	public Vektor3D(Vektor2D v, double x3) {
		this(v.getX1(), v.getX2(), x3);
	}

	/**
	 * Default constructor.<br>
	 * Sets all values to 0.
	 */
	public Vektor3D() {
		this(0, 0, 0);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param v
	 */
	public Vektor3D(Vektor3D v) {
		this.x1 = new Double(v.getX1());
		this.x2 = new Double(v.getX2());
		this.x3 = new Double(v.getX3());

	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * Simple Setter.
	 * 
	 * @return
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * Compare <code>this Vektor3D</code> to another one.
	 * 
	 * @return 0 if they have the same values, -1 else
	 */
	public int compareTo(Vektor3D arg0) {

		if (arg0.x1 == this.x1 && this.x2 == arg0.x2 && this.x3 == arg0.x3) {
			return 0;

		} else
			return (-1);
	}

	/**
	 * Calculates the length of this vector.
	 * 
	 * @return the length.
	 */
	public double length() {
		return Math.sqrt(this.getX1() * this.getX1() + this.getX2() * this.getX2() + this.getX3() * this.getX3());
	}

	/**
	 * Returns this vector normalized.
	 * 
	 * @return the normalized vector.
	 */
	public Vektor3D normierter() {
		return this.mal(1. / this.length());
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * Simple Setter.
	 * 
	 * @return
	 */
	public void setX2(double x2) {
		this.x2 = x2;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getX3() {
		return x3;
	}

	/**
	 * Simple Setter.
	 * 
	 * @return
	 */
	public void setX3(double x3) {
		this.x3 = x3;
		// TODO funzt so nich
	}

	/**
	 * Sums this vector with another one component by component.
	 * 
	 * @param other
	 *            Another vector.
	 * @return The sum vector.
	 */
	public Vektor3D add(Vektor3D other) {
		return new Vektor3D(this.x1 + other.x1, this.x2 + other.x2, this.x3 + other.x3);
	}

	/**
	 * Subtracts a vector from this one and returns the result.
	 * 
	 * @author jan
	 * @param other
	 *            Another vector.
	 * @return The resulting vector.
	 */
	public Vektor3D sub(Vektor3D other) {
		return new Vektor3D(this.x1 - other.x1, this.x2 - other.x2, this.x3 - other.x3);
	}

	/**
	 * Builds component by component the reciprocal and returns that as a new
	 * vector.
	 * 
	 * @return A new vector where every component is the reciprocal of this
	 *         vector.
	 */
	public Vektor3D reziproke() {
		return new Vektor3D(1 / this.x1, 1 / this.x2, 1 / this.x3);
	}

	//@override
	public boolean equals(Object obj) {
		if (obj instanceof Vektor3D) {
			Vektor3D new_name = (Vektor3D) obj;
			if (new_name.getX1() == this.getX1()) {
				if (new_name.getX2() == this.getX2()) {
					if (new_name.getX3() == this.getX3()) {
						return true;

					}
				}
			}
		}
		return false;
	}

	/**
	 * Every component is multiplied by -1.
	 * 
	 * @return A new vector where every component was multiplied by -1.
	 */
	public Vektor3D negiere() {
		return new Vektor3D(-this.x1, -this.x2, -this.x3);
	}

	/**
	 * Every component of this vector is multiplied with i.
	 * 
	 * @param i
	 *            A factor.
	 * @return A new vector.
	 */
	public Vektor3D mal(double i) {
		return new Vektor3D(i * this.x1, i * this.x2, i * this.x3);
	}

	/**
	 * Rounds every component to <code>stellen</code> digits after the point.
	 * <br>
	 * 
	 * 
	 * @param stellen
	 *            Number of digits after the point.
	 * @return
	 */
	public Vektor3D round(int stellen) {
		return new Vektor3D(((int) (Math.pow(10, stellen) * this.x1)) / Math.pow(10, stellen), ((int) (Math.pow(10,
				stellen) * this.x2))
				/ Math.pow(10, stellen), ((int) (Math.pow(10, stellen) * this.x3)) / Math.pow(10, stellen));
	}

	/**
	 * Multiplies component by component and returns the resulting vector.
	 * 
	 * @param other
	 *            Another vector.
	 * @return Resulting vector.
	 */
	public Vektor3D mal(Vektor3D other) {
		return new Vektor3D(other.getX1() * this.x1, other.getX2() * this.x2, other.getX3() * this.x3);
	}

	public String toString() {
		return "(" + this.getX1() + ", " + this.getX2() + ", " + this.getX3() + ")";
	}

}
