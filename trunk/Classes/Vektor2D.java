package Classes;

/**
 * A utility class like <code>Vektor3D</code> only with 2 parameters.
 * 
 * @author ladanz
 * 
 */
public class Vektor2D extends Vektor3D {

	public Vektor2D(double x1, double x2) {
		super(x1, x2, 0);
	}

	public Vektor2D(Vektor3D add) {
		this(add.getX1(), add.getX2());
	}

	@Override
	public Vektor2D add(Vektor3D other) {

		return new Vektor2D(super.add(other));
	}

}