/* $Id: NoSoundFacade.java,v 1.4 2012/07/13 05:56:11 nhnb Exp $ */
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
package games.stendhal.client.sound.nosound;

import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.facade.Time;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NoSoundFacade implements SoundSystemFacade {

	public void changeVolume(float volume) {
		// do nothing
	}

	public void exit() {
		// do nothing
	}

	public SoundGroup getGroup(String groupName) {
		// do nothing
		return new NoSoundGroup();
	}

	public Collection<String> getGroupNames() {
		// do nothing
		return new LinkedList<String>();
	}

	public float getVolume() {
		// do nothing
		return 0;
	}

	public void mute(boolean turnOffSound, boolean useFading, Time delay) {
		// do nothing
	}

	public void playerMoved() {
		// do nothing
	}

	public void stop(SoundHandle sound, Time fadingDuration) {
		// do nothing
	}

	public void update() {
		// do nothing
	}

	public void zoneEntered(String zoneName) {
		// do nothing
	}

	public void zoneLeft(String zoneName) {
		// do nothing
	}

	public List<String> getDeviceNames() {
		return new LinkedList<String>();
	}

}
