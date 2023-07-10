/* $Id: UseableEntity.java,v 1.6 2010/10/30 14:53:16 nhnb Exp $ */
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
package games.stendhal.client.entity;

/**
 * A map entity with a use menu.
 *
 * @author hendrik
 */
public class UseableEntity extends Entity {

	/**
	 * gets the internal state used to pick the correct row in the
	 * sprite image.
	 *
	 * @return row index in the sprite image
	 */
	public int getState() {
		if (!rpObject.has("state")) {
			return 0;
		}
		return rpObject.getInt("state");
	}

}
