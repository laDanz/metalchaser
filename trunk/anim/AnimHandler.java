package anim;

import java.util.LinkedList;

import main.SuperMain;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Classes.Vektor3D;
import Classes.Zeit_messen;
import Classes.v3;

/**
 * Utility class to load Animations from a XML file.
 * 
 * @author laDanz
 * 
 */

public class AnimHandler extends DefaultHandler {
	private String ssav;

	private Zeit_messen t;

	LinkedList<WinkelSet> res = new LinkedList<WinkelSet>();

	WinkelSet ws;
	WinkelSet verschWS;
	WinkelSet rotWS;

	/**
	 * Default constructor.
	 */
	public AnimHandler() {
	}

	/**
	 * Returns the loaded Animations.
	 * 
	 * @return animations.
	 */
	public LinkedList<WinkelSet> getRes() {
		return res;
	}

	/**
	 * Returns the translation vector.
	 * 
	 * @return translation vector.
	 */
	public WinkelSet getVerschiebWS() {
		return verschWS;
	}

	/**
	 * returns the rotation vector.
	 * 
	 * @return rotation vector.
	 */
	public WinkelSet getRotWS() {
		return rotWS;
	}

	@Override
	public void startDocument() {
		ws = new WinkelSet();
		t = new Zeit_messen();
		t.start();
	}

	@Override
	public void endDocument() {
		int zeit = (int) t.ende();
		// t.ausgabe();

	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {

		string_verarbeitung(arg2 + ": " + ssav);

	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		ssav = "";
		if (qName.equals("Auftragstatus")) {
			SuperMain.initAuftrage();
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		for (int i = start; i < (start + length); i++) {

			if (!(ch[i] == '\n'))
				ssav += ch[i];

		}
	}

	/**
	 * Compute the XML file.
	 * 
	 * @param s
	 *            A string from the file.
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	private void string_verarbeitung(String s) {

		if (s.startsWith("lHydra")) {
			s = s.substring(8, s.length());
			ws.setLHydra_stauch(Double.parseDouble(s));
			return;
		}
		if (s.startsWith("rHydra")) {
			s = s.substring(8, s.length());
			ws.setRHydra_stauch(Double.parseDouble(s));
			return;
		}
		if (s.startsWith("Height")) {
			s = s.substring(8, s.length());
			ws.setHeight(Double.parseDouble(s));
			return;
		}
		if (s.startsWith("WinkelSet")) {
			res.add(new WinkelSet(ws));
			return;

		}
		if (s.startsWith("VerschiebWS")) {
			verschWS = new WinkelSet(ws);
			return;

		}
		if (s.startsWith("RotWS")) {
			rotWS = new WinkelSet(ws);
			return;

		}
		for (String possib : new String[] { "Torso", "Weapon1", "Weapon2" })
			if (s.contains(possib)) {
				s = s.substring(possib.length() + 3, s.length() - 1);
				Vektor3D v = new v3(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]), Double
						.parseDouble(s.split(",")[2]));
				if (possib.equals("Torso")) {
					ws.setTorso(v);
					return;
				}
				if (possib.equals("Weapon1")) {
					ws.setWeapon1(v);
					return;
				}
				if (possib.equals("Weapon2")) {
					ws.setWeapon2(v);
					return;
				}

			}

		for (String possib : new String[] { "Foot", "Unter", "Ober" })
			if (s.contains(possib)) {
				String orgi = new String(s);
				s = s.substring(possib.length() + 4, s.length() - 1);
				Vektor3D v = new v3(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]), Double
						.parseDouble(s.split(",")[2]));

				if (orgi.startsWith("rFoot")) {
					ws.setRFoot(v);
					return;
				} else if (orgi.startsWith("lFoot")) {
					ws.setLFoot(v);
					return;
				} else if (orgi.startsWith("rUnter")) {
					ws.setRUnter(v);
					return;
				} else if (orgi.startsWith("lUnter")) {
					ws.setLUnter(v);
					return;
				} else if (orgi.startsWith("rOber")) {
					ws.setROber(v);
					return;
				} else if (orgi.startsWith("lOber")) {
					ws.setLOber(v);
					return;
				}

			}

	}
}
