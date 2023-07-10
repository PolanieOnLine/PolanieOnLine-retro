/* $Id: SoundSystemFacade.java,v 1.1 2012/07/13 05:56:12 nhnb Exp $ */
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
package games.stendhal.client.sound.facade;

import games.stendhal.client.WorldObjects.WorldListener;

import java.util.Collection;
import java.util.List;

/**
 * this class is the interface between the game logic and the
 * sound system.
 * 
 * @author hendrik, silvio
 */
// TODO: Do not extend WorldListener
public interface SoundSystemFacade extends WorldListener {

	public void playerMoved();

	public void zoneEntered(String zoneName);

	public void zoneLeft(String zoneName);

	public void exit();

	public SoundGroup getGroup(String groupName);

	public void update();

	public void stop(SoundHandle sound, Time fadingDuration);

	public void mute(boolean turnOffSound, boolean useFading, Time delay);

	public float getVolume();

	public Collection<String> getGroupNames();

	public void changeVolume(float volume);

	public List<String> getDeviceNames();
	
}
