/* $Id: ErrorBuffer.java,v 1.2 2010/09/19 02:20:44 nhnb Exp $ */
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
package games.stendhal.common;

/**
 * ErrorBuffer stores and concatenates multiple error messages.
 *
 * @author Martin Fuchs
 */
public class ErrorBuffer implements ErrorDrain {

	// start with no errors
	protected String errorBuffer = null;

	/**
	 * Store error message.
	 * 
	 * @param error message
	 */
	public void setError(final String error) {
		if (errorBuffer == null) {
			errorBuffer = error;
		} else {
			errorBuffer += '\n';
			errorBuffer += error;
		}
	}

	/**
	 * Return whether some error has been registered.
	 * 
	 * @return error flag
	 */
	public boolean hasError() {
		return errorBuffer != null;
	}

	/**
	 * Return the concatenated error message.
	 * 
	 * @return error string
	 */
	public String getErrorString() {
		return errorBuffer;
	}

}
