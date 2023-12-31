/* $Id$ */
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
package games.stendhal.server.entity.npc.behaviour.journal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.bakery.BakerNPC;
import games.stendhal.server.maps.ados.goldsmith.GoldsmithNPC;
import games.stendhal.server.maps.ados.meat_market.BlacksheepBobNPC;
import utilities.PlayerTestHelper;


public class ProducerRegisterTest {

	/**
	 * Tests for get.
	 */
	@Test
	public final void testGet() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		assertSame(producerRegister, SingletonRepository.getProducerRegister());
	}

	/**
	 * Tests for add.
	 */
	@Test
	public final void testAdd() {

		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		// check first that it is empty
		assertTrue(producerRegister.getProducers().isEmpty());

		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("mąka", 1);
		requiredResources.put("dorsz", 2);
		requiredResources.put("makrela", 1);
		requiredResources.put("por", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour("linzo_make_fish_pie", Arrays.asList("make", "zrób"), "tarta z rybnym nadzieniem",
		        requiredResources, 5 * 60);

		producerRegister.add("Linzo", behaviour);

		assertFalse(producerRegister.getProducers().isEmpty());
		assertTrue(producerRegister.getProducers().size()==1);
	}

	/**
	 * Tests for adding from the producer adder method
	 */
	@Test
	public final void testAddFromProducerAdder() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		assertTrue(producerRegister.getProducers().isEmpty());

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		// call NPC code which will make ProducerAdder add to register
		new GoldsmithNPC().configureZone(zone, null);

		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("ser", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour("joshua_test", Arrays.asList(""), "", requiredResources, 0);
		SingletonRepository.getProducerRegister().configureNPC("Joshua", behaviour, "");

		assertFalse(producerRegister.getProducers().isEmpty());
	}

	/**
	 * Tests listing the working producers
	 */
	@Test
	public final void testListWorkingProducers() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		Player player = PlayerTestHelper.createPlayer("player");

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		// call NPC code which will make ProducerAdder add to register
		new BakerNPC().configureZone(zone, null);
		new BlacksheepBobNPC().configureZone(zone, null);

		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("ser", 1);
		final ProducerBehaviour bakerBehaviour = new ProducerBehaviour("arlindo_make_pie", Arrays.asList("make,zrób"),
				"tarta", requiredResources, 7 * 60);
		SingletonRepository.getProducerRegister().configureNPC("Arlindo", bakerBehaviour, "");

		final ProducerBehaviour bobBehaviour = new ProducerBehaviour("blacksheepbob_make_sausage", Arrays.asList("make,zrób"),
				"paróweczka", requiredResources, 2 * 60);
		SingletonRepository.getProducerRegister().configureNPC("Blacksheep Bob", bobBehaviour, "");

		assertFalse(producerRegister.getProducers().isEmpty());

		// no orders yet because the player didn't start any
		assertEquals(producerRegister.listWorkingProducers(player),"Nie masz trwających lub nieodebranych zleceń.");

		player.setQuest("arlindo_make_pie", "1;tarta;1");
		player.setQuest("blacksheepbob_make_sausage", "210;paróweczka;"+System.currentTimeMillis());

		assertEquals(producerRegister.listWorkingProducers(player),"\r\nZlecenia: " +
				"\nArlindo ukończył twój tarta."
				+"\nBlacksheep Bob pracuje nad 210 paróweczki. Będzie gotowy za 7 godziny.");

		//collect orders
		player.setQuest("arlindo_make_pie", "done");
		player.setQuest("blacksheepbob_make_sausage", "done");

		// no orders now because they are all collected
		assertEquals(producerRegister.listWorkingProducers(player),"Nie masz trwających lub nieodebranych zleceń.");

	}

	/**
	 * Tests listing the food items
	 */
	@Test
	public final void testGetProducedItems() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		// call NPC code which will make ProducerAdder add to register
		new BakerNPC().configureZone(zone, null);
		new BlacksheepBobNPC().configureZone(zone, null);

		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("ser", 1);
		final ProducerBehaviour bakerBehaviour = new ProducerBehaviour("arlindo_make_pie", Arrays.asList("make,zrób"),
				"tarta", requiredResources, 7 * 60);
		SingletonRepository.getProducerRegister().configureNPC("Arlindo", bakerBehaviour, "");

		final ProducerBehaviour bobBehaviour = new ProducerBehaviour("blacksheepbob_make_sausage", Arrays.asList("make,zrób"),
				"paróweczka", requiredResources, 2 * 60);
		SingletonRepository.getProducerRegister().configureNPC("Blacksheep Bob", bobBehaviour, "");

		assertFalse(producerRegister.getProducers().isEmpty());

		assertEquals(producerRegister.getProducedItemNames("food"), Arrays.asList("tarta", "paróweczka"));
		assertEquals(producerRegister.getProducedItemNames("food").toString(), "[tarta, paróweczka]");


	}


}
