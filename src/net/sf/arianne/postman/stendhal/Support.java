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
package net.sf.arianne.postman.stendhal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.arianne.postman.event.EventType;
import net.sf.arianne.postman.irc.EventHandler;
import net.sf.arianne.postman.irc.PostmanIRC;


/**
 * Support Answer
 *
 * @author hendrik
 */
public class Support extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;
	
	/** support message */
	static final Pattern patternSupportAnswer = Pattern.compile("(?i)^support (.*)$");

	/**
	 * creates a support answer
	 *
	 * @param command    command
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public Support(String command, String sender, PostmanIRC postmanIRC) {
		this.command = command;
		this.sender = sender;
		this.postmanIRC = postmanIRC;
	}

	@Override
	public void fire(EventType eventType, String eventDetail, String ircAccountName) {
		String charname = extractGameAccount(postmanIRC, sender, ircAccountName);
		if (charname == null) {
			return;
		}
		Matcher matcher = patternSupportAnswer.matcher(command);
		if (matcher.find()) {
			String message = matcher.group(1);
			Postman.get().support(charname, message);
		}
	}

}