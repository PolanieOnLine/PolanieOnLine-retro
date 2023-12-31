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
public class WallSoldier3NPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Vincendus") {
			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(79, 43));
				path.add(new Node(79, 60));
				path.add(new Node(76, 60));
				path.add(new Node(76, 43));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Witamy wędrowca w naszym pięknym mieście jakim jest Ados!");
				addJob("Robię tutaj bardzo ważną robotę. Strzegę murów miejskich i upewniam się, że żaden najeźdźca nie przeżyje swego ataku.");
				addHelp("Julius, strażnik głównej bramy, zawsze chętnie pomaga podróżnikom oraz daje im pewne wskazówki.");
				addGoodbye("Uważaj na siebie, wędrowcze.");
/*
If the player types yes then the soldier he will respond like this: example: Kill 10 tigers, 10 lions, 10 gnomes, 10 elephants  the reward will be 100exp and 40 or 50gold

The quest will be repeatable every 24 hours and the kind of monster and the amount that the player needs to kill will be random (it will have a cap on how many needs to kill for example no more than 40 lions the reward will be varied depends on how many of each kind the player neds to kill.
 */
			}
		};

		npc.setDescription("Oto Vincendus, żołnierz, który strzeże mury miasta Ados.");
		npc.setEntityClass("youngsoldiernpc");
		npc.setGender("M");
		npc.setPosition(79, 43);
		zone.add(npc);
	}
}
