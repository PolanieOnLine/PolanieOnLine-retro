/* $Id: TimePassedCondition.java,v 1.16 2012/09/09 12:33:24 nhnb Exp $ */
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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Has 'delay' time passed since the quest was last done?
 * If the quest slot isn't in the expected format, returns true
 *
 * @see games.stendhal.server.entity.npc.action.SayTimeRemainingAction
 * @see games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction
 */
@Dev(category=Category.TIME, label="Time?")
public class TimePassedCondition implements ChatCondition {

	private final String questname;
	private final int delay;
	private final int index;

	/**
	 * Creates a new TimePassedCondition .
	 *
	 * @param questname
	 *            name of quest-slot
	 * @param index
	 *            position of the timestamp within the quest slot 'array'
	 * @param delayInMinutes
	 *            delay in minutes
	 */
	@Dev
	public TimePassedCondition(final String questname, final int index, final int delayInMinutes) {
		this.questname = questname;
		this.delay = delayInMinutes;
		this.index = index;
	}
	/**
	 * Creates a new TimePassedCondition, where the timestamp alone is stored in the quest state.
	 *
	 * @param questname
	 *            name of quest-slot
	 * @param delayInMinutes
	 *            delay in minutes
	 */
	public TimePassedCondition(final String questname, final int delayInMinutes) {
		this.questname = questname;
		this.delay = delayInMinutes;
		this.index = 0;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasQuest(questname)) {
			// never done quest so enough 'time' has passed
			return true;
		} else {
			final String[] tokens = player.getQuest(questname).split(";");
			final long delayInMilliseconds = delay * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
		    if (tokens.length - 1 < index) {
                // old quest status, the split did not work, so we assume enough time is passed.
                return true;
            }
            // timeRemaining is ''time when quest was done +
			// delay - time now''
			// if this is > 0, the time has not yet passed
            long questtime;
            try {
			    questtime = Long.parseLong(tokens[index]);
		    } catch (final NumberFormatException e) {
                // set to 0 if it was no Long, as if this quest was done at the beginning of time.
			    questtime = 0;
		    }
			final long timeRemaining = (questtime + delayInMilliseconds)
				- System.currentTimeMillis();
		return (timeRemaining <= 0L);
		}
	}

	@Override
	public String toString() {
		return delay + " minutes passed since last doing quest " + questname + "?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TimePassedCondition.class);
	}
}
