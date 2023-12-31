/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.canyon;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

/**
 * The bridge tollbooth NPC
 *
 * @author AntumDeluge
 */
public class TollboothNPC implements ZoneConfigurator  {
    private final int REQUIRED_COINS = 25;

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Toller") {
			@Override
			public void createDialog() {
			    addGreeting("Witaj jeśli chcesz przekroczyć most #Antum to musisz #zapłacić " + REQUIRED_COINS + " money.");
				addHelp("Witaj jeśli chcesz przekroczyć most #Antum to musisz #zapłacić " + REQUIRED_COINS + " money.");
				addJob("Pilnuję mostu, który łączy Semos z Antum.");
				addGoodbye("Żegnaj.");
				addReply("antum", "Antum jest wspaniałe.");
			}

            @Override
            protected void onGoodbye(RPEntity player) {
                setDirection(Direction.LEFT);
            }
		};

        // Player has enough money and pays toll
        npc.add(ConversationStates.ATTENDING,
                Arrays.asList("pay", "zapłacić", "zapłać", "zaplac"),
                new PlayerHasItemWithHimCondition("money", 25),
                ConversationStates.IDLE,
                "Nie ma opłat w przypadku powrotu do Semos. Wystarczy tylko przekroczyć przejście.",
                new MultipleActions(new DropItemAction("money", 25),
                        new TeleportAction("0_semos_canyon", 36, 29, Direction.UP)));

        // Player does not have enough money for toll
        npc.add(ConversationStates.ATTENDING,
                Arrays.asList("pay"),
                new NotCondition(new PlayerHasItemWithHimCondition("money", 25)),
                ConversationStates.ATTENDING,
                "Przykro mi, ale nie masz pieniędzy.",
                null);

        npc.setDescription("Oto Toller, obsługa rogatki.");
        npc.setEntityClass("youngsoldiernpc");
        npc.setGender("M");
        npc.setPosition(37, 30);
        npc.setDirection(Direction.LEFT);
        zone.add(npc);
	}
}
