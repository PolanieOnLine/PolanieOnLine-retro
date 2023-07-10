/* $Id: MapObject.java,v 1.2 2010/09/19 02:19:20 nhnb Exp $ */
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

import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

public abstract class MapObject {
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	public MapObject(final IEntity entity) {
		x = entity.getX();
		y = entity.getY();
		width = (int) entity.getWidth();
		height = (int) entity.getHeight();
	}

	/**
	 * Draw the entity
	 * 
	 * @param g Graphics context
	 * @param scale Scaling factor
	 */
	public abstract void draw(Graphics g, int scale);
	
	/**
	 * Scale a world coordinate to canvas coordinates
	 * 
	 * @param crd World coordinate
	 * @param scale Scaling factor
	 * @return corresponding canvas coordinate
	 */
	protected int worldToCanvas(final double crd, final int scale) {
		return (int) (crd * scale);
	}
}
