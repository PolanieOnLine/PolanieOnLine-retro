/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.a_grandfathers_wish;

import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AGrandfathersWish;

/**
 * A special item for An Old Man's Wish quest.
 */
public class AshenHolyWater extends Item {
	public AshenHolyWater(final String name, final String clazz,
			final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public AshenHolyWater(final AshenHolyWater hw) {
		super(hw);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			final Player player = (Player) user;

			if (checkZone(user)) {
				final MylingSpawner spawner = AGrandfathersWish.getMylingSpawner();

				if (checkMylingInWorld(spawner)) {
					player.sendPrivateText("Kropisz wodą święconą głowę mylinga.");

					removeOne();
					spawner.onMylingCured(player);
				} else {
					player.sendPrivateText("Nie ma tu mylinga. Może jak poczekam,"
						+ " to się pojawi.");
				}
			} else {
				player.sendPrivateText("Nie ma tu niczego, do czego można"
					+ " by to wykorzystać.");
			}
		}

		return true;
	}

	/**
	 * Checks if the player is currently in the burrow.
	 */
	private boolean checkZone(final RPEntity user) {
		return user.getZone().equals(SingletonRepository.getRPWorld().getZone("-1_myling_well"));
	}

	private boolean checkMylingInWorld(final MylingSpawner spawner) {
		return spawner != null && spawner.mylingIsActive();
	}
}