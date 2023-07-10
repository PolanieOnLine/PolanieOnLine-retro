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
package games.stendhal.client;

import static games.stendhal.common.Outfits.HATS_NO_HAIR;
import static games.stendhal.common.Outfits.SHIPS_NO_LAYERS;
import static games.stendhal.common.Outfits.LAYER_NAMES;
import static games.stendhal.common.Outfits.RECOLORABLE_OUTFIT_PARTS;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import games.stendhal.client.gui.OutfitColor;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.util.JSONLoader;

/**
 * An outfit store.
 */
public class OutfitStore {
	private Logger logger = Logger.getLogger(OutfitStore.class);

	/** outfit directory */
	private static final String OUTFITS = "data/sprites/outfit";

	// these layers should return an empty sprite for index "0"
	final List<String> emptyForZeroIndex = Arrays.asList("dress", "mouth", "mask", "hair", "hat", "detail");

	private List<Integer> detailRearLayers;

	private boolean initialized = false;

	/**
	 * The singleton.
	 */
	private static final OutfitStore sharedInstance = new OutfitStore(
			SpriteStore.get());

	/**
	 * The sprite store.
	 */
	private SpriteStore store;

	/**
	 * Create an outfit store.
	 *
	 * @param store
	 *            The sprite store to use.
	 */
	private OutfitStore(final SpriteStore store) {
		this.store = store;
		detailRearLayers = new ArrayList<>();
	}

	public void init() {
		if (initialized) {
			logger.warn("tried to re-initialize OutfitStore");
			return;
		}
		initialized = true;

		final JSONLoader loader = new JSONLoader();
		loader.onDataReady = new Runnable() {
			@Override
			public void run() {
				final JSONObject document = (JSONObject) loader.data;
				final JSONArray detailRear = (JSONArray) ((JSONObject) document.get("detail")).get("rear");
				for (final Object o: detailRear) {
					try {
						detailRearLayers.add(Integer.parseInt(o.toString()));
					} catch (final NumberFormatException e) {
						logger.error("Couldn't parse rear detail layer \"" + o.toString() + "\"", e);
					}
				}
			}
		};
		loader.load(OUTFITS + "/outfits.json");
	}

	//
	// OutfitStore
	//

	/**
	 * Checks if we can draw a "rear" detail layer.
	 *
	 * @param detail
	 *     Detail index.
	 * @return
	 *     <code>true</code> if a rear sprite is available.
	 */
	private boolean detailHasRearLayer(final int detail) {
		return detailRearLayers.contains(detail);
	}

	/**
	 * Build an outfit sprite.
	 *
	 * @param strcode
	 * @param color
	 * 		coloring data
	 * @return A walking state tileset.
	 */
	private Sprite buildOutfit(final String strcode, final OutfitColor color) {
		final Map<String, Integer> layer_map = new HashMap<>();

		// make a copy of layer names so it can be amended in cases of special layers
		final List<String> lnames = new LinkedList<>();
		lnames.addAll(LAYER_NAMES);

	    // initialize outfit parts to 0 in case some haven't been specified
		for (String n: lnames) {
			layer_map.put(n, 0);
		}

		for (String layer: strcode.split(",")) {
			if (layer.contains("=")) {
				final String[] key = layer.split("=");
				layer_map.put(key[0], Integer.parseInt(key[1]));
			}
		}

		ImageSprite sprite;

		Sprite layer;

		// detail rear layer
		final int detailIndex = layer_map.get("detail");
		if (detailHasRearLayer(detailIndex)) {
			lnames.add(0, "detail-rear");
			layer_map.put("detail-rear", detailIndex);
		}

		// Body layer
		final int bodyIndex = layer_map.get("body");
		boolean slim = false;
		if (bodyIndex < 0) {
			layer = store.getEmptySprite(48 * 3, 64 * 4);
		} else {
			layer = getLayerSprite("body", layer_map.get("body"), color);
			if (bodyIndex >= 0 && bodyIndex <= 2) {
				slim = true;
			}
		}

		if (layer == null) {
			throw new IllegalArgumentException(
					"No body image found for outfit: " + layer_map.get("body"));
		}

		sprite = new ImageSprite(layer);
		final Graphics g = sprite.getGraphics();

		for (String lname: lnames) {
			// hair is not drawn under certain hats/helmets
			if (lname.equals("hair") && HATS_NO_HAIR.contains(layer_map.get("hat"))) {
				continue;
			}

			// other outfit layers isn't drawn with ship body
			final boolean ship_without_layers = SHIPS_NO_LAYERS.contains(layer_map.get("body"));
			if ((lname.equals("dress") && ship_without_layers)
					|| (lname.equals("head") && ship_without_layers)
					|| (lname.equals("mouth") && ship_without_layers)
					|| (lname.equals("eyes") && ship_without_layers)
					|| (lname.equals("hair") && ship_without_layers)
					|| (lname.equals("mask") && ship_without_layers)
					|| (lname.equals("hat") && ship_without_layers)
					|| (lname.equals("detail") && ship_without_layers)) {
				continue;
			}

			if (RECOLORABLE_OUTFIT_PARTS.contains(lname)) {
				layer = getLayerSprite(lname, layer_map.get(lname), color, slim);
			} else {
				layer = getLayerSprite(lname, layer_map.get(lname), slim);
			}
			layer.draw(g, 0, 0);
		}

		return sprite;
	}

