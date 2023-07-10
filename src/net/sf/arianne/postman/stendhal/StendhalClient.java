package net.sf.arianne.postman.stendhal;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;
import net.sf.arianne.postman.irc.PostmanIRC;

import org.apache.log4j.Logger;

/**
 * a stendhal client
 *
 * @author hendrik
 */
public class StendhalClient implements Runnable {
	private static final Logger logger = Logger.getLogger(StendhalClient.class);

	/** name of character */
	protected String character;

	/** instance of postman client */
	protected Postman postman;

	/** timestamp of last received perception message, used for timing out */
	protected long lastPerceptionTimestamp;

	/** objects in the zone */
	protected Map<RPObject.ID, RPObject> world_objects;

	/** client framework */
	protected marauroa.client.ClientFramework clientManager;

	/** perception handler */
	protected PerceptionHandler handler;

	private final String host;

	private final String username;

	private final String password;

	private final PostmanIRC postmanIRC;

	private final String port;

	/**
	 * Creates a PostmanMain.
	 *
	 * @param h
	 *            host
	 * @param u
	 *            user name
	 * @param p
	 *            password
	 * @param c
	 *            character name
	 * @param P
	 *            port
	 * @param postmanIRC
	 *            postmanIRC
	 * @throws SocketException
	 *             on an network error
	 */
	public StendhalClient(final String h, final String u, final String p,
			final String c, final String P, PostmanIRC postmanIRC)
			throws SocketException {
		host = h;
		username = u;
		password = p;
		character = c;
		port = P;
		this.postmanIRC = postmanIRC;

		world_objects = new HashMap<RPObject.ID, RPObject>();

		handler = new PerceptionHandler(new PerceptionErrorListener());

		clientManager = new marauroa.client.ClientFramework(
				"games/stendhal/log4j.properties") {

			@Override
			protected String getGameName() {
				return "polanieonline";
			}

			@Override
			protected String getVersionNumber() {
				return "bot";
			}

			@Override
			protected void onPerception(final MessageS2CPerception message) {
				lastPerceptionTimestamp = System.currentTimeMillis();
				try {
					handler.apply(message, world_objects);
					for (final RPObject object : world_objects.values()) {
						for (final RPEvent event : object.events()) {
							if (event.getName().equals("private_text")) {
								postman.processPrivateTalkEvent(object,
										event.get("texttype"),
										event.get("text"));
							}
						}
						if (object.has("text")) {
							postman.processPublicTalkEvent(object);
						}
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected List<TransferContent> onTransferREQ(
					final List<TransferContent> items) {
				for (final TransferContent item : items) {
					item.ack = true;
				}

				return items;
			}

			@Override
			protected void onServerInfo(final String[] info) {
				// do nothing
			}

			@Override
			protected void onAvailableCharacters(final String[] characters) {
				try {
					chooseCharacter(character);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onTransfer(final List<TransferContent> items) {
				// do nothing
			}

			@Override
			protected void onPreviousLogins(final List<String> previousLogins) {
				// do nothing
			}
		};
	}

	@Override
	public void run() {
		while (true) {
			boolean keepRunning = false;
			try {
				clientManager.connect(host, Integer.parseInt(port));
				clientManager.login(username, password);
				postman = new Postman(clientManager, postmanIRC);
				postman.teleportPostman();
				keepRunning = true;
			} catch (final SocketException e) {
				logger.warn("Socket Exception");
			} catch (final TimeoutException e) {
				logger.warn("Nie można połączyć się z serwerem PolanieOnLine. Serwer padł?");
			} catch (final Exception e) {
				logger.error(e, e);
			}

			if (keepRunning) {
				gameLoop();
				logger.warn("Closing polanieonline client");
				clientManager.close();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

    private void gameLoop() {
        lastPerceptionTimestamp = 0;
        while (true) {
        	clientManager.loop(0);

        	if ((lastPerceptionTimestamp > 0)
        			&& (lastPerceptionTimestamp + 30 * 1000 < System.currentTimeMillis())) {
        		logger.warn("timeout");
        		break;
        	}
        	if (!clientManager.getConnectionState()) {
        		logger.warn("lost connection");
        		break;
        	}

        	try {
        		Thread.sleep(200);
        	} catch (final InterruptedException e) {
        		// ignore
        	}
        }
    }

	/**
	 * connect to the server
	 *
	 * @param args
	 * @param postmanIRC
	 * @throws SocketException
	 */
	public static void connect(final String[] args, final PostmanIRC postmanIRC) throws SocketException {
		if (args.length > 0) {
			int i = 0;
			String username = null;
			String password = null;
			String character = null;
			String host = null;
			String port = null;

			while (i != args.length) {
				if (args[i].equals("-u")) {
					username = args[i + 1];
				} else if (args[i].equals("-p")) {
					password = args[i + 1];
				} else if (args[i].equals("-c")) {
					character = args[i + 1];
				} else if (args[i].equals("-h")) {
					host = args[i + 1];
				} else if (args[i].equals("-P")) {
					port = args[i + 1];
				}
				i++;
			}

			if ((username != null) && (password != null) && (character != null)
					&& (host != null) && (port != null)) {
				final Thread client = new Thread(new StendhalClient(host,
						username, password, character, port, postmanIRC), "Wątek Klienta PolanieOnLine");
				client.start();
				return;
			}
		}

		System.out.println("PolanieOnLine textClient");
		System.out.println();
		System.out.println("  games.stendhal.bot.PostmanMain -u username -p pass -h host -P port -c character");
		System.out.println();
		System.out.println("Wymagane parametry");
		System.out.println("* -h\tHost, na którym działa serwer Marauroa");
		System.out.println("* -P\tPort, na którym działa serwer Marauroa");
		System.out.println("* -u\tNazwa użytkownika, aby zalogować się do serwera Marauroa");
		System.out.println("* -p\tHasło do logowania do serwera Marauroa");
		System.out.println("* -c\tPostać używana do logowania na serwer Marauroa");
		System.out.println("Parametry opcjonalne");
		System.out.println("* -t\tużyj połączenia tcp z serwerem");
	}
}
