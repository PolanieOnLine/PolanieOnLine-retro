/*
 * $Id: LastMinuteScroll.java,v 1.9 2008/08/27 21:27:25 astridemma Exp $
 */
package games.stendhal.server.entity.item.scroll;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Represents the last minute that takes the player to the desert world zone,
 * after which it will teleport player to a random location in 0_zakopane_c.
 */
public class LastMinuteScroll extends TimedTeleportScroll {

	/**
	 * Creates a new timed marked LastMinuteScroll scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public LastMinuteScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public LastMinuteScroll(final LastMinuteScroll item) {
		super(item);
	}
	
	@Override
	protected boolean useTeleportScroll(final Player player) {
		return super.useTeleportScroll(player);
	}
	
	@Override
	protected String getBeforeReturnMessage() {
		return "Zaczynasz odczuwać pragnie...";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "Znalazłeś się w lesie wyczerpany i odwodniony."
				+ " Nigdy nie czułeś, aż tak wielkiego pragnienia napicia się choć kropli wody.";
	}
}
