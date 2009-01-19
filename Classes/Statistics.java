package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import main.SuperMain;

/**
 * Manages statistics.
 * 
 * @author laDanz
 * 
 */
public class Statistics implements Serializable {

	private static final long serialVersionUID = 5L;

	private long st_time, end_time;
	private int shoots;
	private int hits_taken, hits_given, damage_taken, damage_given;
	private int biggest_damage_taken, biggest_damage_given;
	private int targets_killed;
	private String level;
	int richtzeit;
	/**
	 * When was this score reached.
	 */
	long date;

	private String state_on_Exit;

	/**
	 * Constructor.<br>
	 * The level path must be provided like this:<br>
	 * <code>level/levelX.xml</code>.
	 * 
	 * 
	 * @param level
	 *            The level path.
	 */
	public Statistics(Level level) {
		this.level = level.getFilename();
		this.richtzeit = level.getRichtZeit();
		this.st_time = 0;
		this.end_time = 0;
		this.shoots = 0;
		this.hits_taken = 0;
		this.hits_given = 0;
		this.damage_taken = 0;
		this.damage_given = 0;
		this.biggest_damage_taken = 0;
		this.biggest_damage_given = 0;
		this.targets_killed = 0;
		this.date = 0;
	}

	/**
	 * Simple Getter.
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return The start time of the level in mseconds since 1970.
	 */
	public long getSt_time() {
		return st_time;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param level
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Set the start time to now.
	 */
	public void setStartTime() {
		st_time = System.currentTimeMillis();
	}

	/**
	 * Set the end time to now.
	 */
	public void setEndTime() {
		end_time = System.currentTimeMillis();
	}

	/**
	 * Calculates the time difference and returns it as a string.
	 * 
	 */
	public String getTimeUsedString() {
		String s;
		// Differenz in Sekunden
		long delta = ((end_time == 0 ? System.currentTimeMillis() : end_time) - st_time) / 1000;
		if (st_time == 0) {
			delta = 0;
		}
		s = "" + delta / 60;
		if (s.length() == 1)
			s = "0" + s;
		delta %= 60;
		s += ":";
		if (delta < 10)
			s += "0";
		s += delta;

		return s;

	};

	/**
	 * Returns the time left for this level.As a string.
	 * 
	 * @return
	 */
	public String getTimeLeftString() {
		String s;
		boolean negative = false;
		// Differenz in Sekunden
		long delta = (SuperMain.level.getRichtZeit() - getTimeUsedSeks());
		if (delta < 0) {
			delta = -delta;
			negative = true;
		}
		s = "" + delta / 60;
		if (s.length() == 1)
			s = "0" + s;
		delta %= 60;
		s += ":";
		if (delta < 10)
			s += "0";
		s += delta;
		if (negative) {
			s = "-" + s;
		}
		return s;

	};

	public int getTimeLeftSeks() {

		// Differenz in Sekunden
		int delta = (SuperMain.level.getRichtZeit() - getTimeUsedSeks());
		return delta;
	}

	/**
	 * Returns the time difference in seconds.
	 * 
	 * @return
	 */
	public int getTimeUsedSeks() {
		if (st_time == 0) {
			// return noch nicht gestartet-> 0 sek verbraucht
			return 0;
		}

		long delta = ((end_time == 0 ? System.currentTimeMillis() : end_time) - (st_time)) / 1000;

		return (int) delta;

	};

	/**
	 * Simple Getter.
	 */
	public int getShootsFired() {
		return shoots;
	};

	/**
	 * Increase the shot counter.
	 */
	public void incShootsFired() {
		shoots++;
	}

	/**
	 * Simple Getter.
	 */
	public int getHitsTaken() {
		return hits_taken;
	};

	/**
	 * Increase the hit counter.
	 */
	public void incHitsTaken() {
		hits_taken++;
	};

	/**
	 * Simple Getter.
	 */
	public int getHitsGiven() {
		return hits_given;
	};

	/**
	 * Increase the hit counter.
	 */
	public void incHitsGiven() {
		hits_given++;
	};

	/**
	 * Simple Getter.
	 */
	public int getDamageTaken() {
		return damage_taken;
	};

	/**
	 * Increase the damage counter.
	 */
	public void incDamageTaken(int by) {
		damage_taken += by;
		if (by > biggest_damage_taken)
			biggest_damage_taken = by;
	}

	/**
	 * Simple Getter.
	 */
	public int getDamageGiven() {
		return damage_given;
	};

	/**
	 * Increase the damage counter.
	 * 
	 * @param by
	 */
	public void incDamageGiven(int by) {
		damage_given += by;
		if (by > biggest_damage_given)
			biggest_damage_given = by;
	};

	/**
	 * Returns the biggest damage given in one shot.
	 * 
	 * @return
	 */
	public int getBiggestDamageGiven() {
		return biggest_damage_given;
	}

	/**
	 * Returns the biggest damage taken in one shot.
	 * 
	 * @return
	 */
	public int getBiggestDamageTaken() {
		return biggest_damage_taken;
	}

	/**
	 * Simple Getter.
	 */
	public int getTargets_killed() {
		return targets_killed;
	}

	/**
	 * Increase the kill counter.
	 */
	public void IncTargets_killed() {
		this.targets_killed++;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return The time when the level was played.
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param date
	 *            The time when the level was played.
	 */
	public void setDate(long date) {
		this.date = date;
	}

	/**
	 * Saves the statistic data to a file in the profile folder.<br>
	 * Filename is <code>[TIMESTAMP].hscore</code>.
	 */
	public void save() {
		if (SuperMain.profil != null) {
			long currentDate = System.currentTimeMillis();
			File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + currentDate
					+ ".hscore");
			this.setDate(currentDate);
			if (!(new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/").exists())) {
				File ff = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/");
				ff.mkdir();
			}
			try {
				f.createNewFile();
				java.io.ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
				os.writeObject(SuperMain.statistics);
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}

		}

	}

	/**
	 * Prints all scores for this level.
	 * 
	 * @param s
	 */
	public static void printForLevel(String s) {

		Statistics[] all = getAllForLevel(s);
		for (Statistics st : all) {

			SuperMain.out("score: " + st.getScore());

		}

	}

	/**
	 * Loads all statistics for a certain level.
	 * 
	 * @param level
	 *            A level path.
	 * @return All statistics for this level.
	 */
	public static Statistics[] getAllForLevel(String level) {
		LinkedList<Statistics> res = new LinkedList<Statistics>();
		for (Statistics st : loadAllStatistics()) {
			String lev = st.getLevel();
			if (level.equals(lev))
				res.add(st);
		}
		return res.toArray(new Statistics[0]);
	}

	/**
	 * Loads all statistic files from the active profile folder.
	 * 
	 * @return All statistics.
	 */
	public static Statistics[] loadAllStatistics() {
		// nur wenn schon profil geladen
		if (SuperMain.profil == null) {
			return new Statistics[0];
		}

		// aus dem Profilordner alle Statistiken lesen
		LinkedList<Statistics> stats = new LinkedList<Statistics>();
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/");
		if (f == null || f.listFiles() == null)
			return new Statistics[0];// Ticket #2 --> Behoben
		for (File file : f.listFiles()) {
			if (!file.getAbsolutePath().endsWith(".hscore")) {
				continue;
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				Statistics stat = (Statistics) ois.readObject();
				stats.add(stat);
				// String time = new
				// Date(Long.valueOf(file.getName().split("[.]")[0])).toString();
				// int score = stat.getScore();
				// System.out.println(time + "#" + score);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}
		}
		return stats.toArray(new Statistics[0]);
	}

	/**
	 * Calculates score.
	 * 
	 * @return The score.
	 */
	public int getScore() {
		int score = (int) (getAim() + this.getTargets_killed() * 10 + this.getTimeLeftSeks());
		return score;
	}

	/**
	 * Calculates the hit percentage.
	 * 
	 * @return
	 */
	public int getAim() {
		int aim = (this.getShootsFired() == 0 ? 0 : this.getHitsGiven() * 100 / (this.getShootsFired()));
		return aim;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param state_on_exit
	 */
	public void setEndState(String state_on_exit) {
		state_on_Exit = state_on_exit;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public String getState_on_Exit() {
		return state_on_Exit;
	}

}
