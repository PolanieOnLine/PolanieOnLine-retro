/***************************************************************************
 *                 (C) Copyright 2019-2022 - PolanieOnLine                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.zakopane.house;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author KarajuSs
 */
public class CelinaNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Celina") {
			@Override
			protected void createDialog() {
				addGreeting("*kaszel* Ugh.. Cz..Cześć. *kaszel*");
				addOffer("*kaszel*");
				addHelp("Ta *kaszel* tko obiecał przynieść dla mnie lekarstwo... *kaszel*");
				addGoodbye("Miło było Cię poznać. *kaszel*");
			}
		};

		npc.setDescription("Oto Celina. Jest bardzo chora.");
		npc.setEntityClass("npcdzieckochore");
		npc.setGender("F");
		npc.setPosition(19, 2);
		npc.initHP(50);
		zone.add(npc);
	}
}
