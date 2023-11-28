package cl.kanopus.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class DesktopUtils {

    private static final String OS_MACOS = "Mac OS";
    private static final String OS_WINDOWS = "Windows";

    private static final String[] UNIX_OPEN_CMDS = {
        "xdg-open", //  is the most universal way (work also on KDE)
        // Fall back to assuming it's a text file.
        "gnome-open", // debian update-alternatives target
        "kde-open"};

    private DesktopUtils() {
    }

    /**
     * Opens the given File in the system default viewer application.
     *
     * @param file the File to open
     * @throws IOException if an application couldn't be found or if the File
     * failed to launch
     */
    public static void open(final File file) throws IOException {
        // Try Java 1.6 Desktop class if supported
        if (openDesktop(file)) {
            return;
        }

        final String osName = System.getProperty("os.name");

        if (osName.startsWith(OS_MACOS)) {
            openMac(file);
        } else if (osName.startsWith(OS_WINDOWS)) {
            openWindows(file);
        } else {
            //assume Unix or Linux
            openUnix(file);
        }
    }

    /**
     * Attempt to use com.apple.eio.FileManager by reflection.
     *
     * @param url the URL to launch
     * @throws IOException if the launch failed
     */
    private static void browseMac(final URL url) throws IOException {
        try {
            final Class fileMgr = getAppleFileManagerClass();
            final Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);

            openURL.invoke(null, url.toString());
        } catch (Exception e) {
            throw new IOException("Could not launch Mac URL: " + e.getLocalizedMessage());
        }
    }

    /**
     * Attempt to use java.awt.Desktop by reflection. Does not link directly to
     * Desktop class so that this class can still be loaded in JRE < 1.6.
     *
     * @param file the File to open
     * @return true if open successful, false if we should fall back to other
     * methods
     * @throws IOException if Desktop was found, but the open() call failed.
     */
    private static boolean openDesktop(final File file) throws IOException {
        final Class desktopClass = getDesktopClass();
        if (desktopClass == null) {
            return false;
        }

        final Object desktopInstance = getDesktopInstance(desktopClass);
        if (desktopInstance == null) {
            return false;
        }

        try {
            final Method browseMethod = desktopClass.getDeclaredMethod("open", File.class);
            browseMethod.invoke(desktopInstance, file);
            return true;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                throw new FileNotFoundException(e.getCause().getLocalizedMessage());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Uses shell32.dll to open a file under Windows.
     *
     * @param file the File to open
     * @throws IOException if the open failed
     */
    private static void openWindows(final File file) throws IOException {
        Runtime.getRuntime().exec(new String[]{"rundll32", "shell32.dll,ShellExec_RunDLL", file.getAbsolutePath()});
    }

    /**
     * Attempt to use com.apple.eio.FileManager by reflection.
     *
     * @param file the File to open
     * @throws IOException if the open failed
     */
    private static void openMac(final File file) throws IOException {
        // we use openURL() on the file's URL form since openURL supports file:// protocol
        browseMac(file.getAbsoluteFile().toURL());
    }

    /**
     * Attempts to locate a viewer from a predefined list under Unix.
     *
     * @param file the File to open
     * @throws IOException if the open failed
     */
    private static void openUnix(final File file) throws IOException {
        for (final String cmd : UNIX_OPEN_CMDS) {
            if (unixCommandExists(cmd)) {
                Runtime.getRuntime().exec(new String[]{cmd, file.getAbsolutePath()});
                return;
            }
        }
        throw new IOException("Could not find a suitable viewer");
    }

    /**
     * Find the Desktop class if it exists in this JRE.
     *
     * @return the Desktop class object, or null if it could not be found
     */
    private static Class getDesktopClass() {
        // NB The following String is intentionally not inlined to prevent ProGuard trying to locate the unknown class.
        final String desktopClassName = "java.awt.Desktop";
        try {
            return Class.forName(desktopClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets a Desktop class instance if supported. We check isDesktopSupported()
     * but for convenience we don't bother to check isSupported(method); instead
     * the caller handles any UnsupportedOperationExceptions.
     *
     * @param desktopClass the Desktop Class object
     * @return the Desktop instance, or null if it is not supported
     */
    private static Object getDesktopInstance(final Class desktopClass) {
        try {
            final Method isDesktopSupportedMethod = desktopClass.getDeclaredMethod("isDesktopSupported");
            final boolean isDesktopSupported = (Boolean) isDesktopSupportedMethod.invoke(null);

            if (!isDesktopSupported) {
                return null;
            }

            final Method getDesktopMethod = desktopClass.getDeclaredMethod("getDesktop");
            return getDesktopMethod.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Finds the com.apple.eio.FileManager class on a Mac.
     *
     * @return the FileManager instance
     * @throws ClassNotFoundException if FileManager was not found
     */
    private static Class getAppleFileManagerClass() throws ClassNotFoundException {
        // NB The following String is intentionally not inlined to prevent ProGuard trying to locate the unknown class.
        final String appleClass = "com.apple.eio.FileManager";
        return Class.forName(appleClass);
    }

    /**
     * Checks whether a given executable exists, by means of the "which"
     * command.
     *
     * @param cmd the executable to locate
     * @return true if the executable was found
     * @throws IOException if Runtime.exec() throws an IOException
     */
    private static boolean unixCommandExists(final String cmd) throws IOException {
        final Process whichProcess = Runtime.getRuntime().exec(new String[]{"which", cmd});

        boolean finished = false;
        do {
            try {
                whichProcess.waitFor();
                finished = true;
            } catch (InterruptedException e) {
                //Interrupted waiting for which to complete
            }
        } while (!finished);

        return whichProcess.exitValue() == 0;
    }

}
