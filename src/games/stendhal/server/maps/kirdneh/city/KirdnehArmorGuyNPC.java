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
package games.stendhal.server.maps.kirdneh.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * In Kirdneh open market .
 */
public class KirdnehArmorGuyNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildlawrence(zone);
	}

	private void buildlawrence(final StendhalRPZone zone) {
		final SpeakerNPC lawrence = new SpeakerNPC("Lawrence") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(63, 95));
				nodes.add(new Node(64, 95));
				nodes.add(new Node(64, 93));
				nodes.add(new Node(70, 93));
				nodes.add(new Node(70, 95));
				nodes.add(new Node(71, 95));
				nodes.add(new Node(71, 93));
				nodes.add(new Node(63, 93));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Skupuję zbroje po dobrej cenie.");
				addHelp("Spójrz na tablicę i zobacz co skupuję i za jaką cenę.");
				addOffer("Spójrz na tablicę, aby zobaczyć moje ceny i co skupuję.");
				addQuest("Nie mam zadania dla Ciebie.");
				addGoodbye("Jeżeli znajdziesz ząb z mitycznego czarnego smoka to daj mi znać.");
			}
		};

		lawrence.setDescription("Oto Lawrence. Jego miejsce pracy jest na rynku.");
		lawrence.setEntityClass("man_002_npc");
		lawrence.setGender("M");
		lawrence.setPosition(63, 95);
		zone.add(lawrence);
	}
}
