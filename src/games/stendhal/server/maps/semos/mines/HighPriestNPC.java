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
package games.stendhal.server.maps.semos.mines;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.Occasion;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public class HighPriestNPC implements ZoneConfigurator {
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
		buildMineArea(zone);
	}

	private void buildMineArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Aenihata") {
			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						String reply = "Utrzymuję barierę, która trzyma potwora zwanego #balrog z daleka.";

						if (player.getLevel() < 150) {
							reply += " Balrog Cię zabije. Uciekaj!";
						} else {
							reply += " Będę trzymał barierę, aby chronić Faiumoni. Zabij to.";
						}
						raiser.say(reply);
					}
				});

				addReply("balrog",
						"Najstraszniejszy potwór w armii Balrogów.");
				addGoodbye();
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.LEFT);
			}
		};


		npc.addInitChatMessage(null, new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("AenihataReward")
						&& (player.getLevel() >= 150)) {
					player.setQuest("AenihataReward", "done");

					player.setAtkXP(1000000 + player.getAtkXP());
					player.setDefXP(10000000 + player.getDefXP());
					if (Testing.COMBAT && !Occasion.SECOND_WORLD) {
						player.setRatkXP(500000 + player.getRatkXP());
					}
					player.addXP(100000);

					player.incAtkXP();
					player.incDefXP();
					if (Testing.COMBAT && !Occasion.SECOND_WORLD) {
						player.incRatkXP();
					}
				}

				if (!player.hasQuest("AenihataFirstChat")) {
					player.setQuest("AenihataFirstChat", "done");
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setDescription("Oto Aenihata. Jest jednym z najpotężniejszych Wysokich Kapłanów, który próbuje chronić Faiumoni swoimi umiejętnościami magicznymi.");
		npc.setEntityClass("highpriestnpc");
		npc.setGender("M");
		npc.setPosition(23, 44);
		npc.setDirection(Direction.LEFT);
		npc.initHP(85);
		zone.add(npc);
	}
}
