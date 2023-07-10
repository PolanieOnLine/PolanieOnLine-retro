/* $Id: BehaviourAction.java,v 1.4 2012/06/05 10:54:06 nhnb Exp $ */
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

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.Behaviour;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

/**
 * BehaviourAction handles Behaviour requests.
 */
@Dev(category=Category.IGNORE)
public abstract class BehaviourAction extends AbstractBehaviourAction<Behaviour> {

	public BehaviourAction(final Behaviour behaviour, List<String> userAction, String npcAction) {
		super(behaviour, userAction, npcAction);
	}

	@Override
	public void fireRequestError(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser npc) {
		npc.say(behaviour.getErrormessage(res, userAction, npcAction));
	}

	@Override
	public String toString() {
		return "BehaviourAction";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				BehaviourAction.class);
	}

}
