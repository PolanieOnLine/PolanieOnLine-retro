/* $Id: SetQuestToPlayerAgeAction.java,v 1.6 2012/09/09 12:19:56 nhnb Exp $ */
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
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Sets the state of a quest to the current age of the player.
 */
@Dev(category=Category.STATS, label="State")
public class SetQuestToPlayerAgeAction implements ChatAction {

	private final String questname;
	private final int index;

	/**
	 * Creates a new SetQuestToPlayerAgeAction.
	 *
	 * @param questname name of quest-slot to change
	 */
	public SetQuestToPlayerAgeAction(final String questname) {
		this.questname = questname;
		this.index = -1;
	}

	/**
	 * Creates a new SetQuestToPlayerAgeAction.
	 *
	 * @param questname name of quest-slot to change
	 * @param index index of sub state
	 */
	@Dev
	public SetQuestToPlayerAgeAction(final String questname, final int index) {
		this.questname = questname;
		this.index = index;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String state = Integer.toString(player.getAge());
		if (index > -1) {
			player.setQuest(questname, index, state);
		} else {
			player.setQuest(questname, state);
		}
	}

	@Override
	public String toString() {
		return "SetQuestToPlayerAgeAction<" + questname + "[" + index + "]>";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SetQuestToPlayerAgeAction.class);
	}
}
