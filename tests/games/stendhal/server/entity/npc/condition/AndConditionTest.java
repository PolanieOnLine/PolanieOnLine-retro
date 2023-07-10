/* $Id: AndConditionTest.java,v 1.22 2011/05/01 19:50:06 martinfuchs Exp $ */
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
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class AndConditionTest {
	private AlwaysTrueCondition trueCondition;

	private ChatCondition falsecondition;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
	}
	
	
	@Before
	public void setUp() throws Exception {
		trueCondition = new AlwaysTrueCondition();
		falsecondition = new NotCondition(new AlwaysTrueCondition());

	}

	@Test
	public void selftest() throws Exception {
		assertTrue("true  delivers true", trueCondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		assertFalse("falscondition delivers false", falsecondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for constructor.
	 */
	@Test
	public void testConstructor() throws Throwable {
		new AndCondition();
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		assertFalse(new AndCondition().equals(null));

		final AndCondition obj = new AndCondition();
		assertTrue(obj.equals(obj));
		assertTrue(new AndCondition().equals(new AndCondition()));
		assertTrue(new AndCondition((ChatCondition) null).equals(new AndCondition(
				(ChatCondition) null)));

		assertFalse(new AndCondition((ChatCondition) null).equals(new AndCondition()));
		assertFalse(new AndCondition().equals(new AndCondition(
				(ChatCondition) null)));
		assertFalse(new AndCondition((ChatCondition) null).equals(new AndCondition(
				falsecondition)));
		assertFalse(new AndCondition().equals(Integer.valueOf(100)));
		assertTrue(new AndCondition().equals(new AndCondition() {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for equalsthisandsingle.
	 */
	@Test
	public void testEqualsthisandsingle() throws Exception {
		final String QUEST_SLOT = "quest";
		final ChatCondition andcon =  new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start")
		   , new KilledCondition("dark elf archer", "dark elf captain", "thing"));

		final ChatCondition instate = new QuestInStateCondition(QUEST_SLOT, "start");
		  assertFalse(andcon.equals(instate));
		  assertFalse(instate.equals(andcon));
	}
	
	
	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() throws Throwable {

		assertTrue("empty And is true", new AndCondition().fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		AndCondition and = new AndCondition(trueCondition);
		assertTrue("And with one Allwaystrue is true", and.fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		and = new AndCondition(trueCondition, falsecondition);
		assertFalse("And with one true and on false is false", and.fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		and = new AndCondition(falsecondition, trueCondition);
		assertFalse("And with one false and on true is false", and.fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		and = new AndCondition(new AdminCondition());

		assertFalse("And with one false is false", and.fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("testAndConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() throws Throwable {
		final AndCondition obj = new AndCondition();
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new AndCondition().hashCode(),
				new AndCondition().hashCode());
		assertEquals(new AndCondition((ChatCondition) null).hashCode(),
				new AndCondition((ChatCondition) null).hashCode());

	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() throws Throwable {
		assertEquals("[]", new AndCondition().toString());

		assertEquals("[true]", new AndCondition(trueCondition).toString());
		assertEquals("[true, NOT <true>]", new AndCondition(trueCondition,
				falsecondition).toString());
		assertEquals("[NOT <true>]",
				new AndCondition(falsecondition).toString());
	}
}
