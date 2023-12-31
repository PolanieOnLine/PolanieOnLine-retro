package games.stendhal.server.maps.quests.revivalweeks;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
public class TicTacToeGameTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("0_semos_mountain_n2"));

		new TicTacToeGame().addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//new TicTacToeGame().removeFromWorld();
	}

	@Test
	public void testNPCisThere() throws Exception {
		assertNotNull(NPCList.get().get("Paul Sheriff"));
	}

	@Test
	public void testConversation() throws Exception {
		SpeakerNPC paul = NPCList.get().get("Paul Sheriff");
		Engine engine = paul.getEngine();
		Player player=PlayerTestHelper.createPlayer("ticPlayer");
		engine.step(player, "hi");
		assertEquals("Cześć! Witam w naszej małej grze Tic Tac Toe. Zadaniem jest wypełnieniem rzędu (pionowo, poziomu lub po przekątnej) tymi samymi krążkami. Potrzebujesz rywala, aby #zagrać.", getReply(paul));
		assertEquals(ConversationStates.IDLE, engine.getCurrentState());
		assertEquals(Direction.DOWN, paul.getDirection());

		paul.setDirection(Direction.UP);
		assertEquals(Direction.UP, paul.getDirection());

		engine.step(player, "bye");
		assertEquals("Miło było Cię poznać.", getReply(paul));
		assertEquals(ConversationStates.IDLE, engine.getCurrentState());
		assertEquals(Direction.DOWN, paul.getDirection());


	}

}
