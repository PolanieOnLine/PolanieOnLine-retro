/***************************************************************************
 *                   (C) Copyright 2012 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.sf.arianne.postman.wiki;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.sf.arianne.postman.event.EventType;
import net.sf.arianne.postman.irc.EventHandler;
import net.sf.arianne.postman.irc.PostmanIRC;


/**
 * deletes a wiki page
 *
 * @author hendrik
 */
public class DeleteWikiPage extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;
	
	/** ban target hours message */
	static final Pattern patternBan = Pattern.compile("(?i)^d([^ ]*) (.*)$");

	/**
	 * creates a DeleteWikiPage handler
	 *
	 * @param command    command
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public DeleteWikiPage(String command, String sender, PostmanIRC postmanIRC) {
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
		Matcher matcher = patternBan.matcher(command);
		if (matcher.find()) {
			String wiki = matcher.group(1);
			String page = matcher.group(2);

			String wikiUrl = postmanIRC.getConf("wikiurl-" + wiki);
			String username = postmanIRC.getConf("wikiusername-" + wiki);
			String password = postmanIRC.getConf("wikipassword-" + wiki);
			String adminlist = postmanIRC.getConf("wikiadmins-" + wiki);
			if ((wikiUrl == null) || (adminlist == null)) {
				return;
			}
			Iterable<String> admins = Splitter.on(",").trimResults().split(adminlist.toLowerCase(Locale.ENGLISH));
			if (!Iterables.contains(admins, ircAccountName.toLowerCase(Locale.ENGLISH))) {
				return;
			}

			WikiBot bot = new WikiBot();
			bot.delete(wikiUrl, username, password, page, "niepowiązany spam usunięty przez " + ircAccountName);
		}
	}

}
