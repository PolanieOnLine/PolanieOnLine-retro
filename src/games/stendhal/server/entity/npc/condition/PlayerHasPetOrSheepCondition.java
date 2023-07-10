/* $Id: PlayerHasPetOrSheepCondition.java,v 1.6 2012/09/09 12:33:23 nhnb Exp $ */
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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player have a pet or sheep?
 */
@Dev(category=Category.OTHER, label="Pet?")
public class PlayerHasPetOrSheepCondition implements ChatCondition {

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
	    return (player.hasPet() || player.hasSheep());
	}

	@Override
	public String toString() {
		return "wojownik posiada zwierzątko lub owcę";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasPetOrSheepCondition.class);
	}
}
