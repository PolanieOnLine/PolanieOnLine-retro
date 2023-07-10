/* $Id: StoreMessageAction.java,v 1.6 2012/09/13 21:24:00 nhnb Exp $ */
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
package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.validator.ActionAttributesExist;
import games.stendhal.server.actions.validator.ActionValidation;
import games.stendhal.server.actions.validator.StandardActionValidations;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * Stores a message to another player for postman to deliver
 */
public class StoreMessageAction implements ActionListener, TurnListener {

	/** For identifying the results of this command */
	private final ResultHandle handle = new ResultHandle();

	private ActionValidation validation = new ActionValidation();

	/**
	 * registers "store message" action processor.
	 */
	public static void register() {
		CommandCenter.register("storemessage", new StoreMessageAction());
	}

	/**
	 * Stores a message to another player for postman to deliver
	 */
	public StoreMessageAction() {
		validation.add(new ActionAttributesExist(TARGET));
		validation.add(StandardActionValidations.CHAT);
	}

	/**
	 * Sends the command to store the message and starts waiting for the results.
	 *
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (!validation.validateAndInformPlayer(player, action)) {
			return;
		}

		String message = action.get(TEXT);

		DBCommand command = new StoreMessageCommand(player.getName(), action.get(TARGET), message, "P");
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
	}

	/**
	 * Completes handling the store message action, and
	 * Notifies the player who sent the message of the outcome
	 *
	 * @param currentTurn ignored
	 */
	public void onTurnReached(int currentTurn) {
		StoreMessageCommand checkcommand = DBCommandQueue.get().getOneResult(StoreMessageCommand.class, handle);

		if (checkcommand == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}

		boolean characterExists = checkcommand.targetCharacterExists();
		String source = checkcommand.getSource();
		String target = checkcommand.getTarget();

		final Player sourceplayer = SingletonRepository.getRuleProcessor().getPlayer(source);


		if (sourceplayer != null) {
			// incase source player logged out while waiting we want to avoid NPE
			if(characterExists) {
				sourceplayer.sendPrivateText("postman powiedział Tobie: Wiadomość została zaakceptowana do dostarczenia");
			} else {
				sourceplayer.sendPrivateText(NotificationType.ERROR, "postman powiedział Tobie: Wojownik " + target + " nie został znaleziony z związku z tym twoja wiadomość nie zostanie dostarczona.");
			}
			sourceplayer.setLastPrivateChatter("postman");
		}

		return;
	}

}
