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
package games.stendhal.server.maps.kirdneh.inn;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the barman in kirdneh.
 *
 * @author kymara
 */
public class BarmanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC barmanNPC = new SpeakerNPC("Ruarhi") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15, 4));
				nodes.add(new Node(15, 7));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(4, 20));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(11, 7));
				nodes.add(new Node(11, 11));
				nodes.add(new Node(14, 11));
				nodes.add(new Node(14, 21));
				nodes.add(new Node(14, 8));
				nodes.add(new Node(15, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Cześć!");
				addJob("Jestem barmanem. Jeżeli mógłbym #zaoferować Tobie drinka to daj znać.");
				addHelp("Cii możesz podejść bliżej? (Wiem, że Katerina wygląda jak ruina .. ale ona jest uzdrowicielką .. w dodatku tanią.)");
				addGoodbye("Do widzenia.");
			}
		};

		barmanNPC.setDescription("Oto Ruarhi. Wygląda na właściela baru.");
		barmanNPC.setEntityClass("barman2npc");
		barmanNPC.setGender("M");
		barmanNPC.setPosition(15, 4);
		zone.add(barmanNPC);
	}
}
