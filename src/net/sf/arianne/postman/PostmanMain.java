/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.sf.arianne.postman;

import marauroa.common.Log4J;
import net.sf.arianne.postman.allura.AlluraHandler;
import net.sf.arianne.postman.irc.PostmanIRC;
import net.sf.arianne.postman.scm.CiaHandler;
import net.sf.arianne.postman.stendhal.StendhalClient;
import net.sf.arianne.postman.wiki.SinglePortUdpHandler;
import net.sf.arianne.postman.wiki.UdpHandler;

import org.apache.log4j.Logger;

/**
 * Starts Postman and connect to server.
 *
 * @author hendrik
 */
public class PostmanMain {


	/**
	 * Main entry point.
	 *
	 * @param args see help
	 */
	public static void main(final String[] args) {
		Log4J.init("marauroa/server/log4j.properties");

		try {
			final PostmanIRC postmanIRC = new PostmanIRC();
			postmanIRC.connect();
			new CiaHandler(postmanIRC).listen();
			new AlluraHandler(postmanIRC).listen();
			new UdpHandler(postmanIRC).listen();
			new SinglePortUdpHandler(postmanIRC, 7840).listen();
			new SinglePortUdpHandler(postmanIRC, 7841).listen();
			StendhalClient.connect(args, postmanIRC);
		} catch (final Exception e) {
			Logger.getLogger(PostmanMain.class).error(e, e);
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

}
