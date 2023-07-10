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
package games.stendhal.server.maps.orril.dwarfmine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Orril Dwarf Mine (Underground/Level -2).
 */
public class IronBuyerNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDwarfMineArea(zone);
	}

	private void buildDwarfMineArea(final StendhalRPZone zone) {
		final SpeakerNPC loretta = new SpeakerNPC("Loretta") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(49, 68));
				nodes.add(new Node(45, 68));
				nodes.add(new Node(45, 72));
				nodes.add(new Node(45, 68));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Jestem nadzorczynią odpowiedzialną za utrzymanie torów dla wagoników w tej kopalni, ale mamy problem, ponieważ nie posiadamy żelaza do ich naprawy! Może mógłbyś mi #zaoferować trochę żelaza?");
				addHelp("Jeżeli chcesz dobrą radę to nie idź na południe. Żyje tam straszny smok!");
				addGoodbye("Żegnaj i uważaj na siebie. Inne krasnale nie lubią obcych kręcących się tutaj!");
			}
		};

		loretta.setDescription("Oto Loretta, starsza kobieta krasnal. Pracuje przy torach dla wagoników.");
		loretta.setEntityClass("greendwarfnpc");
		loretta.setGender("F");
		loretta.setPosition(49, 68);
		loretta.setCollisionAction(CollisionAction.STOP);
		zone.add(loretta);
	}
}
