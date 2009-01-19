/**
 * 
 */
package Fenster;

import javax.swing.JDialog;

/**
 * Abstract class for Editor Windows
 * 
 * @author laDanz
 * 
 */
abstract class EditorFenster extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with postion and size of the window 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public EditorFenster(int x, int y, int width, int height) {
		this.setLayout(null);
		this.setBounds(x, y, width, height);
		this.setLocation(x, y);
		this.setTitle("EditorFenster");
		this.setFocusable(false);
		this.setFocusableWindowState(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

	}

}
