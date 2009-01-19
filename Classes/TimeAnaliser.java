package Classes;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Utility class to analyse how much time is spent.
 * 
 * @author ladanz
 * 
 */
public class TimeAnaliser {

	static HashMap<String, LinkedList<Long>> values = new HashMap<String, LinkedList<Long>>();
	private static long lasttime;

	/**
	 * Add a checkpoint for time analysis.
	 * 
	 * @param identifier
	 */
	public static void addPoint(String identifier) {
		LinkedList<Long> points = values.get(identifier);
		if (points == null) {
			points = new LinkedList<Long>();
			values.put(identifier, points);

		}
		long now = System.currentTimeMillis();
		long delta = now - lasttime;
		lasttime = now;
		points.add(delta);
	}

	public static void startNewRound() {
		lasttime = System.currentTimeMillis();
	}

	public static void analyse() {
		HashMap<String, Long> erg = new HashMap<String, Long>();

		for (String s : values.keySet()) {
			int count = 0;
			long sum = 0;
			for (Long l : values.get(s)) {
				count++;
				sum += l;
			}
			long put = Math.round((sum + .0) / count);
			erg.put(s, put);
			System.out.println(s + " -> " + put);
		}
	}

}
