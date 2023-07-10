package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Check if the Player has equipped an item in a specified slot
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasItemEquippedInSlot implements ChatCondition {

	private final String item;

	private final String slot;

	/**
	 * Check if the Player has equipped an item in a specified slot
	 *
	 * @param item name of item
	 * @param slot name of slot
	 */
	public PlayerHasItemEquippedInSlot(final String item, final String slot) {
		this.item = item;
		this.slot = slot;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return player.isEquippedItemClass(this.slot, this.item);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasItemEquippedInSlot.class);
	}

}
