/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import java.util.Date;
import java.util.StringTokenizer;

import marauroa.client.ClientFramework;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import net.sf.arianne.postman.irc.PostmanIRC;

import org.apache.log4j.Logger;

/**
 * Postman.
 *
 * @author hendrik
 */
public class Postman {
	private static final String Y_COORD = "85";
	private static final String X_COORD = "112";
	private static final String POSTMAN_ZONE = "0_semos_plains_n";

	private static final String GREETING = "Cześć, jestem listonoszem. Jak mogę ci pomóc?";
	private static final String INTRO = "Przechowuję wiadomości dla graczy offline i dostarczam je po zalogowaniu.\n";
	private static final String HELP_MESSAGE = "Użyj:\n/msg postman help \t Komunikat pomocy\n/msg postman tell #player #message \t Dostarczę twoją #wiadomość, gdy #wojownik się zaloguje.";

	private static Logger logger = Logger.getLogger(Postman.class);
	private final ClientFramework clientManager;
	private final PostmanIRC postmanIRC;
	private String lastShout;
	private static Postman instance;

	/**
	 * Creates a new postman.
	 *
	 * @param clientManager
	 *            ClientManager
	 * @param postmanIRC
	 *            postmanIRC
	 */
	public Postman(final ClientFramework clientManager, final PostmanIRC postmanIRC) {
		this.clientManager = clientManager;
		this.postmanIRC = postmanIRC;
		set(this);
	}


