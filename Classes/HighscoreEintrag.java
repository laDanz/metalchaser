package Classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.swing.JEditorPane;

import main.SuperMain;
import mechPeck.Mecha;

/**
 * Represents a highscore entry. Collects the statistic data for the levels.
 * 
 * @author ladanz
 * 
 */
public class HighscoreEintrag implements Serializable {

	private static final long serialVersionUID = 1L;

	private Statistics[] statistic;
	private static LinkedList<Object> highscores;
	private Mecha mecha;

	/**
	 * Default Constructor
	 * 
	 * @param stats
	 *            Statistics for all Levels.
	 * @param dates
	 *            From when are the single Statistics.
	 */
	public HighscoreEintrag(Statistics[] stats, Mecha mecha) {
		this.statistic = stats;
		this.mecha = mecha;
	}

	/**
	 * Tries to write a score combined with a name to the online highscore list.<br>
	 * Returns true if it was successful.
	 * 
	 * @param name
	 *            The name.
	 * @param punkte
	 *            The amount of points.
	 * @return True, if successful.
	 */
	public static boolean writeToOnlineHighscore(String name, int punkte) {
		return writeToOnlineHighscore(name, punkte + "");
	}

	private static String calcMD5(String s) {

		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		m.update(s.getBytes(), 0, s.length());
		String bi = new BigInteger(1, m.digest()).toString(16);
		// System.out.println(s);
		// System.out.println("MD5: " + bi);
		return bi;
	}

	/**
	 * Tries to write a score combined with a name to the online highscore list.<br>
	 * Returns true if it was successful.
	 * 
	 * @param name
	 *            The name.
	 * @param punkte
	 *            The amount of points.
	 * @return True, if successful.
	 */
	public static boolean writeToOnlineHighscore(String name, final String punkte) {
		boolean fault = false;

		Hashtable<String, String> ht = new Hashtable<String, String>();

		String ip = getInetIp();
		if (ip == null) {
			throw new RuntimeException("Can't receive InetAddress (#HSEin126)");
		}
		ht.put("hash", calcMD5(punkte + " + " + ip));
		ht.put("ip", ip);
		ht.put("score", punkte);
		ht.put("name", name);

		if (postHSdata(ht)) {
			ERROR = ER_NONE;
		} else {
			ERROR = ER_WRONGSERVER_NOINTERNET;

		}
		// new Thread() {
		// public void run() {
		// // Bildhochladen
		// BildHochLader.ladeBildHoch(SuperMain.profil, punkte);
		// };
		//
		// }.start();

		rebuildHighscores();
		return (ERROR == ER_NONE);
	}

