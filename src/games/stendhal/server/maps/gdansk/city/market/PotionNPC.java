/***************************************************************************
 *                 (C) Copyright 2019-2023 - PolanieOnLine                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.gdansk.city.market;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author zekkeq
 */
public class PotionNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Manulea") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(44, 76));
				nodes.add(new Node(44, 80));
				nodes.add(new Node(47, 80));
				nodes.add(new Node(47, 76));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witaj na rynku...");
				addJob("Zajmuje się alchemią oraz wystawiłam na tym rynku swoje najlepsze produkty...");
				addHelp("Możesz ode mnie kupić parę świetnych eliksirów!");
				addQuest("Nie mam zadania dla Ciebie.");
				addGoodbye();
			}
		};

		npc.setDescription("Oto alchemiczka, która zwie się Manuela. Handluje swoimi wyrobami.");
		npc.setEntityClass("wizardwomannpc");
		npc.setGender("F");
		npc.setPosition(44, 76);
		zone.add(npc);
	}
}
