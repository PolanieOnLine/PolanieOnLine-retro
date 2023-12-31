/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;
import games.stendhal.server.maps.nalwor.forest.AssassinRepairerAdder.AssassinRepairer;

/**
 * An NPC that sells special swords for training.
 */
public class DojoSellerNPC implements ZoneConfigurator {
	private static StendhalRPZone dojoZone;

	private final String sellerName = "Akutagawa";
	private AssassinRepairer seller;

	private AssassinRepairerAdder repairerAdder;

	private static final Map<String, Integer> repairableSellPrices = new LinkedHashMap<String, Integer>() {{
		put("miecz treningowy", 2100);
	}};

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		dojoZone = zone;

		initNPC();
		initShop();
		initRepairShop();
		initDialogue();
	}

	private void initNPC() {
		repairerAdder = new AssassinRepairerAdder();

		seller = repairerAdder.new AssassinRepairer(sellerName, repairableSellPrices);
		seller.setEntityClass("samurai2npc");
		seller.setGender("M");
		seller.setIdleDirection(Direction.LEFT);
		seller.setPosition(37, 80);

		dojoZone.add(seller);
	}

	private void initDialogue() {
		seller.addGreeting("Jeśli szukasz sprzętu treningowego, trafiłeś we właściwe miejsce.");
		seller.addGoodbye();
		seller.addOffer("Spójrz na moją tablicę, by sprawdzić co sprzedaję. Mogę również #naprawić wszystkie używane #'miecze treningowe', które aktualnie masz.");
		seller.addJob("Prowadzę sklep w dojo dla skrytobójców, w którym sprzedaję sprzęt i #naprawiam #'miecze treningowe'.");
		seller.addQuest("Nie mam dla ciebie żadnego zadania. Tylko #naprawiam i sprzedaję sprzęt.");
		seller.addHelp("Jeśli chcesz trenować w dojo, polecam zakupić #'miecz treningowy'.");
		seller.addReply(Arrays.asList("training sword", "miecz treningowy", "miecza treningowego"), "Moje miecze treningowe są lekkie oraz łatwe do machania nimi. Dlatego,"
				+ " że zostały one wykonane z drewna, nie zaszkodzą ci gdy zostaniesz uderzony nim.");
	}

	private void initShop() {
		final Map<String, Integer> pricesSell = new LinkedHashMap<>();
		for (final String itemName: repairableSellPrices.keySet()) {
			pricesSell.put(itemName, repairableSellPrices.get(itemName));
		}
		pricesSell.put("shuriken", 80);
		pricesSell.put("płonący shuriken", 105);

		final ShopsList shops = ShopsList.get();
		for (final String itemName: pricesSell.keySet()) {
			shops.add("dojosell", ShopType.ITEM_SELL, itemName, pricesSell.get(itemName));
		}

		final String rejectedMessage = "Tylko członkowie gildii skrytobójców mogą tutaj handlować.";

		// can only purchase if carrying assassins id
		final SellerBehaviour sellerBehaviour = new SellerBehaviour(pricesSell) {
			@Override
			public ChatCondition getTransactionCondition() {
				return new PlayerHasItemWithHimCondition("licencja na zabijanie");
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new SayTextAction(rejectedMessage);
			}
		};
		new SellerAdder().addSeller(seller, sellerBehaviour, false);

		final ShopSign shopSign = new ShopSign("dojosell", "Sklep w Dojo Skrytobójców", sellerName + " sprzedaje następujące przedmioty", true) {
			/**
			 * Can only view sign if carrying assassins id.
			 */
			@Override
			public boolean onUsed(final RPEntity user) {
				if (user.isEquipped("licencja na zabijanie")) {
					return super.onUsed(user);
				} else {
					seller.say(rejectedMessage);
				}

				return true;
			}
		};
		shopSign.setEntityClass("blackboard");
		shopSign.setPosition(36, 81);

		dojoZone.add(shopSign);
	}

	/**
	 * If players bring their worn training swords they can get them repaired for half the
	 * price of buying a new one.
	 */
	private void initRepairShop() {
		final Map<String, Integer> repairPrices = new LinkedHashMap<>();
		for (final String itemName: repairableSellPrices.keySet()) {
			repairPrices.put(itemName, repairableSellPrices.get(itemName) / 2);
		}

		repairerAdder.add(seller, repairPrices);
	}
}
