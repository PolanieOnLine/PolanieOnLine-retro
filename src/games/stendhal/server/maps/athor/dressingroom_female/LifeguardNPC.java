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
package games.stendhal.server.maps.athor.dressingroom_female;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;

/**
 * Dressing rooms at the Athor island beach (Inside / Level 0).
 *
 * @author daniel
 */
public class LifeguardNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildFemaleDressingRoom(zone);
	}

	private void buildFemaleDressingRoom(final StendhalRPZone zone) {
		final SpeakerNPC pam = new SpeakerNPC("Pam") {
			@Override
			protected void createDialog() {
				addJob("Jestem jedną z ratowniczek na tej plaży. Jak widzisz zajmuję się też damską przebieralnią. Mogę też zrobić #'olejek do opalania'.");
				addHelp("Powiedz #pożycz jeżeli chciałabyś pożyczyć kostium kąpielowy ( #swimsuit )!");
				addGoodbye("Miłej zabawy!");

				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("swimsuit", 5);
				final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList);
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, Arrays.asList("borrow", "pożycz"));

				addReply(Arrays.asList("suntan", "cream", "suntan cream", "olejek do opalania"),
						"Olejek do opalania Davida i mój jest słynny na całą wyspę, ale że wejście do labiryntu"
						+ " jest zablokowane to nie możemy zdobyć wszystkich składników. Jeżeli przyniesiesz mi"
						+ " składniki to mogę zrobić dla Ciebie nasz specjalny krem do opalania."
						+ " Powiedz tylko #zrób.");
				addReply("arandula",
						"Arandula jest ziołem rosnącym w okolicach Semos.");
				addReply("kokuda",
				        "Nie możemy zdobyć Kokudy, która rośnie na wyspie, ponieważ wejście do labiryntu"
				        + " gdzie można znaleźć to zioło jest zablokowane.");
				addReply("mały eliksir",
						"Jest to mała buteleczka wypełniona miksturą. Możesz ją kupić w kilku miejscach.");
			}
		};

		pam.setDescription("Oto Pam. Czeka na modelki, które mogą nosić strój kąpielowy jej najnowszej kolekcji.");
		pam.setEntityClass("lifeguardfemalenpc");
		pam.setGender("F");
		pam.setDirection(Direction.LEFT);
		pam.setPosition(12, 11);
		zone.add(pam);
	}
}
