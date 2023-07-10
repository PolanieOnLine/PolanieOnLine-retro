/* $Id: BareBonesBrowserLaunchCommandsFactory.java,v 1.9 2011/08/04 21:22:56 nhnb Exp $ */
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
package games.stendhal.client.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Factory to create all known {@link SlashAction}s that open a specified URL in the browser
 *  
 * @author madmetzger
 */
public class BareBonesBrowserLaunchCommandsFactory {
	
	private static Map<String, String> commandsAndUrls;
	
	private static void initialize() {
		commandsAndUrls = new HashMap<String, String>();
		commandsAndUrls.put("faq", "http://www.gra.polskaonline.org/faq");
		commandsAndUrls.put("manual", "http://www.gra.polskaonline.org/wprowadzenie");
		commandsAndUrls.put("przewodnik", "http://www.gra.polskaonline.org/wprowadzenie");
		commandsAndUrls.put("rules", "http://www.gra.polskaonline.org/regulamin-gry-polskaonline-mmorpg");
		commandsAndUrls.put("regulamin", "http://www.gra.polskaonline.org/regulamin-gry-polskaonline-mmorpg");
		commandsAndUrls.put("changepassword", "http://pol.polskaonline.org/account/change-password.html");
		commandsAndUrls.put("zmieńhasło", "http://pol.polskaonline.org/account/change-password.html");
		commandsAndUrls.put("loginhistory", "http://pol.polskaonline.org/account/history.html");
		commandsAndUrls.put("historialogowania", "http://pol.polskaonline.org/account/history.html");
		commandsAndUrls.put("merge", "http://pol.polskaonline.org/account/merge.html");
		commandsAndUrls.put("połącz", "http://pol.polskaonline.org/account/merge.html");
		commandsAndUrls.put("halloffame", "http://pol.polskaonline.org/world/hall-of-fame/active_overview.html");
		commandsAndUrls.put("alejasław", "http://pol.polskaonline.org/world/hall-of-fame/active_overview.html");
	}
	
	/**
	 * creates {@link SlashAction}s for all in initialize specified values 
	 * 
	 * @return map of the created actions
	 */
	public static Map<String, SlashAction> createBrowserCommands() {
		initialize();
		Map<String, SlashAction> commandsMap = new HashMap<String, SlashAction>();
		for(Entry<String, String> entry : commandsAndUrls.entrySet()) {
			commandsMap.put(entry.getKey(), new BareBonesBrowserLaunchCommand(entry.getValue()));
		}
		return commandsMap;
	}

}
