/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesCondition;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;

/**
 * Factory for zone achievements
 *
 * @author madmetzger
 */
public class OutsideZoneAchievementFactory extends AbstractAchievementFactory {
	@Override
	protected Category getCategory() {
		return Category.OUTSIDE_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		// All outside zone achievements
		achievements.add(createAchievement(
				"zone.outside.semos", "Młodszy Odkrywca",
				"Odwiedzono wszystkie obszary w regionie Semos",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("semos", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.ados", "Odkrywca Wielkich Miast",
				"Odwiedzono wszystkie obszary w regionie Ados",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("ados", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.fado", "Na Południe",
				"Odwiedzono wszystkie obszary w regionie Fado",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("fado", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.orril", "Harcerz",
				"Odwiedzono wszystkie obszary w regionie Orril",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("orril", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.amazon", "Odkrywca Dżungli",
				"Odwiedzono wszystkie obszary w regionie Amazon",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("amazon", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.athor", "Turysta",
				"Odwiedzono wszystkie obszary w regionie Athor",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("athor", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.kikareukin", "Podniebna Wieża",
				"Odwiedzono wszystkie obszary w regionie Kikareukin",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("kikareukin", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.kirdneh", "Kulturalny Przybysz",
				"Odwiedzono wszystkie obszary w regionie Kirdneh",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("kirdneh", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.kalavan", "Piękne Ogrody",
				"Odwiedzono wszystkie obszary w regionie Kalavan",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("kalavan", Boolean.TRUE, Boolean.TRUE)));

		// Prasłowiańskie
		achievements.add(createAchievement(
				"zone.outside.krakow", "Królewskie Miasto",
				"Odwiedzono wszystkie obszary w regionie Kraków",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("krakow", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.zakopane", "Zimowa Kraina",
				"Odwiedzono wszystkie obszary w regionie Zakopane",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("zakopane", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.wieliczka", "Kraina Soli",
				"Odwiedzono wszystkie obszary w regionie Wieliczka",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("wieliczka", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.tatry", "Pasmo Górskie",
				"Odwiedzono wszystkie obszary w regionie Tatry",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("tatry", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.desert", "Wszędzie Piasek",
				"Odwiedzono wszystkie obszary w regionie pustynnym",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("desert", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.koscielisko", "Zwiedzacz",
				"Odwiedzono wszystkie obszary w regionie Kościelisko",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("koscielisko", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.warszawa", "Warszawski Marszałek",
				"Odwiedzono wszystkie obszary w regionie Warszawa",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesInRegionCondition("warszawa", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
				"zone.outside.dragonland", "Smocza Kraina",
				"Odwiedzono wszystkie obszary smoczej krainy",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerVisitedZonesCondition("0_dragon_land_s", "0_dragon_land_n")));

		//All interior zone achievements

		//Special zone achievements
		achievements.add(createAchievement(
				"zone.special.bank", "Depozyt",
				"Odwiedzono wszystkie banki",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesCondition("int_semos_bank", "int_nalwor_bank", "int_kirdneh_bank",
						"int_fado_bank", "int_magic_bank", "int_ados_bank", "int_zakopane_bank_0")));

		achievements.add(createAchievement(
				"zone.special.afterlife", "Widziałem Światło",
				"Poznał życie pozagrobowe",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesCondition("int_afterlife")));

		achievements.add(createAchievement(
				"zone.special.allvisited", "Wszystko już Widziałem",
				"Odwiedzono niebo, piekło, chmury oraz więzienie",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerVisitedZonesCondition("int_afterlife", "hell", "int_koscielisko_jail", "7_kikareukin_clouds")));

		achievements.add(createAchievement(
				"zone.special.clouds", "Chmurzasty Sen",
				"Odwiedzono chmury nad Zakopanem",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerVisitedZonesCondition("6_zakopane_clouds")));

		return achievements;
	}
}
