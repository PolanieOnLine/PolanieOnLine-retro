/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.tavern;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

/**
 * Food and drink seller,  Inside Semos Tavern - Level 0 (ground floor)
 * Sells the flask required for Tad's quest IntroducePlayers
 */
public class BarMaidNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMargaret(zone);
	}

	private void buildMargaret(final StendhalRPZone zone) {
		final SpeakerNPC margaret = new SpeakerNPC("Margaret") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(11, 4));
				nodes.add(new Node(18, 4));
				nodes.add(new Node(18, 3));
				nodes.add(new Node(11, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addReply("butelki", "Jeżeli chciałbyś kupić flaszę to powiedz mi: #kupię #butelka lub zapytaj co mogłabym jeszcze #zaoferować.");
				addQuest("Och jak miło, że pytasz. Niestety nic dla ciebie nie mam.");
				addJob("Jestem kelnerką w tej oberży. Sprzedajemy #'butelki', importowane i lokalne soki z chmielu oraz dobre jedzenie.");
				addHelp("Oberża ta jest na tyle duża, żeby tu odpocząć i spotkać nowych ludzi! Jeżeli chcesz poznać moją #ofertę, to powiedz mi o tym.");
				addGoodbye();
			}
		};

		//coupon for free beer
        margaret.add(ConversationStates.ATTENDING,
                (Arrays.asList("coupon", "coupons", "beer coupon", "free beer", "kupon", "kupony", "darmowy sok z chmielu")),
                new PlayerHasItemWithHimCondition("kupon"),
                ConversationStates.ATTENDING,
                "Och widzę, że znalazłeś jeden z kuponów, które rozdałam jakiś czas temu. Przyjemnego kosztowania soku z chmielu!",
                new MultipleActions(new DropItemAction("kupon"),
                					new EquipItemAction("sok z chmielu")));

        margaret.add(ConversationStates.ATTENDING,
        		(Arrays.asList("coupon", "coupons", "beer coupon", "free beer", "kupon", "kupony", "darmowy sok z chmielu")),
                new NotCondition(new PlayerHasItemWithHimCondition("kupon")),
                ConversationStates.ATTENDING,
                "Nie kłam. Nie masz kuponu ze sobą. Dziś trudno prowadzić tawernę. Nie oszukuj mnie!",
                null);

        margaret.setDescription("Oto Margaret, wygląda na miłą osobę. Nie możesz jej pomóc, ale możesz coś od niej kupić.");
		margaret.setEntityClass("tavernbarmaidnpc");
		margaret.setGender("F");
		margaret.setPosition(11, 4);
		margaret.setSounds(Arrays.asList("hiccup-01"));
		zone.add(margaret);
	}
}
