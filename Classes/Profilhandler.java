package Classes;

import main.SuperMain;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility class for loading a profile out of a xml file.
 * 
 * @author laDanz
 * 
 */
class Profilhandler extends DefaultHandler {
	private String ssav;

	// private Level level;
	// LinkedList<quad> quads;
	// LinkedList<Objekt> objekte;
	// quad quad;
	// Objekt objekt;
	// int optionCount = 0;
	Profil p = null;
	private Zeit_messen t;

	/**
	 * Constructor.
	 */
	public Profilhandler() {

	}

	public Profil getProfil() {
		return p;
	}

	@Override
	public void startDocument() {
		// System.out.println("Document starts.");

		p = null;
		t = new Zeit_messen();
		t.start();
	}

	@Override
	public void endDocument() {
		// System.out.println("Document ends.");
		int zeit = (int) t.ende();
		// t.ausgabe();

	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {

		// System.out.println("</" + arg2 + ">");
		string_verarbeitung(arg2 + ": " + ssav);

	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		ssav = "";
		// System.out.print("<" + qName + ">");
		if (qName.equals("Auftragstatus")) {
			SuperMain.initAuftrage();
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// System.out.print( "Inhalt: " );

		for (int i = start; i < (start + length); i++) {

			if (!(ch[i] == '\n'))
				ssav += ch[i];

		}
		// System.out.print(ssav);
	}

	@SuppressWarnings( { "static-access", "unchecked" })
	private void string_verarbeitung(String s) {

		if (s.startsWith("Name")) {
			s = s.substring(6, s.length());
			p = new Profil(s);

		} else if (s.startsWith("Credits")) {
			s = s.substring(9, s.length());
			p.setCredits(Double.parseDouble(s));

		} else if (s.startsWith("SoundGain")) {
			s = s.substring(11, s.length());
			p.setSound_gain(Integer.parseInt(s));

		} else if (s.startsWith("Ruf")) {
			s = s.substring(5, s.length());
			p.setRuf(Double.parseDouble(s));

		} else if (s.startsWith("AuftragNr")) {
			s = s.substring(9, s.length()).replace(" ", "");
			// nr: status
			String ss = s.split(":")[0];
			int id = Integer.parseInt(ss);
			ss = s.split(":")[1];
			int state = Integer.parseInt(ss);
			// for (Auftrag a : SuperMain.auftrag) {
			// if (a.getId() == id) {
			// a.setStatus(state);
			// }
			// }
		} else if (s.startsWith("Bildpfad")) {
			s = s.substring(10, s.length());
			p.setBild(s);

		} else if (s.startsWith("Wappenpfad")) {
			s = s.substring(12, s.length());
			p.setWappen(s);

		} else if (s.startsWith("Stimmart")) {
			s = s.substring(10, s.length());
			int i = Integer.parseInt(s);
			p.setStimmart(i);

		} else if (s.startsWith("Taste")) {
			int id = Integer.parseInt(s.substring(5, s.indexOf(":")));
			s = s.substring(7, s.length());
			p.steuerung.setBelegung(id, s);

		}
	}
}
