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
package games.stendhal.server.maps.ados.city;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ListProducedItemDetailAction;
import games.stendhal.server.entity.npc.action.ListProducedItemsOfClassAction;
import games.stendhal.server.entity.npc.condition.TriggerIsProducedItemOfClassCondition;

/**
 * Creates a woman NPC to help populate Ados
 *
 */
public class HolidayingWomanNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Alice Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(47, 90));
				nodes.add(new Node(38, 90));
				nodes.add(new Node(38, 91));
				nodes.add(new Node(3, 91));
				nodes.add(new Node(3, 64));
				nodes.add(new Node(40, 64));
				nodes.add(new Node(40, 75));
				nodes.add(new Node(47, 75));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Cześć.");
				addHelp("Kiedy spacerowałam po mieście, zobaczyłam tawernę. Wyglądała świetnie! Zaglądałeś do środka? Pachnie tam fantastycznie!");
				addOffer("Jestem ekspertką od spraw #żywności, po tych wszystkich podróżach wakacyjnych!");
				addQuest("Możesz spróbować każdego #jedzenia dostępnego u kucharzy i szefów kuchni w całej krainie. Mogę również ci opowiedzieć, czego próbowałam podczas moich podróży.");
				addReply(Arrays.asList("food", "jedzenie", "żywność", "żywności"), null,
						new ListProducedItemsOfClassAction("food", "Sądzę, że spróbowałam już wszystkiego, [#items]. Mogę opowiedzieć więcej o każdej żywności, jeśli chcesz."));
				add(
						ConversationStates.ATTENDING,
						"",
						new TriggerIsProducedItemOfClassCondition("food"),
						ConversationStates.ATTENDING,
						null,
						new ListProducedItemDetailAction()
					);
				addJob("Hahaha! Jestem tutaj na urlopie i po prostu wyszłam na spacer.");
				addGoodbye("Do widzenia.");
			}
		};

		npc.setDescription("Oto Alice Farmer. Spędza wakacje w Ados.");
		npc.setEntityClass("woman_016_npc");
		npc.setGender("F");
		npc.setPosition(47, 90);
		zone.add(npc);
	}
}
