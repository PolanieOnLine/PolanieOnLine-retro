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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Campfire
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Sally, a scout sitting next to a campfire near Or'ril</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Sally asks you for wood for her campfire</li>
 * <li> You collect 10 pieces of wood in the forest</li>
 * <li> You give the wood to Sally.</li>
 * <li> Sally gives you 10 meat or ham in return.<li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 10 meat or ham</li>
 * <li> 50 XP</li>
 * <li> Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Unlimited, but 60 minutes of waiting are required between repetitions</li>
 * </ul>
 */
public class Campfire extends AbstractQuest {
	private static final String QUEST_SLOT = "campfire";
	private final SpeakerNPC npc = npcs.get("Sally");

	private static final int REQUIRED_WOOD = 10;
	private static final int REWARDS = 10;

	private static final int REQUIRED_MINUTES = 60;

	private void prepareRequestingStep() {
		// player returns with the promised wood
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new PlayerHasItemWithHimCondition("polano", REQUIRED_WOOD)),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Witaj znów! Masz te 10 polan, o które wcześniej Cię pytałam?",
			null);

		//player returns without promised wood
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
		    new	AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new NotCondition(new PlayerHasItemWithHimCondition("polano", REQUIRED_WOOD))),
			ConversationStates.ATTENDING,
			"Już wróciłeś? Nie zapomnij, że obiecałeś mi zebrać dziesięć polan dla mnie!",
			null);

		// first chat of player with sally
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING, "Cześć! Potrzebuję małej #'przysługi'...",
			null);

		// player who is rejected or 'done' but waiting to start again, returns
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotInStateCondition(QUEST_SLOT, "start"),
					new QuestStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Witaj ponownie!", 
			null);

		// if they ask for quest while on it, remind them
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"Już mi obiecałeś, że przyniesiesz drewno! Dziesięć kawałków, pamiętasz?",
			null);

		// first time player asks/ player had rejected
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"Potrzebuję więcej drewna, aby podtrzymać ognisko. Nie mogę pójść po nie i zostawić ogniska bez opieki! Czy mógłbyś pójść do lasu i zdobyć trochę dla mnie? Potrzebuję dziesięciu kawałków.",
				null);

		// player returns - enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new QuestStartedCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED, 
				"Moje ognisko znowu potrzebuje drewna! Czy mógłbyś pójść do lasu i zdobyć trochę dla mnie? Potrzebuję dziesięciu kawałków.",
				null);

		// player returns - enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new QuestStartedCondition(QUEST_SLOT),
						new NotCondition(new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES))),
				ConversationStates.ATTENDING, 
				null,
				new SayTimeRemainingAction(QUEST_SLOT,REQUIRED_MINUTES,"Dziękuję, ale drewno, które mi ostatnio przyniosłeś wystarczy na "));

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Dobrze. " +
			"Drewno możesz znaleźć w lesie na północ stąd. " +
			"Możesz również zaryzykować z bobrami w pobliżu rzeki na południe stąd. " +
			"Wróć, gdy będziesz mieć dziesięć polan!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Jak mam upiec całe to mięso? Może powinnam nakarmić nim zwierzęta...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {
		// player has wood and tells sally, yes, it is for her
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("polano", REQUIRED_WOOD));
		reward.add(new EquipItemAction("węgiel drzewny", REWARDS));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT));
		reward.add(new IncreaseKarmaAction(10));
		reward.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				String rewardClass;
				if (Rand.throwCoin() == 1) {
					rewardClass = "mięso";
				} else {
					rewardClass = "szynka";
				}
				npc.say("Dziękuję! Weź trochę " + Grammar.plnoun(REWARDS, rewardClass) + " oraz węgla drzewnego!");
				final StackableItem reward = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
				reward.setQuantity(REWARDS);
				player.equipOrPutOnGround(reward);
				player.notifyWorldAboutChanges();
			}
		});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("polano", REQUIRED_WOOD),
			ConversationStates.ATTENDING, null,
			new MultipleActions(reward));

		//player said the wood was for her but has dropped it from his bag or hands
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("polano", REQUIRED_WOOD)),
			ConversationStates.ATTENDING,
			"Hej! Gdzie położyłeś drewno?",
			null);

		// player had wood but said it is not for sally
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Och... cóż mam nadzieję, że szybko znajdziesz. Ognisko zaczyna przygasać!",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Obozowisko", 
				"Sally chce rozpalić ognisko, ale nie posiada drewna.", 
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add(Grammar.genderVerb(player.getGender(), "Spotkałem") + " Sally w swoim obozowisku.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("Nie chcę wesprzeć obozowiska drewnem.");
			return res;
		}
		res.add("Bardzo chętnie pomogę Sally w zbieraniu polana do ogniska.");
		if (player.isEquipped("polano", REQUIRED_WOOD) || isCompleted(player)) {
			res.add("Udało mi się zebrać 10 polan potrzebnych do rozpalenia ogniska.");
		}
		if (isCompleted(player)) {
			res.add(Grammar.genderVerb(player.getGender(), "Przekazałem") + " drewno Sally. W zamian " + Grammar.genderVerb(player.getGender(), "otrzymałem") + " od niej trochę jedzenia oraz węgla drzewnego na podróż.");
		}
		if(isRepeatable(player)){
			res.add("Sally potrzebuje więcej drewna do ponownego rozpalenia ogniska.");
		}
		return res;
	}

	@Override
	public String getName() {
		return "Obozowisko";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getNPCName() {
		return npc.getName();
	}

	@Override
	public String getRegion() {
		return Region.ORRIL;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT) && !"start".equals(player.getQuest(QUEST_SLOT)) && !"rejected".equals(player.getQuest(QUEST_SLOT));
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)).fire(player, null, null);
	}
}
