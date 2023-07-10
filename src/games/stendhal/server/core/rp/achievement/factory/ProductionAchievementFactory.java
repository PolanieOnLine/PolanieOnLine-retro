/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerProducedNumberOfItemsCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Factory for production achievements
 *
 * @author kymara
 */
public class ProductionAchievementFactory extends AbstractAchievementFactory {
	private static final String[] ITEMS_SNACKS = { "tarta", "tarta z marchewką", "tarta z rybnym nadzieniem", "pączek", "jabłecznik", "ciasto z wiśniami" };
	private static final String[] ITEMS_SMOKINGFOOD = { "mięso wędzone", "szynka wędzona", "wędzony dorsz", "wędzony pstrąg" };

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();

	    final List<String> foodlist = producerRegister.getProducedItemNames("food");
		final String[] foods = foodlist.toArray(new String[foodlist.size()]);
		// It help to understand what we need to make yet
		//String food = String.join(", ", foodlist);
		// may wish to remove mega potion by hand?

		// this includes a lot of foods! at time of writing, this is at least:
		// pie, cheese sausage, sausage, fish pie, apple pie, cherry pie, crepes suzette, sandwich, bread, pizza
		// grilled steak is made using quest code and not production code so we add an extra condition, and it doesn't adhere to standard completion guidelines
		achievements.add(createAchievement(
				"production.class.food", "Smakosz",
				"Zlecono wszystkie rodzaje potraw dostępne u kucharzy",
				Achievement.MEDIUM_BASE_SCORE, true,
				new AndCondition(
						new PlayerProducedNumberOfItemsCondition(1, foods),
						new QuestStateStartsWithCondition("coal_for_haunchy","waiting;"))));


	    final List<String> drinklist = producerRegister.getProducedItemNames("drink");
		final String[] drinks = drinklist.toArray(new String[drinklist.size()]);

		// soups and koboldish torcibud are made using quest code so we add extra conditions for those
		// at time of writing, the other drinks are fierywater, tea, pina colada, and mega potion (which we may remove)
		achievements.add(createAchievement(
				"production.class.drink", "Spragniony Pracownik",
				"Zlecono wszystkie rodzaje napoi dostępne u kucharzy",
				Achievement.MEDIUM_BASE_SCORE, true,
				new AndCondition(
						new PlayerProducedNumberOfItemsCondition(1, drinks),
						new QuestCompletedCondition("soup_maker"),
						new QuestCompletedCondition("fishsoup_maker"),
						new QuestCompletedCondition("mushroomsoup_maker"),
						new QuestCompletedCondition("koboldish_torcibud"))));

	    final List<String> resourcelist = producerRegister.getProducedItemNames("resource");
		final String[] resources = resourcelist.toArray(new String[resourcelist.size()]);
		// at time of writing: gold bar, mithril bar, flour, iron
		achievements.add(createAchievement(
				"production.class.resource", "Alchemik",
				"Wyprodukowano po 50 rodzai każdego cennego metalu i zasobów",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(50, resources)));

		achievements.add(createAchievement(
				"production.flour.1000", "Asystent Jenny",
				"Wyprodukowano 1,000 mąk",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(1000, "mąka")));

		achievements.add(createAchievement(
				"production.sow.flowers.all", "Siew Nasion Radości",
				"Zasiano po 1000 nasion każdego rodzaju",
				Achievement.MEDIUM_BASE_SCORE, true,
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						for (final String flower: Arrays.asList("stokrotki", "lilia", "bratek", "bielikrasa")) {
							if (player.getQuantityOfSownItems(flower) < 1000) {
								return false;
							}
						}
						return true;
					}
				}));

		achievements.add(createAchievement(
				"production.potions.500", "Producent Eliksirów",
				"Wyprodukowano 500 wielkich eliksirów",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(500, "wielki eliksir")));

		achievements.add(createAchievement(
				"production.sandwiches.100", "Głodomor",
				"Wyprodukowano 100 zwykłych kanapek",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(100, "kanapka")));

		achievements.add(createAchievement(
				"production.bread.500", "Mały Chlebownik",
				"Upieczono 500 bochenków chleba",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(500, "chleb")));

		achievements.add(createAchievement(
				"production.sweetsnacks.200", "Słodkie Przekąski",
				"Upieczono po 200 różnych słodkich przekąsek",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(200, ITEMS_SNACKS)));

		achievements.add(createAchievement(
				"production.smokingfood.50", "Wędzarka",
				"Uwędzono po 50 różnego jedzenia",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(50, ITEMS_SMOKINGFOOD)));

		achievements.add(createAchievement(
				"production.boiledpot.1000", "Ziemniaczany Kociołek",
				"Ugotowano 1,000 ziemniaków",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerProducedNumberOfItemsCondition(1000, "gotowane ziemniaki")));

		return achievements;
	}

	@Override
	protected Category getCategory() {
		return Category.PRODUCTION;
	}
}
