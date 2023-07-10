/* $Id: Healer.java,v 1.1 2010/12/02 20:44:49 nhnb Exp $ */
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
package games.stendhal.server.entity.creature.impl.heal;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;

class Healer implements HealerBehavior {

	private int amount;
	private int frequency;

	public Healer(final String healingProfile) {
		init(healingProfile);
	}

	public void init(final String healingProfile) {
		final String[] healingAttributes = healingProfile.split(",");
		amount = Integer.parseInt(healingAttributes[0]);
		frequency = Integer.parseInt(healingAttributes[1]);
	}

	public void heal(final Creature creature) {
		if ((SingletonRepository.getRuleProcessor().getTurn() % frequency == 0)
				&& (creature.getHP() > 0)) {
			creature.heal(amount);
		}
		
	}

}
