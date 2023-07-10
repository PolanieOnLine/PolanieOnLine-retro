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
package games.stendhal.server.maps.krakow.sukiennice;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class WaldekNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Waldek") {
			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Skupuję różne owoce. Na stole leży książka, w niej jest moja oferta.");
				addHelp("Skupuję różne owoce. Na stole leży książka, w niej jest moja oferta.");
				addOffer("Skupuję owoce, oferta moja jest w książce.");
				addGoodbye();
			}
		};

		npc.setDescription("Oto Waldek. Może coś opylisz u niego.");
		npc.setEntityClass("man_008_npc");
		npc.setGender("M");
		npc.setPosition(26, 17);
		npc.setDirection(Direction.LEFT);
		zone.add(npc);
	}
}
