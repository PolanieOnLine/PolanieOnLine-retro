package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Checks if a player has mined a given number of items
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_LOOTED, label="Item?")
public class PlayerMinedNumberOfItemsCondition implements ChatCondition {

	private final List<String> itemMinedList;

	private final int quantity;

	/**
	 * Create a new PlayerMinedNumberOfItemsCondition
	 *
	 * @param number required number of each item
	 * @param items list of required items
	 */
	public PlayerMinedNumberOfItemsCondition(int number, String... items) {
		itemMinedList = new ArrayList<String>();
		if(items != null) {
			for(String item : items) {
				itemMinedList.add(item);
			}
		}
		quantity = number;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for(String item : itemMinedList) {
			if(quantity > player.getQuantityOfMinedItems(item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerMinedNumberOfItemsCondition.class);
	}

	@Override
	public String toString() {
		return "player has mined <"+quantity+" of "+itemMinedList+">";
	}

}
