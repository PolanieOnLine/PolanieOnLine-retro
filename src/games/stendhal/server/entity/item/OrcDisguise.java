/***************************************************************************
 *                    (C) Copyright 2023 - PolanieOnLine                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

public class OrcDisguise extends Item {
	private static String[] orcToIgnore = {
		"ork", "ork włócznik", "ork wojownik", "ork łowca", "szef orków",
		"górski ork", "górski ork wojownik", "górski ork łowca", "szef górskich orków", "złoty ork"
	};

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public OrcDisguise(OrcDisguise item) {
		super(item);
	}

	/**
	 * creates a new item
	 *
	 * @param name item name
	 * @param clazz item class
	 * @param subclass item subclass
	 * @param attributes attributes
	 */
	public OrcDisguise(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public static boolean isEquipped(RPEntity entity) {
		if (entity instanceof Player) {
			return entity.isEquippedItemInSlot("armor", "zbroja orków");
		}
		return false;
	}

	public static boolean canIgnoreEnemy(String orcName) {
		List<String> enemies = Arrays.asList(orcToIgnore);
		if (enemies.contains(orcName)) {
			return true;
		}
		return false;
	}
}
