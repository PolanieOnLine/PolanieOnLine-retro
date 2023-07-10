package net.sf.arianne.postman.irc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class IrcLogger {
	private static final Logger logger = Logger.getLogger(IrcLogger.class);
	private Pattern archivePattern;

	private final String logdir;

	public IrcLogger(Properties conf) {
		this.logdir = conf.getProperty("logdir");
		this.archivePattern = Pattern.compile(conf.getProperty("archivePattern", "https?://[^ ]*"));
	}

	public void handleJoin(JoinEvent<PircBotX> event) {
		User user = event.getUser();
		String channel = event.getChannel().getName();
		String line = "-!- " + formatUserInfo(user) + " has joined " + channel; 
		logLine(channel, line);
	}

	public void handlePart(PartEvent<PircBotX> event) {
		User user = event.getUser();
		String channel = event.getChannel().getName();
		String line = "-!- " + formatUserInfo(user) + " has quit [" + event.getReason() + "]"; 
		logLine(channel, line);
	}
	
	public void handleQuit(Channel channel, QuitEvent<PircBotX> event) {
		User user = event.getUser();
		String line = "-!- " + formatUserInfo(user) + " has quit [" + event.getReason() + "]"; 
		logLine(channel.getName(), line);
	}
	
	public void handleNickChange(Channel channel, NickChangeEvent<PircBotX> event) {
		String line = "-!- " + event.getOldNick() + " is now known as " + event.getNewNick(); 
		logLine(channel.getName(), line);
	}

	public void handleMessage(MessageEvent<PircBotX> event) {
		String line = "< " + event.getUser().getNick() + "> " + event.getMessage(); 
		logLine(event.getChannel().getName(), line);
		handleArchiving(event.getChannel().getName(), line);
	}

	public void handleAction(ActionEvent<PircBotX> event) {
		String line = " * " + event.getUser().getNick() + " " + event.getMessage(); 
		logLine(event.getChannel().getName(), line);
		handleArchiving(event.getChannel().getName(), line);
	}
	

	public void handleSendMessage(String target, String message) {
		if (!target.startsWith("#")) {
			return;
		}
		logLine(target, message);
	}
	

	private String formatUserInfo(User user) {
		return user.getNick() + " [" + user.getLogin() + "@" + user.getHostmask() + "]";
	}
	
	private String format(int number) {
		if (number < 10) {
			return "0" + number;
		}
		return "" + number;
	}

	private void logLine(String channel, String line) {
		if (logdir == null) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		String folder = logdir + "/" + channel + "/" + calendar.get(Calendar.YEAR);
		validateFilename(folder);
		new File(folder).mkdirs();
		String filename = folder + "/" + calendar.get(Calendar.YEAR) 
			+ "-" + format(calendar.get(Calendar.MONTH) + 1) + "-" 
			+ format(calendar.get(Calendar.DAY_OF_MONTH)) + ".log";
		String message = format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + format(calendar.get(Calendar.MINUTE)) + " " + line + "\n";  
		try (FileOutputStream fo = new FileOutputStream(filename, true)) {
			fo.write(message.getBytes("UTF-8"));
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	private void validateFilename(String filename) {
		if (filename.contains("..")) {
			throw new IllegalArgumentException("Nazwa pliku nie może zawierać \"..\": " + filename);
		}
	}


	private void handleArchiving(String channel, String message) {
		List<String> urls = findUrlsForArchiving(message);
		if (urls.isEmpty()) {
			return;
		}

		Calendar calendar = Calendar.getInstance();
		String folder = "/archive/" + channel + "/" + calendar.get(Calendar.YEAR);
		validateFilename(logdir + folder);
		new File(logdir + folder).mkdirs();

		for (String url : urls) {
			String archiveFilename = folder + "/" + RandomStringUtils.random(50, true, true);
			logLine(channel, "!! archive " + url + " " + archiveFilename);
			FileDownloader downloader = new FileDownloader(url, logdir + archiveFilename);
			new Thread(downloader).start();
		}
	}

	List<String> findUrlsForArchiving(String message) {
		List<String> res = new LinkedList<String>();
		Matcher matcher = archivePattern.matcher(message);
		while (matcher.find()) {
			String url = matcher.group();
			res.add(url);
		}
		return res;
	}
}
