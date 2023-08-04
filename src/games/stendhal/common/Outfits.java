/* $Id$ */
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

import java.util.Arrays;
import java.util.List;

/**
 * class to store the max outfit numbers for player available outfits.
 * @author kymara
 */
public class Outfits {

	/*
	 * Edit these fields to add new outfits.
	 * Note: Outfits are numbered starting at 0 and these
	 * variables are the total number of outfits.
	 */

	/** number of player selectable heads */
	public static final int HEAD_OUTFITS = 36;

	/** number of player selectable dresses */
	public static final int CLOTHES_OUTFITS = 100;

	/** number of player selectable hair styles */
	public static final int HAIR_OUTFITS = 117;

	/** number of player selectable body shapes */
	public static final int BODY_OUTFITS = 38;
	private static final List<Integer> DRESS_COMPATIBLE_BODIES = Arrays.asList(); // populate with any other bodies that a dress layer can be worn over
	private static final List<Integer> FEMALE_BODIES = Arrays.asList(6, 7, 8, 9, 10, 11, 13, 25, 29, 30, 35);

	/** number of player selectable hats */
	public static final int HAT_OUTFITS = 0;

	/** number of player selectable eyes */
	public static final int EYES_OUTFITS = 0;

	/** number of player selectable mouths */
	public static final int MOUTH_OUTFITS = 0;

	/** number of player selectable masks */
	public static final int MASK_OUTFITS = 0;

	// hair should not be drawn with hat indexes in this list
	public static final List<Integer> HATS_NO_HAIR = Arrays.asList();
	// some outfit layers should not be drawn with ship dress indexes in this list
	public static final List<Integer> SHIPS_NO_LAYERS = Arrays.asList(17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 50, 51);

	// layers used for building outfits
	public static final List<String> LAYER_NAMES = Arrays.asList(
					"body", "dress", "head", "hair", "detail");
	public static final int LAYERS_COUNT = LAYER_NAMES.size();

	// layers that can be re-colored
	public static final List<String> RECOLORABLE_OUTFIT_PARTS = Arrays.asList(
					"detail", "detail-rear", "dress", "hair", "body", "head");

	public static final List<String> SKIN_LAYERS = Arrays.asList("body", "head");

	/**
	 * Checks if a dress layer can be worn over an outfit index.
	 *
	 * @param body
	 * 		Body index.
	 * @return
	 * 		<code>true</code> if body is <code>null</code>, or if body is within the range
	 * 		of player-selectable indexes, or if the index has been explicitly listed in the
	 * 		dress compatible list.
	 */
	public static boolean isDressCompatibleBody(final Integer body) {
		return body == null || (body >= 0 && body < BODY_OUTFITS) || DRESS_COMPATIBLE_BODIES.contains(body);
	}

	public static boolean isFemaleBody(final Integer body) {
		return FEMALE_BODIES.contains(body);
	}
}
