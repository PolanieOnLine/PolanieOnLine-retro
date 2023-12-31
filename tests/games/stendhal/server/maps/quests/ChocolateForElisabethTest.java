/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.kirdneh.city.LittleGirlNPC;
import games.stendhal.server.maps.kirdneh.city.MummyNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ChocolateForElisabethTest extends ZonePlayerAndNPCTestImpl {

	private static final String CAREY = "Carey";
	private static final String ELISABETH = "Elisabeth";
	private static final String ZONE_NAME = "0_kirdneh_city";

	private static final String CHOCOLATE = "tabliczka czekolady";
	private static final String[] FLOWERS = { "stokrotki", "bielikrasa", "bratek" };

	private static final String LIZ_TALK_GREETING_DEFAULT = "Cześć.";
	private static final String LIZ_TALK_GREETING_FIRST_TIME = "Nie pamiętam kiedy ostatnio czułam zapach dobrej tabliczki #czekolady...";
	private static final String LIZ_TALK_GREETING_WITHOUT_CHOCOLATE = "Mam nadzieje, że ktoś mi przyniesie tabliczkę czekolady...:(";
	private static final String LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED = "Wspaniale! Ta tabliczka czekolady jest dla mnie?";
	private static final String LIZ_TALK_GREETING_WITH_CHOCOLATE_NOT_ALLOWED = "Moja mama chce wiedzieć kogo pytałam o tabliczkę czekolady :(";

	private static final String LIZ_TALK_QUEST_OFFER = "Chciałabym dostać tabliczkę czekolady. Chociaż jedną. Ciemno brązową lub słodką białą lub z posypką. Zdobędziesz jedną dla mnie?";
	private static final String LIZ_TALK_QUEST_OFFER_AGAIN = "Mam nadzieję, że jeżeli poproszę o następną tabliczkę czekolady to nie będę zbyt zachłanna. Czy możesz zdobyć następną?";
	private static final String LIZ_TALK_QUEST_NOT_NOW = "Zjadłam za dużo czekolady. Nie czuję się dobrze.";
	private static final String LIZ_TALK_QUEST_ALREADY_OFFERED = "Łaaaaaaaa! Gdzie jest moja czekolada...";
	private static final String LIZ_TALK_QUEST_REJECT = "Dobrze, poczekam aż moja mama znajdzie pomocną dłoń...";
	private static final String LIZ_TALK_QUEST_ACCEPT = "Dziękuję!";
	private static final String LIZ_TALK_REWARD = "Dziękuję BARDZO! Jesteś świetny. Weź oto te kwiatki jako prezent.";
	private static final String LIZ_TALK_PISSED = "Łaaaaaa! Jesteś wielkim tłuścioszkiem.";

	private static final String MUM_TALK_GREET = "Cześć, miło cię poznać.";
	private static final String MUM_TALK_GREET_AND_APPROVE = "Och, już spotkałeś moją córeczkę Elisabeth. Wyglądasz na miłą osobę i byłabym wdzięczna gdybyś mógł przynieść jej tabliczkę czekolady ponieważ nie jestem zbyt #silna na to.";

	private static final String HISTORY_DEFAULT = "Elisabeth jest miłą małą dziewczynką żyjącą w Kirdneh razem ze swoją rodziną.";
	private static final String HISTORY_REJECTED = "Nie lubię miłych małych dziewczynek.";
	private static final String HISTORY_START = "Mała Elisabeth potrzebuje tabliczkę czekolady.";
	private static final String HISTORY_GOT_CHOCOLATE = "Znalazłem pyszną tabliczkę czekolady dla Elisabeth.";
	private static final String HISTORY_MUM_APPROVES = "Rozmawiałem z Carey, matką Elisabeth i zgodziła się, abym mógł dać jej córce tabliczkę czekolady.";
	private static final String HISTORY_DONE = "Elisabeth je czekoladę, którą jej dałem, a w zamian otrzymałem piękne kwiaty.";
	private static final String HISTORY_REPEATABLE = "Przyniosłem trochę czekolady dla Elisabeth. Dała mi w zamian kwiatki. Może chciałaby więcej czekolady.";

	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ChocolateForElisabethTest() {
		setNpcNames(ELISABETH, CAREY);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new LittleGirlNPC(), ZONE_NAME);
		addZoneConfigurator(new MummyNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new ChocolateForElisabeth();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testRefuseQuest() {
		startTalkingToNpc(ELISABETH);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(LIZ_TALK_QUEST_REJECT, getReply(npc));

		assertEquals("rejected", player.getQuest(questSlot));
		assertFalse(npc.isTalking());
		assertLoseKarma(5);
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_FIRST_TIME, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(LIZ_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testAskForQuestAlreadyAccepted() {
		player.setQuest(questSlot, "start");

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITHOUT_CHOCOLATE, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_ALREADY_OFFERED, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testFoundChocolate() {
		player.setQuest(questSlot, "start");

		equipWithItem(player, CHOCOLATE);

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE);
	}

	@Test
	public void testBringChocolateBeforeTalkingToMum() {
		player.setQuest(questSlot, "start");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_NOT_ALLOWED, responseToGreeting);

		assertTrue(player.isEquipped(CHOCOLATE));
		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE);
	}

	@Test
	public void testTalkToMumAfterQuestStart() {
		player.setQuest(questSlot, "start");

		String responseToGreeting = startTalkingToNpc(CAREY);
		assertEquals(MUM_TALK_GREET_AND_APPROVE, responseToGreeting);

		assertEquals("mummy", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_MUM_APPROVES);
	}

	@Test
	public void testTalkToMumBeforeQuestStart() {
		player.setQuest(questSlot, null);

		String responseToGreeting = startTalkingToNpc(CAREY);
		assertEquals(MUM_TALK_GREET, responseToGreeting);

		assertNull(null, player.getQuest(questSlot));
		assertNoHistory();
	}

	@Test
	public void testBringChocolateAfterTalkingToMum() {
		player.setQuest(questSlot, "mummy");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED, responseToGreeting);

		en.step(player, "yes");
		assertEquals(LIZ_TALK_REWARD, getReply(npc));

		assertFalse(player.isEquipped(CHOCOLATE));
		assertTrue(isEquippedWithFlower());
		assertGainKarma(10);
		assertTrue(player.getQuest(questSlot).startsWith("eating;"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_DONE);
	}

	@Test
	public void testRefuseToGiveChocolate() {
		player.setQuest(questSlot, "mummy");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED, responseToGreeting);

		en.step(player, "no");
		assertEquals(LIZ_TALK_PISSED, getReply(npc));

		assertTrue(player.isEquipped(CHOCOLATE));
		assertFalse(isEquippedWithFlower());
		assertLoseKarma(5);
		assertEquals("mummy", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES);
	}

	@Test
	public void testAskQuestAgain() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.HOURS.toSeconds(1));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		assertTrue(player.getQuest(questSlot).startsWith("eating"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_REPEATABLE);
	}

	@Test
	public void testAskQuestAgaintTooSoon() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.MINUTES.toSeconds(30));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_NOT_NOW, getReply(npc));

		assertTrue(player.getQuest(questSlot).startsWith("eating"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_DONE);
	}

	@Test
	public void testAskQuestAgainAndAccept() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.HOURS.toSeconds(1));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		en.step(player, "yes");

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	private String startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		return getReply(npc);
	}

	private boolean isEquippedWithFlower() {
		for (String flower : FLOWERS) {
			if (player.isEquipped(flower)) {
				return true;
			}
		}
		return false;
	}
}
