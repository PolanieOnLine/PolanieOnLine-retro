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
package games.stendhal.server.maps.nalwor.assassinhq;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Inside Nalwor Assassin Headquarters - cellar .
 */
public class ChiefFalatheenDishwasherNPC implements ZoneConfigurator  {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Chief Falatheen Humble Dishwasher") {
			@Override
			public void createDialog() {
				addGreeting("Lepiej miej dobry powód za zawracanie mi głowy. Jestem zajęty zmywaniem naczyń!");
				addJob("To moja praca zmywanie wszystkich naczyń po tych wszystkich małych bachorach.");
				addHelp("Mogę kupić od Ciebie warzywa i zioła. Spójrz na tablicę na ścianie, aby dowiedzieć się czego potrzebuję.");
				addOffer("Spójrz na tablice na ścianie, aby poznać moje ceny.");
				addQuest("Mógłbyś mi pomóc w #ucieczce od tych bandziorów. Cóż... może nie.");
				addGoodbye("Nie zapomnij gdzie teraz jestem. Wróć kiedyś. Jestem tutaj samotny.");
				addReply(Arrays.asList("escape", "ucieczce"), "Tak! Chce spełnić swoje marzenie. Mother Helena zaoferowała mi wspaniałą pracę. Potrzebuje osoby do zmywania naczyń. Pełno narzekających klientów!!!");
			}
		};

		npc.setDescription("Oto Chief Falatheen Humble Dishwasher wyglądający na silnego mężczyznę. Je dużo zdrowych warzyw, aby tak wyglądać!");
		npc.setEntityClass("chieffalatheennpc");
		npc.setGender("M");
		npc.setPosition(20, 3);
		zone.add(npc);
	}
}
