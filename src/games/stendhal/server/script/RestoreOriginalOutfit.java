/***************************************************************************
 *                   (C) Copyright 2003-2021 Stendhal                      *
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

import static games.stendhal.common.Outfits.RECOLORABLE_OUTFIT_PARTS;
import static games.stendhal.common.Outfits.SKIN_LAYERS;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Usage:
 *   /script RetoreOriginalOutfit.class <player>
 *     Description:
 *         Restores player's original outfit if they are wearing
 *         a temporary one.
 *     Parameters:
 *       - **player:** Name of target player.
 */
public class RestoreOriginalOutfit extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() == 0) {
			admin.sendPrivateText("/script RestoreOriginalOutfit.class <gracz>");
			return;
		}

		final String name = args.get(0);
		final Player player = SingletonRepository.getRuleProcessor().getPlayer(name);
		if (player == null) {
			admin.sendPrivateText("Gracz \"" + name + "\" nie został znaleziony.");
			return;
		}

		if (!player.has("outfit_ext_orig")) {
			admin.sendPrivateText("Gracz \"" + name + "\" nie ma założonego tymczasowego stroju.");
			return;
		}

		player.restoreOriginalOutfit();
		if (player.has("outfit_ext")) {
			final StringBuilder sb = new StringBuilder("Gracza \"" + name + "\" strój został przywrócony:");
			sb.append("\n  Strój: " + player.get("outfit_ext"));
			sb.append("\n  Kolory: ");
			boolean multi = false;

			final List<String> colorable = new ArrayList<>();
			for (final String part: RECOLORABLE_OUTFIT_PARTS) {
				if (!SKIN_LAYERS.contains(part)) {
					colorable.add(part);
				}
			}
			colorable.add("skin");

			for (final String part: colorable) {
				final String color = player.get("outfit_colors", part);
				if (color != null) {
					if (multi) {
						sb.append(", ");
					}
					sb.append(part + "=" + color);
					multi = true;
				}
			}

			admin.sendPrivateText(sb.toString());
		} else {
			admin.sendPrivateText("Atrybut \"outfit_ext\" nie został znaleziony u gracza \"" + name + "\"");
		}
	}
}
