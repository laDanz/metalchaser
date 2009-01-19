package Classes;

/**
 * A shorter version of the <code>Vektor3D</code>.<br>
 * Only for easier writing.
 * 
 * @author laDanz
 * @deprecated Instead of this <code>Vektor3D</code> should be used to avoid
 *             inconsistency.
 */
public class v3 extends Vektor3D {

	public v3() {
		super();
	}

	public v3(double x1, double x2, double x3) {
		super(x1, x2, x3);

	}

	public v3(Vektor2D v, double x3) {
		super(v, x3);

	}

	public v3(Vektor3D v) {
		super(v);
	}

}
