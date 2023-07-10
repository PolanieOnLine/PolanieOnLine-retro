/* $Id: ListProducedItemsOfClassAction.java,v 1.5 2012/09/09 12:19:56 nhnb Exp $ */
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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Lists all items produced, which are of the given item class, as part of a message
 *
 * @author kymara
 */
@Dev(category=Category.ITEMS_PRODUCER, label="List")
public class ListProducedItemsOfClassAction implements ChatAction {

	private final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();

	private final String message;
	private final String clazz;

	/**
	 * Creates a new ListProducedItemsOfClassAction
	 *
	 * @param clazz
	 *            Item class to check
	 * @param message
	 *            message with substitution [items] or [#items] for the list of items
	 *
	 */
	public ListProducedItemsOfClassAction(final String clazz, final String message) {
		this.clazz = clazz;
		this.message = message;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		Map<String, String> substitutes = new HashMap<String, String>();
		substitutes.put("items", Grammar.enumerateCollection(producerRegister.getProducedItemNames(clazz)));
		substitutes.put("#items", Grammar.enumerateCollectionWithHash(producerRegister.getProducedItemNames(clazz)));
		raiser.say(StringUtils.substitute(message, substitutes));
	}

	@Override
	public String toString() {
		return "ListProducedItemsOfClassAction <" + message + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ListProducedItemsOfClassAction.class);
	}

}
