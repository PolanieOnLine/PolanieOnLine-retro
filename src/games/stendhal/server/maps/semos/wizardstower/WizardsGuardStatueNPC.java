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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.CloneManager;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Zekiel, the guardian statue of the Wizards Tower (Zekiel in the basement)
 *
 * @see games.stendhal.server.maps.quests.ZekielsPracticalTestQuest
 * @see games.stendhal.server.maps.semos.wizardstower.WizardsGuardStatueSpireNPC
 */
public class WizardsGuardStatueNPC implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZekiel(zone);
	}

	private void buildZekiel(final StendhalRPZone zone) {
		final SpeakerNPC zekiel = CloneManager.get().clone(new WizardsGuardStatueSpireNPC().getZekiel(), "Zekiel Strażnik");

		zekiel.addGreeting("Witaj cudzoziemcze! Jestem Zekiel, #opiekun wieży.");
		zekiel.addHelp("Przypuszczam, że będziesz chciał zwiedzić #wieżę. Jestem nie tylko #strażnikiem wieży, ale też opiekunem tych którzy przystępują do praktycznego #testu.");
		zekiel.addJob("Jestem  #opiekunem tej #wieży.");
		zekiel.addReply("opiekun", "Pilnuję #wieży, która jest miejscem spotkań wszystkich #magów.");
		zekiel.addReply(Arrays.asList("wieża","wieżę","wieży"),  "Jeżeli chcesz dostać się do wieży musisz przystąpić do praktycznego #testu.");
		zekiel.addGoodbye("Żegnaj!");
		zekiel.addReply("testu", "Egzamin praktyczny jest w formie #zadania , które otrzymasz ode mnie.");
		zekiel.addReply(Arrays.asList("wizard", "wizards", "mag", "magów"),
		        "Do kręgu pentagramu należy siedmiu czarodziejów. Są to #Erastus, #Elana, #Ravashack, #Jaer, #Cassandra, #Silvanus i #Malleus");
		zekiel.addReply("erastus", "Erastus jest arcymagiem w tym kręgu. Jest arcymistrzem swego fachu, najmądrzejszym i najwspanialszym z całego grona. Jako jedyny nie uczestniczy w teście praktycznym.");
		zekiel.addReply("elana", "Elana jest życzliwą i przyjazną wróżką. Jest opiekunką wszystkich żywych stworzeń na ziemi. Korzysta z magi żeby je ratować i leczyć.");
		zekiel.addReply("ravashack", "Ravashack jest bardzo potężnym nekromanem. Studiował czarną magię od wieków. Ravashack używa czarnej magii do zdobycia przewagi nad rywalem w walce z lichami.");
		zekiel.addReply("jaer", "Jaer jest mistrzem iluzji. Uroczy i kapryśny jak wietrzyk w upalne dni. Specjalizuje się w użyciu wiatru. Ma wielu sojuszników na równinach mitycznych duchów.");
		zekiel.addReply("cassandra", "Cassandra jest piękną kobietą, ale przede wszystkim potężna czarodziejką. Domeną Cassandry jest woda i lód. Może być zimna jak lód by tylko osiągnąć swój cel.");
		zekiel.addReply("silvanus", "Silvanus to stary druid i najprawdopodobniej najstarszy z wszystkich elfów. Jest przyjacielem zwierząt, drzew, wróżek oraz entów. Jego domeną jest Ziemia.");
		zekiel.addReply("malleus", "Malleus to najpotężniejszy magik oraz przywódca destruktywnych magów. Jego domeną jest ogień. Żył z demonami aby zrozumieć ich ambicje.");

		zekiel.setPosition(15, 15);
		zone.add(zekiel);
	}
}
