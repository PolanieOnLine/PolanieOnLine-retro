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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.events.SoundEvent;
import marauroa.server.db.command.DBCommandQueue;

public class AcceptOfferHandler extends OfferHandler {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(AcceptOfferChatAction.class);
	private static final List<String> TRIGGERS = Arrays.asList("buy", "accept", "kupię", "akceptuję");

	@Override
	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, TRIGGERS, null, ConversationStates.ATTENDING, null,
				new AcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING, null, new ConfirmAcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Dobrze. W czym jeszcze mogę ci pomóc?", null);
	}

	class AcceptOfferChatAction extends KnownOffersChatAction {
		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (sentence.hasError()) {
				npc.say("Przepraszam, ale nie rozumiem Ciebie. "
						+ sentence.getErrorString());
			} else {
				handleSentence(sentence, npc);
			}
		}

		private void handleSentence(Sentence sentence, EventRaiser npc) {
			MarketManagerNPC manager = (MarketManagerNPC) npc.getEntity();
			try {
				String offerNumber = getOfferNumberFromSentence(sentence).toString();
				Map<String,Offer> offerMap = manager.getOfferMap();
				if (offerMap == null) {
					npc.say("Proszę zacznij najpierw od przejrzenia ofert na liście.");
					return;
				}
				if(offerMap.containsKey(offerNumber)) {
					Offer o = offerMap.get(offerNumber);
					if (o.hasItem()) {
						setOffer(o);
						int quantity = getQuantity(o.getItem());
						npc.say("Czy chcesz kupić " + Grammar.quantityplnoun(quantity, o.getItem().getName()) + " za " + o.getPrice() + " money?");
						npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
						return;
					}
				}
				npc.say("Przepraszam, ale wpierw wybierz numer spośród tych, które ci podałem by zaakceptować ofertę.");
			} catch (NumberFormatException e) {
				npc.say("Przepraszam. Powiedz #akceptuję #numer");
			}
		}
	}

	class ConfirmAcceptOfferChatAction implements ChatAction {
		@Override
		public void fire (Player player, Sentence sentence, EventRaiser npc) {
			Offer offer = getOffer();
			Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			String itemname = offer.getItemName();
			if (m.acceptOffer(offer,player)) {
				// Successful trade. Tell the offerer
				StringBuilder earningToFetchMessage = new StringBuilder();
				earningToFetchMessage.append(Grammar.genderNouns(itemname, "Twój") + " ");
				earningToFetchMessage.append(itemname);
				earningToFetchMessage.append(" " + Grammar.genderNouns(itemname, "został") + " " + Grammar.genderNouns(itemname, "sprzedany") + ". Możesz przyjść do mnie po należne ci pieniądze.");

				final MarketManagerNPC manager = (MarketManagerNPC) npc.getEntity();
				final String managerName = manager.getName();

				logger.debug("wysłanie zawiadomienia do '" + offer.getOfferer() + "': " + earningToFetchMessage.toString());
				DBCommandQueue.get().enqueue(new StoreMessageCommand(managerName, offer.getOfferer(), earningToFetchMessage.toString(), "N"));

				// DISABLED: players can buy their own things from Harold
				//player.incCommerceTransaction(managerName, offer.getPrice().intValue(), false);
				npc.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));
				npc.say("Dziękuję.");

				// Obsolete the offers, since the list has changed
				manager.getOfferMap().clear();
			} else {
				// Trade failed for some reason. Check why, and inform the player
				if (!m.contains(offer)) {
					int quantity = getQuantity(offer.getItem());
					npc.say("Przykro mi, ale " + Grammar.thatthose(quantity) + " "
							+ Grammar.quantityplnoun(quantity, offer.getItem().getName())
							+ " " + Grammar.isare(quantity)
							+ " nie jest już na sprzedaż.");
				} else {
					npc.say("Wybacz, ale w swojej sakiewce nie masz tyle money!");
				}
			}
		}
	}
}
