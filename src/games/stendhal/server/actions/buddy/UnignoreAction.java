/* $Id: UnignoreAction.java,v 1.7 2010/09/19 02:21:47 nhnb Exp $ */
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
package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class UnignoreAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);
			if (player.getIgnore(who) == null) {
				player.sendPrivateText(who
						+ " nie jest ignorowany przez Ciebie.");
			} else if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " został usunięty z listy ignorowanych.");
			}
		}

	}

}
