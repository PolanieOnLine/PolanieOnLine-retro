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
package net.sf.arianne.postman.irc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.output.OutputChannel;

import com.google.common.collect.ImmutableSortedSet;

import net.sf.arianne.postman.event.EventRaiser;
import net.sf.arianne.postman.event.EventType;

/**
 * IRC Bot for postman.
 *
 * @author hendrik
 */
public class PostmanIRC extends ListenerAdapter<PircBotX> {
	private PircBotX bot;

	private static final String STENDHAL_POSTMAN_CONF = ".stendhal-postman-conf.xml";
	private static final String STENDHAL_POSTMAN_ANSWERS = ".stendhal-postman-answers.xml";

	private static final Logger LOGGER = Logger.getLogger(PostmanIRC.class);

	/** myNick targetNick additionalData? :comment */
	// postman-bot-TEST hendrik hendrik :is logged in as
	// postman-bot-TEST hendrik :End of /WHOIS list.
	private static final Pattern patternWhoisResponse = Pattern.compile("^[^ ]* ([^ ]*) ([^ :]*) ?:.*");

	private final Set<String> channels = new TreeSet<String>();
	private final Set<String> signChannels = new TreeSet<String>();
	private final Set<String> devChannels = new TreeSet<String>();
	private final Set<String> stendhalChannels = new TreeSet<String>();
	private String supportChannel;
	private String mainChannel;
	private String chatChannel;
	private String devChannel;
	private final Properties conf = new Properties();
	private final FloodDetection floodDetection = new FloodDetection();
	private final CounterMap<String> kickedHostnames = new CounterMap<String>();
	private IrcLogger ircLogger;

	/**
	 * Creates a new PostmanIRC.
	 */
	public PostmanIRC() {
		try {
			this.conf.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_CONF));
			supportChannel = conf.getProperty("support");
			mainChannel = conf.getProperty("main");
			chatChannel = conf.getProperty("chat");
			devChannel = conf.getProperty("dev");

			channels.add(supportChannel);
			channels.add(mainChannel);
			channels.add(chatChannel);
			channels.add(devChannel);
			for (Map.Entry<Object, Object> entry : conf.entrySet()) {
				if (entry.getKey().toString().startsWith("udp-")) {
					String[] cs = entry.getValue().toString().split(",");
					for (String c : cs) {
						channels.add(c);
					}
				}
			}

			String logchannels = conf.getProperty("logchannels");
			if (logchannels != null) {
				String[] cs = logchannels.split(",");
				for (String c : cs) {
					channels.add(c);
				}
			}

			this.ircLogger = new IrcLogger(this.conf);

			signChannels.add(supportChannel);
			signChannels.add(chatChannel);

			devChannels.add(mainChannel);
			devChannels.add(devChannel);

