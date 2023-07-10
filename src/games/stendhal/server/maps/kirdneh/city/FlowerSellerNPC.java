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
package games.stendhal.server.maps.kirdneh.city;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the flower seller in kirdneh.
 *
 * @author kymara
 */
public class FlowerSellerNPC implements ZoneConfigurator {
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
		final SpeakerNPC sellernpc = new SpeakerNPC("Fleur") {
			@Override
			protected void createDialog() {
				addGreeting("Cześć! Przyszedłeś tutaj #pohandlować?");
				addReply(ConversationPhrases.YES_MESSAGES, "Dobrze! Mogę sprzedać Tobie piękną czerwoną różę. Nie chodzi mi o rzadki orchideę. Tylko Róża Kwiaciarka wie, gdzie one rosną i nikt nie wie gdzie jest Róża Kwiaciarka!");
				addReply(ConversationPhrases.NO_MESSAGES, "Bardzo dobrze. Jeżeli będę mogła pomóc to daj znać.");
				addJob("Sprzedaję tutaj róże.");
				addHelp("Jeżeli będziesz potrzebował pieniędzy to tutaj w Kirdneh jest oddział banku Fado. Mieści się w małym budynku na północ od muzeum we wschodniej części miasta.");
				addGoodbye("Do widzenia i zapraszam ponownie!");
			}
		};

		sellernpc.setDescription("Oto Fleur. Jej róże są dla młodych par.");
		sellernpc.setEntityClass("woman_001_npc");
		sellernpc.setGender("F");
		sellernpc.setPosition(64, 82);
		zone.add(sellernpc);
	}
}
