package anim;

import java.io.BufferedWriter;
import java.io.IOException;

import Classes.Vektor3D;
import Classes.v3;

/**
 * Anglemanagment for the animation.<br>
 * Handles all angles of the mecha.
 * 
 * @author laDanz
 * 
 */
public class WinkelSet {

	private Vektor3D torso;

	private Vektor3D weapon1, weapon2;

	private Vektor3D lOber, lUnter, lFoot;

	private Vektor3D rOber, rUnter, rFoot;

	private double rHydra_stauch, lHydra_stauch, height;

	/**
	 * Default constructor.<br>
	 * Initializes all angles with the value 0.
	 */
	public WinkelSet() {
		torso = new v3();
		weapon1 = new v3();
		weapon2 = new v3();
		lOber = new v3();
		lUnter = new v3();
		lFoot = new v3();
		rOber = new v3();
		rUnter = new v3();
		rFoot = new v3();
		rHydra_stauch = 0;
		height = 0;
		lHydra_stauch = 0;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param winkelset
	 *            The <code>WinkelSet</code> to copy from.
	 */
	public WinkelSet(WinkelSet winkelset) {
		torso = new v3(winkelset.getTorso());
		weapon1 = new v3(winkelset.getWeapon1());
		weapon2 = new v3(winkelset.getWeapon2());
		lOber = new v3(winkelset.getLOber());
		lUnter = new v3(winkelset.getLUnter());
		lFoot = new v3(winkelset.getLFoot());
		rOber = new v3(winkelset.getROber());
		rUnter = new v3(winkelset.getRUnter());
		rFoot = new v3(winkelset.getRFoot());
		rHydra_stauch = winkelset.getRHydra_stauch();
		lHydra_stauch = winkelset.getLHydra_stauch();
		height = winkelset.getHeight();
	}

	/**
	 * Simple getter.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Simple setter.
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Simple getter.
	 */
	public double getRHydra_stauch() {
		return rHydra_stauch;
	}

	/**
	 * Simple setter.
	 */
	public void setRHydra_stauch(double hydra_stauch) {
		rHydra_stauch = hydra_stauch;
	}

	/**
	 * Simple getter.
	 */
	public double getLHydra_stauch() {
		return lHydra_stauch;
	}

	/**
	 * Simple setter.
	 */
	public void setLHydra_stauch(double hydra_stauch) {
		lHydra_stauch = hydra_stauch;
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getTorso() {
		return torso;
	}

	/**
	 * Simple setter.
	 */
	public void setTorso(Vektor3D torso) {
		this.torso = new v3(torso);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getWeapon1() {
		return weapon1;
	}

	/**
	 * Simple setter.
	 */
	public void setWeapon1(Vektor3D weapon1) {
		this.weapon1 = new v3(weapon1);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getWeapon2() {
		return weapon2;
	}

	/**
	 * Simple setter.
	 */
	public void setWeapon2(Vektor3D weapon2) {
		this.weapon2 = new v3(weapon2);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getLOber() {
		return lOber;
	}

	/**
	 * Simple setter.
	 */
	public void setLOber(Vektor3D ober) {
		lOber = new v3(ober);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getLUnter() {
		return lUnter;
	}

	/**
	 * Simple setter.
	 */
	public void setLUnter(Vektor3D unter) {
		lUnter = new v3(unter);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getLFoot() {
		return lFoot;
	}

	/**
	 * Simple setter.
	 */
	public void setLFoot(Vektor3D foot) {
		lFoot = new v3(foot);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getROber() {
		return rOber;
	}

	/**
	 * Simple setter.
	 */
	public void setROber(Vektor3D ober) {
		rOber = new v3(ober);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getRUnter() {
		return rUnter;
	}

	/**
	 * Simple setter.
	 */
	public void setRUnter(Vektor3D unter) {
		rUnter = new v3(unter);
	}

	/**
	 * Simple getter.
	 */
	public Vektor3D getRFoot() {
		return rFoot;
	}

	/**
	 * Simple setter.
	 */
	public void setRFoot(Vektor3D foot) {
		rFoot = new v3(foot);
	}

	/**
	 * Subtracts one <code>WinkelSet</code> from <code>this</code> one.
	 * 
	 * @param winkelset
	 *            The subtrahend.
	 * @return The difference.
	 */
	public WinkelSet minus(WinkelSet winkelset) {
		WinkelSet res = new WinkelSet();
		res.setTorso(this.getTorso().add(winkelset.getTorso().mal(-1)));
		res.setWeapon1(this.getWeapon1().add(winkelset.getWeapon1().mal(-1)));
		res.setWeapon2(this.getWeapon2().add(winkelset.getWeapon2().mal(-1)));
		res.setLOber(this.getLOber().add(winkelset.getLOber().mal(-1)));
		res.setLUnter(this.getLUnter().add(winkelset.getLUnter().mal(-1)));
		res.setLFoot(this.getLFoot().add(winkelset.getLFoot().mal(-1)));
		res.setROber(this.getROber().add(winkelset.getROber().mal(-1)));
		res.setRUnter(this.getRUnter().add(winkelset.getRUnter().mal(-1)));
		res.setRFoot(this.getRFoot().add(winkelset.getRFoot().mal(-1)));
		res.setRHydra_stauch(this.getRHydra_stauch() - winkelset.getRHydra_stauch());
		res.setLHydra_stauch(this.getLHydra_stauch() - winkelset.getLHydra_stauch());
		res.setHeight(this.getHeight() - winkelset.getHeight());
		return res;
	}

	/**
	 * Splits <code> this WinkelSet</code> into <code>teile</code> parts.<br>
	 * This is needed for the animation.
	 * 
	 * @param teile
	 *            The amount of parts to split up into.
	 * @return One part.
	 */
	public WinkelSet teilenauf(int teile) {
		WinkelSet res = new WinkelSet(this);
		res.setTorso(res.getTorso().mal(1. / teile));
		res.setWeapon1(res.getWeapon1().mal(1. / teile));
		res.setWeapon2(res.getWeapon2().mal(1. / teile));
		res.setLOber(res.getLOber().mal(1. / teile));
		res.setLUnter(res.getLUnter().mal(1. / teile));
		res.setLFoot(res.getLFoot().mal(1. / teile));
		res.setROber(res.getROber().mal(1. / teile));
		res.setRUnter(res.getRUnter().mal(1. / teile));
		res.setRFoot(res.getRFoot().mal(1. / teile));
		res.setRHydra_stauch(res.getRHydra_stauch() * (1. / teile));
		res.setLHydra_stauch(res.getLHydra_stauch() * (1. / teile));
		res.setHeight(res.getHeight() * (1. / teile));
		return res;
	}

	/**
	 * Writes this <code>WinkelSet</code> into a file using a BufferedWriter.<br>
	 * 
	 * @param bwriter
	 *            A <code>BufferedWriter</code> associated to a file.
	 * @throws IOException
	 */
	public void save(BufferedWriter bwriter) throws IOException {
		bwriter.write("<Torso>");
		bwriter.newLine();
		bwriter.write(torso.toString());
		bwriter.newLine();
		bwriter.write("</Torso>");
		bwriter.newLine();
		bwriter.write("<Weapon1>");
		bwriter.newLine();
		bwriter.write(weapon1.toString());
		bwriter.newLine();
		bwriter.write("</Weapon1>");
		bwriter.newLine();
		bwriter.write("<Weapon2>");
		bwriter.newLine();
		bwriter.write(weapon2.toString());
		bwriter.newLine();
		bwriter.write("</Weapon2>");
		bwriter.newLine();
		bwriter.write("<lOber>");
		bwriter.newLine();
		bwriter.write(lOber.toString());
		bwriter.newLine();
		bwriter.write("</lOber>");
		bwriter.newLine();
		bwriter.write("<lUnter>");
		bwriter.newLine();
		bwriter.write(lUnter.toString());
		bwriter.newLine();
		bwriter.write("</lUnter>");
		bwriter.newLine();
		bwriter.write("<lFoot>");
		bwriter.newLine();
		bwriter.write(lFoot.toString());
		bwriter.newLine();
		bwriter.write("</lFoot>");
		bwriter.newLine();
		bwriter.write("<rOber>");
		bwriter.newLine();
		bwriter.write(rOber.toString());
		bwriter.newLine();
		bwriter.write("</rOber>");
		bwriter.newLine();
		bwriter.write("<rUnter>");
		bwriter.newLine();
		bwriter.write(rUnter.toString());
		bwriter.newLine();
		bwriter.write("</rUnter>");
		bwriter.newLine();
		bwriter.write("<rFoot>");
		bwriter.newLine();
		bwriter.write(rFoot.toString());
		bwriter.newLine();
		bwriter.write("</rFoot>");
		bwriter.newLine();
		bwriter.write("<rHydra>");
		bwriter.newLine();
		bwriter.write(rHydra_stauch + "");
		bwriter.newLine();
		bwriter.write("</rHydra>");
		bwriter.newLine();
		bwriter.write("<lHydra>");
		bwriter.newLine();
		bwriter.write(lHydra_stauch + "");
		bwriter.newLine();
		bwriter.write("</lHydra>");
		bwriter.newLine();

		bwriter.write("<Height>");
		bwriter.newLine();
		bwriter.write(height + "");
		bwriter.newLine();
		bwriter.write("</Height>");
		bwriter.newLine();

	}

}
