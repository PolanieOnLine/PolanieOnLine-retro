/* $Id: AdminConditionTest.java,v 1.15 2011/05/01 19:50:06 martinfuchs Exp $ */
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
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class AdminConditionTest extends PlayerTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}
	
	/**
	 * Tests for constructor.
	 */
	@Test
	public void testConstructor() throws Throwable {
		final AdminCondition adminCondition = new AdminCondition();
		assertEquals("adminCondition.hashCode()", 5629,
				adminCondition.hashCode());
	}

	/**
	 * Tests for constructor1.
	 */
	@Test
	public void testConstructor1() throws Throwable {
		final AdminCondition adminCondition = new AdminCondition(100);
		assertEquals("adminCondition.hashCode()", 729,
				adminCondition.hashCode());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		final AdminCondition obj = new AdminCondition(100);
		assertTrue(obj.equals(obj));
		assertTrue(new AdminCondition().equals(new AdminCondition()));
		assertFalse(new AdminCondition(100).equals(new AdminCondition(1000)));
		assertFalse(new AdminCondition(100).equals("testString"));
		assertFalse(new AdminCondition(100).equals(null));
		assertTrue("subclass is equal",
				new AdminCondition(100).equals(new AdminCondition(100) {
					// this is an anonymous sub class
				}));
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() throws Throwable {
		assertTrue(new AdminCondition(0).fire(createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		assertFalse(new AdminCondition().fire(createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() throws Throwable {
		assertEquals("result", 629, new AdminCondition(0).hashCode());
		assertEquals("result", 729, new AdminCondition(100).hashCode());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() throws Throwable {
		assertEquals("result", "admin <100>",
				new AdminCondition(100).toString());
	}

	/**
	 * Tests for fireThrowsNullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireThrowsNullPointerException() throws Throwable {
		new AdminCondition(100).fire(null, ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC());
	}

}
