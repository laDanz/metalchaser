package Classes;

/**
 * Abstract class for all game-modules. <br>
 * All modules must implement this interface.<br>
 * Modules are: Editor, Menus, LevelPlay etc.
 * 
 * @author laDanz
 */
public interface GameState extends OGLable {
	/**
	 * Returns the state of the <code>GameState</code>
	 * 
	 * @return state, where '0' means all right.
	 */
	String getState();

	/**
	 * Some actions that must run when the GameState is being finished.
	 */
	void doFinalizeActions();

}
