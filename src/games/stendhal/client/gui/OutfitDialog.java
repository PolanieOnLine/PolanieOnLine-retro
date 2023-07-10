/* $Id: OutfitDialog.java,v 1.100 2011/11/10 19:07:19 kiheru Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - 2011 Stendhal                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.gui;

import games.stendhal.client.OutfitStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Outfits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class OutfitDialog extends JDialog {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(OutfitDialog.class);

	private static final long serialVersionUID = 4628210176721975735L;

	private static final int PLAYER_WIDTH = 48;
	private static final int PLAYER_HEIGHT = 64;
	private static final int SLIDER_WIDTH = 80;

	private final SelectorModel hair;
	private final SelectorModel head;
	private final SelectorModel body;
	private final SelectorModel dress;

	/**
	 * Coloring data used to get the initial colors, and to adjust colors should
	 * the player want those.
	 */
	private final OutfitColor outfitColor;

	/** Sprite direction: 0 for direction UP, 1 RIGHT, 2 DOWN and 3 LEFT */
	private int direction = 2;

	private final SpriteStore store = SpriteStore.get();
	private final OutfitStore ostore = OutfitStore.get();

	private final List<ResetListener> resetListeners = new ArrayList<ResetListener>();

	private JButton okButton;

	/** Label containing the hair image. */
	private OutfitLabel hairLabel;
	/** Label containing the head image. */
	private OutfitLabel headLabel;
	/** Label containing the body image. */
	private OutfitLabel bodyLabel;
	/** Label containing the dress image. */
	private OutfitLabel dressLabel;
	/** Label containing the full outfit image */
	private OutfitLabel outfitLabel;

	/** Selector for the sprite direction */
	private JSlider directionSlider;

	/**
	 * Create a new OutfitDialog.
	 * 
	 * @param parent parent window
	 * @param title title of the dialog
	 * @param outfit number of the outfit
	 * @param outfitColor coloring information. <b>Note that outfitColor
	 *	can be modified by the dialog.</b> 
	 */
	public OutfitDialog(final Frame parent, final String title, final int outfit,
			OutfitColor outfitColor) {
		this(parent, title, outfit, outfitColor, Outfits.HAIR_OUTFITS,
				Outfits.HEAD_OUTFITS, Outfits.BODY_OUTFITS,	Outfits.CLOTHES_OUTFITS);
	}

	/**
	 * Create a new OutfitDialog.
	 * 
	 * @param parent
	 *
	 * @param title
	 *            a String with the title for the dialog
	 * @param outfit
	 *            the current outfit
	 * @param outfitColor coloring data
	 * @param total_hairs
	 *            an integer with the total of sprites with hairs
	 * @param total_heads
	 *            an integer with the total of sprites with heads
	 * @param total_bodies
	 *            an integer with the total of sprites with bodies
	 * @param total_clothes
	 *            an integer with the total of sprites with clothes
	 */
	private OutfitDialog(final Frame parent, final String title, int outfit,
			OutfitColor outfitColor, final int total_hairs,
			final int total_heads, final int total_bodies,
			final int total_clothes) {
		super(parent, false);
		
		this.outfitColor = outfitColor;
		
		hair = new SelectorModel(total_hairs);
		head = new SelectorModel(total_heads);
		body = new SelectorModel(total_bodies);
		dress = new SelectorModel(total_clothes);
		
		// Needs to be after initializing the models
		initComponents();
		applyStyle();
		setTitle(title);

		// Follow the model changes; the whole outfit follows them all
		hair.addListener(hairLabel);
		hair.addListener(outfitLabel);
		head.addListener(headLabel);
		head.addListener(outfitLabel);
		body.addListener(bodyLabel);
		body.addListener(outfitLabel);
		dress.addListener(dressLabel);
		dress.addListener(outfitLabel);

		// analyse current outfit
		int bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		int clothesIndex = outfit % 1000;
		outfit = outfit / 1000;
		int headsIndex = outfit % 100;
		outfit = outfit / 100;
		int hairsIndex = outfit % 1000;

		// reset special outfits
		if (hairsIndex >= total_hairs) {
			hairsIndex = 0;
		}
		if (headsIndex >= total_heads) {
			headsIndex = 0;
		}
		if (bodiesIndex >= total_bodies) {
			bodiesIndex = 0;
		}
		if (clothesIndex >= total_clothes) {
			clothesIndex = 0;
		}
		
		// Set the current outfit indices; this will update the labels as well
		hair.setIndex(hairsIndex);
		head.setIndex(headsIndex);
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);

		pack();
		WindowUtils.closeOnEscape(this);
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		final JComponent content = (JComponent) getContentPane();
		final int pad = SBoxLayout.COMMON_PADDING;
		content.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		content.setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL, pad));
		final JComponent partialsColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		content.add(partialsColumn);

		// --------- outfit parts column ----------
		
		// Hair
		SpriteRetriever hairRetriever = new SpriteRetriever() {
			public Sprite getSprite() {
				return getHairSprite();
			}
		};
		hairLabel = new OutfitLabel(hairRetriever);
		partialsColumn.add(createSelector(hair, hairLabel));
		
		// Head
		SpriteRetriever headRetriever = new SpriteRetriever() {
			public Sprite getSprite() {
				return getHeadSprite();
			}
		};
		headLabel = new OutfitLabel(headRetriever);
		partialsColumn.add(createSelector(head, headLabel));
		
		// Body
		SpriteRetriever bodyRetriever = new SpriteRetriever() {
			public Sprite getSprite() {
				return getBodySprite();
			}
		};
		bodyLabel = new OutfitLabel(bodyRetriever);
		partialsColumn.add(createSelector(body, bodyLabel));
		
		// Dress
		SpriteRetriever dressRetriever = new SpriteRetriever() {
			public Sprite getSprite() {
				return getDressSprite();
			}
		};
		dressLabel = new OutfitLabel(dressRetriever);
		partialsColumn.add(createSelector(dress, dressLabel));
		
		// --------- Color selection column ---------
		JComponent column = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		content.add(column, SBoxLayout.constraint(SLayout.EXPAND_Y));
		JComponent selector = createColorSelector("włosów", OutfitColor.HAIR, hairLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		SBoxLayout.addSpring(column);
		selector = createColorSelector("ubrania", OutfitColor.DRESS, dressLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		
		// --------- whole outfit side ----------
		column = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		column.setAlignmentY(CENTER_ALIGNMENT);
		content.add(column);

		outfitLabel = new OutfitLabel(bodyRetriever, dressRetriever,
				headRetriever, hairRetriever);
		outfitLabel.setAlignmentX(CENTER_ALIGNMENT);
		column.add(outfitLabel);

		directionSlider = new JSlider();
		directionSlider.setMaximum(3);
		directionSlider.setSnapToTicks(true);
		directionSlider.setValue(2);
		directionSlider.setInverted(true);
		Dimension d = directionSlider.getPreferredSize();
		d.width = SLIDER_WIDTH;
		directionSlider.setPreferredSize(d);
		directionSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent evt) {
				sliderDirectionStateChanged(evt);
			}
		});
		column.add(directionSlider);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				okActionPerformed(evt);
			}
		});
		okButton.setAlignmentX(CENTER_ALIGNMENT);
		column.add(okButton);
	}

	/**
	 * Create a selector for outfit part.
	 * 
	 * @param model model that the buttons should modify
	 * @param label central image label
	 * @return selector component
	 */
	private JComponent createSelector(final SelectorModel model, OutfitLabel label) {
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);

		JButton button = new JButton("<");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.scrollDown();
			}
		});
		row.add(button);
		row.add(label);
		button = new JButton(">");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.scrollUp();
	}
		});
		row.add(button);

		return row;
	}

	/**
	 * this is called every time the user moves the slider.
	 * @param evt
	 */
	private void sliderDirectionStateChanged(final ChangeEvent evt) {
		direction = directionSlider.getValue();

		outfitLabel.changed();
		hairLabel.changed();
		headLabel.changed();
		bodyLabel.changed();
		dressLabel.changed();
	}

	/**
	 * Get the hair sprite.
	 *
	 * @return hair sprite
	 */
	private Sprite getHairSprite() {
		return store.getTile(ostore.getHairSprite(hair.getIndex(), outfitColor),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}

	/**
	 * Get the head sprite.
	 *
	 * @return head sprite
	 */
	private Sprite getHeadSprite() {
		return store.getTile(ostore.getHeadSprite(head.getIndex()), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}

	/**
	 * Get the body sprite.
	 * 
	 * @return body sprite
	 */
	private Sprite getBodySprite() {
		return store.getTile(ostore.getBaseSprite(body.getIndex()), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}

	/**
	 * Get the dress sprite.
	 *
	 * @return dress sprite
	 */
	private Sprite getDressSprite() {
		return store.getTile(ostore.getDressSprite(dress.getIndex(), outfitColor), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}

	/**
	 * Create a color selection component for an outfit part.
	 * 
	 * @param niceName outfit part name that is capitalizes for user to see
	 * @param key outfit part identifier
	 * @param label outfit part display that should be kept up to date with the
	 * 	color changes (in addition of the whole outfit display)
	 * @return color selection component
	 */
	private JComponent createColorSelector(final String niceName, final String key, final OutfitLabel label) {
		final JComponent container = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		final JCheckBox enableToggle = new JCheckBox("Kolor " + niceName);
		
		container.add(enableToggle);
		// get the current state
		boolean colored = outfitColor.getColor(key) != null;
		enableToggle.setSelected(colored);
		final ColorSelector selector = new ColorSelector();
		selector.setEnabled(colored);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		container.add(selector);
		final ColorSelectionModel model = selector.getSelectionModel(); 
		model.setSelectedColor(outfitColor.getColor(key));
		selector.getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				outfitColor.setColor(key, model.getSelectedColor());
				label.changed();
				outfitLabel.changed();
			}
		});

		enableToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (enableToggle.isSelected()) {
					// restore previously selected color, if any
					outfitColor.setColor(key, model.getSelectedColor());
					label.changed();
					outfitLabel.changed();
					selector.setEnabled(true);
				} else {
					// use default coloring
					outfitColor.setColor(key, null);
					label.changed();
					outfitLabel.changed();
					selector.setEnabled(false);
				}
			}
		});

		// For restoring the state
		resetListeners.add(new ResetListener() {
			public void reset() {
				Color color = outfitColor.getColor(key);
				boolean colored = color != null;
				if (colored) {
					/*
					 * Changing the model triggers setting the color in
					 * outfitColor, and null color is interpreted as grey in the
					 * selector model, so avoid setting that.
					 * 
					 * As a side effect, the color selector remembers the
					 * previously selected color for non colored outfit parts.
					 * That is likely a better default than mid grey anyway.  
					 */
					model.setSelectedColor(color);
				}
				selector.setEnabled(colored);
				enableToggle.setSelected(colored);
			}
		});
		
		return container;
	}

	/**
	 * OK Button action.
	 * @param evt
	 */
	private void okActionPerformed(final ActionEvent evt) {
		sendAction();
		this.dispose();
	}

	/**
	 * Sent the outfit change action to the server.
	 */
	private void sendAction() {
		StendhalClient client = StendhalClient.get();
		if (client == null) {
			/** If running standalone, just print the outfit */
			System.out.println("OUTFIT is: "
					+ (body.getIndex() + dress.getIndex() * 100 + head.getIndex() * 1000
							* 100 + hair.getIndex() * 100 * 1000 * 100));
			return;
		}

		final RPAction rpaction = new RPAction();
		rpaction.put("type", "outfit");
		rpaction.put("value", body.getIndex() + dress.getIndex() * 100 + head.getIndex()
				* 1000 * 100 + hair.getIndex() * 100 * 1000 * 100);
		Color color = outfitColor.getColor("hair");
		if (color != null) {
			rpaction.put(OutfitColor.HAIR, color.getRGB());
		}
		color = outfitColor.getColor(OutfitColor.DRESS);
		if (color != null) {
			rpaction.put(OutfitColor.DRESS, color.getRGB());
		}
		client.send(rpaction);
	}

	/**
	 * Apply Stendhal style to all components.
	 */
	private void applyStyle() {
		Style style = StyleUtil.getStyle();
		if (style != null) {
			// Labels (Images). Making all JLabels bordered would be undesired
			bodyLabel.setBorder(style.getBorderDown());
			dressLabel.setBorder(style.getBorderDown());
			outfitLabel.setBorder(style.getBorderDown());
			hairLabel.setBorder(style.getBorderDown());
			headLabel.setBorder(style.getBorderDown());
		}
	}

	/**
	 * Set the state of the selector.
	 * 
	 * @param outfit outfit code
	 * @param colors color state. Unlike the one passed to the constructor, this
	 * 	will not be modified
	 */
	void setState(int outfit, OutfitColor colors) {
		// Copy the original colors
		outfitColor.setColor(OutfitColor.DRESS, colors.getColor(OutfitColor.DRESS));
		outfitColor.setColor(OutfitColor.HAIR, colors.getColor(OutfitColor.HAIR));
		
		// analyze the outfit code
		int bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		int clothesIndex = outfit % 1000;
		outfit = outfit / 1000;
		int headsIndex = outfit % 100;
		outfit = outfit / 100;
		int hairsIndex = outfit % 1000;
		
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);
		head.setIndex(headsIndex);
		hair.setIndex(hairsIndex);

		// Color selectors, and their toggles
		for (ResetListener l : resetListeners) {
			l.reset();
		}
	}
	
	private interface ResetListener {
		void reset();
	}
	
	/**
	 * An image label for outfit and outfit parts.
	 */
	private static class OutfitLabel extends JLabel implements IndexChangeListener {
		final SpriteRetriever[] retrievers;

		/**
		 * Create a new OutfitLabel.
		 * 
		 * @param retrievers sprite sources used to update the image, when
		 *	changed() is called
		 */
		OutfitLabel(SpriteRetriever ... retrievers) {
			setOpaque(true);
			this.retrievers = retrievers;
		}

		public void changed() {
			// Update image
			BufferedImage img = getGraphicsConfiguration().createCompatibleImage(PLAYER_WIDTH, PLAYER_HEIGHT);
			Graphics g = img.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT);
			for (SpriteRetriever retriever : retrievers) {
				retriever.getSprite().draw(g, 0, 0);
			}
			g.dispose();
			ImageIcon icon = new ImageIcon(img);
			setIcon(icon);
		}
	}

	/**
	 * A ranged, circular, index model.
	 */
	private static class SelectorModel {
		final int n;
		int index;
		final List<IndexChangeListener> listeners = new ArrayList<IndexChangeListener>();

		/**
		 * Create a new SelectorModel. Valid indices are 0 to n - 1.
		 * 
		 * @param n maximum value
		 */
		SelectorModel(int n) {
			if (n <= 0) {
				throw new IllegalArgumentException("Can not create a model with " + n + " elements");
			}
			this.n = n;
		}

		/**
		 * Add a new listener for value changes.
		 * 
		 * @param listener
		 */
		void addListener(IndexChangeListener listener) {
			listeners.add(listener);
		}

		/**
		 * Get the number of elements. (maximum index + 1)
		 * 
		 * @return maximum index + 1
		 */
		int getN() {
			return n;
		}

		/**
		 * Set index.
		 *  
		 * @param index
		 */
		void setIndex(int index) {
			if ((index < 0) || (index >= n)) {
				LOGGER.warn("Index out of allowed range [0-" + n + "]: " + index, 
						new Throwable());
				index = 0;
			} 
			this.index = index;
			fire();
		}

		/**
		 * Scroll index value downwards.
		 */
		void scrollDown() {
			// avoid negatives
			index += n - 1;
			index %= n;
			fire();
	} 

		/**
		 * Scroll the index value upwards.
		 */
		void scrollUp() {
			index++;
			index %= n;
			fire();
		}

		/**
		 * Get the current index value.
		 * 
		 * @return index
		 */
		int getIndex() {
			return index;
		}

		/**
		 * Notify listeners that the value has changed.
		 */
		private void fire() {
			for (IndexChangeListener listener : listeners) {
				listener.changed();
			} 
		}
	}

	/**
	 * An interface for objects that can fetch outfit part sprites.
	 */
	private interface SpriteRetriever {
		/**
		 * Get the sprite.
		 * 
		 * @return sprite
		 */
		Sprite getSprite();
	}

	/**
	 * Interface for listening SelectorModel changes.
	 */
	private interface IndexChangeListener {
		/**
		 * Called when the model changes.
		 */
		void changed();
	}

	private void generateAllOutfits(final String baseDir) {
		/** TEST METHOD: DON'T NO USE */
		for (body.setIndex(0); body.getIndex() < body.getN(); body.setIndex(body.getIndex() + 1)) {
			for (dress.setIndex(0); dress.getIndex() < dress.getN(); dress.setIndex(dress.getIndex() + 1)) {
				for (head.setIndex(0); head.getIndex() < head.getN(); head.setIndex(head.getIndex() + 1)) {
					for (hair.setIndex(0); hair.getIndex() < hair.getN(); hair.setIndex(hair.getIndex() + 1)) {
						final String name = Integer.toString(body.getIndex()
								+ dress.getIndex() * 100 + head.getIndex() * 1000 * 100
								+ hair.getIndex() * 100 * 1000 * 100);
						final File file = new File(baseDir + "outfits/" + name
								+ ".png");

						// for performance reasons only write new files.
						if (!file.exists()) {
							System.out.println("Creating " + name + ".png");
							final Image image = new BufferedImage(PLAYER_WIDTH,
									PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
							Graphics g = image.getGraphics();
							getBodySprite().draw(g, 0, 0);
							getDressSprite().draw(g, 0, 0);
							getHeadSprite().draw(g, 0, 0);
							getHairSprite().draw(g, 0, 0);
							g.dispose();
							try {
								ImageIO.write((RenderedImage) image, "png",
										file);
							} catch (final Exception e) {
								LOGGER.error(e, e);
							}
						}
					}
				}
			}
		}
	}

	public static void main(final String[] args) {
		String baseDir = "";
		if (args.length > 0) {
			baseDir = args[0] + "/";
		}

		final OutfitDialog f = new OutfitDialog(null, "PolskaOnLine - Wybierz wygląd",
				0, OutfitColor.PLAIN);
		// show is required now, because getGraphics() returns null otherwise
		f.setVisible(true);
		f.generateAllOutfits(baseDir);
	}
}
