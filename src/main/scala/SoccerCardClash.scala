import scalafx.application.JFXApp3
import controller.{Events, IController}
import controller.base.Controller
import model.cardComponent.base.RegularCard
import model.playingFiledComponent.base.PlayingField
import view.gui.Gui
import view.tui.Tui

import scala.io.StdIn.readLine

object SoccerCardClash {
  private val controller: IController = new Controller()
  private val gui: Gui = Gui(controller)
  private val tui: Tui = Tui(controller)

  def main(args: Array[String]): Unit = {
    new Thread(() => {
      gui.main(Array.empty)
    }).start()

    var input: String = ""

    while (input != ":quit") {
      input = readLine()
      tui.processInputLine(input)
    }
  }

//  def main(args: Array[String]): Unit = {
//    val controller = new Controller()
//
//    // 🎮 Initialize Players & Start Game
//    val player1Name = "Alice"
//    val player2Name = "Bob"
//    controller.startGame(player1Name, player2Name)
//
//    val playingField = controller.getPlayingField
//    val attacker = playingField.getAttacker
//    val defender = playingField.getDefender
//
//    println("\n🎲 **GAME STARTED** 🎲")
//    printGameState(playingField)
//
//    // 🎯 Execute an Attack
//    println("\n⚔️ **ATTACK INITIATED** ⚔️")
//    val defenderPosition = controller.selectDefenderPosition()
//
//    if (defenderPosition == -1) {
//      println(s"🥅 ${attacker.name} is attacking the goalkeeper!")
//    } else if (defenderPosition == -2) {
//      println(s"🔢 Select a defender index:")
//      val pos = scala.io.StdIn.readInt() // Get defender position from input
//      controller.executeSingleAttackCommand(pos)
//    } else {
//      controller.executeSingleAttackCommand(defenderPosition)
//    }
//
//    // 🔄 Print Game State after Attack
//    println("\n🔄 **GAME STATE AFTER ATTACK** 🔄")
//    printGameState(playingField)
//  }
//
//  /** ✅ Prints the game state */
//  def printGameState(playingField: PlayingField): Unit = {
//    val attacker = playingField.getAttacker
//    val defender = playingField.getDefender
//
//    println(s"👊 **Attacker:** ${attacker.name}")
////    println(s"🎴 ${attacker.name}'s Hand Cards: ${playingField.fieldState.getPlayerHand(attacker).mkString(", ")}")
//    println(s"🏟️ ${attacker.name}'s Field Cards: ${playingField.fieldState.getPlayerField(attacker).mkString(", ")}")
//
//    println("\n🛡 **Defender:** ${defender.name}")
////    println(s"🎴 ${defender.name}'s Hand Cards: ${playingField.fieldState.getPlayerHand(defender).mkString(", ")}")
//    println(s"🏟️ ${defender.name}'s Field Cards: ${playingField.fieldState.getPlayerField(defender).mkString(", ")}")
//  }
}

