/* $Id: QuestNotInStateConditionTest.java,v 1.14 2011/05/01 19:50:06 martinfuchs Exp $ */
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
package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotInStateConditionTest {
	private static final String QUESTNAME = "questname";

	@BeforeClass
	public static void setUpClass() throws Exception {
		MockStendlRPWorld.get();
		Log4J.init();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		final String validState = "valid";
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("QuestNotInStateConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest(QUESTNAME, "valid");
		assertFalse(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				bob,
				ConversationParser.parse("QuestNotInStateConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "");
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				bob,
				ConversationParser.parse("QuestNotInStateConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, null);
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				bob,
				ConversationParser.parse("QuestNotInStateConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "done");
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				bob,
				ConversationParser.parse("QuestNotInStateConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for questNotInStateCondition.
	 */
	@Test
	public final void testQuestNotInStateCondition() {
		new QuestNotInStateCondition(QUESTNAME, "");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals(
				"QuestNotInState <questname[-1] = testToString>",
				new QuestNotInStateCondition(QUESTNAME, "testToString").toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		final String state = "state";
		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(null));

		final QuestNotInStateCondition obj = new QuestNotInStateCondition(QUESTNAME,
				state);
		assertTrue(obj.equals(obj));
		assertTrue(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state)));
		assertTrue(new QuestNotInStateCondition(null, null).equals(new QuestNotInStateCondition(
				null, null)));
		assertTrue(new QuestNotInStateCondition(null, state).equals(new QuestNotInStateCondition(
				null, state)));
		assertTrue(new QuestNotInStateCondition(QUESTNAME, null).equals(new QuestNotInStateCondition(
				QUESTNAME, null)));

		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state + "2")));

		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new Object()));
		assertFalse(new QuestNotInStateCondition(null, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state)));
		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				null, state)));

		assertFalse(new QuestNotInStateCondition(QUESTNAME, null).equals(new QuestNotInStateCondition(
				QUESTNAME, state)));
		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, null)));

		assertTrue(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state) {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for hashcode.
	 */
	@Test
	public void testhashcode() throws Throwable {
		final String state = "state";
		final QuestNotInStateCondition obj = new QuestNotInStateCondition(QUESTNAME,
				state);

		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new QuestNotInStateCondition(QUESTNAME, state).hashCode(),
				new QuestNotInStateCondition(QUESTNAME, state).hashCode());
		assertEquals(new QuestNotInStateCondition(null, null).hashCode(),
				new QuestNotInStateCondition(null, null).hashCode());
		assertEquals(new QuestNotInStateCondition(null, state).hashCode(),
				new QuestNotInStateCondition(null, state).hashCode());
		assertEquals(new QuestNotInStateCondition(QUESTNAME, null).hashCode(),
				new QuestNotInStateCondition(QUESTNAME, null).hashCode());
	}

}
