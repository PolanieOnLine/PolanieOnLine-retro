/* $Id: PlayerHasStorableEntityCondition.java,v 1.4 2012/06/05 00:33:06 nhnb Exp $ */
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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.office.StorableEntityList;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is there a storable entity in the specified list that has name
 * of the current player as identifier?
 */
@Dev(category=Category.IGNORE)
public class PlayerHasStorableEntityCondition implements ChatCondition {
	private final StorableEntityList< ? > storeableEntityList;

	/**
	 * creates a PlayerHasStorableEntityCondition
	 *
	 * @param storeableEntityList list to analyze
	 */
	public PlayerHasStorableEntityCondition(final StorableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return storeableEntityList.getByName(player.getName()) != null;
	}

	@Override
	public String toString() {
		return "in list <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
			PlayerHasStorableEntityCondition.class);
	}
}
