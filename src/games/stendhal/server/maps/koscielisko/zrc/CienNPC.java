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
package games.stendhal.server.maps.koscielisko.zrc;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * A shadow (original name: Straznik) who heals players without charge.
 * 
 * @author ?
 */
public class CienNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Witaj wędrowcze.");
		npc.addJob("Prowadzę spokojne życie. Pilnuje wejścia do Zakonu Rycerzy Cienia.");
		npc.addHelp("hm.");
		npc.addGoodbye("Do widzenia kolego.");
	}
}
