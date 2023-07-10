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
package games.stendhal.server.maps.fado.tavern;

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
 * Builds the tavern maid NPC.
 *
 * @author timothyb89/kymara
 */
public class MaidNPC implements ZoneConfigurator {
	private static final int TIME_OUT = 60;

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
		final SpeakerNPC tavernMaid = new SpeakerNPC("Old Mother Helena") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(28, 15));
				nodes.add(new Node(10, 15));
				nodes.add(new Node(10, 27));
				nodes.add(new Node(19, 27));
				nodes.add(new Node(19, 28));
				nodes.add(new Node(20, 28));
				nodes.add(new Node(21, 28));
				nodes.add(new Node(21, 27));
				nodes.add(new Node(28, 27));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("Jestem kelnerką w tej oberży. Sprzedajemy importowane napoje i dobre jedzenie.");
				addHelp("Dlaczego nie wziąć przyjaciół i zrobić sobie przerwy. Możesz skorzystać z długiego stołu, na którym można postawić jedzenie.");
				addQuest("Och nie mam czasu na coś takiego.");
				addGoodbye("Do widzenia. Wszyscy, ty i klienci sprawiacie, że praca jest ciężka ...");
			}
		};

		tavernMaid.setDescription("Oto Old Mother Helena. Jest świetną kucharką a jej zupa jest znana w całej krainie Faiumoni i nie tylko.");
		tavernMaid.setEntityClass("oldmaidnpc");
		tavernMaid.setGender("F");
		tavernMaid.setPlayerChatTimeout(TIME_OUT);
		tavernMaid.setPosition(10, 16);
		tavernMaid.setCollisionAction(CollisionAction.STOP);
		zone.add(tavernMaid);
	}
}
