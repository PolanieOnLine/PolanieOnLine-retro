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
package games.stendhal.server.maps.ratcity.bakery;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Provides a Ratman chef running the Rat City bakery.
 *
 * @author omero
 */
public class RatChefNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Gaston") {
			@Override
			protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(12, 3));
                nodes.add(new Node(3, 3));
                nodes.add(new Node(7, 3));
                nodes.add(new Node(7, 6));
                nodes.add(new Node(9, 6));
                nodes.add(new Node(9, 12));
                nodes.add(new Node(16, 12));
                nodes.add(new Node(16, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("Jestem najlepszym szefem kuchni od  #naleśników z polewą #czekoladową w mieście. Zapytaj mnie o #upiecznie ich dla Ciebie!");
				addReply(Arrays.asList("naleśników", "naleśniki z polewą czekoladową"),
                    "Ach, le dessert dla księcia... Smak, w który naprawdę wierzę i który mógłby zrobić z kanibali cywilizowanych dżentelmenów.");
                /**
                 * chocolate bar is the item, and the parser knows nothing about bar/bars.
                 * Handle all possibilities explicitly.
                 */
				addReply(Arrays.asList("chocolate", "chocolate bar", "czekoladową", "czekolada", "tabliczka czekolady"),
					"Rzadkość. Wygląda na to, że tylko bardzo paskudni i morderczy ludzie mają ją tylko przy sobie.");
				addReply(Arrays.asList("flour","mąka"),
                    "Wcią.. ahem.. zdbywam swoje zapasy mąki z okolic miasta Semos.");
				addReply(Arrays.asList("egg","jajo"),
                    "Poszukaj go, gdzie kury są.");
				addReply(Arrays.asList("milk","mleko"),
                    "Farma jest dobrym miejscem, gdzie można znaleźć mleko.");
				addReply(Arrays.asList("butter","osełka masła"),
                    "Tam gzie znajdziesz mleko to znajdziesz też masło!");
				addReply(Arrays.asList("ekstrakt litworowy"),
                    "Otrzymuje się z destylizacji trzciny cukrowej. Możesz także spróbować popytać o to na bazarze w Ados.");
                addReply(Arrays.asList("sugar","cukier"),
                    "Powstaje z przerobienia trzciny cukrowej, który możesz ściąć na #polu #trzciny."
                        + " Potrzebujesz trochę #narzędzi #kuchennych do zmielenia.");
                addReply(Arrays.asList("kitchen tool" ,"narzędzi kuchennych"),
                    "Tak młynek do cukru! Bardzo źle straciłem swój, który pożyczyłem od Erny jakiś czas temu... Proszę nie wspominaj jej mojego imienia!");
                addReply(Arrays.asList("erna", "erny"),
                    "Jest asystentką Leandera. Zawsze możesz ją znaleźć w piekarni w Semos!");
                addReply(Arrays.asList("cane", "canes", "cane field", "cane fields", "sugar cane", "sugar canes", "polu trzciny", "pole", "pole trzciny"),
                    "Słyszałem, że trzcina cukrowa potrzebują ciepła i wilgotnego klimatu do życia. Może mógłbyś znaleźć je na wyspie Athor");
				addOffer("Serwuje naleśniki z polewą czekoladową.");
				addHelp("Piekę naleśniki z polewą czekoladową. Powiedz #'upiecz naleśniki z polewą czekoladową', a upiekę je dla ciebie.");
				addGoodbye("Au revoir voyageur... Wróć i odwiedź mnie kiedy tylko chcesz!");
			}
		};

		npc.setDescription("Oto Gaston. Jest najlepszym szefem kuchni w całym mieście szczurów.");
		npc.setEntityClass("ratchefnpc");
		npc.setGender("M");
		npc.setDirection(Direction.DOWN);
		npc.setPosition(16, 3);
		zone.add(npc);
	}
}
