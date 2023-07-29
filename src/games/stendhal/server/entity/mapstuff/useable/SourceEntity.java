package games.stendhal.server.entity.mapstuff.useable;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ImageEffectEvent;
import games.stendhal.server.events.SoundEvent;

public class SourceEntity extends PlayerActivityEntity {
	private static final Logger logger = Logger.getLogger(SourceEntity.class);

	/**
	 * The chance that prospecting is successful.
	 */
	private static final double FINDING_PROBABILITY = 0.02;

	private final String startSound = "pickaxe_01";
	private final String successSound = "rocks-1";
	private final int SOUND_RADIUS = 20;

	/**
	 * Calculates the probability that the given player finds stone. This is
	 * based on the player's mining skills, however even players with no skills
	 * at all have a 5% probability of success.
	 *
	 * @param player
	 *            The player,
	 *
	 * @return The probability of success.
	 */
	private double getSuccessProbability(final Player player) {
		double probability = FINDING_PROBABILITY;

		final String skill = player.getSkill("mining");
		if (skill != null) {
			probability = Math.max(probability, Double.parseDouble(skill));
		}
		return probability + player.useKarma(0.02);
	}

	@Override
	protected int getDuration(final Player player) {
		return 7 + Rand.rand(4);
	}

	@Override
	protected boolean isPrepared(final Player player) {
		if (!player.isEquipped("kilof")) {
			player.sendPrivateText("Potrzebujesz kilofa do wydobywania minerału.");
			return false;
		}

		return true;
	}

	/**
	 * Decides if the activity was successful.
	 *
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isSuccessful(final Player player) {
		final int random = Rand.roll1D100();
		return (random <= (getSuccessProbability(player) * 100));
	}

	/**
	 * Called when the activity has started.
	 *
	 * @param player
	 *            The player starting the activity.
	 * @param mes
	 *            The message what player will get after activity.
	 */
	public void sendMessage(final Player player, final String mes) {
		addEvent(new SoundEvent(startSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
		player.sendPrivateText(mes);
		notifyWorldAboutChanges();
		addEvent(new ImageEffectEvent("mining", true));
		notifyWorldAboutChanges();
	}

	/**
	 * Called when the activity has finished.
	 *
	 * @param player
	 *            The player that did the activity.
	 * @param successful
	 *            If the activity was successful.
	 */
	@Override
	protected void onFinished(final Player player, final boolean successful) {
		// do nothing
	}

	protected void successFinish(final Player player, final boolean successful, String itemName) {
		if (successful) {
			addEvent(new SoundEvent(successSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
	    	final Item item = SingletonRepository.getEntityManager().getItem(itemName);

			if (item != null) {
				player.equipOrPutOnGround(item);
				player.incMinedForItem(item.getName(), item.getQuantity());
			    SingletonRepository.getAchievementNotifier().onObtain(player);
			    player.sendPrivateText(Grammar.genderVerb(player.getGender(), "Wydobyłeś") + " " + item.getTitle() + ".");
			} else {
				logger.error("could not find item: " + itemName);
			}
	    } else {
	    	player.sendPrivateText("Nic nie " + Grammar.genderVerb(player.getGender(), "wydobyłeś") + ".");
	    }
	}

	/**
	 * Called when the activity has started.
	 *
	 * @param player
	 *            The player starting the activity.
	 */
	@Override
	protected void onStarted(final Player player) {
		sendMessage(player, Grammar.genderVerb(player.getGender(), "Rozpocząłeś") + " wydobywanie surowca.");
	}
}
