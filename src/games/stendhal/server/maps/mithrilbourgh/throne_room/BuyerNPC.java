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
package games.stendhal.server.maps.mithrilbourgh.throne_room;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds an NPC to buy previously un bought mainio weapons.
 * He is the
 *
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Despot Halb Errvl") {
			@Override
			protected void createDialog() {
				addGreeting("Mam nadzieję, że miałeś dobry powód, aby mi przeszkodzić?");
				addReply(ConversationPhrases.YES_MESSAGES, "Dobra odpowiedź. Czego chcesz!");
				addReply(ConversationPhrases.NO_MESSAGES, "W takim razie wynoś się stąd nim nakarmię tobą smoki!");
				addJob("Czy mój tytuł nie mówi sam za siebie...?");
				addReply("mainio","Moi doradcy powiedzieli mi znaczenie słowa 'excellent' w obcych językach. Jeżeli tak jest to moi ludzie muszą to nosić! Nie sądzę, aby Diehelm Brui wystarczająco dobrze ich uzbrajał!");
				addHelp("Moja armia musi mieć najlepsze przedmioty. #Zaoferuj mi jakiś rzadki #mainiocyjski ekwipunek. Słyszałem historie i dobrze zapłacę.");
				//addQuest("Teraz wojsko Mithrilbourghtów i Ja nie potrzebujemy twoich usług.");
				addGoodbye("Do widzenia.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.setDescription("Oto Despot Halb Errvl, niecierpliwy człowiek. Otacza się wojskiem lotniczym.");
		npc.setEntityClass("blacklordnpc");
		npc.setGender("M");
		npc.setPosition(19, 4);
		zone.add(npc);
	}
}
