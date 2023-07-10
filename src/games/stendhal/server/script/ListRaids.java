/* $Id: ListRaids.java,v 1.4 2010/11/24 23:18:39 martinfuchs Exp $ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Lists raid scripts
 */
public class ListRaids extends ScriptImpl {
	private static Logger logger = Logger.getLogger(ListRaids.class);

	@Override
	public void execute(final Player admin, final List<String> args) {
		StringBuilder textToSend = new StringBuilder("Znane skrypty Rajdów:\n");
		try {
			ArrayList<Class<?>> dir = getClasses("games.stendhal.server.script");
			for (final Class<?> clazz : dir) {
				if (CreateRaid.class.isAssignableFrom(clazz)) {
					textToSend.append(clazz.getSimpleName()).append("\n");
				}
			}

		} catch (final ClassNotFoundException e) {
			logger.error(e, e);
		} catch (final SecurityException e) {
			logger.error(e, e);
		}
		admin.sendPrivateText(textToSend.toString());
	}

	private static ArrayList<Class<?>> getClasses(final String pckgname)
			throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		// Get a File object for the package
		File directory = null;
		try {
			final ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			final String path = pckgname.replace('.', '/');
			final URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (final NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");
		}

		return classes;
	}
}
