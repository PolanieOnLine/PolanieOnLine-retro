/* $Id: ChatCondition.java,v 1.6 2011/05/01 19:50:07 martinfuchs Exp $ */
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
package games.stendhal.server.entity.npc;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.fsm.PreTransitionCondition;
import games.stendhal.server.entity.player.Player;

public interface ChatCondition extends PreTransitionCondition {

	boolean fire(Player player, Sentence sentence, Entity npc);
}
