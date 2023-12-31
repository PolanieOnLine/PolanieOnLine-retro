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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.OwnedItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Speak with Hayunn
 * <p>
 * PARTICIPANTS: <ul><li> Hayunn Naratha</ul>
 *
 * STEPS: <ul>
 * <li> Talk to Hayunn to activate the quest.
 * <li> He asks you to kill a rat, also offering to teach you how
 * <li> Return and learn how to loot, identify items and heal
 * <li> Return and learn how to double click move, and get some URLs
 * </ul>
 *
 * REWARD: <ul><li> 150 XP <li> 25 gold coins <li> puklerz </ul>
 *
 * REPETITIONS: <ul><li> Get the URLs as much as wanted but you only get the reward once.</ul>
 */
public class MeetPietrek extends AbstractQuest {
	private static final Logger logger = Logger.getLogger(MeetPietrek.class);

	private static final String QUEST_SLOT = "meet_pietrek";
	private final SpeakerNPC npc = npcs.get("Pietrek");

	//This is 1 minute at 300 ms per turn
	private static final int TIME_OUT = 200;

	private static final int registryPrice = 50000;

	private void preparePietrek() {
		// player wants to learn how to attack
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"Gdy zajmowałem się poszukiwaniem przygód to używałem prawego przycisku na potworach i wybierałem ATAK. Możesz się mnie zapytać jaki jest powód ryzykowania swojego życia, aby coś zabić?",
			null);

