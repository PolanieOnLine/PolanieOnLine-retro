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
package games.stendhal.server.maps.gdansk.tavern;

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
public class HandlarzBroniaNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Harrison") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(36, 12));
				nodes.add(new Node(32, 12));
				nodes.add(new Node(32, 16));
				nodes.add(new Node(36, 16));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witaj, co cię sprowadza do mnie?");
				addJob("Zajmuje się handlem brońmi. Jeśli chcesz coś ode mnie kupić lub mi sprzedać to lepiej spójrz na te tablice i nie zawracaj mi głowy... Mam ręce pełne roboty.");
				addHelp("Chwilowo nie potrzebuję twojej pomocy.");
				addOffer("Spójrz proszę na wywieszone tablice na ścianie.");
				addGoodbye();
			}
		};

		npc.setDescription("Oto Harrison. Zajmuje się handlem brońmi.");
		npc.setEntityClass("weaponsellernpc");
		npc.setGender("M");
		npc.setPosition(36, 12);
		zone.add(npc);
	}
}
