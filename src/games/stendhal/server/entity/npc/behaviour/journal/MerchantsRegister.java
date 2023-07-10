/* $Id: MerchantsRegister.java,v 1.1 2012/08/23 20:05:44 yoriy Exp $ */
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
package games.stendhal.server.entity.npc.behaviour.journal;

import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

public class MerchantsRegister {

	private static MerchantsRegister instance;

	private final List<Pair<String, BuyerBehaviour>> buyers;
	private final List<Pair<String, SellerBehaviour>> sellers;

	public static MerchantsRegister get() {
		if (instance == null) {
			new MerchantsRegister();
		}
		return instance;
	}

	protected MerchantsRegister() {
		instance = this;
		buyers  = new LinkedList<Pair<String, BuyerBehaviour>>();
		sellers  = new LinkedList<Pair<String, SellerBehaviour>>();
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 * 
	 * @param npcName
	 *            The NPC that should be added
	 * @param behaviour   
	 *            The MerchantBehaviour of that NPC
	 */
	public void add(final String npcName, final BuyerBehaviour behaviour) {
		Pair<String, BuyerBehaviour> pair = new Pair<String, BuyerBehaviour>(npcName, behaviour);
		buyers.add(pair);
	}

	public void add(final String npcName, final SellerBehaviour behaviour) {
		Pair<String, SellerBehaviour> pair = new Pair<String, SellerBehaviour>(npcName, behaviour);
		sellers.add(pair);
	}

	public List<Pair<String, BuyerBehaviour>> getBuyers() {
		return buyers;
	}

	public List<Pair<String, SellerBehaviour>> getSellers() {
		return sellers;
	}

}
