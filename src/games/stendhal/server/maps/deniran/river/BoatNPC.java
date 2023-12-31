/***************************************************************************
 *                      (C) Copyright 2019 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.river;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SilentNPC;

public class BoatNPC implements ZoneConfigurator  {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPCs(zone);
	}

	private void buildNPCs(StendhalRPZone zone) {
		final SilentNPC npc = new SilentNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(-3, 70));
				nodes.add(new Node(-3, 72));
				nodes.add(new Node(129, 72));
				nodes.add(new Node(129, 70));
				setPath(new FixedPath(nodes, true));
			}
		};

		npc.setIgnoresCollision(true);
		npc.setResistance(0);
		npc.setEntityClass("row_boat");
		npc.setDescription("Oto łódka.");
		npc.setPosition(125, 68);
		npc.setDirection(Direction.LEFT);
		npc.setName("Łódka");
		zone.add(npc);
	}
}
