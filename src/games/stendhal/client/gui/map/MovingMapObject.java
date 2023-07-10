/* $Id: MovingMapObject.java,v 1.4 2012/04/06 19:36:32 kiheru Exp $ */
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

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;

public class MovingMapObject extends MapObject implements EntityChangeListener<IEntity> {
	/**
	 * The color of a general entity (pale green).
	 */
	private static final Color COLOR = new Color(200, 255, 200);
	
	public MovingMapObject(final IEntity entity) {
		super(entity);
		
		entity.addChangeListener(this);
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, COLOR);
	}
	
	/**
	 * Draw the <code>RPEntity</code> in specified color.
	 * @param g Graphics context
	 * @param scale Scaling factor
	 * @param color Drawing color
	 */
	protected void draw(final Graphics g, final int scale, final Color color) {
		final int rx = worldToCanvas(x, scale);
		final int ry = worldToCanvas(y, scale);
		final int rwidth = width * scale;
		final int rheight = height * scale;

		g.setColor(color);
		g.fillRect(rx, ry, rwidth, rheight);
	}
	
	public void entityChanged(final IEntity entity, final Object property) {
		if (property == IEntity.PROP_POSITION) {
			x = entity.getX();
			y = entity.getY();
		}
	}
}
