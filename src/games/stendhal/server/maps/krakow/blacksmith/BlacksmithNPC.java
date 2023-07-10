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
package games.stendhal.server.maps.krakow.blacksmith;

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
public class BlacksmithNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Samson") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(17, 89));
				nodes.add(new Node(17, 90));
				nodes.add(new Node(20, 90));
				nodes.add(new Node(20, 89));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("Specjalizuje się w przetapianiu miedzi, ale również możesz o ode mnie otrzymać dobry sprzęt!");
				addOffer("Sprzedaję: toporek 25, topór jednoręczny 35, topór 50, pyrlik 90, pordzewiała kosa 210, misa do płukania złota 270.");
				addGoodbye();
			}
		};

		npc.setDescription("Oto Samson. Jest miejscowym kowalem i specjalizuje się w przetapianiu miedzi. Dodatkowo prowadzi też sprzedaż potrzebnych do pracy narzędzi.");
		npc.setEntityClass("blacksmithnpc");
		npc.setGender("M");
		npc.setPosition(20, 89);
		zone.add(npc);
	}
}
