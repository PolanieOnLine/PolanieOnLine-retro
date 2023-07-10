/* $Id: FaceAction.java,v 1.3 2009/02/25 23:42:52 astridemma Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.move;

import static games.stendhal.common.constants.Actions.DIR;
import static games.stendhal.common.constants.Actions.FACE;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class FaceAction implements ActionListener {



	public static void register() {
		CommandCenter.register(FACE, new FaceAction());
	}

	public void onAction(final Player player, final RPAction action) {

		if (action.has(DIR)) {
			player.stop();
			player.setDirection(Direction.build(action.getInt(DIR)));
			player.notifyWorldAboutChanges();
		}

	}
}
