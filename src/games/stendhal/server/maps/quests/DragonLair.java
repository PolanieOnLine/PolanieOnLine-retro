/* $Id: DragonLair.java,v 1.25 2012/06/28 19:22:37 kymara Exp $ */
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

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Dragon Lair Access
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Wishman, storm trooper extraordinaire from Blordrough's dark legion, guards the remaining dragons
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Wishman 
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> admittance to dragon lair
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> after 1 week.
 * </ul>
 */

public class DragonLair extends AbstractQuest {

	private static final String QUEST_SLOT = "dragon_lair";
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {
		
		final SpeakerNPC npc = npcs.get("Wishman");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"Czy chciałbyś odwiedzić jaskinię smoków?",
				null);
				
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK))),
				ConversationStates.ATTENDING, 
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK, "Sądzę, że mają już dosyć wrażeń na jakiś czas. Wróć za "));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK)),
				ConversationStates.QUEST_OFFERED, 
				"Uważaj smoki zaczęły ziać ogniem! Czy chciałbyś ponownie odwiedzić nasze smoki?",
				null);
		
		// shouldn't happen
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Dziękuję za zatrzymanie się. Wróć ponownie.",
				null);
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Wspaniale! Ciesz się wizytą. Wiem, że oni będą. Aha uważaj. Mamy parę jeźdźców smoków chaosu dowodzących naszymi smokami. Nie wchodź im w drogę!",
				new MultipleActions(new TeleportAction("-1_ados_outside_w", 25, 28, Direction.DOWN), new SetQuestAction(QUEST_SLOT, "done;"), new SetQuestToTimeStampAction(QUEST_SLOT,1)));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Dobrze, ale nasze smoki będą zawiedzione jeżeli nie wpadniesz do nich.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		fillQuestInfo(
				"Legowisko Smoka",
				"Wishman, nadzwyczajny żołnierz z Mrocznego Legionu Blordrough, strzeże smoków... i umożliwi Ci do nich dostęp.",
				true);

	}
	
	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			final String questState = player.getQuest(QUEST_SLOT);
			res.add("Wishman zaproponował, ·abym zabawił się z jego smokami!");
			if ("rejected".equals(questState)) {
				res.add("Dla mnie wyglądają trochę przerażająco");
				return res;
			} 
			if (isRepeatable(player)) {
				res.add("Te smoki znów potrzebują zabawy. Powinienem je wkrótce odwiedzić.");
			} else {
				res.add("Ostatnio smoki miały mnóstwo zabazwy. Wishman jeszcze nie pozwala mi wrócić.");
			}
			return res;
	}
	@Override
	public String getName() {
		return "DragonLair";
	}
	
	// getting past the assassins to this location needs a higher level; the lair itself is dangerous too
	@Override
	public int getMinLevel() {
		return 100;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,MathHelper.MINUTES_IN_ONE_WEEK)).fire(player, null, null);
	}
	@Override
	public String getNPCName() {
		return "Wishman";
	}
}
