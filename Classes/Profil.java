package Classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import main.SuperMain;
import mechPeck.Mecha;

/**
 * Represents the profile.
 * 
 * @author laDanz
 * 
 */
public class Profil {

	private String bild_path;
	private Texture bild;
	private String wappen_path;
	private Texture wappen;
	private int stimm_art;
	private String name;

	/**
	 * Sound zwischen [0,10]
	 */
	private int sound_gain;

	private static LinkedList<String> bilder;
	private static LinkedList<String> wappens;

	private double credits = 0;
	private double ruf = 0;

	private HashMap<Integer, Integer> auftrag_status;

	public Steuerung steuerung;

	public static Mecha mecha = null;

	/**
	 * Constructor.<br>
	 * Initializes images and emblem.
	 * 
	 * @param name
	 *            The profile name.
	 */
	public Profil(String name) {
		// FIXME was wenn das wappen/bild net mehr existiert?
		getBild(0);
		getWappen(0);
		stimm_art = 0;
		sound_gain = 5;
		this.name = name;
		steuerung = new Steuerung();
		// mecha = new Mecha(); erst durch init Mecha wird der Mecha
		// initialisiert!
		auftrag_status = new HashMap<Integer, Integer>();
	}

	/**
	 * Simple Getter.
	 */
	public int getSound_gain() {
		return sound_gain;
	}

	/**
	 * Simple Setter.
	 */
	public void setSound_gain(int sound_gain) {
		this.sound_gain = sound_gain;
		myAudio.setGain(sound_gain * 10);
	}

	/**
	 * Initialize the mecha 3D objects.
	 */
	public void initMecha() {
		if (mecha == null)
			mecha = new Mecha();

	}

	/**
	 * Copy constructor.
	 * 
	 * @param p
	 *            Another Profile.
	 */
	public Profil(Profil p) {
		this(p.getName());
		this.setBild(p.bild_path);
		this.setWappen(p.wappen_path);
		this.stimm_art = p.stimm_art;
		this.steuerung = new Steuerung(p.steuerung);
		setSound_gain(p.getSound_gain());
	}

	/**
	 * Save the profile to a file.<br>
	 */
	public void save() {
		try {
			// existiert der Ordner ???
			File f = new File(main.SuperMain.ordner + "profile/" + name);
			if (!(f.exists() && f.isDirectory())) {
				f.mkdir();
			}
			FileWriter fw = new FileWriter(main.SuperMain.ordner + "profile/" + name + "/conf.cfg");
			BufferedWriter f1 = new BufferedWriter(fw);
			f1.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			f1.newLine();
			f1.write("<Profil>");
			f1.newLine();
			f1.write("<Name>");
			f1.write(name);
			f1.write("</Name>");
			f1.newLine();
			f1.write("<SoundGain>");
			f1.write(sound_gain + "");
			f1.write("</SoundGain>");
			f1.newLine();
			f1.write("<Bildpfad>");
			f1.write(bild_path.replace('\\', '/'));
			f1.write("</Bildpfad>");
			f1.newLine();
			f1.write("<Wappenpfad>");
			f1.write(wappen_path.replace('\\', '/'));
			f1.write("</Wappenpfad>");
			f1.newLine();
			f1.write("<Stimmart>");
			f1.write(stimm_art + "");
			f1.write("</Stimmart>");
			f1.newLine();
			f1.write("<Credits>");
			f1.write(credits + "");
			f1.write("</Credits>");
			f1.newLine();
			f1.write("<Ruf>");
			f1.write(ruf + "");
			f1.write("</Ruf>");
			f1.newLine();
			f1.write("<Auftragstatus>");
			f1.newLine();
			// for (Auftrag a : SuperMain.auftrag) {
			// f1.write("<AuftragNr" + a.getId() + ">");
			// f1.write("" + a.getStatus());
			// f1.write("</AuftragNr" + a.getId() + ">");
			// f1.newLine();
			// }
			f1.write("</Auftragstatus>");
			f1.newLine();
			steuerung.save(f1);
			f1.write("</Profil>");
			f1.close();
		} catch (IOException e) {
			e.printStackTrace();
			SuperMain.out(e);
			throw new RuntimeException("Unerwarteter Fehler beim Profil speichern.");

		}
	}

