/* $Id: LoadSignFromHallOfFameAction.java,v 1.7 2012/06/05 10:54:06 nhnb Exp $ */
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
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.sign.SignFromHallOfFameLoader;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


/**
 * Displays text from the hall of fame on a sign.
 *
 * @author hendrik
 */
@Dev(category=Category.IGNORE)
public class LoadSignFromHallOfFameAction implements ChatAction {

	private Sign sign;
	private final String introduction;
	private final String fametype;
	private final int max;
	private final boolean ascending;

	/**
	 * creates a new LoadSignFromHallOfFame
	 *
	 * @param sign the sign to modify
	 * @param introduction introduction for the sign
	 * @param fametype type of fame
	 * @param max maximum number of returned characters
	 * @param ascending sort ascending or descending
	 */
	public LoadSignFromHallOfFameAction(Sign sign, String introduction, String fametype, int max, boolean ascending) {
		this.sign = sign;
		this.introduction = introduction;
		this.fametype = fametype;
		this.max = max;
		this.ascending = ascending;
	}

	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (sign != null) {
			SignFromHallOfFameLoader loader = new SignFromHallOfFameLoader(sign, introduction, fametype, max, ascending, false);
			TurnNotifier.get().notifyInTurns(0, loader);
		}
	}

	/**
	 * sets the sign to be updated
	 *
	 * @param sign a Sign or <code>null</code>
	 */
	public void setSign(Sign sign) {
		this.sign = sign;
	}
}
