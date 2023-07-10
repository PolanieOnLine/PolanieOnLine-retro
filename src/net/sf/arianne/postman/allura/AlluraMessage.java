package net.sf.arianne.postman.allura;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;


/**
 * a CIA message
 *
 * @author hendrik
 */
public class AlluraMessage {
	private static final Logger logger = Logger.getLogger(AlluraMessage.class);
	private static final Pattern toolPattern = Pattern.compile(".*<([^@]*)@([^.]*)\\.([^.]*)\\.p.*>");
	private static final Pattern fromPattern = Pattern.compile(".*<([^@]*)@users\\..*>");

	private String project;
	private String tool;
	private String author;
	private String url;
	private String message;

	/**
	 * parses an email into an AlluraMessage
	 *
	 * @param subject subject-header
	 * @param to     to-header
	 * @param from   from-header
	 * @return AlluraMessage
	 */
	public static AlluraMessage parse(String subject, String to, String from) {
		AlluraMessage res = new AlluraMessage();
		Matcher matcher = toolPattern.matcher(to);
		if (!matcher.matches()) {
			logger.warn("Failed to match toolPattern on " + to);
			return null;
		}

		String item = matcher.group(1);

		// no commit notifications for now (we use cia-messages for that because it supports git-committers)
		if (item.equals("noreply")) {
			logger.info("Ignored commit message");
			return null;
		}

		res.tool = matcher.group(2);
		res.project = matcher.group(3);

		matcher = fromPattern.matcher(from);
		res.author = "<anon>";
		if (matcher.matches()) {
			res.author = matcher.group(1);
		}

		int pos = subject.indexOf("]");
		res.message = subject;
		if (pos > -1) {
			res.message = subject.substring(pos + 1).trim();
		}

		// construct the url
		Escaper escaper = UrlEscapers.urlPathSegmentEscaper();
		StringBuilder sb = new StringBuilder();
		sb.append("http://sf.net/p/");
		sb.append(escaper.escape(res.project));
		sb.append("/");
		sb.append(escaper.escape(res.tool));
		sb.append("/");
		sb.append(escaper.escape(item));
		res.url = sb.toString();

		return res;
	}

	/**
	 * gets the project name
	 *
	 * @return project name
	 */
	public String getProject() {
		return project;
	}

	/**
	 * gets the tool name
	 *
	 * @return tool name
	 */
	public String getTool() {
		return tool;
	}

	/**
	 * gets the author
	 *
	 * @return author name
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * gets the url
	 *
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * gets the message
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}


}
