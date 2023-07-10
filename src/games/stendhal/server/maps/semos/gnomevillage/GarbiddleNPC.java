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
package games.stendhal.server.maps.semos.gnomevillage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Inside Gnome Village.
 */
public class GarbiddleNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildgarbiddle(zone);
	}

	private void buildgarbiddle(final StendhalRPZone zone) {
		final SpeakerNPC garbiddle = new SpeakerNPC("Garbiddle") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(37, 112));
				nodes.add(new Node(41, 112));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witam w naszej cudownej wiosce.");
				addJob("Jestem tutaj, aby skupować towary na deszczowy dzień.");
				addHelp("Skupuję kilka rzeczy. Poczytaj znak, aby dowiedzieć się czego potrzebujemy.");
				addOffer("Poczytaj znak, aby dowiedzieć się czego potrzebujemy.");
				addQuest("Dziękuję za pytanie, ale czuję się dobrze.");
				addGoodbye("Do widzenia. Cieszę się, że odwiedziłeś nas.");
			}
		};

		garbiddle.setDescription("Oto Garbiddle, mała pani gnom. Czeka na klientów.");
		garbiddle.setEntityClass("gnomenpc");
		garbiddle.setGender("F");
		garbiddle.setPosition(37, 112);
		zone.add(garbiddle);
	}
}
