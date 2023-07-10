/* $Id: NPC.java,v 1.54 2012/07/14 16:46:41 kiheru Exp $ */
/***************************************************************************
 *						(C) Copyright 2003 - Marauroa					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.Events;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;

import java.util.ArrayList;
import java.util.List;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

public abstract class NPC extends RPEntity {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(NPC.class);

	/**
	 * The NPC's current idea/thought.
	 */
	private String idea;

	/**
	 * The range in which the NPC will notice other subjects like players or enemies.
	 */
	private int perceptionRange = 5;

	/**
	 * The range in which the NPC will search for movement paths.
	 */
	private int movementRange = 20;

	public static void generateRPClass() {
		try {
			final RPClass npc = new RPClass("npc");
			npc.isA("rpentity");
			npc.addAttribute("class", Type.STRING);
			npc.addAttribute("subclass", Type.STRING);
			//npc.addAttribute("text", Type.LONG_STRING, Definition.VOLATILE);
			npc.addRPEvent("text", Definition.VOLATILE);
			npc.addAttribute("idea", Type.STRING, Definition.VOLATILE);
			npc.addAttribute("outfit", Type.INT);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public NPC(final RPObject object) {
		super(object);
		setRPClass("npc");
		update();
	}

	public NPC() {
		setRPClass("npc");
		put("type", "npc");
	}

	/**
	 * Set the NPC's idea/thought.
	 * 
	 * @param idea
	 *			  The idea mnemonic, or <code>null</code>.
	 */
	public void setIdea(final String idea) {
		if (idea != null) {
			if (!idea.equals(this.idea)) {
				put("idea", idea);
			}
		} else if (has("idea")) {
			remove("idea");
		}

		this.idea = idea;
	}

	/**
	 * Get the NPC's idea/thought.
	 * 
	 * @return The idea mnemonic, or <code>null</code>.
	 */
	public String getIdea() {
		return idea;
	}

	public void say(final String text) {
		final RPEvent rpe = new RPEvent(Events.PUBLIC_TEXT);
		rpe.put("text", text);
		this.addEvent(rpe);
		this.notifyWorldAboutChanges();
	}

	/**
	 * moves to the given entity. When the distance to the destination is
	 * between <code>min</code> and <code>max</code> and this entity does
	 * not have a path already one is searched and saved.
	 * <p>
	 * <b>Note:</b> When the distance to the destination is less than
	 * <code>min</code> the path is removed. <b>Warning:</b> The pathfinder
	 * is not asynchronous, so this thread is blocked until a path is found.
	 * 
	 * @param destEntity
	 *			  the destination entity
	 * @param min
	 *			  minimum distance to the destination entity
	 * @param max
	 *			  maximum distance to the destination entity
	 * @param maxPathRadius
	 *			  the maximum radius in which a path is searched
	 */
	public void setMovement(final Entity destEntity, final double min, final double max,
			final double maxPathRadius) {
		if (nextTo(destEntity, min)) {
			stop();

			if (hasPath()) {
				logger.debug("Removing path because nextto("
						+ destEntity.getX() + "," + destEntity.getY() + ","
						+ min + ") of (" + getX() + "," + getY() + ")");
				clearPath();
			}
		} else if ((squaredDistance(destEntity) > max)) {
			logger.debug("Creating path because (" + getX() + "," + getY()
					+ ") distance(" + destEntity.getX() + ","
					+ destEntity.getY() + ")>" + max);
			final List<Node> path = Path.searchPath(this, destEntity, maxPathRadius);
			setPath(new FixedPath(path, false));
		}
	}

	/**
	 * Set a random destination as a path.
	 * 
	 * @param distance
	 *			  The maximum axis distance to move.
	 * @param x
	 *			  The origin X coordinate for placement.
	 * @param y
	 *			  The origin Y coordinate for placement.
	 */
	public void setRandomPathFrom(final int x, final int y, final int distance) {
		final int dist2_1 = distance + distance + 1;
		final int dx = Rand.rand(dist2_1) - distance;
		final int dy = Rand.rand(dist2_1) - distance;

		final List<Node> path = new ArrayList<Node>(1);
		path.add(new Node(x + dx, y + dy));

		setPath(new FixedPath(path, false));
	}

	/**
	 * Query the range in which the NPC will notice other subjects like players or enemies.
	 * @return perception range
	 */
	public int getPerceptionRange() {
		return perceptionRange;
	}

	/**
	 * Set the perception range.
	 * @param perceptionRange
	 */
	public void setPerceptionRange(int perceptionRange) {
		this.perceptionRange = perceptionRange;
	}

	/**
	 * Query the range in which the NPC will search for movement paths.
	 * @return movement range
	 */
	public int getMovementRange() {
		return movementRange;
	}

	/**
	 * Set the movement range.
	 * @param movementRange
	 */
	public void setMovementRange(int movementRange) {
		this.movementRange = movementRange;
	}

	//
	// RPEntity
	//

	/**
	 * Returns true if this RPEntity is attackable.
	 */
	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	protected void dropItemsOn(final Corpse corpse) {
		// sub classes can implement this method
	}

	@Override
	public void logic() {
		// sub classes can implement this method
	}

}
