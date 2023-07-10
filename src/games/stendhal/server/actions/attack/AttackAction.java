/* $Id: AttackAction.java,v 1.5 2009/02/25 23:42:54 astridemma Exp $ */
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
package games.stendhal.server.actions.attack;

import static games.stendhal.common.constants.Actions.ATTACK;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

public class AttackAction implements ActionListener {



	/**
	 * registers the AttackAction with its trigger word "attack".
	 */
	public static void register() {
		CommandCenter.register(ATTACK, new AttackAction());
	}

	/**
	 * performs an attack action, if the TARGET is an RPEntity.
	 * 
	 * 
	 * @param player
	 *            the attacker
	 * @param action
	 *            the attack Action containing the TARGET's name
	 */
	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			// evaluate the target parameter
			final Entity entity = EntityHelper.entityFromTargetName(
					action.get(TARGET), player);

			if (entity instanceof RPEntity) {
				StendhalRPAction.startAttack(player, (RPEntity) entity);
			}
		}
	}
}