	/**
	 * Loads a profile out of a file.
	 * 
	 * @param filename
	 *            The source filename.
	 * @return The loaded profile.
	 */
	public static Profil load(String filename) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		Profil p = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			e.printStackTrace();
			SuperMain.out(e);
			throw new RuntimeException("XML-Init Fehler beim Laden im Modul Profil: " + e);
		}

		Profilhandler handler = new Profilhandler();
		try {
			saxParser.parse(new File(filename), handler);

			p = handler.getProfil();

		} catch (Exception e) {
			SuperMain.out("Fehler beim Laden im Modul Profil: " + e);
			e.printStackTrace();
			SuperMain.out(e);

		}

		return p;
	}

	private void getBild(int i) {
		if (bilder == null) {
			bilder = getImagesFromDirectory(main.SuperMain.ordner + "img/profile/avatare/");
		}
		while (i < 0) {
			i += Math.max(1, bilder.size());
		}
		while (i >= bilder.size()) {
			i -= bilder.size();
		}
		setBild(bilder.get(i));

	}

	private void getWappen(int i) {
		if (wappens == null) {
			wappens = getImagesFromDirectory(main.SuperMain.ordner + "img/profile/wappen/");
		}
		while (i < 0) {
			i += Math.max(1, wappens.size());
		}
		while (i >= wappens.size()) {
			i -= wappens.size();
		}
		setWappen(wappens.get(i));

	}

	private LinkedList<String> getImagesFromDirectory(String string) {
		LinkedList<String> res = new LinkedList<String>();
		File f = new File(string);
		for (File f_ : f.listFiles()) {
			if (f_.getAbsolutePath().toLowerCase().endsWith("jpg")
					|| f_.getAbsolutePath().toLowerCase().endsWith("png")
					|| f_.getAbsolutePath().toLowerCase().endsWith("bmp")) {
				res.add(f_.getAbsolutePath());
			}
		}

		return res;
	}

	/**
	 * Set the profile image by file path.
	 * 
	 * @param string
	 *            A file path.
	 */
	public void setBild(String string) {
		string = string.replace('\\', '/');
		if (!string.toLowerCase().startsWith("img/profile")) {
			this.bild_path = string.substring(string.indexOf("img/profile"));
		} else {
			this.bild_path = string;
		}
		SuperMain.toRun.add(new Runnable() {
			public void run() {

				try {
					Profil.this.bild = main.SuperMain.loadTex(bild_path);
				} catch (IOException e) {
					e.printStackTrace();
					SuperMain.out(e);
					Profil.this.bild_path = null;
					Profil.this.bild = null;

				}
			}
		});

	}

	/**
	 * Set the profile emblem by file path.
	 * 
	 * @param string
	 *            The source file path.
	 */
	public void setWappen(String string) {
		string = string.replace('\\', '/');
		if (!(string.toLowerCase().startsWith("img/profile"))) {
			this.wappen_path = string.substring(string.indexOf("img/profile"));
		} else {
			this.wappen_path = string;
		}
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					Profil.this.wappen = main.SuperMain.loadTex(wappen_path);
				} catch (IOException e) {
					Profil.this.wappen_path = null;
					Profil.this.wappen = null;
					throw new RuntimeException("Couldn't load wappen ! ");

				}
			}
		});

	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Texture getBild() {
		// TODO Auto-generated method stub
		return bild;
	}

	public String getBildPathString() {
		return bild_path;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Texture getWappen() {
		// TODO Auto-generated method stub
		return wappen;
	}

	/**
	 * Set the voice of the player.<br>
	 * 
	 * @deprecated unused.
	 * @param i
	 */
	public void setStimmart(int i) {
		this.stimm_art = i;
	}

	/**
	 * Change the emblem to the previous.
	 */
	public void prevWappen() {
		int i = 0;
		for (String s : wappens) {
			s = s.replace("\\", "/");
			String ss = (SuperMain.ordner + wappen_path).replace("\\", "/");
			if (s.equals(ss)) {
				break;
			}
			i++;
		}
		getWappen(i - 1);

	}

	/**
	 * Change the emblem to the next.
	 */

	public void nextWappen() {
		int i = 0;
		for (String s : wappens) {
			s = s.replace("\\", "/");
			String ss = (SuperMain.ordner + wappen_path).replace("\\", "/");
			if (s.equals(ss)) {
				break;
			}
			i++;
		}
		getWappen(i + 1);

	}

	/**
	 * Change the image to the previous.
	 */

	public void prevBild() {
		int i = 0;
		for (String s : bilder) {
			s = s.replace("\\", "/");
			String ss = (SuperMain.ordner + bild_path).replace("\\", "/");
			if (s.equals(ss)) {
				break;
			}
			i++;
		}
		getBild(i - 1);

	}

	/**
	 * Change the image to the next.
	 */

	public void nextBild() {
		int i = 0;
		for (String s : bilder) {
			s = s.replace("\\", "/");
			String ss = (SuperMain.ordner + bild_path).replace("\\", "/");
			if (s.equals(ss)) {
				break;
			}
			i++;
		}
		getBild(i + 1);

	}

	/**
	 * Simple Setter.
	 * 
	 * @param d
	 */
	public void setCredits(double d) {
		credits = d;

	}

	/**
	 * Simple Setter.
	 * 
	 * @param d
	 */
	public void setRuf(double d) {
		ruf = d;
	}

}
