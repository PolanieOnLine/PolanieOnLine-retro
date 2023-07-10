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
package games.stendhal.server.maps.semos.tavern.market;

import java.util.Set;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.events.SoundEvent;

/**
 * chat action to let a player fetch his earnings from the market
 *
 * @author madmetzger
 */
public class FetchEarningsChatAction implements ChatAction {

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (sentence.hasError()) {
			npc.say("Przepraszam, ale nie rozumiem Ciebie. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if ((sentence.getExpressions().iterator().next().toString().equals("fetch"))
				|| (sentence.getExpressions().iterator().next().toString().equals("wypłata"))
				|| (sentence.getExpressions().iterator().next().toString().equals("wypłać"))){
			handleSentence(player, npc);
		}
	}

	private void handleSentence(Player player, EventRaiser npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		Set<Earning> earnings = market.fetchEarnings(player);
		int collectedSum = 0;
		final String text = "Wypłaciłem tobie zarobione pieniądze. Co jeszcze mogę zrobić?";
		for (Earning earning : earnings) {
			collectedSum += earning.getValue().intValue();
		}
        if (collectedSum > 0) {
        	// DISABLED: players can buy their own things from Harold
        	//player.incCommerceTransaction(npc.getName(), collectedSum, true);
        	npc.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));
        	player.sendPrivateText("Zebrałeś "+Integer.valueOf(collectedSum).toString()+" money.");
        	if (npc.getName().equals("Radzimir")) {
        		npc.say("Witaj w centrum handlu Zakopane. " + text);
        	} else {
        		npc.say("Witaj w centrum handlu Semos. " + text);
        	}
        } else {
        	//either you have no space in your bag or there isn't anything to collect
        	if (npc.getName().equals("Radzimir")) {
        		npc.say("Witaj w centrum handlu Zakopane. W czym mogę #pomóc?");
        	} else {
        		npc.say("Witaj w centrum handlu Semos. W czym mogę #pomóc?");
        	}
		}
		npc.setCurrentState(ConversationStates.ATTENDING);
	}
}
