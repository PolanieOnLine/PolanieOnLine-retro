/* $Id: RentedSignTransformer.java,v 1.3 2010/09/19 02:22:47 nhnb Exp $ */
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
package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.office.RentedSign;
import marauroa.common.game.RPObject;

public class RentedSignTransformer implements Transformer {

	public RPObject transform(final RPObject object) {
				return new RentedSign(object);
	}

}
