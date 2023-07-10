package net.sf.arianne.postman.wiki;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * executes commands on the wiki
 *
 * @author hendrik
 */
public class WikiBot {

	/**
	 * deletes a wiki page
	 *
	 * @param wiki url to wiki
	 * @param username username of wiki account
	 * @param password password of wiki account
	 * @param page title of page
	 * @param reason reason for deletion
	 */
	public void delete(String wiki, String username, String password, String page, String reason) {
		MediaWikiBot bot = new MediaWikiBot(wiki);
		bot.login(username, password);
		bot.delete(page, reason);
	}
}
