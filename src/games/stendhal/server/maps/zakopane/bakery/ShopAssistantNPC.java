/***************************************************************************
 *                 (C) Copyright 2003-2023 - PolanieOnLine                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.zakopane.bakery;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author Legolas (based on: ShopAssistantNPC in Semos bakery)
 */
public class ShopAssistantNPC implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Małgosia") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(26, 9));
				nodes.add(new Node(26, 6));
				nodes.add(new Node(28, 6));
				nodes.add(new Node(28, 2));
				nodes.add(new Node(28, 5));
				nodes.add(new Node(22, 5));
				nodes.add(new Node(22, 4));
				nodes.add(new Node(22, 7));
				nodes.add(new Node(26, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("Ja piekę chleb w tej piekarni.");
				addReply("mąka",
						"Do naszej pracy potrzebujemy mąkę, którą mielono we młynie na północ stąd, ale wilki pożarły chłopca, który nam ją przynosił! Jeśli przyniesiesz nam mąkę w nagrodę upieczemy przepyszny chleb dla Ciebie. Powiedz tylko #upiecz.");
				addHelp("Chleb jest bardzo dobry, zwłaszcza dla takiego śmiałka jak ty, któremu już niedobrze, gdy widzi surowe mięsiwo. Mój szef Jaś, robi najlepsze kanapki w okolicy!");
				addGoodbye();
			}
		};

		npc.setEntityClass("housewifenpc");
		npc.setGender("F");
		npc.setPosition(26, 9);
		zone.add(npc);
	}
}