	/**
	 * Get the shared instance.
	 *
	 * @return The shared [singleton] instance.
	 */
	public static OutfitStore get() {
		return sharedInstance;
	}

	/**
	 * Get a string value of convention xxx from "index"
	 *
	 * @param index
	 * 		The sprite index number
	 * @return
	 * 		A string value of the format xxx
	 *
	 * FIXME:	Probably not necessary since there are no more than 100
	 * 			sprites for each group.
	 */
	private String getSpriteSuffix(final int index) {
		String suffix;

		/** Get the value of the index using xxx naming convention */
		if (index < 10) {
			suffix = "00" + Integer.toString(index);
		} else if (index < 100) {
			suffix = "0" + Integer.toString(index);
		} else {
			suffix = Integer.toString(index);
		}

		return suffix;
	}

	/**
	 * Get the empty sprite tileset.
	 *
	 * @return The sprite.
	 */
	private Sprite getEmptySprite() {
		return store.getEmptySprite();
	}

	/**
	 * Get the layer sprite tileset.
	 *
	 * @param layer
	 *     Name of the layer.
	 * @param index
	 *     The resource index.
	 * @param color
	 *     Layer coloring.
	 * @param slim
	 *     Body type is "slim".
	 * @return
	 *     The Sprite or <code>null</code>.
	 */
	public Sprite getLayerSprite(String layer, final int index, final OutfitColor color,
			final boolean slim) {
		if (emptyForZeroIndex.contains(layer)) {
			if (index <= 0) {
				return getEmptySprite();
			}
		} else {
			if (index < 0) {
				return getEmptySprite();
			}
		}

		String ref = getSpriteSuffix(index);
		if (layer.endsWith("-rear")) {
			ref += "-rear";
			layer = layer.replaceFirst("-rear$", "");
		}

		ref = OUTFITS + "/" + layer + "/" + ref;
		if (layer.equals("body") && WtWindowManager.getInstance().getPropertyBoolean("gamescreen.nonude", true)) {
			final URL nonudeURL = DataLoader.getResource(ref + "-nonude.png");
			if (nonudeURL != null) {
				ref = ref + "-nonude";
			}
		} else if (layer.equals("dress") && slim) {
			// check if "slim" dress exists
			final URL bustyURL = DataLoader.getResource(ref + "s.png");
			if (bustyURL != null) {
				ref = ref + "s";
			}
		}
		ref = ref + ".png";

		if (color == null) {
			return store.getSprite(ref);
		} else {
			final String layer_color;
			final List<String> skin_layers = Arrays.asList("body", "head");
			if (skin_layers.contains(layer)) {
				layer_color = "skin";
			} else {
				layer_color = layer;
			}

			return store.getColoredSprite(ref, color.getColor(layer_color));
		}
	}

