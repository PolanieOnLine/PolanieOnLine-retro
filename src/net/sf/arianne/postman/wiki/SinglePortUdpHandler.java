package net.sf.arianne.postman.wiki;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import net.sf.arianne.postman.irc.PostmanIRC;

import org.apache.log4j.Logger;

/**
 * handler for prefixed udp packets
 *
 * @author hendrik
 */
public class SinglePortUdpHandler {

	/** logger */
	protected static Logger logger = Logger.getLogger(SinglePortUdpHandler.class);
	private final PostmanIRC postmanIRC;
	private final int port;

	/**
	 * udp handler
	 *
	 * @param postmanIRC postman irc bot
	 * @param port port
	 */
	public SinglePortUdpHandler(PostmanIRC postmanIRC, int port) {
		this.postmanIRC = postmanIRC;
		this.port = port;
	}

	/**
	 * listens for udp messages
	 */
	public void listen() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					DatagramSocket serverSocket = new DatagramSocket(port);
					byte[] data = new byte[1024];
					while (true) {
						DatagramPacket receivePacket = new DatagramPacket(data, data.length);
						serverSocket.receive(receivePacket);
						String line = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), "UTF-8");
						String text = line;
						if (!text.contains("Special:Log/newusers") && !text.contains("create") && !text.contains("User account")) {
							postmanIRC.sendMessageToUdpChannels(Integer.toString(port), text);
					    }
					}
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		}, "UdpHandler");
		t.setDaemon(true);
		t.start();
	}

}
