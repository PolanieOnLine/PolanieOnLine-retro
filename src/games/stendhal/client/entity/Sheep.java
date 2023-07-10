/* $Id: Sheep.java,v 1.88 2010/03/23 19:14:34 nhnb Exp $ */
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
package games.stendhal.client.entity;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import marauroa.common.game.RPObject;

/** A Sheep entity. */
public class Sheep extends DomesticAnimal {
	//
	// DomesticAnimal
	//

	@Override
	public void initialize(RPObject object) {
		super.initialize(object);
		addSounds(SoundLayer.CREATURE_NOISE.groupName, "small", "sheep-2", "sheep-4");
		addSounds(SoundLayer.CREATURE_NOISE.groupName, "big"  , "sheep-1", "sheep-3");
	}

	@Override
	protected void probableChat(final int chance) {
		if (Rand.rand(100) < chance) {
			if (getWeight() > 50) {
				playRandomSoundFromCategory(SoundLayer.CREATURE_NOISE.groupName, "big");
			} else {
				playRandomSoundFromCategory(SoundLayer.CREATURE_NOISE.groupName, "small");
			}
		}
	}
}
