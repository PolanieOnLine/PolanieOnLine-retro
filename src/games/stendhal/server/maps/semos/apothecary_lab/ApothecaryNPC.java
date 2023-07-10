/***************************************************************************
 *                    Copyright © 2019 - Stendhal                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.apothecary_lab;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author AntumDeluge
 */
public class ApothecaryNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Jameson") {
			@Override
			protected void createPath() {
				List<Node> nodes=new LinkedList<Node>();
				nodes.add(new Node(7,9));
				nodes.add(new Node(16,9));
				nodes.add(new Node(16,12));
				nodes.add(new Node(19,12));
				nodes.add(new Node(19,16));
				nodes.add(new Node(19,12));
				nodes.add(new Node(15,12));
				nodes.add(new Node(15,17));
				nodes.add(new Node(15,12));
				nodes.add(new Node(7,12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Cześć i witaj w moim laboratorium.");
				addJob("Kiedyś byłem #aptekarzem, ale teraz jestem na emeryturze.");
				addHelp("Przykro mi, ale nie jestem w stanie tobie pomóc.");
				addOffer("Nie mam nic do zaoferowania.");
				addQuest("Wybacz, ale w tej chwili nie mam nic dla ciebie."); // Antivenom Ring quest not loaded
				addGoodbye("Proszę nikomu nie mów o moim laboratorium.");
				addReply(Arrays.asList("Apothecary", "aptekarzem"), "Byłem głównym badaczem oraz pracowałem dla jednego z najpotężniejszych przywódców Faimouni."
						+ "Jednak ten przywódca został skorumpowany i zażądał, abym wykorzystał swoje umiejętności do stworzenia śmiercionośnej broni wojennej."
						+ "W każdym razie uciekłem i odtąd się tu chowam.");
				addReply("Klaas", "O tak, mój stary dobry przyjaciel. Często podróżowałem na wyspę #Athor, aby zbierać bardzo rzadkie zioła"
						+ " #kokuda. Dzięki temu bardzo dobrze poznałem Klaasa.");
				addReply("Athor", "Jeszcze nie odwiedziłeś Athor? To piękna wyspa. Wspaniałe miejsce na oderwanie się od trosk życia codziennego."
						+ " Ale trzymaj się z daleka od terytorium kanibali. Jeśli zaproszą cię na obiad to możesz nigdy nie zobaczyć domu.");
				/* this is a required ingredient for Antivenom Ring quest, but reply is added here because Jameson highlights
				 * keyword "kokuda" even if quest is not active
				 */
				addReply("kokuda", "Kokuda to zioło, które można znaleźć tylko w labiryncie na wyspie #Athor.");
			}
		};

		npc.setDescription("Oto Jameson. Stale pracuje z dala od ludzi.");
	    npc.setEntityClass("apothecarynpc");
	    npc.setGender("M");
		npc.setPosition(7, 9);
		zone.add(npc);
	}
}
