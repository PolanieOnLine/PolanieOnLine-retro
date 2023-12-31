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
package games.stendhal.server.maps.krakow.planty;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class MysliwyNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Myśliwy") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 127));
				nodes.add(new Node(16, 127));
				nodes.add(new Node(16, 122));
				nodes.add(new Node(27, 122));
				nodes.add(new Node(27, 126));
				nodes.add(new Node(33, 126));
				nodes.add(new Node(33, 121));
				nodes.add(new Node(37, 121));
				nodes.add(new Node(37, 127));
				nodes.add(new Node(44, 127));
				nodes.add(new Node(44, 120));
				nodes.add(new Node(37, 120));
				nodes.add(new Node(37, 116));
				nodes.add(new Node(27, 116));
				nodes.add(new Node(27, 117));
				nodes.add(new Node(8, 117));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witaj nieznajomy.");
				addJob("Tak, mam. Przynieś mi uzębienie wszelkich zwierząt. Możesz coś #zaoferować.");
				addHelp("Skupuję kły, zęby itp, jeżeli coś masz to #zaoferuj mi to.");
				addOffer("Skupuję kieł wilczy 10, kieł niedźwiedzi 15, dziób ptaka 30, ząb potwora 200, kieł smoka 500, kieł złotej kostuchy 1000.");
				addQuest("Nie teraz, jestem zajęty. Właśnie znalazłem trop.");
				addGoodbye("Do widzenia.");
			}
		};

		npc.setDescription("Oto myśliwy króla.");
		npc.setEntityClass("scarletarmynpc");
		npc.setGender("M");
		npc.setPosition(8, 127);
		zone.add(npc);
	}
}
