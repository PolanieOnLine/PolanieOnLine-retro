/* $Id: ComplainAboutSentenceErrorAction.java,v 1.7 2012/09/09 12:19:56 nhnb Exp $ */
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
 * Tells the player that the NPC did not understand the sentence;
 * use it in combination with SentenceHasErrorCondition.
 */
@Dev(category=Category.CHAT, label="Error")
public class ComplainAboutSentenceErrorAction implements ChatAction {

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (sentence.hasError()) {
			raiser.say("Przepraszam, ale nie rozumiem ciebie. "
				+ sentence.getErrorString());
		}
	}

	@Override
	public String toString() {
		return "complainSentenceError";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ComplainAboutSentenceErrorAction.class);
	}
}
