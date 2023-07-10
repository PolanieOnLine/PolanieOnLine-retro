/***************************************************************************
 *                   (C) Copyright 2010-2021 - Stendhal                    *
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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

public class PierscienMieszczanina extends AbstractQuest {
	private static final String QUEST_SLOT = "pierscien_mieszczanina";
	private final SpeakerNPC npc = npcs.get("Marianek");

	private static final String MRSYETI_QUEST_SLOT = "mrsyeti";
	private static final String ANDRZEJ_MAKE_ZLOTA_CIUPAGA_QUEST_SLOT = "andrzej_make_zlota_ciupaga";

	private static Logger logger = Logger.getLogger(PierscienMieszczanina.class);

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
						if (player.isQuestCompleted(MRSYETI_QUEST_SLOT)) {
							if (player.getLevel() >= 150) {
								if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
									raiser.say("Czy chcesz zdobyć pierścień mieszczanina?.");
								} else if (player.isQuestCompleted(QUEST_SLOT)) {
									raiser.say("Odebrałeś już swój pierścień, żegnaj.");
									raiser.setCurrentState(ConversationStates.ATTENDING);
								}
							} else {
								npc.say("Twój stan społeczny jest zbyt niski aby podjąć te zadanie. Wróć gdy zdobędziesz 150 lvl.");
								raiser.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							npc.say("Widzę, że nie pomogłeś mrs.Yeti! Wróć gdy jej pomożesz.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				}
			}); 

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					raiser.say("Ale wpierw sprawdzę czy masz wszystkie zadania zrobione nim dostaniesz #pierścień.");
					player.addKarma(10);
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE,
			"Nie to nie.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	private void checkCollectingQuests() {
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(MRSYETI_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Witaj przyjacielu. Mam dla Ciebie wyzwanie dzięki, któremu zdobędziesz #pierścień mieszczanina.",
			null);

			npc.add(ConversationStates.ATTENDING,
			Arrays.asList("challenge", "pierścień", "pierscien"), 
			new AndCondition(new QuestCompletedCondition(MRSYETI_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(ANDRZEJ_MAKE_ZLOTA_CIUPAGA_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Wciąż masz zadanie do wykonania u kowala Andrzeja!",
			null);
	}

	private void requestItem() {
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("challenge", "pierścień", "pierscien"),
			new AndCondition(
					new QuestCompletedCondition(MRSYETI_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING, "Aby zdobyć #pierścień musisz przynieść kilka przedmiotów.",
			new SetQuestAction(QUEST_SLOT, "start" ));

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("money",200000));
		reward.add(new DropItemAction("ciupaga",1));
		reward.add(new DropItemAction("sztabka złota",50));
		reward.add(new DropItemAction("bryłka mithrilu",20));
		reward.add(new EquipItemAction("pierścień mieszczanina", 1, true));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("challenge", "pierścień", "pierscien"),
			new AndCondition(
					new QuestInStateCondition(QUEST_SLOT,"start"),
					new PlayerHasItemWithHimCondition("money",200000),
					new PlayerHasItemWithHimCondition("ciupaga",1),
					new PlayerHasItemWithHimCondition("sztabka złota",50),
					new PlayerHasItemWithHimCondition("bryłka mithrilu",20)),
			ConversationStates.ATTENDING, "Widzę, że masz wszystko o co cię prosiłem. A oto nagroda pierścień mieszczanina.",
			new MultipleActions(reward));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("challenge", "pierścień", "pierscien"),
			new AndCondition(
					new QuestInStateCondition(QUEST_SLOT,"start"),
					new NotCondition(
							new AndCondition(new PlayerHasItemWithHimCondition("money",200000),
							new PlayerHasItemWithHimCondition("ciupaga",1),
							new PlayerHasItemWithHimCondition("sztabka złota",50),
							new PlayerHasItemWithHimCondition("bryłka mithrilu", 20)))),
			ConversationStates.ATTENDING, "Potrzebuję:\n"
									+"#'200000 money'\n" 
									+"#'1 ciupaga'\n"
									+"#'50 sztabek złota'\n"
									+"#'20 bryłek mithrilu'\n"
									+" Proszę przynieś mi to wszystko naraz. Słowo klucz to #'/pierścień/'. Dziękuję!", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"Pierścień Mieszczanina",
			"Uporaj się z wyzwaniami, które postawił przed tobą kowal Marianek.",
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
		res.add(Grammar.genderVerb(player.getGender(), "Spotkałem") + " kowala Marianka.");
		res.add("Zaproponował mi pierścień mieszczanina.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("Nie potrzebne mi są błyskotki..");
			return res;
		} 
		if (questState.equals("start")) {
			return res;
		} 
		res.add("Kowal Marianek poprosił, abym mu " + Grammar.genderVerb(player.getGender(), "dostarczył") + ": 50 sztabek srebra, 50 sztabek mithrilu, 150 sztabek złota i 30 bryłek mithrilu. po zdobyciu tych rzeczy mam mu powiedzieć: pierścień.");
		if (questState.equals("start")) {
			return res;
		} 
		res.add("Kowal Marianek był zadowolony z mojej postawy. W zamian " + Grammar.genderVerb(player.getGender(), "dostałem") + " pierścień mieszczanina.");

		if (isCompleted(player)) {
			return res;
		} 

		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Stan zadania to: " + questState);
		logger.error("Historia nie pasuje do stanu poszukiwania " + questState);
		return debug;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "Pierścień Mieszczanina";
	}

	@Override
	public String getNPCName() {
		return npc.getName();
	}
}