			stendhalChannels.add(supportChannel);
			stendhalChannels.add(mainChannel);
			stendhalChannels.add(chatChannel);
		} catch (final Exception e) {
			LOGGER.error(e, e);
		}
	}

	/**
	 * Postman IRC bot.
	 *
	 * @throws IOException  in case input/output error
	 * @throws IrcException in case of an irc issue
	 * @throws InterruptedException in case of a timeout
	 */
	public void connect() throws IOException, IrcException,
			InterruptedException {
		if (!Boolean.parseBoolean(conf.getProperty("irc"))) {
			return;
		}
		final String ircserver = conf.getProperty("ircserver");
        final String nick = conf.getProperty("name");
        final String pass = conf.getProperty("pass");

		Builder<PircBotX> builder = new Configuration.Builder<PircBotX>()
			.setServer(ircserver, 6697, pass)
			.setNickservPassword(pass)
			.setSocketFactory(SSLSocketFactory.getDefault())
			.addListener(this)
			.setName(nick)
			.setLogin(conf.getProperty("login"))

			.setVersion("9.1")
			.setFinger("postman")

			.setAutoReconnect(true)
			.setAutoNickChange(true);

		for (final String channelName : channels) {
			builder.addAutoJoinChannel(channelName);
		}

		Configuration<PircBotX> configuration = builder.buildConfiguration();

		bot = new PircBotX(configuration);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					bot.startBot();
				} catch (RuntimeException e) {
					LOGGER.error(e, e);
					System.exit(1);
				} catch (IOException e) {
					LOGGER.error(e, e);
					System.exit(1);
				} catch (IrcException e) {
					LOGGER.error(e, e);
					/*
						Exception in thread "Thread-0" java.lang.RuntimeException: Cannot call shutdown twice
						        at org.pircbotx.PircBotX.shutdown(PircBotX.java:443)
						        at org.pircbotx.PircBotX.shutdown(PircBotX.java:428)
						        at org.pircbotx.PircBotX.startLineProcessing(PircBotX.java:287)
						        at org.pircbotx.PircBotX.connect(PircBotX.java:236)
						        at org.pircbotx.PircBotX.startBot(PircBotX.java:151)
						        at net.sf.arianne.postman.irc.PostmanIRC$1.run(PostmanIRC.java:176)
						        at java.base/java.lang.Thread.run(Thread.java:829)
					 */
					System.exit(1);
				}
			}
		}).start();
	}

	/**
	 * sends a message to the support channel
	 *
	 * @param text text to send
	 */
	public void sendSupportMessage(final String text) {
		sendMultilineMessage(supportChannel, text.replaceAll("Użyj #/supportanswer .*", "").trim());
	}

	/**
	 * sends a multi line message to a target (channel or nickname)
	 *
	 * @param target  target
	 * @param message message
	 */
	public final void sendMultilineMessage(String target, String message) {
		StringTokenizer st = new StringTokenizer(message, "\r\n");
		while (st.hasMoreTokens()) {
			sendMessage(target, st.nextToken());
		}
	}

	/**
	 * sends a message to all channels
	 *
	 * @param text message to send
	 */
	public void sendMessageToAllStendhalChannels(final String text) {
		for (final String channelName : stendhalChannels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * sends a message to sign channels
	 *
	 * @param text message to send
	 */
	public void sendMessageToSignChannels(String text) {
		for (final String channelName : signChannels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * sends a message to dev channels
	 *
	 * @param text message to send
	 */
	public void sendMessageToDevChannels(String text) {
		for (final String channelName : devChannels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * sends a message to udp channels
	 *
	 * @param marker channel marker
	 * @param text message to send
	 */
	public void sendMessageToUdpChannels(String marker, String text) {
		String cs = conf.getProperty("udp-" + marker);
		if (cs == null) {
			return;
		}
		for (String channel : cs.split(",")) {
			sendMultilineMessage(channel, text);
		}
	}

	/**
	 * gets the game account name associated to an irc account
	 *
	 * @param ircAccountName irc account
	 * @return game account
	 */
	String getGameUsername(String ircAccountName) {
		return conf.getProperty("ircaccount-" + ircAccountName);
	}

	@Override
	public synchronized void onMessage(MessageEvent<PircBotX> event) {
		try {
			String message = event.getMessage();
			String channel = event.getChannel().getName();
			String sender = event.getUser().getNick();
			if ((message != null) && message.startsWith("$")) {
				handleCanedResponse(channel, message);
			}
			floodDetection.add(sender, message);
			handlePossibleFlood(event);
			handlePossibleWikiLink(channel, message);
			ircLogger.handleMessage(event);
		} catch (RuntimeException e) {
			LOGGER.error(e, e);
			throw e;
		}
	}


	@Override
	public synchronized void onAction(ActionEvent<PircBotX> event) throws Exception {
		ircLogger.handleAction(event);
	}

	@Override
	public synchronized void onGenericChannelUser(GenericChannelUserEvent<PircBotX> event) throws Exception {
		super.onGenericChannelUser(event);
		if (event instanceof JoinEvent) {
			ircLogger.handleJoin((JoinEvent<PircBotX>) event);
		}
	}

	@Override
	public synchronized void onPart(PartEvent<PircBotX> event) throws Exception {
		super.onPart(event);
		ircLogger.handlePart(event);
	}

	@Override
	public synchronized void onQuit(QuitEvent<PircBotX> event) throws Exception {
		super.onQuit(event);
		for (final Channel channel: bot.getUserChannelDao().getChannels(event.getUser())) {
			ircLogger.handleQuit(channel, event);
		}
	}

	@Override
	public synchronized void onNickChange(NickChangeEvent<PircBotX> event) throws Exception {
		super.onNickChange(event);
		ImmutableSortedSet<Channel> channelList = event.getBot().getUserChannelDao().getChannels(event.getUser());
		for (Channel channel : channelList) {
			ircLogger.handleNickChange(channel, event);
		}
	}
	

	/**
	 * prints an url to a wiki article
	 *
	 * @param channel channel
	 * @param message message
	 */
	private void handlePossibleWikiLink(String channel, String message) {
		String baseUrl = conf.getProperty("wiki-" + channel);
		if (baseUrl == null) {
			return;
		}
		Pattern pattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
		Matcher matcher = pattern.matcher(message);
		StringBuilder res = new StringBuilder();
		while (matcher.find()) {
			String name = matcher.group(1);
			res.append(Colors.DARK_GRAY);
			res.append("[[");
			res.append(Colors.OLIVE);
			res.append(name);
			res.append(Colors.DARK_GRAY);
			res.append("]] ");
			res.append(Colors.DARK_BLUE);
			res.append(baseUrl + name.replace(' ', '_'));
			res.append(Colors.NORMAL);
			res.append("  ");
		}
		if (res.length() != 0) {
			sendMessage(channel, res.toString());
		}
	}

	private void handlePossibleFlood(MessageEvent<PircBotX> event) {
		User user = event.getUser();
		Channel channel = event.getChannel();

		if ((channel.getOps().contains(user) || channel.getVoices().contains(user))) {
			return;
		}
		if (user.getNick().equals(bot.getNick())) {
			return;
		}

		String sender = user.getNick();
		String hostname = user.getHostmask();

		if (floodDetection.isFlooding(sender, event.getMessage())) {
			floodDetection.clear(sender);

            String trustedSenders = getConf("trusted-senders");
            if (floodDetection.isTrustedSender(sender, trustedSenders)) {
                return;
            }
            String trustedHosts = getConf("trusted-hosts");
            if (floodDetection.isTrustedSender(hostname, trustedHosts)) {
                return;
            }

			int cnt = kickedHostnames.getCount(hostname);
			kickedHostnames.add(hostname);

			if (cnt < 2) {
				EventRaiser.get().addEventHandler(EventType.IRC_OP, channel.getName(), new IrcFloodKick(sender, this, conf.getProperty("floodkick-message", "Użyj http://pastebin.com/, aby wkleić duże ilości tekstu, proszę.")));
				bot.sendIRC().message("ChanServ", "op " + channel);
			} else {
				for (final String channelName : channels) {
					EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcFloodKick(sender, this, conf.getProperty("floodkick-message", "Użyj http://pastebin.com/, aby wkleić duże ilości tekstu, proszę.")));
					EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcFloodBan(hostname, this));
					bot.sendIRC().message("ChanServ", "op " + channelName);
				}
			}
		}
	}

	@Override
	public synchronized void onPrivateMessage(PrivateMessageEvent<PircBotX> event) {

		EventHandler handler = CommandFactory.create(event.getMessage(), event.getUser().getNick(), this);
		if (handler != null) {
			EventRaiser.get().addEventHandler(EventType.IRC_WHOIS, event.getUser().getNick(), handler);
			bot.sendRaw().rawLine("WHOIS "+ event.getUser().getNick());
		} else if (event.getMessage().equals("help")) {
			sendHelpReply(event.getUser().getNick());
		} else if (event.getMessage().startsWith("$")) {
			handleCanedResponse(event.getUser().getNick(), event.getMessage());
		}
	}

	private void sendHelpReply(String sender) {
		bot.sendIRC().message(sender, "Witam, jestem listonoszem.");
		bot.sendIRC().message(sender, "W POL dostarczam wiadomości do graczy, a na IRC pomagam administratorom PolanieOnline.");
		bot.sendIRC().message(sender, "Aha, i w 2012 zostałem awansowany do zgłaszania zmian w kodzie źródłowym i ostatnich zmian na wiki.");
		bot.sendIRC().message(sender, " ");
		listCanedResponses(sender);
		bot.sendIRC().message(sender, " ");
		bot.sendIRC().message(sender, "Polecenia administratora:");
		bot.sendIRC().message(sender, " /msg postman ban <cel> <godziny> <treść>");
		bot.sendIRC().message(sender, " /msg postman adminnote <cel> <treść>");
		bot.sendIRC().message(sender, " /msg postman ircban <ip>");
		bot.sendIRC().message(sender, " /msg postman npcshout <nazwa npc> <treść>");
		bot.sendIRC().message(sender, " /msg postman support <treść>");
		bot.sendIRC().message(sender, " /msg postman supporta <treść>");
		bot.sendIRC().message(sender, " /msg postman tellall <treść>");
	}

	@Override
	public synchronized void onServerResponse(ServerResponseEvent<PircBotX> event) {

		// 330 is confirmed account someone is logged in to
		if ((event.getCode() == 330) || (event.getCode() == ReplyConstants.RPL_ENDOFWHOIS)) {
			// group(1): nick, group(2): account or ""
			EventRaiser.get().fire(EventType.IRC_WHOIS, event.getParsedResponse().get(1), event.getParsedResponse().get(2));
		}
	}


	private void listCanedResponses(String target) {
		// load answer file (reload it every time so that it can be edited)
		Properties answers = new Properties();
		try {
			answers.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_ANSWERS));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (Object key : answers.keySet()) {
			sb.append(" " + key);
		}

		sendMessage(target, "Znam następujące wiadomości:" + sb.toString());
	}

	private long lastCommand;

	private void handleCanedResponse(String channel, String message) {
		// prevent flooding
		if (lastCommand + 5000 > System.currentTimeMillis()) {
			return;
		}

		// extract commands
		StringTokenizer st = new StringTokenizer(message);
		String command = st.nextToken();
		String user = "";
		if (st.hasMoreElements()) {
			user = " " + st.nextToken();
		}

		// load answer file (reload it every time so that it can be edited)
		Properties answers = new Properties();
		try {
			answers.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_ANSWERS));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// get the entry, do nothing if not defined
		String canedMessage = answers.getProperty(command);
		if (canedMessage == null) {
			return;
		}

		// send it as message or action to the channel
		canedMessage = String.format(canedMessage, user);
		sendMessage(channel, canedMessage);

		lastCommand = System.currentTimeMillis();
	}

	/**
	 * bans an ip-address in all channels
	 *
	 * @param address address to ban
	 */
	public void banIp(String address) {
		for (final String channelName : channels) {
			EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcBan(address, this));
			bot.sendIRC().message("ChanServ", "op " + channelName);
		}
	}

	@Override
	public synchronized void onOp(OpEvent<PircBotX> event) {
		if (event.isOp()) {
			EventRaiser.get().fire(EventType.IRC_OP, event.getChannel().getName(), null);
			bot.sendIRC().message("ChanServ", "deop " + event.getChannel().getName());
		}
	}

	/**
	 * forgets about the hosts that had been kiced for flooding
	 */
	public void flashThing() {
		kickedHostnames.clear();
		sendMessage(mainChannel, "/me patrzy w czerwony blask");
	}

	/**
	 * gets a configuration setting
	 *
	 * @param key ḱey
	 * @return String
	 */
	public String getConf(String key) {
		return conf.getProperty(key);
	}

	/**
	 * sends a message
	 *
	 * @param target target to send the message to
	 * @param message message
	 */
	public void sendMessage(String target, String message) {
		if (message != null && message.startsWith("/me")) {
			bot.sendIRC().action(target, message.replaceAll("^/me ", ""));
			ircLogger.handleSendMessage(target, " * postman " + message.replaceAll("^/me ", ""));
		} else {
			bot.sendIRC().message(target, message);
			ircLogger.handleSendMessage(target, "< postman> " + message);
		}

	}

	/**
	 * kicks a user from a channel
	 *
	 * @param channel channel to kick the user from
	 * @param user    name of user
	 * @param reason  reason for kick
	 */
	public void kick(String channel, String user, String reason) {
		OutputChannel c = bot.getUserChannelDao().getChannel(channel).send();
		c.kick(bot.getUserChannelDao().getUser(user), reason);
	}

	/**
	 * bans a user from a channel
	 *
	 * @param channel channel to ban a user from
	 * @param hostmask hostmask to ban
	 */
	public void ban(String channel, String hostmask) {
		OutputChannel c = bot.getUserChannelDao().getChannel(channel).send();
		c.ban(hostmask);
	}
}
