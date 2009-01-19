package glmodel;
import java.io.*;
import java.net.URL;


/**
 * This class opens a given file either from the local filesystem or from
 * the JAR containing this class.  Hides the differences between filesystem
 * and JAR so that files can be loaded by an app independently of the
 * way that app is packaged.
 *
 * In your code, open files this way:
 *
 *             InputStream in = FILE.getInputStream("models/someModel.obj");
 *
 * To access a file in a JAR we have to call SomeClass.class.getResource(...).
 * The FILE class provides a convenient static class to use as the reference
 * class object.
 *
 * Place the FILE class in the root of your JAR to have access to all
 * files in the folders beneath the root.
 */
public class FILE {

    /**
     * Open a file InputStream and trap errors.  Will look first for a
     * local file.  If that fails, try to retrieve resource from JAR.
     *
     * @param filename
     * @return InputStream
     */
    public static InputStream getInputStream(String filename) {
        InputStream in = null;
        // try to open local file
        try {
            in = new FileInputStream(filename);
        }
        catch (IOException ioe) {
            System.out.println("FILE.getInputStream (" + filename + "): " + ioe);
            if (in != null) {
                try {
                    in.close();
                    in = null;
                }
                catch (Exception e) {}
            }
        }
        catch (Exception e) {
            System.out.println("FILE.getInputStream (" + filename + "): " + e);
        }
        // if no luck, try JAR
        if (in == null) {
            // Couldn't open file: try looking in jar.
            // NOTE: this will look only in the folder that this class is in.
            //System.out.println("GLApp.getInputStream (" +filename+ "): in == null, try jar");
            URL u = FILE.class.getResource(filename);
            if (u != null) {
                //System.out.println("GLApp.getInputStream (" +filename+ "): trying jar, got url=" + u);
                try {
                    in = u.openStream();
                }
                catch (Exception e) {
                    System.out.println("GLApp.getInputStream (" +filename+ "): Can't load from jar: " + e);
                }
            }
        }
        return in;
    }

}