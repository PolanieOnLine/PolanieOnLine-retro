/* $Id: CounterMap.java,v 1.3 2010/12/04 14:08:37 nhnb Exp $ */
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
package net.sf.arianne.postman.irc;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * counts how often objects are added
 *
 * @author hendrik
 * @param <T> type of object
 */
public class CounterMap<T> {
	private Map<T, Integer> map = new HashMap<T, Integer>();

	/**
	 * creates a new counter map.
	 */
	public CounterMap() {
		this(false);
	}

	/**
	 * creates a new counter map.
	 *
	 * @param weak <code>true</code> to create a weak key map, 
	 *             <code>false</code> to create a normal map
	 */
	public CounterMap(boolean weak) {
		if (weak) {
			map = new WeakHashMap<T, Integer>();
		} else {
			map = new HashMap<T, Integer>();
		}
	}

	/**
	 * clears the counting and starts at 0 again.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * increments the counter of the specified object
	 *
	 * @param object object to count
	 */
	public void add(T object) {
		add(object, 1);
	}

	/**
	 * increments the counter of the specified object
	 *
	 * @param object object to count
	 * @param inc the amount to increment
	 */
	public void add(T object, int inc) {
		Integer current = map.get(object);
		if (current == null) {
			current = Integer.valueOf(inc);
		} else {
			current = Integer.valueOf(current.intValue() + inc);
		}
		map.put(object, current);
	}

	/**
	 * gets the number the specified object was coutned
	 *
	 * @param object object
	 * @return number
	 */
	public int getCount(T object) {
		Integer current = map.get(object);
		if (current == null) {
			return 0;
		} else {
			return current.intValue();
		}
	}

	/**
	 * gets the object with the highest amount
	 *
	 * @return object with highest amount
	 */
	public T getHighestCountedObject() {
		Map.Entry<T, Integer> highestEntry = null;
		for (Map.Entry<T, Integer> entry : map.entrySet()) {
			if (highestEntry == null) {
				highestEntry = entry;
			} else {
				if (highestEntry.getValue().compareTo(entry.getValue()) < 0) {
					highestEntry = entry;
				}
			}
		}

		if (highestEntry == null) {
			return null;
		} else {
			return highestEntry.getKey();
		}
	}
}
