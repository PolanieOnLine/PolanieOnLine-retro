/* $Id: PerceptionDispatcher.java,v 1.2 2010/09/19 02:17:49 nhnb Exp $ */
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
package games.stendhal.client;

import java.util.LinkedList;
import java.util.List;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public class PerceptionDispatcher implements IPerceptionListener {

	private final List<IPerceptionListener> listenerList = new LinkedList<IPerceptionListener>();

	public boolean onAdded(final RPObject object) {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			l.onAdded(object);

		}
		return returnValue;
	}

	public boolean onClear() {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			 l.onClear();

		}
		return returnValue;
	}

	public boolean onDeleted(final RPObject object) {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			 l.onDeleted(object);

		}
		return returnValue;
	}

	public void onException(final Exception exception,
			final MessageS2CPerception perception) {
		for (final IPerceptionListener l : listenerList) {
			l.onException(exception, perception);

		}

	}

	public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			returnValue |= l.onModifiedAdded(object, changes);

		}
		return returnValue;
	}

	public boolean onModifiedDeleted(final RPObject object,
			final RPObject changes) {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			returnValue |= l.onModifiedDeleted(object, changes);

		}
		return returnValue;
	}

	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		boolean returnValue = false;
		for (final IPerceptionListener l : listenerList) {
			returnValue |= l.onMyRPObject(added, deleted);

		}
		return returnValue;
	}

	public void onPerceptionBegin(final byte type, final int timestamp) {
		for (final IPerceptionListener l : listenerList) {
			l.onPerceptionBegin(type, timestamp);

		}

	}

	public void onPerceptionEnd(final byte type, final int timestamp) {
		for (final IPerceptionListener l : listenerList) {
			l.onPerceptionEnd(type, timestamp);

		}

	}

	public void onSynced() {
		for (final IPerceptionListener l : listenerList) {
			l.onSynced();

		}

	}

	public void onUnsynced() {
		for (final IPerceptionListener l : listenerList) {
			l.onUnsynced();

		}

	}

	public void register(final IPerceptionListener listener) {
		listenerList.add(listener);

	}

	public void unregister(final IPerceptionListener listener) {
		listenerList.remove(listener);

	}

}
