/* $Id: Creature.java,v 1.87 2010/03/23 19:15:03 nhnb Exp $ */
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

import games.stendhal.common.constants.SoundLayer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

public class Creature extends AudibleEntity {
	/**
	 * Debug property.
	 */
	public static final Property PROP_DEBUG = new Property();

	/**
	 * Metamorphosis property.
	 */
	public static final Property PROP_METAMORPHOSIS = new Property();

	@Override
	protected void nonCreatureClientAddEventLine(final String text) {
		
		// no logging for Creature "sounds" in the client window
	}

	/**
	 * The current debug info.
	 */
	private String debug;

	/**
	 * The current metamorphosis.
	 */
	private String metamorphosis;

	//
	// Creature
	//

	public String getDebug() {
		return debug;
	}

	/**
	 * Get the metamorphosis in effect.
	 * 
	 * @return The metamorphosis, or <code>null</code>.
	 */
	public String getMetamorphosis() {
		return metamorphosis;
	}

	//
	// Entity
	//

	/**
	 * Get the area the entity occupies.
	 * 
	 * @return A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(getWidth() - 1.0) < 0.1)
				&& (Math.abs(getHeight() - 2.0) < 0.1)) {
			return new Rectangle.Double(getX(), getY() + 1.0, 1.0, 1.0);
		}

		return super.getArea();
	}

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		final String type = getType();

		if (object.has("name")) {
			final String name = object.get("name");

			addSounds(SoundLayer.FIGHTING_NOISE.groupName, "attack",
				"pol-slash", "pol-crash", "pol-sword",
 				"pol-swing", "pol-kling", "punch-1",
				"punch-2"  , "punch-3"  , "punch-4",
				"punch-5"  , "punch-6"  , "swingaxe-1",
				"slap-1"   , "arrow-1");

			if (type.startsWith("creature")) {
				if (name.equals("wilk") || name.equals("dziki pies")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "bark-1", "howl-5", "howl-2", "howl-11");
				} else if (name.equals("szczur") || name.equals("szczur jaskiniowy") || name.equals("wściekły szczur") || name.equals("szara mysz") || name.equals("mysz domowa")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "rats-2", "rats-41");
				} else if (name.equals("krwiożerczy szczur") || name.equals("biała mysz")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "rats-1");
				} else if (name.equals("gargulec")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "hyena-1", "hyena-2", "hyena-3");
				} else if (name.equals("dzik") || name.equals("prosiak")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pig-1", "pig-2");
				} else if (name.equals("niedźwiedź") || name.equals("niedźwiedzica")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "bear-1", "bear-2", "bear-3");
				} else if (name.equals("szczur olbrzymi")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "bobcat-1", "leopard-11");
				} else if (name.equals("wąż")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "snake-1");
				} else if (name.equals("kobold")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "panda-1", "panda-2", "racoon-1", "lama-1");
				} else if (name.equals("goblin")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "saur-3", "saur-4");
				} else if (name.equals("trol")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "gorilla-1", "gorilla-2", "gorilla-3", "gorilla-4", "gorilla-5");
				} else if (name.equals("ork")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "lion-11", "lion-22");
				} else if (name.equals("ogr")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "yell-1", "groan-1", "moan-1", "fart-1");
				} else if (name.equals("szkielet") || name.equals("szkielet uzbrojony")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "bones-1", "evillaugh-3", "evillaugh-5", "ghost-1", "ghost-2");
				} else if (name.equals("cyklop")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "yell-1", "laugh-33", "evillaugh-4", "grunt-1", "grunt-2");
				} else if (name.equals("tygrys") || name.equals("biały tygrys")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-tiger");
				} else if (name.equals("gołąbek")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-bird_pigeon");
				} else if (name.equals("kruk")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-crow");
				} else if (name.equals("mewa")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-seagull");
				} else if (name.equals("żabiczłek")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "frog-1");
				} else if (name.equals("żabiczłek elitarny")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "frog-1");
				} else if (name.equals("żabiczłek czarodziej")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "frog-1");
				} else if (name.equals("chmara much")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-buzz");
				} else if (name.equals("rój os")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-buzz");
				} else if (name.equals("rój szerszeni")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-buzz");
				} else if (name.equals("jastrząb")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-hawk1", "pol-hawk2");
				} else if (name.equals("chochoł") || name.equals("chochoł suchy") || name.equals("chochoł wielki") || name.equals("krzew wędrujący")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-chochol");
				} else if (name.equals("strzyga polna") || name.equals("strzyga leśna")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "evillaugh-5");
				} else if (name.equals("golem z lawy")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-lava");
				} else if (name.equals("kamień") || name.equals("głaz") || name.equals("lawina kamienna")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-stones2");
				} else if (name.equals("kula śnieżna") || name.equals("lawina")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-snow");
				} else if (name.equals("wieża") || name.equals("olbrzymia wieża")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-giant");
				} else if (name.equals("błędny ognik") || name.equals("błękitny ognik")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-ognisko");
				} else if (name.equals("cerber") || name.equals("wilczykruk")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-growl");
				} else if (name.equals("banita")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-banita");
				} else if (name.equals("banita gajowy")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-banita-gajowy");
				} else if (name.equals("banitka")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-banitka");
				} else if (name.equals("gajowy")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-gajowy");
				} else if (name.equals("starzec")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-starzec");
				} else if (name.equals("wieśniak")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-wiesniak");
				} else if (name.equals("zbójnik leśny starszy")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-zbo_las_starszy");
				} else if (name.equals("zbójnik leśny")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-zbo_les");
				} else if (name.equals("zbójnik leśny oszust")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-zbo_les_oszust");
				} else if (name.equals("zbójnik leśny tchórzliwy")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-zbo_les_tchorzliwy");
				} else if (name.equals("zbójnik leśny zwiadowca")) {
					addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "pol-zbo_les_zwiadowca");
				}
			}
		}

		if (object.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
		} else {
			metamorphosis = null;
		}
	}

	/**
	 * When the entity's position changed.
	 * 
	 * @param x
	 *            The new X coordinate.
	 * @param y
	 *            The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);
		playRandomSoundFromGroup("creature", "move", 10000);
	}

	@Override
	public void onDamaged(Entity attacker, int damage) {
		super.onDamaged(attacker, damage);
		playRandomSoundFromCategory(SoundLayer.FIGHTING_NOISE.groupName, "attack");
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Debuging?
		 */
		if (changes.has("debug")) {
			debug = changes.get("debug");
			fireChange(PROP_DEBUG);
		}

		if (changes.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
			fireChange(PROP_METAMORPHOSIS);
		}
	}

	/**
	 * The object removed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("metamorphosis")) {
			metamorphosis = null;
			fireChange(PROP_METAMORPHOSIS);
		}
	}
}
