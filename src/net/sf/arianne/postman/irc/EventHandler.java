/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.sf.arianne.postman.irc;

import net.sf.arianne.postman.event.EventType;

/**
 * event handling
 *
 * @author hendrik
 */
public abstract class EventHandler {

	/**
	 * handles an event
	 *
	 * @param eventType   type of event to listen for
	 * @param eventDetail details of the event (e. g. for IRC_OP this is the channel name)
	 * @param furtherData additional data submitted by the event source
	 */
	public abstract void fire(EventType eventType, String eventDetail, String furtherData);

	/**
	 * extracts the game account from an irc account
	 *
	 * @param postmanIRC PostmanIRC
	 * @param sender     nick name of sender to send error message to
	 * @param ircAccountName irc account name to lookup
	 * @return game account or <code>null</code>
	 */
	public String extractGameAccount(PostmanIRC postmanIRC, String sender, String ircAccountName) {
		if ((ircAccountName == null) || ircAccountName.equals("")) {
			postmanIRC.sendMessage(sender, "Proszę najpierw uwierzytelnić się w NickServ");
			return null;
		}

		String charname = postmanIRC.getGameUsername(ircAccountName);
		if ((charname == null) || charname.equals("")) {
			postmanIRC.sendMessage(sender, "Twój pseudonim nie jest powiązany z kontem administratora w grze.");
			return null;
		}
		return charname;
	}
}
