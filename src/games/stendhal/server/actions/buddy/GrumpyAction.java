/* $Id: GrumpyAction.java,v 1.6 2010/09/19 02:21:47 nhnb Exp $ */
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

import static games.stendhal.common.constants.Actions.REASON;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GrumpyAction implements ActionListener {

	/**
	 * Handle a Grumpy action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (Jail.isInJail(player) || GagManager.isGagged(player)) {
			player.sendPrivateText("Nie możesz zmienić stanu na niedostępny.");
		} else {
			if (action.has(REASON)) {
				player.setGrumpyMessage(action.get(REASON));
			} else {
				player.setGrumpyMessage(null);
			}
			player.notifyWorldAboutChanges();
		}

	}

}
