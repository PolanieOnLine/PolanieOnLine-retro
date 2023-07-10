/* $Id: PlayerInAreaConditionTest.java,v 1.11 2010/09/19 02:39:46 nhnb Exp $ */
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
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerInAreaConditionTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for hashCode.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testHashCode() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});

		final PlayerInAreaCondition cond = new PlayerInAreaCondition(null);

		assertEquals(cond.hashCode(), cond.hashCode());
		assertEquals((new PlayerInAreaCondition(null)).hashCode(),
				new PlayerInAreaCondition(null).hashCode());
		assertEquals((new PlayerInAreaCondition(ar)).hashCode(),
				new PlayerInAreaCondition(ar).hashCode());
	}

	/**
	 * Tests for fire.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testFire() {
		final StendhalRPZone zone = new StendhalRPZone("test");
		final Area ar = new Area(zone, new Rectangle(-2, -2, 4, 4) {
			// this is an anonymous sub class
		});
		final PlayerInAreaCondition cond = new PlayerInAreaCondition(ar);
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse(cond.fire(player, null, null));
		zone.add(player);
		assertTrue(ar.contains(player));
		assertTrue(cond.fire(player, null, null));

	}

	/**
	 * Tests for fireNPE.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireNPE() throws Exception {
		final PlayerInAreaCondition cond = new PlayerInAreaCondition(null);
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse(cond.fire(player, null, null));
	}

	/**
	 * Tests for playerInAreaCondition.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testPlayerInAreaCondition() {
		new PlayerInAreaCondition(null);
		new PlayerInAreaCondition(new Area(new StendhalRPZone("test"),
				new Rectangle() {
					// this is an anonymous sub class
			}));
	}

	/**
	 * Tests for toString.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testToString() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});
		assertEquals("player in <null>",
				new PlayerInAreaCondition(null).toString());
		assertEquals("player in <" + ar.toString() + ">",
				new PlayerInAreaCondition(ar).toString());
	}

	/**
	 * Tests for equalsObject.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testEqualsObject() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});
		final Area ar2 = new Area(new StendhalRPZone("test2"), new Rectangle() {
			// this is an anonymous sub class
		});
		final PlayerInAreaCondition cond = new PlayerInAreaCondition(null);

		assertTrue(cond.equals(cond));
		assertTrue((new PlayerInAreaCondition(null)).equals(new PlayerInAreaCondition(
				null)));
		assertTrue((new PlayerInAreaCondition(ar)).equals(new PlayerInAreaCondition(
				ar)));

		assertFalse((new PlayerInAreaCondition(ar)).equals(null));

		assertFalse((new PlayerInAreaCondition(ar)).equals(new PlayerInAreaCondition(
				ar2)));
		assertFalse((new PlayerInAreaCondition(null)).equals(new PlayerInAreaCondition(
				ar2)));
		assertFalse((new PlayerInAreaCondition(ar)).equals(new PlayerInAreaCondition(
				null)));

		assertTrue(new PlayerInAreaCondition(ar).equals(new PlayerInAreaCondition(ar) {
			// this is an anonymous sub class
		}));
	}

}
