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
package games.stendhal.server.maps.athor.holiday_area;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class TouristFromAdosNPC implements ZoneConfigurator  {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Zara") {
			@Override
			public void say(final String text) {
				// She doesn't move around because she's "lying" on her towel.
				say(text, false);
			}

			@Override
			public void createDialog() {
				addGreeting("Miło Cię poznać!");
				addJob("Jestem na wakacjach! Porozmawiajmy o czymś innym!");
				addHelp("Uważaj! Na tej wyspie jest pustynia gdzie wielu poszukiwaczy przygód straciło życie...");
				addGoodbye("Mam nadzieję, że zobaczymy się później!");
				// more dialog is defined in the SuntanCreamForZara quest.
			}
		};

		npc.setDescription("Oto Zara opalająca się na plaży i sądząca, że za bardzo się spiekła.");
		npc.setEntityClass("swimmer8npc");
		npc.setGender("F");
		npc.setPosition(60, 33);
		npc.setDirection(Direction.DOWN);
		npc.put("no_shadow", ""); // sunbather laying down
		zone.add(npc);
	}
}
