/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deathmatch;

import static games.stendhal.server.core.rp.achievement.factory.DeathmatchAchievementFactory.HELPER_SLOT;
import static games.stendhal.server.core.rp.achievement.factory.DeathmatchAchievementFactory.SOLOER_SLOT;
import static games.stendhal.server.core.rp.achievement.factory.DeathmatchAchievementFactory.WINS_SLOT;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.WriteHallOfFamePointsCommand;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Handles player claim of victory by giving reward after verifying the winning.
 */
public class DoneAction implements ChatAction {
	private static final Logger logger = Logger.getLogger(DoneAction.class);

	private final DeathmatchInfo deathmatchInfo;

	public DoneAction(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	};

	/**
	 * Creates the player bound special trophy helmet and equips it.
	 *
	 * @param player Player object
	 * @return Helmet
	 */
	private Item createTrophyHelmet(final Player player) {
		final Item helmet = SingletonRepository.getEntityManager().getItem("zdobyczny hełm");
		helmet.setBoundTo(player.getName());
		helmet.put("def", 1);
		helmet.setInfoString(player.getName());
		helmet.setPersistent(true);
		helmet.setDescription("Oto główna nagroda dla wojownika " + player.getName()
		        + " za wygranie Deathmatchu. Noś ją z dumą.");
		player.equipOrPutOnGround(helmet);
		return helmet;
	}

	/**
	 * Updates the player's points in the hall of fame for deathmatch.
	 *
	 * @param player Player
	 */
	private void updatePoints(final Player player) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		DBCommandQueue.get().enqueue(new WriteHallOfFamePointsCommand(player.getName(), "D", deathmatchState.getPoints(), true), DBCommandPriority.LOW);
	}

	/**
	 * Tracks helping players & updates achievements related to helping with deathmatch.
	 *
	 * @param aided
	 * 		The player who is being helped.
	 * @param timestamp
	 * 		Time the deathmatch was completed.
	 */
	private void updateHelpers(final Player aided, final long timestamp) {
		for (final Player helper: deathmatchInfo.getArena().getPlayers()) {
			final String helperName = helper.getName();
			// player must have helped kill at least 3 deathmatch creatures to count towards achievement
			final int aidedKills = deathmatchInfo.getAidedKills(helperName);
			if (aidedKills > 2) {
				int helpCount = 0;
				if (helper.hasQuest(HELPER_SLOT)) {
					try {
						helpCount = Integer.parseInt(helper.getQuest(HELPER_SLOT, 0));
					} catch (final NumberFormatException e) {
						logger.error("Deathmatch helper quest slot value not an integer.");
						e.printStackTrace();
					}
				}
				helpCount++;

				helper.setQuest(HELPER_SLOT, 0, Integer.toString(helpCount));
				helper.setQuest(HELPER_SLOT, 1, Long.toString(timestamp));

				SingletonRepository.getAchievementNotifier().onFinishDeathmatch(helper);
			}
		}
	}
	
	/**
	 * Tracks winning player & updates achievements related to win in deathmatch.
	 *
	 * @param player
	 * 		The player who winning a deathmatch.
	 */
	private void updateWins(final Player player) {
		int winCount = 0;
		if (player.hasQuest(WINS_SLOT)) {
			try {
				winCount = Integer.parseInt(player.getQuest(WINS_SLOT, 0));
			} catch (final NumberFormatException e) {
				logger.error("Deathmatch wins quest slot value not an integer.");
				e.printStackTrace();
			}
		}
		winCount++;
		player.setQuest(WINS_SLOT, 0, Integer.toString(winCount));
	}

	/**
	 * Tracks soloing players & updates achievements related to deathmatch.
	 *
	 * @param soloer
	 *     The player who started the deathmatch.
	 * @param timestamp
	 *     Time the deathmatch was completed.
	 */
	private void updateSoloer(final Player soloer, final long timestamp) {
		if (deathmatchInfo.wasAided()) {
			return;
		}
		final int soloCount = MathHelper.parseInt(soloer.getQuest(SOLOER_SLOT, 0)) + 1;
		soloer.setQuest(SOLOER_SLOT, 0, Integer.toString(soloCount));
		soloer.setQuest(SOLOER_SLOT, 1, Long.toString(timestamp));
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.VICTORY) {
			raiser.say("Nie oszukuj mnie! Jedyne co możesz zrobić to #wycofać się lub wygrać.");
			return;
		}

		updatePoints(player);

		// We assume that the player only carries one trophy helmet.
		final Item helmet = player.getFirstEquipped("zdobyczny hełm");
		if (helmet == null) {
			createTrophyHelmet(player);
			raiser.say("Oto twój specjalny zdobyczny hełm. Trzymaj tak dalej, a z każdym ukończonym deathmatchem "
				+ " jego obrona będzie się zwiększać o 1. Teraz powiedz mi czy chcesz wyjść, mówiąc #wyjdź.");
		} else {
			int defense = 1;
			if (helmet.has("def")) {
				defense = helmet.getInt("def");
			}
			defense++;
			final int maxdefense = 5 + (player.getLevel() / 5);
			if (defense > maxdefense) {
				helmet.put("def", maxdefense);
				raiser.say("Z przykrością oznajmiam, że osiągnąłeś szczyt obrony dla swojego hełmu, który wynosi "
				                + maxdefense);
			} else {
				helmet.put("def", defense);
				String message;
				if (defense == maxdefense) {
					message = "Twój hełm został magicznie wzmocniony do maksimum jak na twój poziom czyli " + defense;
				} else {
					message = "Twój hełm został magicznie wzmocniony do obrony " + defense;
				}
				raiser.say(message + ". Powiedz mi #wyjdź, kiedy będziesz chciał opuścić arenę.");
			}
		}
		player.updateItemAtkDef();
		TurnNotifier.get().notifyInTurns(0, new NotifyPlayerAboutHallOfFamePoints((SpeakerNPC) raiser.getEntity(), player.getName(), "D", "deathmatch_score"));

		new SetQuestAction("deathmatch", 0, "done").fire(player, sentence, raiser);
		// Track the number of wins.
		updateWins(player);

		// track helpers & soloers
		final long timestamp = System.currentTimeMillis();
		updateHelpers(player, timestamp);
		updateSoloer(player, timestamp);
		SingletonRepository.getAchievementNotifier().onFinishDeathmatch(player);
	}

}
