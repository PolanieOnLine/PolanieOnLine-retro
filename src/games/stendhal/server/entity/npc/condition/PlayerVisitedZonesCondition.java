/* $Id: PlayerVisitedZonesCondition.java,v 1.7 2012/09/09 12:33:24 nhnb Exp $ */
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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * Checks if player has visited a list of certain zones
 *
 * @author madmetzger
 */
@Dev(category=Category.LOCATION, label="Zone?")
public class PlayerVisitedZonesCondition implements ChatCondition {

	private final List<String> zoneNames;

	/**
	 * Creates a new PlayerVisitedZonesCondition
	 *
	 * @param zones the zone names
	 */
	public PlayerVisitedZonesCondition(String... zones) {
		this.zoneNames = new LinkedList<String>();
		for (String zone : zones) {
			zoneNames.add(zone);
		}
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for(String zone : zoneNames) {
			StendhalRPZone zoneObject = SingletonRepository.getRPWorld().getZone(zone);
			if (zoneObject != null) {
				if(!player.hasVisitedZone(zoneObject)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerVisitedZonesCondition.class);
	}

	@Override
	public String toString() {
		return "player visited <"+zoneNames+">";
	}

}
