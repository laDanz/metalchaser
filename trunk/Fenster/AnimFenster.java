package Fenster;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import main.AnimateIt;

/**
 * Main Menue with buttons for loading, saving and for animation-window
 * 
 * @author laDanz
 * 
 */
public class AnimFenster extends EditorFenster { 
	JButton bu_save = new JButton("speichern");

	JButton bu_load = new JButton("laden");

	static public JCheckBox cb_rechtesbein = new JCheckBox("linkes Bein");

	static public JButton bu_kalib = new JButton("kalibrieren");

	static JLabel file_name_l = new JLabel("");

	public AnimFenster(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.setTitle("Animation");
		initComp();
		repaint();
	}

	/**
	 * Simple Setter
	 * @param s
	 */
	public static void setFileName(String s) {
		s = s.substring(Math.max(0, s.length() - 20));
		file_name_l.setText(s);
	}

	private void initComp() {
		bu_save.setBounds(20, 20, 100, 20);
		bu_save.addMouseListener(onSave());
		add(bu_save);
		bu_load.setBounds(20, 50, 100, 20);
		bu_load.addMouseListener(onLoad());
		add(bu_load);
		bu_kalib.setBounds(20, 100, 100, 20);
		bu_kalib.addMouseListener(onKalib());
		add(bu_kalib);

		cb_rechtesbein.setBounds(20, 70, 100, 20);
		add(cb_rechtesbein);

		file_name_l.setBounds(10, 140, 200, 20);
		add(file_name_l);

	}

	private MouseListener onKalib() {

		return new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent e) {
			};

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
			};

			public void mouseReleased(MouseEvent e) {
			AnimateIt.kalibrate();

			}
		};
	}

	private MouseListener onLoad() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {
				AnimateIt.load();

			}

		};
	}

	private MouseListener onSave() {
		// TODO Auto-generated method stub
		return new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent arg0) {
				AnimateIt.save();

			};
		};
	}

}
