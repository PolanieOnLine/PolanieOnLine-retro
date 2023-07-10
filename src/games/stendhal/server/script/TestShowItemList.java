/* $Id: TestShowItemList.java,v 1.6 2010/09/19 02:36:26 nhnb Exp $ */
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
package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a portable NPC who gives ALL players powerful items, increases their
 * level and makes them admins. This is used on test-systems only. Therefore it
 * is disabled in default install and you have to use this parameter:
 * -Dstendhal.testserver=junk as a vm argument.
 * 
 * As admin uses /script AdminMaker.class to summon her right next to him/her.
 * Please unload it with /script -unload AdminMaker.class
 */

public class TestShowItemList extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		List<Item> itemList = new LinkedList<Item>();

		if (args.isEmpty()) {
			itemList.add(prepareItem("maczuga", 100));
			itemList.add(prepareItem("skórzana zbroja", -100));
			itemList.add(prepareItem("miecz lodowy", -10000));
		} else {
			ShopList shops = SingletonRepository.getShopList();
			Map<String, Integer> items = shops.get(args.get(0));
			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				itemList.add(prepareItem(entry.getKey(), Integer.valueOf(entry.getValue())));
			}
		}

		ShowItemListEvent event = new ShowItemListEvent("Sklep Aramyk", 
				"Porozmawiaj z Aramykiem, aby #kupić lub #sprzedać przedmioty.", 
				itemList);
		admin.addEvent(event);
	}

	/**
	 * prepares an item for displaying
	 *
	 * @param name   name of item
	 * @param price  price of item (negative is for cases in which the player has to pay money)
	 * @return Item
	 */
	private Item prepareItem(String name, int price) {
		Item item = SingletonRepository.getEntityManager().getItem(name);
		item.put("price", -price);
		item.put("description", item.describe());
		return item;
	}
}
