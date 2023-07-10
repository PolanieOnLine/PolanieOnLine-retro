/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id: GuidedEntity.java,v 1.49 2012/08/13 10:19:52 yoriy Exp $
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.core.pathfinder.EntityGuide;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;

import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	
	protected double baseSpeed;

	private final EntityGuide guide = new EntityGuide();
	
	public Registrator pathnotifier = new Registrator();

	/**
	 * Create a guided entity.
	 */
	public GuidedEntity() {
		baseSpeed = 0;
		guide.guideMe(this);
	}

	/**
	 * Create a guided entity.
	 * 
	 * @param object
	 *            The source object.
	 */
	public GuidedEntity(final RPObject object) {
		super(object);
		baseSpeed = 0;
		guide.guideMe(this);
		update();
	}

	//
	// TEMP for Transition
	//

	/**
	 * Get the normal movement speed.
	 * 
	 * @return The normal speed when moving.
	 */
	public final double getBaseSpeed() {
		return this.baseSpeed;
	}

	/**
	 * Set the normal movement speed.
	 *
	 * @param bs - New normal speed for moving.
	 */
	public final void setBaseSpeed(final double bs) {
		this.baseSpeed = bs;
	}
	
	//
	// GuidedEntity
	//

	/**
	 * Set a path for this entity to follow. Any previous path is cleared and
	 * the entity starts at the first node (so the first node should be its
	 * position, of course). The speed will be set to the default for the
	 * entity.
	 * 
	 * @param path
	 *            The path.
	 */
	public final void setPath(final FixedPath path) {
		if ((path != null) && !path.isFinished()) {
			setSpeed(getBaseSpeed());
			guide.path = path;
			guide.pathPosition = 0;
			guide.followPath(this);
		} else {
			guide.clearPath();
		}
	}
	
	/**
	 * function return current entity's path.
	 * @return path
	 */
	public FixedPath getPath() {
		return guide.path;
	}

	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		guide.clearPath();

	}

	/**
	 * Determine if the entity has a path.
	 * 
	 * @return <code>true</code> if there is a path.
	 */
	public boolean hasPath() {
		return (guide.path != null);
	}


	/**
	 * Is the path a loop.
	 * @return true if running in circles
	 */
	public boolean isPathLoop() {
		if (guide.path == null) {
			return false;
		} else {
			return guide.path.isLoop();
		}
	}

	/**
	 * Get the path nodes position.
	 * @return position in path
	 */
	public int getPathPosition() {
		return guide.pathPosition;
	}

	/**
	 * Set the path nodes position.
	 * @param pathPos 
	 */
	public void setPathPosition(final int pathPos) {
		guide.pathPosition = pathPos;
	}
	
	/**
	 * Plan a new path to the old destination.
	 */
	public void reroute() {
		if (hasPath()) {
			Node node = guide.path.getDestination();
			final List<Node> path = Path.searchPath(this, node.getX(), node.getY());
		
			if (path.size() >= 1) {
				setPath(new FixedPath(path, false));
			} else {
				/*
				 * It can happen that some other entity goes to occupy the
				 * target position after the path has been planned. Just 
				 * stop if that happens and we are next to the goal. 
				 */
				clearPath();
				stop();
			}
		}
	}

	//
	// ActiveEntity
	//

	/**
	 * Apply movement and process it's reactions.
	 */
	@Override
	public void applyMovement() {
		if (hasPath()) {
			followPath();
			super.applyMovement();
			faceNext();
		} else {
			super.applyMovement();
		}
	}
	
	/**
	 * Set facing to next <code>Node</code>, if any 
	 */
	private void faceNext() {
		guide.faceNext(this);
	}

	public boolean followPath() {
		return guide.followPath(this);
	}

	public EntityGuide getGuide() {
		return guide;
	}
	
	@Override
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		super.onMoved(oldX, oldY, newX, newY);
		
		/*
		 * Adjust speed based on the resisting entities at the same coordinate.
		 */
		if (getSpeed() > 0) {
			int resistance = getLocalResistance();
			
			if ((getSpeed() < getBaseSpeed()) || (resistance != 0)) {
				setSpeed(getBaseSpeed() * (100 - resistance) / 100.0);
			}
		}
	}
	
	/**
	 * 
	 */
	public void onFinishedPath() {
		pathnotifier.setChanges();
		pathnotifier.notifyObservers();
	}
	
	/**
	 * Get resistance caused by other entities occupying the same, or part
	 * of the same space.
	 * 
	 * @return resistance
	 */
	private int getLocalResistance() {
		int resistance = 0;
		double size = getWidth() * getHeight();
		
		Rectangle2D thisArea = getArea();
		Rectangle2D otherArea;
		Rectangle2D intersect = new Rectangle2D.Double();
		for (final RPObject obj : getZone()) {
			final Entity entity = (Entity) obj;
			if (this != entity) {
				otherArea = entity.getArea();
				Rectangle2D.intersect(thisArea, otherArea, intersect);
				// skip entities far away
				if (!intersect.isEmpty()) {
					int r = getResistance(entity);
					if (r != 0) {
						/*
						 * Only count resistance by the proportion the resisting
						 * entity covers the area of this entity. Allows large
						 * monsters trample over small obstacles faster than a
						 * small one trying to run right through it.
						 */
						double part = intersect.getWidth() * intersect.getHeight() / size;
						r *= part;

						/*
						 * Add up like probabilities to avoid small resistance
						 * quickly resulting in a massive slow down.
						 */
						resistance = 100 - ((100 - resistance)) * (100 - r) / 100;
					}
				}
			}
		}
		
		return resistance;
	}

	@Override
	protected void handleObjectCollision() {
		stop();
		clearPath();
	}

	public void updateModifiedAttributes() {
		//TODO base speed does not get transfered to the client? testing showed, that speed is used at client side
	}
	
}
