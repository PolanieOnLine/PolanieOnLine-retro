/***************************************************************************
 *                 (C) Copyright 2018-2023 - PolanieOnLine                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.tatry.kuznice.tavern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author KarajuSs
 */
public class JagienkaNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildnpc(zone);
	}

	private void buildnpc(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jagienka") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(26, 29));
				nodes.add(new Node(35, 29));
				nodes.add(new Node(35, 12));
				nodes.add(new Node(26, 12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witamy miłego gościa w karczmie! Czy może coś podać?");
				addJob("Jestem kelnerką w tej karczmie. Sprzedajemy importowane i lokalne trunki oraz dobre jedzenie. Na deser też coś się znajdzie.");
				addHelp("Lepszego miejsca okolic Tatr nie znajdziesz! Można tu dobrze odpocząć i zjeść. Jeżeli chcesz poznać naszą #ofertę, to zapytaj.");
				addGoodbye("Smacznego i miłej zabawy do samego rana.");
			}
		};

		npc.setDescription("Oto Jagienka, która wygląda na bardzo uprzejmą osobę.");
		npc.setEntityClass("npcjagienka");
		npc.setGender("F");
		npc.setPosition(26, 29);
		zone.add(npc);
	}
}