	/**
	 * Processes a talk event.
	 *
	 * @param object
	 *            the talking person
	 */
	public void processPublicTalkEvent(final RPObject object) {

		try {
			if (object == null) {
				return;
			}
			final int xdiff = object.getInt("x") - Integer.parseInt(X_COORD);
			final int ydiff = object.getInt("y") - Integer.parseInt(Y_COORD);
			if (xdiff * xdiff + ydiff * ydiff > 36) {
				logger.debug("***Postman*** object x: " + object.getInt("x") + ", object y: " + object.getInt("y")
							 + ", postman x " + Integer.parseInt(X_COORD) + ", postman y: " + Integer.parseInt(Y_COORD)
							 + ", xdiff: " + xdiff + ", ydiff:  " + ydiff);
				return;
			}

			if (object.getRPClass().getName().equals("player")
					&& object.has("name")) {
				if (object.has("text")) {
					if (!object.get("name").equals("postman")) {
						final String text = object.get("text");
						String playerName = "";
						playerName = object.get("name");

						final java.text.Format formatter = new java.text.SimpleDateFormat(
								"[HH:mm] ");
						final String dateString = formatter.format(new Date());
						System.err.println(dateString + playerName + ": "
								+ text);

						final StringTokenizer st = new StringTokenizer(text, " ");
						String cmd = "";
						if (st.hasMoreTokens()) {
							cmd = st.nextToken();
						}
						if (cmd.equalsIgnoreCase("hi")
								|| cmd.equalsIgnoreCase("hello")
								|| cmd.equalsIgnoreCase("hallo")
								|| cmd.equalsIgnoreCase("greetings")
								|| cmd.equalsIgnoreCase("hola")) {
							chat(GREETING);
						} else if (cmd.equalsIgnoreCase("bye")
								|| cmd.equalsIgnoreCase("goodbye")
								|| cmd.equalsIgnoreCase("farewell")
								|| cmd.equalsIgnoreCase("cya")
								|| cmd.equalsIgnoreCase("adios")) {
							chat("Do widzenia.");
						} else if (cmd.equalsIgnoreCase("help")
								|| cmd.equalsIgnoreCase("ayuda")
								|| cmd.equalsIgnoreCase("info")
								|| cmd.equalsIgnoreCase("job")
								|| cmd.equalsIgnoreCase("work")
								|| cmd.equalsIgnoreCase("offer")
								|| cmd.equalsIgnoreCase("letter")
								|| cmd.equalsIgnoreCase("parcel")) {
							chat(INTRO + HELP_MESSAGE);
						} else if (cmd.equalsIgnoreCase("msg")
								|| cmd.equalsIgnoreCase("tell")) {
							onTell(playerName, st);
						}
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e, e);
		}
	}

	/**
	 * Processes a talk event.
	 *
	 * @param object
	 *            RPObject
	 * @param texttype
	 *            texttype
	 * @param text
	 *            text
	 */
	public void processPrivateTalkEvent(final RPObject object, final String texttype, final String text) {

		try {
			if (object == null) {
				return;
			}
			if (object.getRPClass().getName().equals("player")
					&& object.has("name")) {
				if (object.get("name").equals("postman")) {

					final java.text.Format formatter = new java.text.SimpleDateFormat(
							"[HH:mm:ss] ");
					final String dateString = formatter.format(new Date());
					System.err.println(dateString + text);

					final StringTokenizer st = new StringTokenizer(text, " ");
					final String from = st.nextToken();

					// tells
					final String arianneCmd = st.nextToken();

					// you:
					st.nextToken();

					if (arianneCmd.equals("tells")) {
						// Command was send by a player
						// cmd
						String cmd = st.nextToken();
						if (cmd.startsWith("/")) {
							cmd = cmd.substring(1);
						}
						if (cmd.equalsIgnoreCase("tell")
								|| cmd.equalsIgnoreCase("msg")
								|| cmd.equalsIgnoreCase("/tell")
								|| cmd.equalsIgnoreCase("/msg")) {
							onTell(from, st);
						} else if (cmd.equalsIgnoreCase("hi")) {
							tell(from, GREETING);
						} else if (cmd.equalsIgnoreCase("help")
								|| cmd.equalsIgnoreCase("info")
								|| cmd.equalsIgnoreCase("job")
								|| cmd.equalsIgnoreCase("letter")
								|| cmd.equalsIgnoreCase("offer")
								|| cmd.equalsIgnoreCase("parcel")) {
							tell(from, INTRO + HELP_MESSAGE);
						} else {
							tell(from,
									"Przepraszam, nie zrozumiałem Cię. (Czy zapomniałeś o \"tell\"?)\n"
											+ HELP_MESSAGE);
						}
					} else if (text.startsWith("Administrator KRZYCZY: ")) {
						postmanIRC.sendMessageToAllStendhalChannels(text);
					} else if (text.matches("[^:]* krzyczy: .*")) {
						if (!text.equals(lastShout)) {
							postmanIRC.sendMessageToSignChannels(text);
							lastShout = text;
						}
					} else if (text.matches("[^:]* wynajął .*")) {
						// for signs
						postmanIRC.sendMessageToSignChannels(text);
					} else if (texttype.equalsIgnoreCase("support")) {
						postmanIRC.sendSupportMessage(text);
						dumpPlayerPosition();
					}
				}

			}
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e, e);
		}
	}

	private void onTell(final String from, final StringTokenizer st) {
		String param = null;
		String msg = "";
		if (st.hasMoreTokens()) {

			// player
			param = st.nextToken();
		}
		if (st.hasMoreTokens()) {
			// the rest of the message
			msg = st.nextToken("\0").trim();
		}

		final RPAction action = new RPAction();
		action.put("type", "storemessageonbehalfofplayer");
		action.put("source", from);
		action.put("target", param);
		action.put("text", msg);
		clientManager.send(action);
	}

	private void tell(final String to, final String message) {
		if (to.equals("postman")) {
			logger.warn("I am not speaking to myself: " + message);
			return;
		}
		final RPAction tell = new RPAction();
		tell.put("type", "tell");
		tell.put("target", to);
		tell.put("text", message);
		clientManager.send(tell);
	}

	private void chat(final String message) {
		final RPAction chat = new RPAction();
		chat.put("type", "chat");
		chat.put("text", message);
		clientManager.send(chat);
	}

	/**
	 * teleports postman to his favorite spot
	 */
	public void teleportPostman() {
		final RPAction teleport = new RPAction();
		teleport.put("type", "teleport");
		teleport.put("target", "postman");
		teleport.put("zone", POSTMAN_ZONE);
		teleport.put("x", X_COORD);
		teleport.put("y", Y_COORD);
		clientManager.send(teleport);
	}

	private void dumpPlayerPosition() {
		final RPAction chat = new RPAction();
		chat.put("type", "script");
		chat.put("target", "PlayerPositionMonitoring.class");
		clientManager.send(chat);
	}

	/**
	 * sets the postman instant
	 *
	 * @param postman postman
	 */
	private static void set(Postman postman) {
	    Postman.instance = postman;
	}

	/**
	 * gets the postman intance
	 *
	 * @return Postman
	 */
	public static Postman get() {
		return instance;
	}


	/**
	 * sends a support answer
	 *
	 * @param sender  sender
	 * @param target  target
	 * @param message message
	 */
	public void supportAnswer(String sender, String target, String message) {
		final RPAction tell = new RPAction();

		tell.put("type", "supportanswer");
		tell.put("sender", sender);
		tell.put("target", target);
		tell.put("text", message);
		clientManager.send(tell);
	}

	/**
	 * sends a support answer
	 *
	 * @param sender  sender
	 * @param target  target
	 * @param hours   hours
	 * @param message message
	 */
	public void ban(String sender, String target, String hours, String message) {
		final RPAction ban = new RPAction();

		ban.put("type", "ban");
		ban.put("sender", sender);
		ban.put("target", target);
		ban.put("hours", hours);
		ban.put("reason", message);
		clientManager.send(ban);
	}

	/**
	 * sends a support answer
	 *
	 * @param sender  sender
	 * @param ip      ip
	 * @param mask    mask
	 * @param message message
	 */
	public void ipban(String sender, String ip, String mask, String message) {
		final RPAction ban = new RPAction();

		ban.put("type", "ipban");
		ban.put("sender", sender);
		ban.put("ip", ip);
		ban.put("mask", mask);
		ban.put("reason", message);
		clientManager.send(ban);
	}

	/**
	 * shouts as an NPC
	 *
	 * @param charname name of character
	 * @param target  target
	 * @param message message
	 */
	public void npcShout(String charname, String target, String message) {
		final RPAction action = new RPAction();
		action.put("type", "script");
		action.put("sender", charname);
		action.put("target", "NPCShout.class");
		action.put("args", target + " " + message);
		clientManager.send(action);
	}

	/**
	 * sends a support query
	 *
	 * @param charname name of character
	 * @param message message
	 */
	public void support(String charname, String message) {
		final RPAction action = new RPAction();
		action.put("type", "support");
		action.put("sender", charname);
		action.put("text", message);
		clientManager.send(action);
	}

	/**
	 * attaches an admin note
	 *
	 * @param charname name of character
	 * @param target  target player
	 * @param message message
	 */
	public void adminNote(String charname, String target, String message) {
		final RPAction action = new RPAction();
		action.put("type", "adminnote");
		action.put("sender", charname);
		action.put("target", target);
		action.put("note", message);
		clientManager.send(action);
	}

	/**
	 * shouts as admin
	 *
	 * @param charname name of character
	 * @param message message
	 */
	public void tellAll(String charname, String message) {
		final RPAction action = new RPAction();
		action.put("type", "tellall");
		action.put("sender", charname);
		action.put("text", message);
		clientManager.send(action);
	}
}
