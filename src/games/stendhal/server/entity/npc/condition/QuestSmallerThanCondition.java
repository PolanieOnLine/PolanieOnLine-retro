/* $Id: QuestSmallerThanCondition.java,v 1.8 2012/09/09 12:33:24 nhnb Exp $ */
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
 * Is this quest state smaller than the value in this condition?
 */
@Dev(category=Category.IGNORE, label="State?")
public class QuestSmallerThanCondition implements ChatCondition {

	private final String questname;
	private final int index;
	private final int state;

	/**
	 * Creates a new QuestSmallerThanCondition.
	 *
	 * @param questname name of quest-slot
	 * @param state state
	 */
	public QuestSmallerThanCondition(final String questname, final int state) {
		this.questname = questname;
		this.index = -1;
		this.state = state;
	}


	/**
	 * Creates a new QuestSmallerThanCondition.
	 *
	 * @param questname name of quest-slot
	 * @param index index of sub state
	 * @param state state
	 */
	public QuestSmallerThanCondition(final String questname, final int index, final int state) {
		this.questname = questname;
		this.index = index;
		this.state = state;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasQuest(questname)) {
			return false;
		}

		String questState = getQuestState(player);
		int questStateInt;
		try {
			questStateInt = Integer.parseInt(questState);
		} catch (NumberFormatException e) {
			return false;
		}

		return questStateInt < state;
	}

	/**
	 * gets the state of the quest
	 *
	 * @param player Player
	 * @return state of quest
	 */
	private String getQuestState(final Player player) {
		String questState;
		if (index > -1) {
			questState = player.getQuest(questname, index);
		} else {
			questState = player.getQuest(questname);
		}
		return questState;
	}

	@Override
	public String toString() {
		return "QuestSmallerThan <" + questname + "[" + index + "] = " + state + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestSmallerThanCondition.class);
	}
}
