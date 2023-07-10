/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

public class ClearQuestPierscien extends AbstractQuest {
	private static final String QUEST_SLOT = "clear_questy_pierscieni";
	private final SpeakerNPC npc = npcs.get("eFuR");

	private static final String PIERSCIEN_MIESZCZANINA_QUEST_SLOT = "pierscien_mieszczanina"; 
	private static final String PIERSCIEN_RYCERZA_QUEST_SLOT = "pierscien_rycerza";
	private static final String PIERSCIEN_BARONA_QUEST_SLOT = "pierscien_barona";
	private static final String PIERSCIEN_MAGNATA_QUEST_SLOT = "pierscien_magnata";

	private static final String MITHRIL_CLOAK_QUEST_SLOT = "mithril_cloak";
	private static final String MITHRILSHIELD_QUEST_SLOT = "mithrilshield_quest";

	private static final String LAST = "Jeśli wszystko już posiadasz powiedz mi #'cofnij'.";

	private static Logger logger = Logger.getLogger(ClearQuestPierscien.class);

	private void checkLevelHelm() {
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					if (player.isBadBoy()){ 
						raiser.say("Z twej ręki zginął rycerz! Nie masz tu czego szukać, pozbądź się piętna czaszki. A teraz precz mi z oczu!");
					} else {
						if (player.isQuestCompleted(PIERSCIEN_MIESZCZANINA_QUEST_SLOT) || player.isQuestCompleted(PIERSCIEN_RYCERZA_QUEST_SLOT) || player.isQuestCompleted(PIERSCIEN_BARONA_QUEST_SLOT) || player.isQuestCompleted(PIERSCIEN_MAGNATA_QUEST_SLOT)) {
							if (player.getLevel() >= 150) {
								if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
									raiser.say("Jestem w stanie wyzerować twój stan zadania na pierścień #'mieszczanina', #'rycerza', #'barona', #magnata lub na #płaszcz z mithrilu oraz #tarczę. Który stan zadania mam wyzerować?");
								}
							} else {
								npc.say("Nie rozmawiam z nikim kto nie jest przynajmniej mieszczaninem. Wróć gdy zdobędziesz 150 poziom.");
								raiser.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							npc.say("Nie posiadasz żadnego z pierścieni, więc nie mamy o czym rozmawiać.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				}
			});
	}

	private void checkCollectingQuests() {
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new OrCondition(
						new QuestCompletedCondition(PIERSCIEN_MIESZCZANINA_QUEST_SLOT),
						new QuestCompletedCondition(PIERSCIEN_RYCERZA_QUEST_SLOT),
						new QuestCompletedCondition(PIERSCIEN_BARONA_QUEST_SLOT),
						new QuestCompletedCondition(PIERSCIEN_MAGNATA_QUEST_SLOT)),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Witaj przyjacielu. Cofam zadania na pierścień: #mieszczanina, #rycerza, #barona, #magnata lub na #płaszcz oraz #tarczę z mithrilu. Które z zadań mam wyzerować?",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("rycerza"),
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(PIERSCIEN_RYCERZA_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Jak mam ci cofnąć zadanie na pierścień rycerza, jeżeli nie masz go zrobionego!!!",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("barona"),
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(PIERSCIEN_BARONA_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Jak mam ci cofnąć zadanie na pierścień barona, jeżeli nie masz go zrobionego!!!",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("magnata"),
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(PIERSCIEN_MAGNATA_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Jak mam ci cofnąć zadanie na pierścień magnata, jeżeli nie masz go zrobionego!!!",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("płaszcz"),
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(MITHRIL_CLOAK_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Jak mam ci cofnąć zadanie na płaszcz z mithrilu, jeżeli nie masz go zrobionego!!!",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("tarcze", "tarcza","tarczę"),
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(MITHRILSHIELD_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Jak mam ci cofnąć zadanie na tarczę z mithrilu, jeżeli nie masz go zrobionego!!!",
			null);
	}

	private void requestItem() {
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("płaszcz"),
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na płaszcz z mithrilu żądam konkretnej #zapłaty." + LAST,
				new SetQuestAction(QUEST_SLOT, "plaszcz"));

		final List<ChatAction> plaszczactions = new LinkedList<ChatAction>();
		plaszczactions.add(new DropItemAction("money",2000000));
		plaszczactions.add(new IncreaseXPAction(1000));
		plaszczactions.add(new SetQuestAction(MITHRIL_CLOAK_QUEST_SLOT, null));
		plaszczactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("plaszcz","cofnij","płaszcz"),
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT,"plaszcz"),
						new PlayerHasItemWithHimCondition("money",2000000)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na płaszcz z mithrilu zostało wyzerowane.",
				new MultipleActions(plaszczactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("zapłaty","zaplaty"),
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT,"plaszcz"),
						new NotCondition(new PlayerHasItemWithHimCondition("money",2000000))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'2000000 money'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli będziesz miał kasę powiedz mi #'/płaszcz/'. Dziękuję!", null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("tarcza", "tarcze"),
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na tarczę z mithrilu żądam konkretnej #zapłaty." + LAST,
				new SetQuestAction(QUEST_SLOT, "tarcza"));

		final List<ChatAction> tarczaactions = new LinkedList<ChatAction>();
		tarczaactions.add(new DropItemAction("money",1500000));
		tarczaactions.add(new IncreaseXPAction(1000));
		tarczaactions.add(new SetQuestAction(MITHRILSHIELD_QUEST_SLOT, null));
		tarczaactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("tarcza","tarcze","cofnij"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"tarcza"),
				new PlayerHasItemWithHimCondition("money",1500000)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na tarczę z mithrilu zostało wyzerowane.",
				new MultipleActions(tarczaactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("zapłaty","zaplaty"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"tarcza"),
								 new NotCondition(
								 new AndCondition(new PlayerHasItemWithHimCondition("money",1500000)))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'1500000 money'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli będziesz miał kasę powiedz mi #'/tarcza/'. Dziękuję!", null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("mieszczanina"),
				new AndCondition( 
						new QuestCompletedCondition(PIERSCIEN_MIESZCZANINA_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na pierścień mieszczanina #żądam konkretnej zapłaty." + LAST,
				new SetQuestAction(QUEST_SLOT, "mieszczanin"));

		final List<ChatAction> mieszczaninactions = new LinkedList<ChatAction>();
		mieszczaninactions.add(new DropItemAction("money",250000));
		mieszczaninactions.add(new DropItemAction("sztabka złota",30));
		mieszczaninactions.add(new IncreaseXPAction(1000));
		mieszczaninactions.add(new SetQuestAction(PIERSCIEN_MIESZCZANINA_QUEST_SLOT, null));
		mieszczaninactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("mieszczanin","cofnij"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"mieszczanin"),
						new PlayerHasItemWithHimCondition("money",250000),
						new PlayerHasItemWithHimCondition("sztabka złota",30)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na pierścień mieszczanina zostało wyzerowane.",
				new MultipleActions(mieszczaninactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("żądam","zadam"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"mieszczanin"),
						new NotCondition(
						new AndCondition(new PlayerHasItemWithHimCondition("money",250000),
								new PlayerHasItemWithHimCondition("sztabka złota",30)))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'250 000 money'\n"
									+"#'30 sztabek złota'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli zapomnisz powiedz mi #'/mieszczanin/'. Dziękuję!", null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("rycerza"),
				new AndCondition(
						new QuestCompletedCondition(PIERSCIEN_RYCERZA_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na pierścień rycerza żądam #zapłaty." + LAST,
				new SetQuestAction(QUEST_SLOT, "rycerz"));

		final List<ChatAction> rycerzactions = new LinkedList<ChatAction>();
		rycerzactions.add(new DropItemAction("money",350000));
		rycerzactions.add(new DropItemAction("sztabka złota",50));
		rycerzactions.add(new IncreaseXPAction(1000));
		rycerzactions.add(new SetQuestAction(PIERSCIEN_RYCERZA_QUEST_SLOT, null));
		rycerzactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("rycerz","cofnij"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"rycerz"),
				new PlayerHasItemWithHimCondition("money",350000),
				new PlayerHasItemWithHimCondition("sztabka złota",50)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na pierścień rycerza zostało wyzerowane.",
				new MultipleActions(rycerzactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("zapłaty","zaplaty"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"rycerz"),
						new NotCondition(
						new AndCondition(new PlayerHasItemWithHimCondition("money",350000),
								new PlayerHasItemWithHimCondition("sztabka złota",50)))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'350 000 money'\n"
									+"#'50 sztabek złota'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli zapomnisz powiedz mi #'/rycerz/'. Dziękuję!", null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("barona"),
				new AndCondition( 
						new QuestCompletedCondition(PIERSCIEN_BARONA_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na pierścień barona #opłata jest wysoka." + LAST,
				new SetQuestAction(QUEST_SLOT, "baron"));

		final List<ChatAction> baronactions = new LinkedList<ChatAction>();
		baronactions.add(new DropItemAction("money",450000));
		baronactions.add(new DropItemAction("sztabka złota",70));
		baronactions.add(new IncreaseXPAction(1000));
		baronactions.add(new SetQuestAction(PIERSCIEN_BARONA_QUEST_SLOT, null));
		baronactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("baron","cofnij"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"baron"),
				new PlayerHasItemWithHimCondition("money",450000),
				new PlayerHasItemWithHimCondition("sztabka złota",70)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na pierścień barona zostało wyzerowane.",
				new MultipleActions(baronactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("opłata","oplata"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"baron"),
								 new NotCondition(
								 new AndCondition(new PlayerHasItemWithHimCondition("money",450000),
												  new PlayerHasItemWithHimCondition("sztabka złota",70)))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'450 000 money'\n"
									+"#'70 sztabek złota'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli zapomnisz powiedz mi #'/baron/'. Dziękuję!", null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("magnata"),
				new AndCondition( 
						new QuestCompletedCondition(PIERSCIEN_MAGNATA_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, "Za cofnięcie zadania na pierścień magnata #cenię się wysoko." + LAST,
				new SetQuestAction(QUEST_SLOT, "magnat"));

		final List<ChatAction> magnatactions = new LinkedList<ChatAction>();
		magnatactions.add(new DropItemAction("money",600000));
		magnatactions.add(new DropItemAction("sztabka złota",100));
		magnatactions.add(new IncreaseXPAction(1000));
		magnatactions.add(new SetQuestAction(PIERSCIEN_MAGNATA_QUEST_SLOT, null));
		magnatactions.add(new SetQuestAction(QUEST_SLOT, null));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("magnat","cofnij"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"magnat"),
				new PlayerHasItemWithHimCondition("money",600000),
				new PlayerHasItemWithHimCondition("sztabka złota",100)),
				ConversationStates.ATTENDING, "Widzę, że dogadaliśmy się, zadanie na pierścień magnata zostało anulowane.",
				new MultipleActions(magnatactions));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("cenię","cenie"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"magnat"),
						new NotCondition(
						new AndCondition(new PlayerHasItemWithHimCondition("money",600000),
								new PlayerHasItemWithHimCondition("sztabka złota",100)))),
				ConversationStates.ATTENDING, "Musisz mi zapłacić:\n"
									+"#'600 000 money'\n"
									+"#'100 sztabek złota'\n"
									+"Przyjmuje zapłatę w całości, nie na raty!!!\n"
									+"Jeżeli zapomnisz powiedz mi #'/magnat/'. Dziękuję!", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Anulowanie Zadań",
				"eFuR anuluje zadania w zamian za opłatę...",
				true);
		checkLevelHelm(); 
		checkCollectingQuests();
		requestItem();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("Napotkałem przedziwnego smoka, który karze się nazywać eFuR.");
		res.add("Zaproponował mi anulowanie jednego z zadań na pierścień mieszczanina lub ryczerza lub barona lub magnata lub na płaszcz lub na tarcze z mithrilu.");
		if ("rejected".equals(questState)) {
			res.add("Narazie stan zadań niech zostanie taki jaki jest...");
			return res;
		}
		if ("start".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na pierścień mieszczanina 250 000 money i 30 sztabek złota. Hasło: mieszczanin.");
		if ("mieszczanin".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na pierścień rycerza 350 000 money i 50 sztabek złota. Hasło: rycerz.");
		if ("rycerz".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na pierścień barona 450 000 money i 70 sztabek złota. Hasło: baron.");
		if ("baron".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na pierścień magnata 600 000 money i 100 sztabek złota. Hasło: magnat.");
		if ("magnat".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na płaszcz z mithrilu 2 000 000 money. Hasło: płaszcz.");
		if ("plaszcz".equals(questState)) {
			return res;
		}
		res.add("eFuR zażądał za anulowanie zadania na tarczę z mithrilu 1 500 000 money. Hasło: tarcza.");
		if ("tarcza".equals(questState)) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Stan zadania to: " + questState);
		logger.error("Historia nie pasuje do stanu poszukiwania " + questState);
		return debug;
	}

	@Override
	public String getName() {
		return "Anulowanie Zadań";
	}

	@Override
	public String getNPCName() {
		return npc.getName();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
}
