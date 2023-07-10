/* $Id: EmptySprite.java,v 1.13 2010/11/09 19:29:52 kiheru Exp $ */
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
package games.stendhal.client.sprite;


import java.awt.Graphics;

/**
 * An empty (non-drawing) sprite.
 */
public class EmptySprite implements Sprite {
	/**
	 * The identifier reference.
	 */
	protected Object reference;

	/**
	 * The sprite height.
	 */
	protected int height;

	/**
	 * The sprite width.
	 */
	protected int width;

	/**
	 * Create an empty sprite.
	 * 
	 * @param width
	 *            The sprite width.
	 * @param height
	 *            The sprite height.
	 * @param reference 
	 */
	public EmptySprite(final int width, final int height, final Object reference) {
		this.width = width;
		this.height = height;
		this.reference = reference;
	}

	//
	// EmptySprite
	//

	/**
	 * Create an empty sprite reference.
	 * @param width 
	 * @param height 
	 * @return an Object of the type ESRef
	 * 
	 * 
	 */
	public static Object createReference(final int width, final int height) {
		return new ESRef(width, height);
	}

	//
	// Sprite
	//

	/**
	 * Create a sub-region of this sprite. <strong>NOTE: This does not use
	 * caching.</strong>
	 * 
	 * @param x
	 *            The starting X coordinate.
	 * @param y
	 *            The starting Y coordinate.
	 * @param width
	 *            The region width.
	 * @param height
	 *            The region height.
	 * @param ref
	 *            The sprite reference.
	 * 
	 * @return A new sprite.
	 */
	public Sprite createRegion(final int x, final int y, final int width,
			final int height, final Object ref) {
		return new EmptySprite(width, height, ref);
	}

	/**
	 * Draw the sprite onto the graphics context provided.
	 * 
	 * @param g
	 *            The graphics context on which to draw the sprite
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	public void draw(final Graphics g, final int x, final int y) {
	}

	/**
	 * Draws the image.
	 * 
	 * @param g
	 *            the graphics context where to draw to
	 * @param destx
	 *            destination x
	 * @param desty
	 *            destination y
	 * @param x
	 *            the source x
	 * @param y
	 *            the source y
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 */
	public void draw(final Graphics g, final int destx, final int desty, final int x, final int y, final int w,
			final int h) {
	}

	/**
	 * Get the height of the drawn sprite.
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the sprite reference. This identifier is an externally opaque object
	 * that implements equals() and hashCode() to uniquely/repeatably reference
	 * a keyed sprite.
	 * 
	 * @return The reference identifier, or <code>null</code> if not
	 *         referencable.
	 */
	public Object getReference() {
		return reference;
	}

	/**
	 * Get the width of the drawn sprite.
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return width;
	}

	//
	//

	/**
	 * An opaque sprite reference for an empty sprite.
	 */
	protected static class ESRef {
		protected int width;
		protected int height;

		public ESRef(final int width, final int height) {
			this.width = width;
			this.height = height;
		}

		//
		// Object
		//

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof ESRef) {
				final ESRef esref = (ESRef) obj;

				if (width != esref.width) {
					return false;
				}

				if (height != esref.height) {
					return false;
				}

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return width ^ height;
		}

		@Override
		public String toString() {
			return "[" + width + "x" + height + "]";
		}
	}
}
