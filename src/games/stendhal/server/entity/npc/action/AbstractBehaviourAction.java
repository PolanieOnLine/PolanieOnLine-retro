/* $Id: AbstractBehaviourAction.java,v 1.4 2012/06/05 10:54:06 nhnb Exp $ */
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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.Behaviour;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * AbstractBehaviourAction is the base of ChatActions handling Behaviour requests.
 */
@Dev(category=Category.IGNORE)
abstract class AbstractBehaviourAction<B extends Behaviour> implements ChatAction {

	protected final B behaviour;
	protected final List<String> userAction;
	protected final String npcAction;

	public AbstractBehaviourAction(final B behaviour, List<String> userAction, String npcAction) {
		this.behaviour = behaviour;
		this.userAction = userAction;
		this.npcAction = npcAction;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		if (sentence.hasError()) {
			fireSentenceError(player, sentence, npc);
		} else {
			ItemParserResult res = behaviour.parse(sentence);

			if (res.wasFound()) {
				fireRequestOK(res, player, sentence, npc);
			} else {
				fireRequestError(res, player, sentence, npc);
			}
		}
	}

	/**
	 * The user input could not be parsed in a valid Sentence.
	 * fireSentenceError() should inform the player about the problem.
	 * @param player
	 * @param sentence
	 * @param npc
	 */
	public void fireSentenceError(Player player, Sentence sentence, EventRaiser npc) {
		npc.say("Nie rozumiem Ciebie. " + sentence.getErrorString());
	}

	/**
	 * The user input was parsed as a behaviour request.
	 * fireRequestOK() should check the request and execute an action as appropriate.
	 * @param res
	 * @param player
	 * @param sentence
	 * @param npc
	 */
	public abstract void fireRequestOK(ItemParserResult res, Player player, Sentence sentence, EventRaiser npc);

	/**
	 * The user input was parsed as valid Sentence, but could not transformed into a Behaviour request.
	 * fireRequestError() should inform the player about the problem.
	 * @param res
	 * @param player
	 * @param sentence
	 * @param npc
	 */
	public abstract void fireRequestError(ItemParserResult res, Player player, Sentence sentence, EventRaiser npc);

}
