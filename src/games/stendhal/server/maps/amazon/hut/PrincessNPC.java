/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.amazon.hut;

import java.util.Arrays;
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
 * Builds the princess in Princess Hut on amazon island.
 *
 * @author Teiv
 */
public class PrincessNPC implements ZoneConfigurator {
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
		final SpeakerNPC princessNPC = new SpeakerNPC("Księżniczka Esclara") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(6, 13));
				nodes.add(new Node(14, 13));
				nodes.add(new Node(14, 4));
				nodes.add(new Node(6, 4));
				nodes.add(new Node(6, 3));
				nodes.add(new Node(4, 3));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(6, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Huh, co ty tutaj robisz?!");
				addReply(Arrays.asList("sorry", "przepraszam"), "Nie powinieneś się do mnie wślizgiwać od tak!");
				addReply(Arrays.asList("look", "zobacz", "zobaczyć", "przeszukaj"), "Nie powinieneś tu szperać, tutaj wszystko jest moje!");
				addReply(Arrays.asList("nothing", "nic"), "Odejdź i idź robić to gdzie indziej, a nie w moim domku!");
				addJob("Praca? Myślisz, że księżniczka taka jak ja potrzebuje pracy? Ha!");
				addHelp("Strzeż się moich sióstr na wyspie. One nie lubią obcych.");
				addOffer("Nie mam nic do zaoferowania.");
				addGoodbye("Do widzenia i strzeż się barbarzyńców.");
			}
		};

		princessNPC.setDescription("Oto księżniczka Esclara. Pachnie kokosem i ananasem...");
		princessNPC.setEntityClass("amazoness_princessnpc");
		princessNPC.setGender("F");
		princessNPC.setPosition(6, 13);
		princessNPC.setCollisionAction(CollisionAction.STOP);
		zone.add(princessNPC);
	}
}
