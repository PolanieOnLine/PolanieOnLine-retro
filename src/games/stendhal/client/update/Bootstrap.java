/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import static java.io.File.separator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

/**
 * Starts a program after doing some classpath magic.
 * 
 * @author hendrik
 */
public class Bootstrap {
	private String jarFolder;
	private Properties bootProp;
	private Properties bootPropOrg;


	/**
	 * saves modified boot properties to disk.
	 * 
	 * @throws IOException
	 *             if an IO-error occurs
	 */
	public void saveBootProp() throws IOException {
		// only try to save it, if it was changed (so that we do not have to
		// care about all the things which could go wrong unless an update
		// was done this time.)
		if (!bootProp.equals(bootPropOrg)) {
			final String propFile = jarFolder + "jar.properties";
			final OutputStream os = new FileOutputStream(propFile);
			try {
				bootProp.store(os, "PolskaOnLine Boot Configuration");
			} finally {
				os.close();
			}
		}
	}

	/**
	 * initializes the startup process.
	 */
	void init() {
		// discover folder for .jar-files
		// Copied from stendhal.java; don't change this to anything different
		// from than what's there
		final String stendhal = ClientGameConfiguration.get("GAME_NAME").toLowerCase();
		System.out.println("GAME: " + stendhal);
		String topFolder = System.getProperty("user.home") + separator + stendhal + separator;
		String unixLikes = "AIX|Digital Unix|FreeBSD|HP UX|Irix|Linux|Mac OS X|Solaris";
		String system = System.getProperty("os.name");
		if (system.matches(unixLikes)) {
			// Check first if the user has important data in the default folder.
			File f = new File(topFolder + "user.dat");
			if (!f.exists()) {
				topFolder = System.getProperty("user.home") + separator
				+ ".config" + separator + stendhal + separator;
			}
		}

		jarFolder = topFolder + "jar" + separator;
		final File folder = new File(jarFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		System.setProperty("log4j.ignoreTCL", "true");
	}

	/**
	 * Sets a dynamic classpath up and returns a Class reference loaded from it.
	 * 
	 * @param includeUpdates should updates from jar.properties be included
	 * @param firstPhase true, if this is the first phase before the updater is executed
	 * @return ClassLoader object
	 * @throws Exception
	 *             if an unexpected error occurs
	 */
	ClassLoader createClassloader(boolean includeUpdates, boolean firstPhase) throws Exception {
		final List<URL> jarFiles = new LinkedList<URL>();
		if (includeUpdates) {
			// load jar.properties
			final String propFile = jarFolder + "jar.properties";
			bootProp = new Properties();
			if (new File(propFile).canRead()) {
				final InputStream is = new FileInputStream(propFile);
				try {
					bootProp.load(is);
				} finally {
					is.close();
				}
				bootPropOrg = (Properties) bootProp.clone();

				// get list of .jar-files
				final String jarNameString = bootProp.getProperty("load-0.28.4", "");
				final StringTokenizer st = new StringTokenizer(jarNameString, ",");
				while (st.hasMoreTokens()) {
					final String filename = st.nextToken();
					if (SignatureVerifier.get().checkSignature(jarFolder + filename, bootProp.getProperty("file-signature." + filename))) {
						jarFiles.add(new File(jarFolder + filename).toURI().toURL());
					} else {
						if (firstPhase) {
							// if the signature of one file is not valid, ignore all files and do a fresh download
							clearUpdateFiles();
							ClassLoader loader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
							return loader;
						}
					}
				}
				System.out.println("our classpath: " + jarNameString);
			} else {
				System.out.println("no jar.properties");
			}
		}

		// add the files in the download distribution at the end of the classpath
		ClassLoader orgClassloader = Bootstrap.class.getClassLoader();
		String[] includedJarFiles = new String[] { "lib/log4j.jar", "lib/marauroa.jar", "lib/jorbis.jar",
				"lib/polskaonline.jar", "lib/polskaonline-data.jar", "lib/polskaonline-sound-data.jar",
				"lib/polskaonline-music-data.jar"};
		for (String includedJarFile : includedJarFiles) {
			URL url = orgClassloader.getResource(includedJarFile);
			if (url != null) {
				jarFiles.add(url);
			}
		}
		System.out.println("Creating custom class loader for: " + jarFiles);

		// Create new class loader with the list of .jar-files as classpath
		final URL[] urlArray = jarFiles.toArray(new URL[jarFiles.size()]);
		final ClassLoader loader = new URLClassLoader(urlArray,
				this.getClass().getClassLoader());
		return loader;
	}

	/**
	 * Do the whole start up process in a privileged block.
	 * @param <T> 
	 */
	private class PrivilegedBoot<T> implements PrivilegedAction<T> {

		private final String className;

		private final String[] args;

		/**
		 * Creates a PrivilagedBoot object.
		 * 
		 * @param className
		 *            className to boot
		 * @param args
		 *            arguments for the main-method
		 */
		public PrivilegedBoot(final String className, final String[] args) {
			this.className = className;
			this.args = args;
		}

		/**
		 * Handles the update procedure.
		 */
		private void handleUpdate() {
			// invoke update handling first
			try {
				final ClassLoader classLoader = createClassloader(true, true);
				// is this the initial download (or do we already have the
				// program downloaded)?
				boolean initialDownload = false;
				try {
					classLoader.loadClass(className);
					classLoader.loadClass("org.apache.log4j.Logger");
					classLoader.loadClass("marauroa.common.Logger");
					classLoader.loadClass("marauroa.client.ClientFramework");
					if (classLoader.getResource(ClientGameConfiguration.get("GAME_ICON")) == null) {
						throw new ClassNotFoundException(ClientGameConfiguration.get("GAME_ICON"));
					}
					if (classLoader.getResource("data/gui/offline.png") == null) {
						throw new ClassNotFoundException(ClientGameConfiguration.get("data/gui/offline.png"));
					}
				} catch (final ClassNotFoundException e) {
					initialDownload = true;
					System.out.println("Initial Download triggered by the following missing classes: " + e);
				}

				// start update handling
				final Class< ? > clazz = classLoader.loadClass("games.stendhal.client.update.UpdateManager");
				final Method method = clazz.getMethod("process", String.class, Properties.class, Boolean.class, ClassLoader.class);
				method.invoke(clazz.newInstance(), jarFolder, bootProp, initialDownload, classLoader);
			} catch (final SecurityException e) {
				throw e;
			} catch (final Exception e) {
				unexpectedErrorHandling("State: UpdateManager\r\n", e);
			}
		}

		/**
		 * store boot prop (if they were altered during update).
		 */
		private void storeBootProp() {
			try {
				saveBootProp();
			} catch (final IOException e) {
				JOptionPane.showMessageDialog(
					null,
					new SelectableLabel("Wystąpił błąd podczas pobierania aktualizacji. Nie można zapisać bootProperties"));
			}
		}

		/**
		 * load program.
		 */
		private void loadProgram() {
			// regenerate classloader stuff, because in handleUpdate additional
			// .jar-files may have been added

			try {
				final ClassLoader classLoader = createClassloader(true, false);
				final Class< ? > clazz = classLoader.loadClass(className);
				final Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (final Throwable e) {
				unexpectedErrorHandling("State: in game\r\n", e);
			}

		}

		public T run() {
			init();
			handleUpdate();
			storeBootProp();
			loadProgram();
			return null;
		}
	}

	/**
	 * Is this package signed? Note it does not validate the signature, just
	 * looks for the presence of one.
	 * 
	 * @return true, if there is some kind of signature; false otherwise
	 */
	private boolean isSigned() {
		final URL url = Bootstrap.class.getClassLoader().getResource(
				ClientGameConfiguration.get("UPDATE_SIGNER_FILE_NAME"));
		return url != null;
	}

	/**
	 * Starts the main-method of specified class after dynamically building the
	 * classpath.
	 * 
	 * @param className
	 *            name of class with "main"-method
	 * @param args
	 *            command line arguments
	 */
	public void boot(final String className, final String[] args) {
		try {
			System.setSecurityManager(null);
		} catch (final Throwable t) {
			t.printStackTrace(System.err);
		}

		boolean startSelfBuild = true;
		if (isSigned()) {
			startSelfBuild = false;
			// official client, look for updates and integrate additional .jar
			// files
			System.err.println("Integrating old updates and looking for new ones");
			try {
				AccessController.doPrivileged(new PrivilegedBoot<Object>(className, args));
			} catch (final SecurityException e) {
				// partly update
				e.printStackTrace();

				final int res = JOptionPane.showConfirmDialog(
						null,
						new SelectableLabel("Sorry an error occurred because of inconsistent code signing.\r\n"
						+ "Delete update files so that they are downloaded again after you restart " + ClientGameConfiguration.get("GAME_NAME") + "?\r\n"
						+ "Note: This exception can occur if you include signed jars into a self build client."),
						ClientGameConfiguration.get("GAME_NAME"), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					clearUpdateFiles();
					System.exit(-1);
				}
				startSelfBuild = true;
			}
		}

		if (startSelfBuild) {
			// self build client, do not try to update it
			System.err.println("Self build client, starting without update .jar-files");
			try {
				final ClassLoader classLoader = createClassloader(false, false);
				final Class< ? > clazz = classLoader.loadClass(className);
				final Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (final Exception err) {
				err.printStackTrace(System.err);
				JOptionPane.showMessageDialog(null,
						new SelectableLabel("Something nasty happened while trying to start your self build client: "
								+ err));
			}
		}
	}

	/**
	 * Handles exceptions during program invocation.
	 * 
	 * @param message error message
	 * @param t exception
	 */
	void unexpectedErrorHandling(String message, final Throwable t) {
		// unwrap chained exceptions
		Throwable e = t;
		while (e.getCause() != null) {
			e = e.getCause();
		}

		e.printStackTrace();

		if (e instanceof OutOfMemoryError) {
			JOptionPane.showMessageDialog(null,
					"Brak pamięci. Uruchom ponownie " + ClientGameConfiguration.get("GAME_NAME") + ".");
		} else if (e instanceof LinkageError || e instanceof SecurityException || e instanceof ClassNotFoundException) {
			final int res = JOptionPane.showConfirmDialog(
					null,
					new SelectableLabel(message
					+ " Wystąpił błąd z powodu niewłaściwego stanu aktualizacji. Usunąłeś pliki aktualizacji i pobrałeś je ponownie po zrestartowaniu " + ClientGameConfiguration.get("GAME_NAME") +"?"),
					ClientGameConfiguration.get("GAME_NAME"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (res == JOptionPane.YES_OPTION) {
				clearUpdateFiles();
			}
		} else {
			String errorMessage = stacktraceToString(e);
			JOptionPane.showMessageDialog(
					null,
					new SelectableLabel(
					message + "Wystąpił niespodziewany błąd.\r\nPrzejdź do formularza kontaktowego na http://www.gra.polskaonline.org/kontakt-gmgags i napisz wiadomość o błędzie:\r\n"
							+ errorMessage));
		}
		System.exit(1);
	}

	private void clearUpdateFiles() {
		bootProp.remove("load");
		bootProp.remove("load-0.17");
		bootProp.remove("load-0.25.2");
		bootProp.remove("load-0.28.4");
		try {
			saveBootProp();
		} catch (final IOException e1) {
			JOptionPane.showMessageDialog(null, new SelectableLabel("Could not write jar.properties"));
		}
	}

	/**
	 * converts a Throwable into a string representation 
	 * @param e throwable
	 * @return string
	 */
	String stacktraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		for (StackTraceElement frame : e.getStackTrace()) {
			sb.append("\r\n");
			sb.append(frame.toString());
		}
		return sb.toString();
	}
}
