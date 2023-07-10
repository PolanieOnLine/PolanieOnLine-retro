/* $Id: VolumeAction.java,v 1.9 2012/07/13 05:56:12 nhnb Exp $ */
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
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.common.NotificationType;
import games.stendhal.common.math.Numeric;

/**
 * Set sound characteristics.
 */
class VolumeAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		if (params[0] == null) {
			float volume = ClientSingletonRepository.getSound().getVolume();
			UserInterface ui = ClientSingletonRepository.getUserInterface();
			ui.addEventLine(new StandardEventLine("Użyj /volume <nazwa> <wartość>, aby regulować poziom głośności."));
			ui.addEventLine(new HeaderLessEventLine("<nazwa> jest to pozycja z poniższej listy. \"master\" przedstawia głobalne ustawienia głośności.", NotificationType.CLIENT));
			ui.addEventLine(new HeaderLessEventLine("<wartość> jest z zakresu od 0 do 100, ale może być ustawiona wyższa.", NotificationType.CLIENT));
			ui.addEventLine(new HeaderLessEventLine("master -> " + Numeric.floatToInt(volume, 100.0f), NotificationType.CLIENT));

			for (String name : ClientSingletonRepository.getSound().getGroupNames()) {
				volume = ClientSingletonRepository.getSound().getGroup(name).getVolume();
				ui.addEventLine(new HeaderLessEventLine(name + " -> " + Numeric.floatToInt(volume, 100.0f), NotificationType.CLIENT));
			}
		} else if (params[1] != null) {
			changeVolume(params[0], params[1]);
		} else {
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine("Użyj /volume dla pomocy.",
					NotificationType.ERROR));
		}
		return true;
	}

	/**
	 * changes the volume for the specified group
	 *
	 * @param groupName name of group
	 * @param volumeString new volume
	 */
	private void changeVolume(String groupName, String volumeString) {
		try {
			boolean groupExists = false;

			for (String name : ClientSingletonRepository.getSound().getGroupNames()) {
				if (name.equals(groupName)) {
					groupExists = true;
					break;
				}
			}

			if (groupExists) {
				int volume = Integer.parseInt(volumeString);
				SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);
				group.changeVolume(Numeric.intToFloat(volume, 100.0f));
				WtWindowManager.getInstance().setProperty("sound.volume." + groupName, Integer.toString(volume));
			} else {
				if (groupName.equals("master")) {
					int volume = Integer.parseInt(volumeString);
					ClientSingletonRepository.getSound().changeVolume(Numeric.intToFloat(volume, 100.0f));
					WtWindowManager.getInstance().setProperty("sound.volume." + groupName, Integer.toString(volume));
				} else {
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Nie istnieje żadna grupa dźwięku \"" + groupName + "\""));
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Wpisz \"/volume show\", aby zobaczyć listę grup"));
				}
			}
		} catch (NumberFormatException exception) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(volumeString + " nie jest prawidłowym numerem", NotificationType.ERROR));
		}
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
