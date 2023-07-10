/* $Id: PortalMapObject.java,v 1.2 2010/09/19 02:19:20 nhnb Exp $ */
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
package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

public class PortalMapObject extends StaticMapObject {
	public PortalMapObject(final IEntity entity) {
		super(entity);
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, Color.WHITE, Color.BLACK);
	}
}
