/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
// Base on ../games/stendhal/server/maps/ados/barracks/BuyerNPC.java
package games.stendhal.server.maps.dragon_knights;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class MarianekNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Marianek") {
			@Override
			protected void createDialog() {
				addGreeting("Witaj dzielny rycerzu!");
				addJob("Jestem kowalem na tych włościach. Może mam dla ciebie #zadanie.");
				addHelp("Niczego nie potrzebuję.");
				addGoodbye("Żegnam.");
			}
		};

		npc.setDescription("Oto Marianek. Wyglądem przypomina kowala.");
		npc.setEntityClass("blacksmithnpc");
		npc.setGender("M");
		npc.setPosition(5, 4);
		zone.add(npc);
	}
}
