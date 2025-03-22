package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

object GameStatusMessages {
  val GAME_STARTED = "Game Started! Player 1 attacks Player 2!"
  val ATTACK_INITIATED = "⚔️ {attacker} attacks {defender}!"
  val ATTACK_SUCCESSFUL = "✅ {attacker} succeeded in the attack!"
  val ATTACK_FAILED = "❌ {defender} defended successfully!"
  val GOAL_SCORED = "⚽ {attacker} scored a goal!"
  val UNDO_PERFORMED = "🔄 Undo performed!"
  val REDO_PERFORMED = "🔄 Redo performed!"
  val NOTHING_TO_UNDO = "⚠️ Nothing to undo!"
  val NOTHING_TO_REDO = "⚠️ Nothing to redo!"
  val NO_DEFENDER_SELECTED = "⚠️ No defender selected to attack!"
  val NO_CARD_SELECTED = "No card selected!"
  val REGULAR_SWAP_PERFORMED = "Regular Swap performed!"
  val REVERSE_SWAP_PERFORMED = "Reverse Swap performed!"
  val BOOST_PERFORMED = "Defender Boost performed!"

  def format(message: String, attacker: String = "", defender: String = ""): String = {
    message
      .replace("{attacker}", attacker)
      .replace("{defender}", defender)
  }
}
