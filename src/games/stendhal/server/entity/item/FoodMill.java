/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPObject;

public class FoodMill extends Item {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(RPEntity.class);

	/** The item to be processed */
	private String input;
	/** The required container for processing */
	private String container;
	/** The resulting processed item */
	private String output;
	/** Items that do not require a "container". */
	private final List<String> containerNotRequired = new ArrayList<String>() {{
		add("zwój czyszczący");
		add("obrotowy nożyk");
	}};

    public FoodMill(final String name, final String clazz,
            final String subclass, final Map<String, String> attributes) {
        super(name, clazz, subclass, attributes);
        init();
    }

    public FoodMill(final FoodMill item) {
        super(item);
        init();
    }

    /** Sets up the input, output and container based on item name */
    private void init() {
    	final String tool = getName();

    	if ("młynek do cukru".equals(tool)) {
    		input = "trzcina cukrowa";
    		container = "pusty worek";
    		output = "cukier";
    	} else if ("zwój czyszczący".equals(tool)) {
    		input = "zwój zapisany";
    		output = "niezapisany zwój";
    	} else if ("obrotowy nożyk".equals(tool)) {
    		input = "skóra zwierzęca";
    		output = "skórzana nić";
    	} else {
    		input = "jabłko";
    		container = "buteleczka";
    		output = "sok jabłkowy";
    	}
    }

    @Override
	public boolean onUsed(final RPEntity user) {
    	final String tool = getName();
    	final boolean containerRequired = !containerNotRequired.contains(tool);

    	/* Items/Tools not listed in "containerNotRequired" must have a "container" defined. */
    	if (containerRequired && container == null) {
    		logger.error("Input \"" + input + "\" requires a container, but container value is null.");
    		return false;
    	}

    	/* is the mill equipped at all? */
    	if (!isContained()) {
    		user.sendPrivateText("Powinieneś mieć " + tool + ", aby móc go użyć.");
    		return false;
    	}

    	final String slotName = getContainerSlot().getName();

    	/* is it in a hand? */
    	if (!slotName.endsWith("hand")) {
    		user.sendPrivateText("Powinieneś trzymać " + tool + " w drugiej ręce, aby móc go użyć.");
    		return false;
    	}

    	final String otherhand = getOtherHand(slotName);

    	final RPObject first = user.getSlot(otherhand).getFirst();

    	/* is anything in the other hand? */
    	if (first == null) {
    		user.sendPrivateText("Twoja druga ręka wygląda na pustą.");
    		return false;
    	}

    	/*
    	 * the player needs to equip at least the input in his other hand
    	 * and have the correct container in his inventory
    	 */
    	if (!input.equals(first.get("name"))) {
    		user.sendPrivateText("Musisz mieć conajmniej " + input + " w drugiej dłoni.");
    		return false;
    	}

    	if (containerRequired && !user.isEquipped(container)) {
    		user.sendPrivateText("Nie masz " + container + " ze sobą.");
    		return false;
    	}

        /* all is okay, lets process this item */
    	final Item item = SingletonRepository.getEntityManager().getItem(output);

    	if (first instanceof StackableItem) {
			StackableItem dropOneOfMe = (StackableItem) first;
			dropOneOfMe.removeOne();
		} else {
			user.drop((Item) first);
		}

    	if (containerRequired) {
    		user.drop(container);
    	}
    	if ("obrotowy nożyk".equals(tool)) {

    		final StackableItem stackable = (StackableItem) item;
    		stackable.setQuantity(5);

    		user.equipOrPutOnGround(stackable);
    	} else {
    		user.equipOrPutOnGround(item);
    	}

    	return true;
    }

    /**
     * @param handSlot should be rhand or lhand
     * @return the opposite hand to handSlot
     */
    private String getOtherHand(final String handSlot) {
        if ("rhand".equals(handSlot)) {
            return "lhand";
        } else {
            return "rhand";
        }
    }
}
