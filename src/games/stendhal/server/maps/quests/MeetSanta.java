/* $Id: MeetSanta.java,v 1.76.2.1 2012/12/15 15:11:25 nhnb Exp $ */
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
package games.stendhal.server.maps.quests;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NakedCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerIsAGoodBoyCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Meet Santa anywhere around the World.
 *<p>
 * PARTICIPANTS: <ul><li> Santa Claus</ul>
 *
 * STEPS: <ul><li> Find Santa <li>Say hi <li> Get reward <li> Get hat</ul>
 *
 * REWARD: <ul><li> a stocking which can be opened to obtain a random good reward: food,
 * money, potions, items, etc... </ul>
 *
 * REPETITIONS:None
 */
public class MeetSanta extends AbstractQuest implements LoginListener {
	
	// quest slot changed ready for 2011
	private static final String QUEST_SLOT = "meet_santa_12";
	// date changed ready for 2011
    private static final GregorianCalendar notXmas = new GregorianCalendar(2013, Calendar.JANUARY, 6);

	public static final String QUEST_NAME = "MeetSanta";

	/** the Santa NPC. */
	protected SpeakerNPC santa;

	private StendhalRPZone zone;

	private TeleporterBehaviour teleporterBehaviour;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private SpeakerNPC createSanta() {
		santa = new SpeakerNPC("Święty Mikołaj") {
			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new QuestCompletedCondition(QUEST_SLOT),
                                                        new PlayerIsAGoodBoyCondition(),
                                                        new NotCondition(new NakedCondition())),
					ConversationStates.IDLE,
					"Witaj ponownie! Pamiętaj, aby być grzecznym jeżeli chcesz dostąc prezent w przyszłym roku!",
				    new ChatAction() {
					    public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) { 
					    	addHat(player);	    
					    }
					}
				);

				final List<ChatAction> reward = new LinkedList<ChatAction>();
				reward.add(new EquipItemAction("skarpeta"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));
				reward.add(new ChatAction() {
				        public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						    addHat(player);
						}
				    }
				);
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new QuestNotCompletedCondition(QUEST_SLOT),
                                                        new PlayerIsAGoodBoyCondition(),
                                                        new NotCondition(new NakedCondition())),
					ConversationStates.IDLE,
					"Wesołych Świąt! Mam prezent i czapkę dla Ciebie. Dowidzenia i pamiętaj, aby być grzecznym jeżeli chcesz dostąc prezent w przyszłym roku!",
					new MultipleActions(reward));

				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
                                                        new NotCondition(new PlayerIsAGoodBoyCondition())),
					ConversationStates.IDLE,
					"Ho ho ho! Wygląda na to, że nie byłeś grzeczny w tym roku! Tylko grzeczne dzieci dostają prezent! Sezon wita!",
					null);

				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new NakedCondition()),
					ConversationStates.IDLE,
					"Moje dziecko przeziębisz się jak będziesz bez okrycia! Ubierz się ciepło, a ja dam ci czapkę! Ho ho ho!",
					null);
			}
		};
		santa.setEntityClass("santaclausnpc");
		santa.initHP(100);

		// start in int_admin_playground
		zone = SingletonRepository.getRPWorld().getZone("int_admin_playground");
		santa.setPosition(17, 13);
		zone.add(santa);

		return santa;
	}

	private void addHat(final Player player) {
		// fetch old outfit as we want to know the current hair
		final Outfit oldoutfit = player.getOutfit();
		// all santa hat sprites are at 50 + current hair
		if (oldoutfit.getHair() < 50) {
			final int hatnumber = oldoutfit.getHair() + 44;
			// the new outfit only changes the hair, rest is null
			final Outfit newOutfit = new Outfit(hatnumber, null, null, null);
			//put it on, and store old outfit.
			player.setOutfit(newOutfit.putOver(oldoutfit), true);
			player.registerOutfitExpireTime(43200);
		} else if (oldoutfit.getHair() > 93) {
			final int hatnumber = oldoutfit.getHair() - 44;
			// the new outfit only changes the hair, rest is null
			final Outfit newOutfit = new Outfit(hatnumber, null, null, null);
			//put it on, and store old outfit.
			player.setOutfit(newOutfit.putOver(oldoutfit), true);
			player.registerOutfitExpireTime(43200);
		}
	}


	public void onLoggedIn(final Player player) {
		// is it Christmas?
		final Outfit outfit = player.getOutfit();
		final int hairnumber = outfit.getHair();
		if ((hairnumber >= 50) && (hairnumber < 94)) {
			final Date now = new Date();
			final Date dateNotXmas = notXmas.getTime();
			if (now.after(dateNotXmas)) {
				final int newhair = hairnumber - 44;
				final Outfit newOutfit = new Outfit(newhair, null, null, null);
				player.setOutfit(newOutfit.putOver(outfit), false);
			}
		}
	}

	/**
	 * removes an NPC from the world and NPC list
	 *
	 * @param name name of NPC
	 */
	private void removeNPC(String name) {
		SpeakerNPC npc = NPCList.get().get(name);
		if (npc == null) {
			return;
		}
		npc.getZone().remove(npc);
	}
	
	@Override
	public void addToWorld() {
		
		super.addToWorld();
		fillQuestInfo(
				"Spotkanie Świętego Mikołaja",
				"Pada śnieg, pada śnieg, spamuje cały czas... Ho Ho Ho! Spiesz się i znajdź Świętego Mikołaja w Faiumoni! Jeżeli byłeś grzeczny to dostaniesz prezent...",
				false);
		SingletonRepository.getLoginNotifier().addListener(this);
		
		if (System.getProperty("stendhal.santa") != null) {
			// activate santa here
			createSanta();
			teleporterBehaviour = new TeleporterBehaviour(santa, "Ho, ho, ho! Wesołych Świąt!", false);
		}
	}
	
	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Święty Mikołaj");
		// remove the turn notifiers left from the TeleporterBehaviour
		SingletonRepository.getTurnNotifier().dontNotify(teleporterBehaviour);
		return true;
	}

	@Override
	public String getName() {
		return "MeetSanta";
	}
	
	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}
	
	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getNPCName() {
		return "Święty Mikołaj";
	}
	
}
