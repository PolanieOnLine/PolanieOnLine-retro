/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.pirate_island.ship;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.util.TimeUtil;

/**
 * This class simulates a ferry going back and forth between the mainland and
 * the island. Note that, even though this class lies in a maps package, this is
 * not a zone configurator.
 * 
 * NPCs that have to do with the ferry:
 * <li> Eliza - brings players from the mainland docks to the ferry.
 * <li>Jessica - brings players from the island docks to the ferry.
 * <li>Jackie - brings players from the ferry to the docks. Captain - the ship
 * captain.
 * <li>Laura - the ship galley maid.
 * <li>Ramon - offers blackjack on the ship.
 * 
 * 
 */
public final class PirateFerry implements TurnListener {

	/** How much it costs to board the ferry. */
	public static final int PRICE = 1000;

	/** The Singleton instance. */
	private static PirateFerry instance;

	private Status current;

	/**
	 * A list of non-player characters that get notice when the ferry arrives or
	 * departs, so that they can react accordingly, e.g. inform nearby players.
	 */
	private final List<IFerryListener> listeners;


	private PirateFerry() {
		listeners = new LinkedList<IFerryListener>();
		current = Status.ANCHORED_AT_GDANSK;
	}

	/**
	 * @return The Singleton instance.
	 */
	public static PirateFerry get() {
		if (instance == null) {
			instance = new PirateFerry();

			// initiate the turn notification cycle
			SingletonRepository.getTurnNotifier().notifyInSeconds(1, instance);

		}
		return instance;
	}

	public Status getState() {
		return current;
	}

	/**
	 * Gets a textual description of the ferry's status.
	 *
	 * @return A String representation of time remaining till next state.
	 */

	private String getRemainingSeconds() {
		final int secondsUntilNextState = SingletonRepository.getTurnNotifier()
				.getRemainingSeconds(this);
		return TimeUtil.approxTimeUntil(secondsUntilNextState);
	}

	/**
	 * Is called when the ferry has either arrived at or departed from a harbor.
	 * @param currentTurn the turn when this listener is called
	 */
	@Override
	public void onTurnReached(final int currentTurn) {
		// cycle to the next state

		current = current.next();
		for (final IFerryListener npc : listeners) {
			npc.onNewFerryState(current);
		}
		SingletonRepository.getTurnNotifier().notifyInSeconds(current.duration(), this);
	}

	public void addListener(final IFerryListener npc) {
		listeners.add(npc);
	}

	/**
	 * Auto registers the listener to PirateFerry.
	 * deregistration must be implemented if it is used for short living objects
	 * @author astridemma
	 *
	 */
	public abstract static class FerryListener implements IFerryListener {
		public FerryListener() {
			SingletonRepository.getPirateFerry().addListener(this);
		}
	}

	public interface IFerryListener {
		void onNewFerryState(Status current);
	}

	public enum Status {
		ANCHORED_AT_GDANSK {
			@Override
			Status next() {
				return DRIVING_TO_ISLAND;
			}

			@Override
			int duration() {
				return 2 * 60;
			}

			@Override
			public String toString() {
				return "Prom jest zakotwiczony w Gdańsku. Zajmie to "
						+ SingletonRepository.getPirateFerry().getRemainingSeconds() + ".";
			}
		},

		DRIVING_TO_ISLAND {
			@Override
			Status next() {
				return ANCHORED_AT_ISLAND;
			}

			@Override
			int duration() {
				return 5 * 60;
			}

			@Override
			public String toString() {
				return "Prom płynie na piracką wyspę. Przypłynie za "
						+ SingletonRepository.getPirateFerry().getRemainingSeconds() + ".";
			}
		},

		ANCHORED_AT_ISLAND {
			@Override
			Status next() {
				return DRIVING_TO_GDANSK;
			}

			@Override
			int duration() {
				return 2 * 60;
			}

			@Override
			public String toString() {
				return "Prom jest zakotwiczony na pirackiej wyspie. Zajmie to "
						+ SingletonRepository.getPirateFerry().getRemainingSeconds() + ".";
			}

		},

		DRIVING_TO_GDANSK {
			@Override
			Status next() {
				return ANCHORED_AT_GDANSK;
			}

			@Override
			int duration() {
				return 5 * 60;
			}

			@Override
			public String toString() {
				return "Prom płynie do Gdańska. Przypłynie za "
						+ SingletonRepository.getPirateFerry().getRemainingSeconds() + ".";
			}
		};

		/**
		 * gives the following status.
		 * @return the next Status
		 */
		abstract Status next();

		/**
		 * how long will this state last.
		 * @return time in seconds;
		 */
		abstract int duration();
	}
}
