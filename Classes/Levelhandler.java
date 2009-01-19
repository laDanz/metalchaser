package Classes;

import java.util.LinkedList;

import main.Editor;
import main.Laden;
import main.SuperMain;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Fenster.DebugFenster;

/**
 * A xml interpreter for loading levels.
 * 
 * @author laDanz
 * 
 */
class Levelhandler extends DefaultHandler {
	private String ssav;

	private Level level;
	LinkedList<quad> quads;
	LinkedList<Objekt> objekte;
	quad quad;
	Objekt objekt;
	int optionCount = 0;
	boolean calced = false;
	private Zeit_messen t;

	/**
	 * Constructor.
	 * 
	 * @param l
	 *            A level.
	 */
	public Levelhandler(Level l) {
		level = l;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Level getLevel() {
		return level;
	}

	@Override
	public void startDocument() {
		level = new Level();

		quad = null;
		objekt = null;
		quads = new LinkedList<quad>();
		objekte = new LinkedList<Objekt>();
		t = new Zeit_messen();
		t.start();
	}

	@Override
	public void endDocument() {
		int zeit = (int) t.ende();
		// t.ausgabe();

		DebugFenster.settimetoload(zeit);
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {

		string_verarbeitung(arg2 + ": " + ssav);

	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		ssav = "";

		if (qName.equals("Objekt")) {
			objekt = null;
			optionCount = 0;
		}
		if (qName.equals("quad")) {
			quad = new quad();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		for (int i = start; i < (start + length); i++) {

			if (!(ch[i] == '\n'))
				ssav += ch[i];

		}
	}

	long last_dot = 0;

	/**
	 * Compute strings from the file.
	 * 
	 * @param s
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	private void string_verarbeitung(String s) {

		if (System.currentTimeMillis() - last_dot > 1000) {
			last_dot = System.currentTimeMillis();
			Laden.insertDot();
		}

		if (s.startsWith("Klasse")) {
			// quads fertig, dann height berechnen#
			if (!calced) {
				// level.addQuads(quads);
				level.calc_height();
				calced = true;
				SuperMain.level.copy(level);
			}
			s = s.substring(14, s.length());
			try {
				ClassLoader cl = ClassLoader.getSystemClassLoader();
				Class c3 = cl.loadClass(s);
				// System.out.println(s);

				objekt = (Objekt) c3.newInstance();
			} catch (Exception ne) {
				ne.printStackTrace();
				SuperMain.out(ne);
			}
		} else if (s.startsWith("Position")) {
			s = s.substring(11, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor3D v = new Vektor3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]));
			objekt.setPositionDirectly(v);
			if (objekt.getClass().toString().equalsIgnoreCase("class spezielles.startpunkt")) {
				// level.startPosition=v.add(new
				// Vektor3D((level.count()-1)*80,0,0));
			}

		} else if (s.startsWith("Objekt")) {
			level.addObjekt(objekt);
			if (!Editor.isActualGameState()) {
				// Laden.insertDot();
			}
		} else if (s.startsWith("quad")) {
			quads.add(quad);
			level.addQuad(quad);
			if (quad.getMaxX() > level.width)
				level.width = quad.getMaxX();
			if (-quad.getMinZ() > level.depth)
				level.depth = -quad.getMinZ();
		} else if (s.startsWith("ol")) {
			s = s.substring(5, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor3D v = new Vektor3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]));
			quad.ol = v;
		} else if (s.startsWith("ul")) {
			s = s.substring(5, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor3D v = new Vektor3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]));
			quad.ul = v;
		} else if (s.startsWith("or")) {
			s = s.substring(5, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor3D v = new Vektor3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]));
			quad.or = v;
		} else if (s.startsWith("ur")) {
			s = s.substring(5, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor3D v = new Vektor3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]));
			quad.ur = v;
		} else if (s.startsWith("texture")) {
			s = s.substring(9, s.length() - 0);
			SuperMain.toLoad.add(s);
		} else if (s.startsWith("tex")) {
			s = s.substring(5, s.length() - 0);
			quad.tex = Integer.parseInt(s);
		} else if (s.startsWith("TexCoord1")) {
			s = s.substring(12, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor2D v = new Vektor2D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]));
			quad.setTexCoord(v);
		} else if (s.startsWith("TexCoord2")) {
			s = s.substring(12, s.length() - 1);
			String[] s1 = s.split(",");
			Vektor2D v = new Vektor2D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]));
			quad.setTexCoord2(v);
		}

		else if (s.startsWith("option")) {
			if (optionCount < objekt.getOptionCount()) {
				s = s.substring(8, s.length() - 0);
				switch (objekt.getOptionType(optionCount)) {
				case Objekt.BOOL:
					objekt.setOptionValue(optionCount, (s.equals("true")));
					break;
				case Objekt.STRING:
					objekt.setOptionValue(optionCount, s);
					break;
				case Objekt.INT:
					objekt.setOptionValue(optionCount, Integer.parseInt(s));
					break;
				case Objekt.DOUBLE:
					objekt.setOptionValue(optionCount, Double.parseDouble(s));
					break;
				case Objekt.FILE:
					objekt.setOptionValue(optionCount, s);
					break;
				default:
					break;
				}
			}
			optionCount++;

		}
	}
}
