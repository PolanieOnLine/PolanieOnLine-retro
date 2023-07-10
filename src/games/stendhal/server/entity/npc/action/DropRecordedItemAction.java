/* $Id: DropRecordedItemAction.java,v 1.11 2012/09/09 12:19:56 nhnb Exp $ */
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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * Drops the specified item.
 *
 * @see games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction
 * @see games.stendhal.server.entity.npc.action.SayRequiredItemAction
 * @see games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition
 *
 */
@Dev(category=Category.ITEMS_OWNED, label="Item-")
public class DropRecordedItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropRecordedItemAction.class);

	private final String questname;
	private final int index;

	/**
	 * Creates a new DropRecordedItemAction.
	 *
	 * @param questname
	 *            name of quest to get the item and quantity from
	 */
	public DropRecordedItemAction(final String questname) {
		this.questname = questname;
		this.index = -1;
	}

	/**
	 * Creates a new DropRecordedItemAction.
	 *
	 * @param questname
	 *            name of quest to get the item and quantity from
	 * @param index
	 *            index of sub state
	 */
	@Dev
	public DropRecordedItemAction(final String questname, final int index) {
		this.questname = questname;
		this.index = index;
	}


	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		if (!player.hasQuest(questname)) {
			logger.error(player.getName() + " does not have quest " + questname);
			return;
		}
		final String questSubString = player.getQuest(questname, index);
		final String[] elements = questSubString.split("=");
		String itemname = elements[0];
		int amount = 1;

		// some older quests may have stored an item name but not the amount
		// so we use the initial value of 1 if the string can't be split
	    if(elements.length > 1) {
			amount=MathHelper.parseIntDefault(elements[1], 1);
	    }
		final boolean res = player.drop(itemname, amount);
		if (!res) {
			logger.error("Cannot drop " + amount + " " + itemname,
					new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop recorded item from questslot <" + questname + ">";
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + index;
		if (questname == null) {
			result = PRIME * result;

		} else {
			result = PRIME * result + questname.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DropRecordedItemAction other = (DropRecordedItemAction) obj;
		if (index != other.index) {
			return false;
		}
		if (questname == null) {
			if (other.questname != null) {
				return false;
			}
		} else if (!questname.equals(other.questname)) {
			return false;
		}
		return true;
	}
}
