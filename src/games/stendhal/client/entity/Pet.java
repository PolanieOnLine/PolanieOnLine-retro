/* $Id: Pet.java,v 1.17 2012/03/26 20:12:21 nhnb Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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
 * A Pet entity.
 */
public class Pet extends DomesticAnimal {
	
	@Override
	protected void probableChat(final int chance) {
		// do not chat
	}
}
