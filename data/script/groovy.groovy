/* Disabled
import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.entity.player.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*
import games.stendhal.server.pathfinder.Path

// Simple sample script that creates a sign prooving Groovy is active

// game is a predefined variable of the current ScriptInGroovy
// environment. all world and zone operations should be accessed
// through this to support unloading of scripts:
//  boolean game.setZone(String name)
//  game.add(NPC npc)
//  game.add(RPObject object)
//  game.getItem(String name)
// logger is a predefined variable of the current ScriptInGroovy
logger.debug("Starting PolanieOnLine Groovy Script")

// Adding a sign to the game world that shows Groovy is active
myZone = "0_semos_city"
if(game.setZone(myZone))   // if zone exists
  {
  // We create now a sign and place it on position 31,50 with some text
  sign=new Sign()
  sign.setPosition(8, 47)
  sign.setText("Witamy w PolanieOnLine!\nProsimy o wysyłanie reportów o każdym możliwym błędzie, który występuje aktualnie w grze oraz odwiedź naszą stronę internetową.\nhttps://s1.polanieonline.eu")

  // Add our new Object to the game world
  game.add(sign)
  }
else
  {
 logger.error("Cannot set Zone " + myZone)
  }

logger.debug("Finished PolanieOnLine Groovy Script")
*/