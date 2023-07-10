package net.sf.arianne.postman.allura;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Map;

import net.sf.arianne.postman.irc.PostmanIRC;

import org.apache.log4j.Logger;
import org.pircbotx.Colors;

import com.google.common.collect.Maps;

/**
 * listens for email messages
 *
 * @author hendrik
 */
public class AlluraHandler {
	/** logger */
	protected static Logger logger = Logger.getLogger(AlluraHandler.class);
	private PostmanIRC postmanIRC;

	/**
	 * Cia Handler
	 *
	 * @param postmanIRC postman irc bot
	 */
	public AlluraHandler(PostmanIRC postmanIRC) {
		this.postmanIRC = postmanIRC;
	}

	/**
	 * listens for cia messages
	 */
	public void listen() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ServerSocket server = new ServerSocket();
					server.bind(new InetSocketAddress("localhost", 7839));
					while (true) {
						Socket client = server.accept();
						logger.info("Got allura message");
						Thread clientThread = new Thread(new AlluraClientThread(client), "AlluraHandler");
						clientThread.setDaemon(true);
						clientThread.start();
					}
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	/**
	 * handles Cia client messages
	 */
	class AlluraClientThread implements Runnable {
		private Socket client = null;

		/**
		 * CiaClientThread
		 *
		 * @param client client
		 */
		public AlluraClientThread(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				InputStream is = client.getInputStream();
				AlluraMessage msg = read(is);
				is.close();
				client.close();
				if (msg != null) {
					String line = format(msg);
					postmanIRC.sendMessageToDevChannels(line);
				}
			} catch (IOException e) {
				logger.error(e, e);
			}
		}
		
	}

	/**
	 * processes an CIA email
	 *
	 * @param is input stream delivering the email
	 * @return IRC command
	 * @throws IOException in case of an input/output error
	 */
	public AlluraMessage read(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		try {
			Map<String, String> header = parseHeader(br);
			logger.info(header);
			return AlluraMessage.parse(header.get("subject"), header.get("to"), header.get("from"));
		} finally {
			br.close();
		}
	}


	/**
	 * parses the mail header
	 *
	 * @param br BufferedReader
	 * @throws IOException in case of an input/output error
	 */
	Map<String, String> parseHeader(BufferedReader br) throws IOException {
		Map<String, String> res = Maps.newHashMap();
		String lastHeader = "";
		String line = br.readLine();
		while ((line != null) && (!line.trim().isEmpty())) {
			if (line.startsWith(" ") || line.startsWith("\t")) {
				res.put(lastHeader, res.get(lastHeader) + line);
			} else {
				int pos = line.indexOf(":");
				if (pos < 0) {
					continue;
				}
				lastHeader = line.substring(0, pos).toLowerCase(Locale.ENGLISH);
				res.put(lastHeader, line.substring(pos + 1).trim());
			}
			line = br.readLine();
		}
		return res;
	}

//	20:33 < CIA-18> arianne_rpg: madmetzger * r464dd9c4d5bb marauroa/ (18 files in 13 dirs): Merge branch 'master' of ssh://madmetzger@arianne.git.sourceforge.net/gitroot/arianne/marauroa
//	18:15 < CIA-27> arianne_rpg: kiheru * stendhal/src/games/stendhal/client/gui/ (6 files in 2 dirs): Autoinspect containers. Removed the old, more complicated, approach

	/**
	 * formats a CiaMessage for IRC
	 *
	 * @param msg CiaMessage
	 * @return formatted irc line
	 */
	public String format(AlluraMessage msg) {
		StringBuilder res = new StringBuilder();
		addFormattedToken(res, Colors.BOLD, msg.getProject() + ": ");
		addFormattedToken(res, Colors.DARK_GREEN, msg.getAuthor());
		res.append(" * ");
		addFormattedToken(res, Colors.TEAL, msg.getTool());
		res.append(" ");
		res.append(msg.getMessage());
		res.append(" ");
		addFormattedToken(res, Colors.DARK_BLUE, msg.getUrl());
		return res.toString();
	}

	/**
	 * formats a token
	 *
	 * @param out    output buffer
	 * @param format color or format to use
	 * @param token  message
	 */
	private void addFormattedToken(StringBuilder out, String format, String token) {
		out.append(format);
		out.append(Colors.removeFormattingAndColors(token));
		out.append(Colors.NORMAL);
	}

}