	private static String getInetIp() {
		URLConnection conn;
		try {
			conn = new URL("http://showmyip.com").openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("displaycopy(\"") && !line.startsWith("displaycopy(\"\"")) {
					line = line.substring(13, line.length() - 3);
					// System.out.println(line);
					return line;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static boolean postHSdata(Hashtable<String, String> data_set) {
		try {
			// Construct data
			String data = new String();

			for (Map.Entry<String, String> entry : data_set.entrySet()) {
				data += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8")
						+ "&";

			}
			data = data.substring(0, data.length() - 1);
			// System.out.println(data);
			// Send data
			URL url = new URL("http://metalchaser.hannes-flor.de/?id=highscore");// :80/cgi
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				// System.out.println(line);
			}
			wr.close();
			rd.close();
			return true;
		} catch (Exception e) {
			SuperMain.out(e);

		}
		return false;

	}

	static final public int ER_NONE = 0;
	static final public int ER_WRONGSERVER_NOINTERNET = 1;
	static final public int ER_WRONGHIGHSCOREFILE = 2;
	static int ERROR = ER_NONE;

	/**
	 * Builds a <code>JEditorPane</code> with the Highscore website on it.
	 * 
	 * @return If successful a JEditorPane with the score on it,
	 *         <code>null</code> else.
	 * 
	 * @deprecated Somehow this method isnt used.
	 */
	public static JEditorPane getHighscorePanel() {
		String url_to_show = "http://superwg.kicks-ass.net/highscore.php";
		try {
			JEditorPane ep = new JEditorPane(new URL(url_to_show));
			ERROR = ER_NONE;
			return ep;
		} catch (MalformedURLException e) {
			// bad protocol
			SuperMain.out(e);
		} catch (UnknownHostException e) {
			// Wrong server or No Internet
			ERROR = ER_WRONGSERVER_NOINTERNET;
			SuperMain.out(e);

		} catch (FileNotFoundException e) {
			// Wrong Hiscorefile syntax
			ERROR = ER_WRONGHIGHSCOREFILE;
			SuperMain.out(e);
		} catch (IOException e) {
			// Other...
			SuperMain.out(e);
		}
		return null;
	}

	/**
	 * Calculates the cumulative score.
	 * 
	 * @return The overall score.
	 */
	public int getGesamtPunktzahl() {
		int score = 0;
		for (Statistics st : statistic) {
			if (st == null)
				continue;
			score += st.getScore();
		}
		return score;
	}

	/**
	 * From when is this highscore.
	 * 
	 * @return the date from the last level statistic.
	 */
	public long getDate() {
		return statistic[statistic.length - 1].getDate();
	}

	/**
	 * Save the highscore as a file in the profile folder- <br>
	 * Filename is <code>[TIMESTAMP].score</code>
	 */
	public void save() {
		if (SuperMain.profil != null) {
			if (!isConnectedToTheInternet()) {
				// Write local highscore data
				long currentDate = System.currentTimeMillis();
				File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + currentDate
						+ ".score");

				if (!(new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/").exists())) {
					File ff = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/");
					ff.mkdir();
				}
				try {
					f.createNewFile();
					java.io.ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
					os.writeObject(this);
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}
			}// <\local>

			// write online highscore data
			if (isConnectedToTheInternet()) {
				writeToOnlineHighscore(SuperMain.profil.getName(), getGesamtPunktzahl());
			}

		}

	}

	private static boolean isConnectedToTheInternet() {
		URLConnection con;
		try {
			con = new URL("http://google.com").openConnection();

			InputStream is = con.getInputStream();

			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			SuperMain.out(e);
		} catch (UnknownHostException e) {
			// Wrong server or No Internet

			SuperMain.out(e);

		} catch (FileNotFoundException e) {
			// Wrong Hiscorefile syntax

			SuperMain.out(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SuperMain.out(e);
		}
		return false;
	}

	/**
	 * Sets the highscore <code>null</code> so that it will be refreshed on
	 * the next tick.
	 */
	static public void rebuildHighscores() {
		highscores = null;
	}

	/**
	 * Returns and resets the Error-Bit.
	 * 
	 * @return The Error-Bit.
	 */
	public static int getERROR() {
		int error = ERROR;
		ERROR = ER_NONE;
		return error;
	}

	/**
	 * Loads all Highscores from all Profiles and merges them with the saved
	 * ones from the Online Highscore.
	 * 
	 * @return Vector<ProfilName,Score>
	 */
	static public Vector[] loadAllLocalHighscores() {
		if (highscores != null)
			return highscores.toArray(new Vector[0]);

		highscores = new LinkedList<Object>();
		for (Vector v : loadAllPureLocalHighscores()) {
			highscores.add(v);
		}

		// noch zusaetzlich die aus der temporoeren online HS einfuegen
		File tempHS = new File(SuperMain.ordner + "/profile/temp.hscore");
		if (tempHS.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(tempHS));
				String s;
				while ((s = br.readLine()) != null) {
					Vector v = new Vector();
					String punkte = s.substring(0, s.indexOf('\t'));
					String name = s.substring(s.indexOf('\t')).replace(" ", "").replace("\t", "");
					v.add(name);
					v.add(punkte);
					highscores.add(v);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// Sortieren
		Vector<Object>[] vec = highscores.toArray(new Vector[0]);
		Arrays.sort(vec, getHighscoreComparator());
		highscores.clear();
		for (Vector v : vec) {
			highscores.add(v);
		}
		return highscores.toArray(new Vector[0]);
	}

	/**
	 * Loads all Highscores from all Profiles .
	 * 
	 * @return Vector<ProfilName,Score>
	 */
	static private Vector[] loadAllPureLocalHighscores() {
		LinkedList<Object> highscores;

		highscores = new LinkedList<Object>();
		File f = new File(SuperMain.ordner + "profile/");
		for (File profilnames : f.listFiles()) {
			if (!profilnames.isDirectory())
				continue;

			for (File inside : profilnames.listFiles()) {
				if (inside.getAbsolutePath().endsWith(".score")) {
					Vector v = new Vector();
					try {
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inside));
						HighscoreEintrag temp = (HighscoreEintrag) ois.readObject();
						String profil = inside.getAbsolutePath().replace("\\", "/").split("/")[inside.getAbsolutePath()
								.replace("\\", "/").split("/").length - 2];
						v.add(profil);
						v.add(temp.getGesamtPunktzahl());
						highscores.add(v);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SuperMain.out(e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SuperMain.out(e);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SuperMain.out(e);
					} catch (IndexOutOfBoundsException iab) {
						SuperMain.out(iab);
						iab.printStackTrace();
					}
				}
			}

		}

		// Sortieren
		Vector<Object>[] vec = highscores.toArray(new Vector[0]);
		Arrays.sort(vec, getHighscoreComparator());
		highscores.clear();
		for (Vector v : vec) {
			highscores.add(v);
		}
		return highscores.toArray(new Vector[0]);
	}

	public static void writeLocaltoOnline() {
		Vector[] vec = loadAllPureLocalHighscores();
		if (isConnectedToTheInternet()) {
			// write to online HS
			for (Vector<Object> v : vec) {
				writeToOnlineHighscore(v.get(0).toString(), v.get(1).toString());
			}
			// Delete local scores
			File f = new File(SuperMain.ordner + "profile/");
			for (File profilnames : f.listFiles()) {
				if (!profilnames.isDirectory())
					continue;

				for (File inside : profilnames.listFiles()) {
					if (inside.getAbsolutePath().endsWith(".score")) {
						inside.delete();
					}
				}
			}
		}
	}

	private static Comparator<? super Vector<Object>> getHighscoreComparator() {

		return new Comparator<Vector<Object>>() {
			// @override
			public int compare(Vector<Object> o1, Vector<Object> o2) {
				if (o1.size() != 2 || o2.size() != 2) {
					throw new RuntimeException("Wrong length!");
				}
				if (!(o1.get(1) instanceof String || o1.get(1) instanceof Integer)) {
					throw new RuntimeException("Vector!");
				}
				if (!(o2.get(1) instanceof String || o2.get(1) instanceof Integer)) {
					throw new RuntimeException("Vector!");
				}
				return (o2.get(1).toString().compareTo(o1.get(1).toString()));
			}
		};
	}

	/**
	 * 
	 * @return LinkedList (String)
	 */
	public static String[] loadAllOnlineHighscores() {
		if (highscores != null)
			return highscores.toArray(new String[0]);

		LinkedList<Object> result = new LinkedList<Object>();
		String url_to_show = "http://metalchaser.hannes-flor.de/?id=highscore";

		URLConnection con;
		try {
			con = new URL(url_to_show).openConnection();
			con.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write("plain=1");
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = rd.readLine()) != null) {
				if (inputLine.startsWith("<") || inputLine.length() < 3)
					continue;

				for (String s : inputLine.split("<br />")) {
					if (s.startsWith("<"))
						continue;
					while (s.startsWith(" "))
						s = s.substring(1);
					result.add(s);
					// System.out.println(s);
				}

			}

			wr.close();
			rd.close();
			saveInternetHighscoreAsTempFile(result);
			ERROR = ER_NONE;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			SuperMain.out(e);
		} catch (UnknownHostException e) {
			// Wrong server or No Internet
			ERROR = ER_WRONGSERVER_NOINTERNET;
			SuperMain.out(e);

		} catch (FileNotFoundException e) {
			// Wrong Hiscorefile syntax
			ERROR = ER_WRONGHIGHSCOREFILE;
			SuperMain.out(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SuperMain.out(e);
		}

		highscores = result;
		return result.toArray(new String[0]);
	}

	private static void saveInternetHighscoreAsTempFile(LinkedList<Object> result) {
		File f = new File(SuperMain.ordner + "/profile/temp.hscore");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));

			for (String s : result.toArray(new String[0])) {
				bw.write(s + "\n");
			}
			bw.close();

		} catch (IOException e) {
			SuperMain.out(e);
		}
	}

}
