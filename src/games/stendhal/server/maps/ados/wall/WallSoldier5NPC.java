/***************************************************************************
 *                     (C) Copyright 2017 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.wall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Soldiers on the wall
 *
 * @author snowflake
 * @author hendrik
 */
public class WallSoldier5NPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildAdosWallSoldier(zone);
	}

	/**
	 * Creatures a soldier on the city wall
	 *
	 * @param zone StendhalRPZone
	 */
	private void buildAdosWallSoldier(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Hephestus") {
			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(79,  83));
				path.add(new Node(79, 100));
				path.add(new Node(76, 100));
				path.add(new Node(76,  83));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Dzień dobry, w czym mogę Ci pomóc?");
				addJob("Strzeżenie murów miasta wiąże się z wielką odpowiedzialnością.");
				addHelp("Strażnik Julius, znajdujący się przy głównym wejściu, pomaga nowicjuszom.");
				addQuest("Przepraszam, ale nie mam dla Ciebie żadnego zadania.");
				addGoodbye("Do widzenia, obywatelu.");
			}
		};

		npc.setDescription("Oto Hephestus, żołnierz, który strzeże mury miasta Ados.");
		npc.setEntityClass("youngsoldiernpc");
		npc.setGender("M");
		npc.setPosition(79, 83);
		zone.add(npc);
	}
}
