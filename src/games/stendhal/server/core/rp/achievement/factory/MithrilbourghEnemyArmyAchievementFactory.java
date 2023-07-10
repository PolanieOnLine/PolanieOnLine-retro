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
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

/**
 * Factory for MithrilbourghEnemyArmyAchievement
 */
public class MithrilbourghEnemyArmyAchievementFactory extends AbstractAchievementFactory {
	@Override
	protected Category getCategory() {
		return Category.QUEST_MITHRILBOURGH_ENEMY_ARMY;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		// Index where number of completions is stored
		final int IDX = 3;

		achievements.add(createAchievement(
				"quest.special.kill_enemy_army.0005", "Sierżant",
				"Ukończono zadanie 'Zabij Wrogą Armię' 5 razy",
				Achievement.MEDIUM_BASE_SCORE, true,
				new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 4)));

		achievements.add(createAchievement(
				"quest.special.kill_enemy_army.0025", "Major",
				"Ukończono zadanie 'Zabij Wrogą Armię' 25 razy",
				Achievement.HARD_BASE_SCORE, true,
				new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 24)));

		achievements.add(createAchievement(
				"quest.special.kill_enemy_army.0050", "Główny Generał",
				"Ukończono zadanie 'Zabij Wrogą Armię' 50 razy",
				Achievement.HARD_BASE_SCORE, true,
				new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 49)));

		achievements.add(createAchievement(
				"quest.special.kill_enemy_army.0100", "Marszałek Polowy",
				"Ukończono zadanie 'Zabij Wrogą Armię' 100 razy",
				Achievement.HARD_BASE_SCORE, true,
				new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 99)));

		return achievements;
	}
}
