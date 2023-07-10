package games.stendhal.server.entity.npc.condition;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

/**
 * Tests for {@link PlayerLootedNumberOfItemsCondition}
 *
 * @author madmetzger
 */
public class PlayerLootedNumberOfItemsConditionTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
	}

	@Test
	public void testEqualsHashCode() {
		PlayerLootedNumberOfItemsCondition actual = new PlayerLootedNumberOfItemsCondition(1, "topór");
		assertThat(actual.toString(), is("player has looted <1 of [topór]>"));
		assertThat(actual, is(actual));
		assertThat(actual.hashCode(), is(actual.hashCode()));
		assertThat(actual, is(new PlayerLootedNumberOfItemsCondition(1, "topór")));
		assertThat(actual.hashCode(), is(new PlayerLootedNumberOfItemsCondition(1, "topór").hashCode()));
		assertThat(actual, not(is(new PlayerLootedNumberOfItemsCondition(1, "sztylecik"))));
	}

	@Test
	public void testFire() {
		PlayerLootedNumberOfItemsCondition condition = new PlayerLootedNumberOfItemsCondition(5, "topór");
		Player player = PlayerTestHelper.createPlayer("looter");
		assertThat(condition.fire(player, null, null), is(false));
		player.incLootForItem("sztylecik", 12);
		assertThat(condition.fire(player, null, null), is(false));
		player.incLootForItem("topór", 4);
		assertThat(condition.fire(player, null, null), is(false));
		player.incLootForItem("topór", 2);
		assertThat(condition.fire(player, null, null), is(true));
	}

}
