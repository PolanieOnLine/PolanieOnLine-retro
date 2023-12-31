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
package games.stendhal.server.maps.ados.farmhouse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * NPC to sell milk.
 *
 * @author kymara
 */
public class FarmersWifeNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Philomena") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(27, 4));
				nodes.add(new Node(33, 4));
				nodes.add(new Node(33, 10));
				nodes.add(new Node(27, 10));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Dzień dobry!");
				addJob("Mój mąż prowadzi to gospodarstwo, a ja głównie opiekuję się jego młodszą siostrą i jej chłopakiem, są na górze. Jeżeli możesz powiedzieć coś pomocnego na ich temat, to mów. Słyszałam wcześniej jej płacz...");
				addQuest("Gdybyś potrafił rozwiązywać Junit testy, moja córka potrzebowałaby Cię. Zapytaj Diogenesa, jak możesz pomóc jej w projekcie.");
				addHelp("Mogę sprzedać Ci butelkę mleka albo trochę masła, prosto od naszych kochanych krów! Jeśli chcesz, oczywiście.");
				addGoodbye("Żegnaj.");
			}
		};

		npc.setDescription("Oto Philomena. Pachnie nieco krowami, których mleko jest wyjątkowe.");
		npc.setEntityClass("wifenpc");
		npc.setGender("F");
		npc.setPosition(27, 4);
	    zone.add(npc);
	}
}
