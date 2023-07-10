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
package games.stendhal.server.maps.ados.tavern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Ados Tavern (Inside / Level 0).
 *
 * @author hendrik
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
		buildTavern(zone);
	}

	private void buildTavern(final StendhalRPZone zone) {
		final SpeakerNPC tavernMaid = new SpeakerNPC("Coralia") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 9));
				nodes.add(new Node(13, 7));
				nodes.add(new Node(6, 7));
				nodes.add(new Node(6, 12));
				nodes.add(new Node(10, 12));
				nodes.add(new Node(10, 5));
				nodes.add(new Node(17, 5));
                nodes.add(new Node(17, 3));
                nodes.add(new Node(3, 3));
                nodes.add(new Node(3, 6));
                nodes.add(new Node(13, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Och witam! Czy nie przeszkodziłam czasem w podziwianiu mojego pięknego #kapelusza?");
				addJob("Jestem kelnerką w tej oberży. Sprzedajemy zarówno importowane jak i lokalne piwo oraz dobre jedzenie.");
				addHelp("Ta oberża jest świetnym miejscem na odpoczynek i poznanie nowych ludzi! Jeżeli chciałbyś poznać naszą #ofertę to daj znać.");
				addGoodbye();
			}
		};

		tavernMaid.setDescription("Oto Coralia. Na kapeluszu nosi egzotyczne jedzenie.");
		tavernMaid.setEntityClass("maidnpc");
		tavernMaid.setGender("F");
		tavernMaid.setPosition(13, 9);
		zone.add(tavernMaid);
	}
}