	/**
	 * Get the layer sprite tileset.
	 *
	 * @param layer
	 *     Name of the layer.
	 * @param index
	 *     The resource index.
	 * @param color
	 *     Layer coloring.
	 * @return
	 *     The Sprite or <code>null</code>.
	 */
	public Sprite getLayerSprite(final String layer, final int index, final OutfitColor color) {
		return getLayerSprite(layer, index, color, false);
	}

	/**
	 * Get the layer sprite tileset.
	 *
	 * @param layer
	 *     Name of the layer.
	 * @param index
	 *     The resource index.
	 * @param slim
	 *     Body type is "slim".
	 * @return
	 *     The Sprite or <code>null</code>.
	 */
	public Sprite getLayerSprite(final String layer, final int index, final boolean slim) {
		return getLayerSprite(layer, index, null, slim);
	}

	/**
	 * Get the layer sprite tileset.
	 *
	 * @param layer
	 *     Name of the layer.
	 * @param index
	 *     The resource index.
	 * @return
	 *     The Sprite or <code>null</code>.
	 */
	public Sprite getLayerSprite(final String layer, final int index) {
		return getLayerSprite(layer, index, null, false);
	}

	/**
	 * Get the failsafe outfit.
	 *
	 * @return The failsafe outfit tileset.
	 */
	public Sprite getFailsafeOutfit() {
		try {
			final String failsafe_str = "body=0,dress=0,head=0,mouth=0,eyes=0,mask=0,hair=0,hat=0,detail=0";
			return getOutfit(failsafe_str, OutfitColor.PLAIN);
		} catch (RuntimeException e) {
			logger.warn("Cannot build failsafe outfit. Trying to use standard failsafe sprite.", e);
			return store.getFailsafe();
		}
	}

	/**
	 * Get an outfit sprite.
	 *
	 * @param strcode
	 * @param color
	 * 		Colors for coloring some outfit parts.
	 * @return outfit
	 */
	private Sprite getOutfit(final String strcode, final OutfitColor color) {
		// Use the normalized string for the reference
		String colorString = "null";
		if (color != null) {
			colorString = color.toString();
		}

		final String reference = buildReference(strcode, colorString);
		return getOutfit(strcode, color, reference);
	}

	/**
	 * Get outfit for a known outfit reference.
	 *
	 * @param strcode
	 * @param color
	 * 		Colors for coloring some outfit parts.
	 * @param reference
	 * 		Outfit reference.
	 * @return outfit
	 */
	private Sprite getOutfit(final String strcode, final OutfitColor color, final String reference) {
		final SpriteCache cache = SpriteCache.get();

		// FIXME: set sprite to null until reference for extended outfit is fixed
		//Sprite sprite = cache.get(reference);
		Sprite sprite = null;

		if (sprite == null) {
			sprite = buildOutfit(strcode, color);
			cache.add(reference, sprite);
		}

		return sprite;
	}

	/**
	 * Get an outfit with color adjustment, such as a player in colored light.
	 */
	public Sprite getAdjustedOutfit(final String strcode, final OutfitColor color, final Color adjColor, final Composite blend) {
		if (adjColor == null || blend == null) {
			return getOutfit(strcode, color);
		} else {
			final String reference = buildReference(strcode, color.toString());
			String fullRef = reference + ":" + adjColor.getRGB() + blend.toString();

			// FIXME: set sprite to null until reference for extended outfit is fixed
			//Sprite sprite = cache.get(fullRef);
			Sprite sprite = null;

			if (sprite == null) {
				Sprite plain = getOutfit(strcode, color);
				sprite = store.modifySprite(plain, adjColor, blend, fullRef);
			}

			return sprite;
		}
	}

	/**
	 * Create an unique reference for an outfit.
	 *
	 * @param code outfit code
	 * @param colorCode color information for outfit parts
	 * @return outfit reference
	 */
	private String buildReference(final String strcode, final String colorCode) {
		return "OUTFIT:" + strcode + "@" + colorCode;
	}

	/*
	@Deprecated
	private String buildReference(final int code, final int mouth, final int eyes, final int mask, final int hat,
			final String colorCode) {
		return "OUTFIT:" + code + "@" + colorCode;
	}
	*/
}
