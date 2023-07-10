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
package games.stendhal.server.maps.kirdneh.museum;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a Curator NPC in Kirdneh museum .
 *
 * @author kymara
 */
public class CuratorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Hazel") {
			@Override
			protected void createDialog() {
				addGreeting("Witam w Muzeum Kirdneh.");
				addJob("Jestem kuratorką tego muzeum. Oznacza to, że ja organizuję wystawy i szukam nowych #eksponatów.");
				addHelp("To miejsce jest na rzadkie artefakty i specjalne #eksponaty.");
				addReply(Arrays.asList("exhibits", "eksponaty", "eksponatów"),"Być może będziesz mieć dryg do wyszukiwania rzadkich przedmiotów i chciałbyś zrobić #zadanie dla mnie.");
				// remaining behaviour defined in games.stendhal.server.maps.quests.WeeklyItemQuest
				addGoodbye("Do widzenia. Miło się z tobą rozmawiało.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.RIGHT);
			}
		};

		npc.setDescription("Oto Hazel, kustoszka muzeum Kirdneh.");
		npc.setEntityClass("curatornpc");
		npc.setGender("F");
		npc.setPosition(2, 38);
		npc.setDirection(Direction.RIGHT);
		zone.add(npc);
	}
}
