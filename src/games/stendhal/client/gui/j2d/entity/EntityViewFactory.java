/* $Id: EntityViewFactory.java,v 1.36 2012/06/10 20:08:28 kiheru Exp $ */
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

import games.stendhal.client.Triple;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 */
public class EntityViewFactory { 
	/**
	 * Log4J.
	 */
	private static final Logger LOGGER = Logger.getLogger(EntityViewFactory.class);

	private static Map<Triple<String, String, String>, Class<? extends EntityView>> viewMap = new HashMap<Triple<String, String, String>, Class<? extends EntityView>>();
	/**
	 * The shared instance.
	 */
	private static final EntityViewFactory sharedInstance = new EntityViewFactory();

	/**
	 * Create an entity view factory.
	 */
	public EntityViewFactory() {

		configure();
	}

	//
	// Entity2DViewFactory
	//

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass
	 * 
	 * @return the java class of the Entity belonging to type and eclass
	 */
	public static Class<? extends EntityView> getClass(final String type, final String eclass,
			final String subClass) {
		Class<? extends EntityView> result = viewMap.get(new Triple<String, String, String>(type,
				eclass, subClass));
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, eclass, null));
		}
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, null, null));
		}
		return result;
	}
	
	/**
	 * Create an entity view from an entity.
	 * 
	 * @param entity
	 *            An entity.
	 * 
	 * @return The corresponding view, or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public static EntityView<IEntity> create(final IEntity entity) {

		try {
			// use User2DView for the active player
			if (entity.isUser()) {
				Entity2DView<IEntity> user2DView = new User2DView();
				user2DView.initialize(entity);
				return user2DView;
			}

			final String type = entity.getType();
		
			// lookup class
			String eclass = entity.getEntityClass();
			String subClass = entity.getEntitySubclass();
			final Class<? extends EntityView> entityClass = getViewClass(type, eclass, subClass);
			if (entityClass == null) {
				LOGGER.debug("No view for this entity. type: " + type + " class: " + eclass
						+ " subclass: " + subClass);
				return null;
			}

			// hack to hide blood
			if (entityClass == Blood2DView.class) {
				boolean showBlood = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("gamescreen.blood", "true"));
				if (!showBlood) {
					return null;
				}
			}

			final EntityView<IEntity> en = entityClass.newInstance();
			en.initialize(entity);

			return en;
		} catch (final Exception e) {
			LOGGER.error("Error creating entity for object: " + entity, e);
			return null;
		}
		
	}

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass
	 * 
	 * @return the java class of the Entity belonging to type and eclass
	 */
	public static Class<? extends EntityView> getViewClass(final String type, final String eclass,
			final String subClass) {
		Class<? extends EntityView> result = viewMap.get(new Triple<String, String, String>(type,
				eclass, subClass));
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, eclass, null));
		}
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, null, null));
		}
		return result;
	}

	/**
	 * Configure the view map.
	 */
	protected void configure() {
		
		register("blood", null, null, Blood2DView.class);
		register("creature", "ent", null, BossCreature2DView.class);
		
		register("item", "box", null, Box2DView.class);
		
		register("growing_entity_spawner", "items/grower/wood_grower", null, CarrotGrower2DView.class);
		register("growing_entity_spawner", "items/grower/carrot_grower", null, CarrotGrower2DView.class);
		register("chest", null, null, Chest2DView.class);
		register("corpse", null, null, Corpse2DView.class);
		
		register("creature", null, null, Creature2DView.class);
		
		register("door", null, null, Door2DView.class);
	
		register("fire", null, null, UseableEntity2DView.class);
		register("fish_source", null, null, FishSource2DView.class);
		register("source_ametyst", null, null, SourceAmetyst2DView.class);
		register("source_carbuncle", null, null, SourceCarbuncle2DView.class);
		register("source_emerald", null, null, SourceEmerald2DView.class);
		register("source_gold", null, null, SourceGold2DView.class);
		register("source_iron", null, null, SourceIron2DView.class);
		register("source_mithril", null, null, SourceMithril2DView.class);
		register("source_obsidian", null, null, SourceObsidian2DView.class);
		register("source_salt", null, null, SourceSalt2DView.class);
		register("source_sapphire", null, null, SourceSapphire2DView.class);
		register("source_silver", null, null, SourceSilver2DView.class);
		register("source_sulfur", null, null, SourceSulfur2DView.class);
		register("wood_source", null, null, WoodSource2DView.class);

		register("game_board", null, null, GameBoard2DView.class);
		register("gate", null, null, Gate2DView.class);
		
		register("gold_source", null, null, GoldSource2DView.class);
		
		register("growing_entity_spawner", null, null, GrainField2DView.class);
		
		register("house_portal", null, null, HousePortal2DView.class);
		
		register("area", null, null,  InvisibleEntity2DView.class);
		
		register("item", "special", "mithril clasp", Item2DView.class);
		register("item", null, null,  Item2DView.class);
		register("npc", null, null, NPC2DView.class);
		
		register("cat", null, null, Pet2DView.class);
		register("owczarek", null, null, Pet2DView.class);
		register("owczarek_podhalanski", null, null, Pet2DView.class);
		register("pet", null, null, Pet2DView.class);
		register("baby_dragon", null, null, Pet2DView.class);
		
		register("plant_grower", null, null, PlantGrower2DView.class);
		
		register("player", null, null, Player2DView.class);
		
		register("portal", null, null, Portal2DView.class);
		
		register("item", "ring", "emerald-ring", Ring2DView.class);
		
		register("sheep", null, null,  Sheep2DView.class);
		register("food", null, null, SheepFood2DView.class);
		register("spell", null, null, Spell2DView.class);
		
		register("sign", null, null,  Sign2DView.class);
		register("blackboard", null, null,  Sign2DView.class);
		register("rented_sign", null, null, Sign2DView.class);
		register("shop_sign", null, null, ShopSign2DView.class);
		register("tradecentersign", null, null, TradeCenterSign2DView.class);
		
		register("item", "jewellery", null,  StackableItem2DView.class);
		register("item", "flower", null,  StackableItem2DView.class);
		register("item", "resource", null,  StackableItem2DView.class);
		register("item", "herb", null, StackableItem2DView.class);
		register("item", "misc", null,  StackableItem2DView.class);
		register("item", "money", null,  StackableItem2DView.class);
		register("item", "missile", null,  StackableItem2DView.class);
		register("item", "ammunition", null,  StackableItem2DView.class);
		register("item", "magia", null,  StackableItem2DView.class);
		register("item", "container", null,  StackableItem2DView.class);
        register("item", "special", null,  StackableItem2DView.class);
		
		register("item", "club", "wizard_staff",  UseableItem2DView.class);
        register("item", "misc", "seed", UseableItem2DView.class);
        register("item", "misc", "bulb", UseableItem2DView.class);

		register("item", "scroll", null,  UseableItem2DView.class);

		register("item", "food", null, UseableItem2DView.class);
		register("item", "drink", null,  UseableItem2DView.class);
		register("item", "tool", "foodmill",  UseableItem2DView.class);
		register("item", "tool", "sugarmill",  UseableItem2DView.class);
		register("item", "tool", "scrolleraser",  UseableItem2DView.class);
		
		register("item", "ring", null, UseableRing2DView.class);
		
		register("useable_entity", null, null, UseableEntity2DView.class);

		register("walkblocker", null, null, WalkBlocker2DView.class);
		register("well_source", null, null, WellSource2DView.class);
	}

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass 
	 * @param entityClazz
	 *            the java class of the Entity
	 */
	private static void register(final String type, final String eclass, final String subClass,
			final Class<? extends EntityView> entityClazz) {
		viewMap.put(new Triple<String, String, String>(type, eclass, subClass), entityClazz);
	}

	/**
	 * Get the shared [singleton] instance.
	 * 
	 * @return the singleton instance
	 */
	public static EntityViewFactory get() {
		return sharedInstance;
	}
}
