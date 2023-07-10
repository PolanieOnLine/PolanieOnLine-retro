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
public class UdpHandler {

	/** logger */
	protected static Logger logger = Logger.getLogger(UdpHandler.class);
	private final PostmanIRC postmanIRC;

	/**
	 * udp handler
	 *
	 * @param postmanIRC
	 *            postman irc bot
	 */
	public UdpHandler(PostmanIRC postmanIRC) {
		this.postmanIRC = postmanIRC;
	}

	/**
	 * listens for udp messages
	 */
	public void listen() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					DatagramSocket serverSocket = new DatagramSocket(7839);
					byte[] data = new byte[1024];
					while (true) {
						DatagramPacket receivePacket = new DatagramPacket(data, data.length);
						serverSocket.receive(receivePacket);
						String line = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), "UTF-8");
						int pos = line.indexOf('/');
						if (pos > -1) {
							String marker = line.substring(0, pos);
							String text = line.substring(pos + 1);
							if (!text.contains("Special:Log/newusers") && !text.contains("create") && !text.contains("User account")) {
								postmanIRC.sendMessageToUdpChannels(marker, text);
						    }
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