		//player doesn't want to learn how to attack
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.NO_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"Dobrze, wyglądasz na inteligentną osobę. Sądzę, że sobie poradzisz!",
			null);

		//player returns to Hayunn not having killed a rat
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new NotCondition(new KilledForQuestCondition(QUEST_SLOT,1))),
			ConversationStates.ATTENDING,
			"Widzę, że jeszcze nie zabiłeś szczura. Czy chcesz, abym powiedział Tobie jak z nimi walczyć? #Tak?",
			null);

		//player returns to Hayunn having killed a rat
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(150));
		actions.add(new SetQuestAction(QUEST_SLOT, "killed"));

		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new KilledForQuestCondition(QUEST_SLOT, 1)),
			ConversationStates.INFORMATION_1,
			"Pokonałeś szczura! Teraz możesz zapytać jaki jest powód ryzykowania swojego życia w celu ich zabicia? #Tak?",
			new MultipleActions(actions));

		// player wants to learn more from Hayunn
		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_2,
			"Aha! Możesz wziąć przedmioty ze zwłok. W tym celu powinieneś na zwłokach nacisnąć prawy przycisk i wybrać PRZESZUKAJ. Oczywiście, gdy jesteś wystarczająco blisko zwłok tak, aby je dosięgnąć. Możesz przeciągnąć przedmioty do swojego plecaka. Czy chcesz wiedzieć jak zidentyfikować przedmioty? #Tak?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_3,
			"Na przedmiocie możesz nacisnąć prawy przycisk i wybrać ZOBACZ, aby otrzymać opis. Wiem o czym myślisz. Jak przetrwać, aby nie zginąć? Chcesz wiedzieć? #Tak?",
			null);

		npc.add(
			ConversationStates.INFORMATION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_4,
			"Musisz jeść regularnie! Używaj w tym celu prawego przycisku na jedzeniu, które znajduje się w plecaku, bądź na ziemi. Z każdym kęsem twoje życie powoli będzie się odnawiało. Zabiera to trochę czasu. Jest kilka sposobów na regenerację życia... chcesz posłuchać? #Tak?",
			null);

		npc.add(
			ConversationStates.INFORMATION_4,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_5,
			"Gdy zarobisz wystarczającą ilość pieniędzy to powinieneś odwiedzić naszego uzdrowiciela w Zakopanem Gaździnę Jadźkę i kupić miksturę. Mikstury są bardzo praktyczne, gdy jesteś sam w górach lub w lesie Zakopanego. Czy chcesz wiedzieć gdzie jest Zakopane? #Tak?",
			null);

		// The player has had enough info for now. Send them to semos. When they come back they can learn some more tips.

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 25));
		reward.add(new IncreaseXPAction(150));
		reward.add(new SetQuestAction(QUEST_SLOT, "taught"));
		reward.add(new ExamineChatAction("npcgenowefa.png", "Gaździna Jadźka", "Centrum Zakopanego."));

		npc.add(
			ConversationStates.INFORMATION_5,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Wyjdź z domku, a znajdziesz się w centrum Zakopanego. Natomiast w lasku na północ stąd znajduje się portal do Semos. Na początek pozwiedzaj Zakopane oraz okolicę. Powodzenia!!",
			new MultipleActions(reward));

		// incase player didn't finish learning everything when he came after killing the rat, he must have another chance. Here it is.
		// 'little tip' is a pun as he gives some money, that is a tip, too.
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "killed"),
			ConversationStates.INFORMATION_1,
			"Szybko się uwinąłeś od momentu, gdy przyszedłeś mi oznajmić, że zabiłeś szczura! Chciałbym Ci przekazać kilka wskazówek i podpowiedzi. Czy chcesz je usłyszeć? #Tak?",
			null);

		// Player has returned to say hi again.
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "taught"),
			ConversationStates.INFORMATION_6,
			"Witaj ponownie. Przyszedłeś, aby się dowiedzieć więcej ode mnie? #Tak?",
			null);

		npc.add(
			ConversationStates.INFORMATION_6,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_7,
			"Zapewne poruszałeś się już po lesie Zakopanego. Ścieżki są tam wąskie. Najlepiej poruszać się tam szybko i sprawnie. Czy chcesz posłuchać o tym? #Tak?",
			null);

		npc.add(
			ConversationStates.INFORMATION_7,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_8,
			"To naprawdę proste. Naciskaj dwukrotnie na miejsce do którego chcesz się udać. Tam jest więcej informacji, których nie mogę sobie przypomnieć. Wyleciały mi z głowy... chcesz wiedzieć gdzie można o nich poczytać? #Tak?",
			null);

		final String epilog = "Na #https://s1.polanieonline.eu/ możesz znaleźć wiele odpowiedzi, listy wszelkiego rodzaju zwierząt, potworów i innych wrogów\n Teraz najważniejsze. Na #'https://s1.polanieonline.eu/regulamin/regulamin-gry-polanieonline' koniecznie przeczytaj regulamin PolanieOnLine. To ważne!\n ";

			//This is used if the player returns, asks for #help and then say #yes
			npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES, new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			epilog + "Wiesz, przypominasz mi mnie, gdy",
			null);

		final List<ChatAction> reward2 = new LinkedList<ChatAction>();
		reward2.add(new EquipItemAction("puklerz"));
		reward2.add(new IncreaseXPAction(250));
		reward2.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_8,
			ConversationPhrases.YES_MESSAGES, new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.IDLE,
			epilog + "Cóż, powodzenia w walkach! Ta tarcza powinna Ci pomóc. Tutaj znajdziesz sławę i chwałę. Uważaj na potwory!",
			new MultipleActions(reward2));

		npc.add(new ConversationStates[] { ConversationStates.ATTENDING,
					ConversationStates.INFORMATION_1,
					ConversationStates.INFORMATION_2,
					ConversationStates.INFORMATION_3,
					ConversationStates.INFORMATION_4,
					ConversationStates.INFORMATION_5,
					ConversationStates.INFORMATION_6,
					ConversationStates.INFORMATION_7,
					ConversationStates.INFORMATION_8},
				ConversationPhrases.NO_MESSAGES, new NotCondition(new QuestInStateCondition(QUEST_SLOT, "start")), ConversationStates.IDLE,
				"Och mam nadzieję, że ktoś się zatrzyma i porozmawia ze mną.",
				null);

		npc.setPlayerChatTimeout(TIME_OUT);
	}

	private void initShop() {
		final Map<String, Integer> prices = new LinkedHashMap<String, Integer>() {{
			put("spis", registryPrice);
		}};

		final SellerBehaviour behaviour = new SellerBehaviour(prices) {
			@Override
			public ChatCondition getTransactionCondition() {
				//return new QuestCompletedCondition(QUEST_SLOT);
				return questCompletedCondition;
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						npc.say("Pierw musisz chwilę ze mną porozmawiać!");
					}
				};
			}

			@Override
			public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
				if (super.transactAgreedDeal(res, seller, player)) {
					seller.say("Zapisałem w nim twoje imię wojażu, na wypadek gdybyś zgubił. Pamiętaj, że przedmioty, które odnajdziesz"
							+ " spisał je do swojej księgi. Byś miał czym głosić swą przyszłą chwałę!");

					return true;
				}

				return false;
			}

			@Override
			public Item getAskedItem(final String askedItem, final Player player) {
				final Item item = super.getAskedItem(askedItem, player);

				if (item != null && player != null) {
					// set owner to prevent others from using it
					((OwnedItem) item).setOwner(player.getName());
					return item;
				}

				if (player == null) {
					logger.error("Player is null, cannot set owner in bestiary");
				}
				if (item == null) {
					logger.error("Could not create bestiary item");
				}

				return null; // don't give a bestiary without owner
			}
		};
		new SellerAdder().addSeller(npc, behaviour, false);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"Mogę sprzedać Ci #spis przedmiotów.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("registry", "spis", "przedmiotów"),
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"Spis pozwala zobaczyć jakie i w jakich ilościach zdobyłeś przedmioty.",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Spotkanie Pietrka",
				"Pietrek pomaga nowym bohaterom w poznaniu świata PolanieOnLine.",
				false);
		preparePietrek();
		initShop();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private final ChatCondition questCompletedCondition = new ChatCondition() {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return isCompleted(player);
		}
	};

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "Spotkanie Pietrka";
	}

	@Override
	public String getNPCName() {
		return "Pietrek";
	}

	@Override
	public String getRegion() {
		return Region.ZAKOPANE_CITY;
	}
}
