/* $Id: ScriptImpl.java,v 1.5 2010/09/19 02:22:53 nhnb Exp $ */
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
package games.stendhal.server.core.scripting;

import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Default implementation of the Script interface.
 * 
 * @author hendrik
 */
public class ScriptImpl implements Script {

	/** all modifications must be done using this object to be undoable on unload. */
	protected ScriptingSandbox sandbox;

	public void execute(final Player admin, final List<String> args) {
		// do nothing
	}

	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {
		this.sandbox = sandbox;
	}

	public void unload(final Player admin, final List<String> args) {
		// do nothing
	}

}
