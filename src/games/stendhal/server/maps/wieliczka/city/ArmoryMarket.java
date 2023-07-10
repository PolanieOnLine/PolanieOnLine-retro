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
package games.stendhal.server.maps.wieliczka.city;

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
public class ArmoryMarket implements ZoneConfigurator {
    @Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Kilian") {
			@Override
			public void createDialog() {
				addGreeting();
				addJob("Skupuję zbroje po dobrej cenie.");
				addHelp("Spójrz na tablicę i zobacz co skupuję i za jaką cenę.");
				addOffer("Spójrz na tablicę, aby zobaczyć moje ceny i co skupuję.");
				addQuest("Nie mam zadania dla Ciebie.");
				addGoodbye();
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(105, 45));
				nodes.add(new Node(102, 45));
				nodes.add(new Node(102, 48));
				nodes.add(new Node(105, 48));
				setPath(new FixedPath(nodes, true));
			}
		};

		npc.setDescription("Oto Kilian. Zajmuje się skupowaniem uzbrojenia.");
		npc.setEntityClass("man_002_npc");
		npc.setGender("M");
		npc.setPosition(105, 48);
		zone.add(npc);
	}
}
