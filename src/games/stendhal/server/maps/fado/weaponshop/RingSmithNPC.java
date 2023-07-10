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
package games.stendhal.server.maps.fado.weaponshop;

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
 * Builds an NPC to buy gems and gold and sell engagement ring.
 * <p>
 * He is also the NPC who can fix a broken emerald ring
 * (../../quests/Ringmaker.java)
 * <p>
 * He is also the NPC who casts the wedding ring (../../quests/Marriage.java)
 *
 * @author kymara
 */
public class RingSmithNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Ognir") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(18, 8));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(15, 10));
				nodes.add(new Node(16, 10));
				nodes.add(new Node(16, 14));
				nodes.add(new Node(18, 14));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Cześć. W czym mogę #pomóc?");
				addJob("Pracuję ze #złotem oraz naprawiam i wytwarzam biżuterię.");
				addOffer("Sprzedaję pierścionki zaręczynowe z diamentem, które wyrabiam własnoręcznie. Skupuję także kamyki i złoto. Zobacz czerwony katalog na stole.");
				addReply(
						Arrays.asList("złotem", "gold"),
						"Jest odlewany ze złotych samorodków. które można wydobyć w pobliżu rzeki Or'ril. Nie odlewam złota własnoręcznie, ale kowal w Ados tak.");
				addHelp("Jestem ekspertem od #'obrączki ślubnej' i #'pierścień szmaragdowy' czasami zwanymi ring of #life.");
				addQuest("Cóż mógłbyś rozważyć możliwość wzięcia ślubu jako zadania! Zapytaj mnie o kupno pierścionka zaręczynowego #'buy pierścień zaręczynowy' jeżeli potrzebujesz.");
				addGoodbye("Do widzenia przyjacielu.");
			}
		};

		npc.setDescription("Oto Ognir, przyjazny brodaty gość.");
		npc.setEntityClass("ringsmithnpc");
		npc.setGender("M");
		npc.setPosition(18, 8);
		zone.add(npc);
	}
}
