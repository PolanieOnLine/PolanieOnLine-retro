/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.styled.styles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.styled.Style;

/**
 * Factory for creating Styles.
 */
public class StyleFactory {
	private static final Logger logger = Logger.getLogger(StyleFactory.class);

	/**
	 * Create the appropriate style for a style identifier.
	 *
	 * @param styleId style name
	 * @return pixmap style matching the name
	 */
	public static Style createStyle(String styleId) {
		StyleDefinition creator = StyleDefinition.parse(styleId);
		return creator.create();
	}

	/**
	 * Get names of all available styles.
	 *
	 * @return style names
	 */
	public static Collection<String> getAvailableStyles() {
		Collection<String> rval = new ArrayList<String>();
		for (StyleDefinition s : StyleDefinition.values()) {
			rval.add(s.toString());
		}

		return rval;
	}

	/**
	 * Available style definitions.
	 */
	private static enum StyleDefinition {
		WOOD("Drewno (default)") {
			@Override
			Style create() {
				Color highlight = new Color(163, 120, 97);
				Color shadow = new Color(50, 25, 12);
				return new PixmapStyle("data/gui/panel_wood.jpg",
						true, highlight, shadow, new Color(107, 72, 50), Color.white);
			}
		},

		WOOD2("Drewno 2") {
			@Override
			Style create() {
				Color highlight = new Color(163, 120, 97);
				Color shadow = new Color(50, 25, 12);
				return new PixmapStyle("data/gui/panel_wood_v.png",
						true, highlight, shadow, new Color(107, 72, 50), Color.white);
			}
		},

		WOOD3("Drewno 3") {
			@Override
			Style create() {
				return new PixmapStyle("data/gui/panel_wood_h.png", true,
						new Color(163, 120, 97), new Color(50, 25, 12),
						new Color(107, 72, 50), Color.white);
			}
		},

		AUBERGINE("Fiolet") {
			@Override
			Style create() {
				// Text
				Color highlight = new Color(184, 149, 193);
				Color shadow = new Color(42, 7, 51);
				PixmapStyle style = new PixmapStyle("data/gui/panel_aubergine.png",
						false, highlight, shadow, Color.white, Color.white);

				// Border
				Color bhighdark = new Color (119,74,130);
				Color bshadowlight = new Color(84, 42, 95);
				Color bshadowdark = new Color(37, 1, 46);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						bhighdark, highlight, bshadowdark, bshadowlight));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						bhighdark, highlight, bshadowdark, bshadowlight));
				return style;
			}
		},

		BRICK_BROWN("Ceglany") {
			@Override
			Style create() {
				// Text
				Color highlight = new Color(219, 191, 130);
				Color shadow = new Color(36, 14, 0); // Light brown (also inactive text)
				PixmapStyle style = new PixmapStyle("data/gui/panel_brick.png",
						false, highlight, shadow, Color.white, Color.white);
				// Border
				Color blightin = new Color(181, 140, 50);
				Color bshadowin = new Color(98, 47, 15);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, blightin, shadow, bshadowin));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, blightin, shadow, bshadowin));
				return style;
			}
		},

		HONEYCOMB("Plaster miodu") {
			@Override
			Style create() {
				// Font
				Color highlight = new Color(184, 142, 64); // light brown (also text shadow)
				Color shadow = new Color(39, 26, 4); // dark brown
				Color plain = new Color(129, 104, 58); // light brown
				PixmapStyle style = new PixmapStyle("data/gui/panel_honeycomb.png",
						false, highlight, shadow, plain, Color.white);

				// Border
				Color blightout = new Color(255, 255, 180);
				Color blightin = new Color(202, 202, 101);
				Color bshadowin = new Color(160, 128, 32);
				Color bshadowout = new Color(86, 69, 17);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						blightout, blightin, bshadowout, bshadowin));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						blightout, blightin, bshadowin, bshadowout));
				return style;
			}
		},

		LEATHER_BLACK("Ciemny rzemienny") {
			@Override
			Style create() {
				// Text
				Color foreground = Color.white;
				Color highlight = new Color(130, 130, 130); // Grey
				PixmapStyle style = new PixmapStyle("data/gui/panel_leather.png",
						false, highlight, Color.black, Color.white, foreground);

				// Border
				Color bhighout = new Color(120, 120, 120);
				Color bhighin = new Color(80, 80, 80);
				Color bshadowin = new Color(40, 40, 40);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						bhighout, bhighin, Color.black, bshadowin));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						bhighout, bhighin, bshadowin, Color.black));
				return style;
			}
		},

		PARQUET_BROWN("Drewniany parkiet") {
			@Override
			Style create() {
				Color highlight = new Color(59, 25, 0); // Dark brown
				Color shadow = new Color(225, 173, 110);
				PixmapStyle style = new PixmapStyle("data/gui/panel_wood_parquet.png",
						false, highlight, shadow, Color.white, Color.white);

				// Border
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						Color.white, shadow, highlight, Color.black));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						Color.white, shadow, highlight, Color.black));

				return style;
			}
		},

		STONE("Kamienny") {
			@Override
			Style create() {
				Color highlight = new Color(50, 50, 50); // Dark grey (also text shadow)
				Color shadow = new Color(175, 175, 175); // Light grey (also inactive text color)
				PixmapStyle style = new PixmapStyle("data/gui/panel_stone.jpg",
						false, highlight, shadow, Color.white, Color.white);
				Color bhighout = new Color(130, 130, 130);
				Color bhighin = new Color(100, 100, 100);
				Color bshadow = new Color(20, 20, 20);

				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						bhighout, bhighin, highlight, bshadow));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						bhighout, bhighin, highlight, bshadow));
				return style;
			}
		},

		TILE_AQUA("Basenowy kafelek") {
			@Override
			Style create() {
				// Text
				Color highlight = new Color(137, 157, 157);
				PixmapStyle style = new PixmapStyle("data/gui/panel_aqua_tile.png",
						false, highlight, Color.black, Color.white, Color.white);

				// Border
				Color blightout = new Color(187, 240, 240);
				Color bshadowin = new Color(23, 71, 71);
				Color bshadowout = new Color(16, 36, 36);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, blightout, bshadowout, bshadowin));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, blightout, bshadowout, bshadowin));
				return style;
			}
		};

		// ---- implementation ----

		/** Human readable name of the style. */
		private final String name;
		/**
		 * Create the Style.
		 *
		 * @return style corresponding to the definition
		 */
		abstract Style create();

		/**
		 * Create a new StyleDefinition.
		 *
		 * @param name human readable name of the style
		 */
		private StyleDefinition(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Find the StyleDefinition matching a style name.
		 *
		 * @param name style name to look up
		 * @return StyleDefinition with the name, or the default definition if
		 * 	no matching style was found
		 */
		static StyleDefinition parse(String name) {
			for (StyleDefinition s : values()) {
				if (s.toString().equals(name)) {
					return s;
				}
			}

			logger.warn("Style '" + name + "' not found. Using the default.");

			return WOOD;
		}
	}
}
