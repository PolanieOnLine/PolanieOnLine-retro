/* $Id: UseableEntity2DView.java,v 1.18 2012/09/01 20:17:54 kiheru Exp $ */
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
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.UseableEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

/**
 * The 2D view of a useable entity
 * 
 * @param <T> type of useable entity 
 */
class UseableEntity2DView<T extends UseableEntity> extends Entity2DView<T> {

	private ActionType action;

	/**
	 * creates a new UseableEntity2DView
	 */
	public UseableEntity2DView() {
		this.action = ActionType.USE;
	}

	/**
	 * creates a new USeableEntity2DView
	 *
	 * @param action use action
	 */
	public UseableEntity2DView(ActionType action) {
		this.action = action;
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildRepresentation(T entity) {
		final SpriteStore store = SpriteStore.get();
		Sprite sprite;
		ZoneInfo info = ZoneInfo.get();
		if (entity.getType().equals("useable_entity")) {
			if (entity.getName() == null) {
				sprite = store.getSprite(translate("signs/transparent"));
			} else {
				sprite = store.getModifiedSprite(translate("useable/" + getClassResourcePath() + "/" + entity.getName()),
						info.getZoneColor(), info.getColorMethod());
			}
		} else {
			// compatiblity with 0.86 server
			sprite = store.getModifiedSprite(translate("useable/source/" + entity.getType()),
					info.getZoneColor(), info.getColorMethod());
		}

		/*
		 * Entities are [currently] always 1x1. Extra columns are animation.
		 * Extra rows are ignored.
		 */
		final int imageWidth = sprite.getWidth();
		final int width = Math.max((int) entity.getWidth(), 1);
		final int height = Math.max((int) entity.getHeight(), 1);
		int frames = imageWidth / IGameScreen.SIZE_UNIT_PIXELS / width;
		
		// Just use the normal sprite if there are no animation frames
		int state = entity.getState();
		if (frames > 1) {
			sprite = store.getAnimatedSprite(sprite,
					0, state * IGameScreen.SIZE_UNIT_PIXELS * height,
					imageWidth / IGameScreen.SIZE_UNIT_PIXELS / width,
					IGameScreen.SIZE_UNIT_PIXELS * width,
					IGameScreen.SIZE_UNIT_PIXELS * height,
					100);
		} else {
			sprite = store.getTile(sprite, 
					0, state * IGameScreen.SIZE_UNIT_PIXELS * height, 
					IGameScreen.SIZE_UNIT_PIXELS * width,
					IGameScreen.SIZE_UNIT_PIXELS * height);
		}

		setSprite(sprite);
	}

	/**
	 * An entity was changed.
	 * 
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final T entity, final Object property) {
		super.entityChanged(entity, property);

		if ((property == IEntity.PROP_CLASS) || (property == IEntity.PROP_STATE)) {
			representationChanged = true;
		}
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 3000;
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 * 
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		if (entity.getRPObject().has("menu")) {
			list.add(entity.getRPObject().get("menu"));
		} else {
		list.add(action.getRepresentation());
		}

		super.buildActions(list);
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(action);
	}

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(ActionType at) {
		if (at == null) {
			at = action;
		}
		if (isReleased()) {
			return;
		}
		if (at.getActionCode().equals(this.action.getActionCode())) {
			at.send(at.fillTargetInfo(entity));
		} else {
			super.onAction(at);
		}
	}
}
