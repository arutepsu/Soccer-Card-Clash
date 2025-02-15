//package view
//
//import controller.IController
//import util.Observer
//import tui.TuiMainMenu
//import tui.TuiGameLoop
//import tui.TuiView
//import controller.baseControllerImplementation.GameState
//class Tui(controller: IController) extends Observer {
//  controller.add(this)
//
//  private var activeView: TuiView = new TuiMainMenu(controller)
//
//  def start(): Unit = {
//    activeView.start()
//  }
//
//  override def update: Unit = {
//    if (GameState.gameStarted) {
//      println("🎮 TUI detected that the game has started! Switching to game mode...")
//
//      if (!activeView.isInstanceOf[TuiGameLoop]) {
//        activeView = new TuiGameLoop(controller) // ✅ Now valid
//        activeView.start() // ✅ Calls start(), which runs the game loop
//      }
//    } else {
//      println("📜 TUI detected that the game has returned to the Main Menu.")
//
//      if (!activeView.isInstanceOf[TuiMainMenu]) {
//        activeView = new TuiMainMenu(controller) // ✅ Now valid
//        activeView.start()
//      }
//    }
//  }
//}
package view

import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.cardComponent.Card
import util.Observable
import util.Observer
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.IController

class Tui(controller: IController) extends Observer {

  // ✅ Register as an Observer
  controller.add(this)

  /** Starts the TUI loop */
  def start(): Unit = {
    println("Welcome to the Soccer Card Game (TUI Mode)!")

    // ✅ Ensure the game is initialized
    controller.startGame()

    println("Type 'attack', 'undo', 'redo', 'swap', or 'exit' to quit.")
    update // ✅ Show initial game state

    var running = true
    while (running) {
      val input = readLine().trim.toLowerCase
      input match {
        case "attack" =>
          val defenderPosition = controller.selectDefenderPosition()
          if (defenderPosition == -1) {
            println("Attacking the goalkeeper.")
          } else if (defenderPosition == -2) {
            println("Select a defender position to attack:")
            val pos = readLine().toIntOption.getOrElse(-1)
            if (pos >= 1) controller.executeAttackCommand(pos - 1)
            else println("❌ Invalid input.")
          }

        case "undo" =>
          controller.undo()
          println("🔄 Undo executed!")

        case "redo" =>
          controller.redo()
          println("🔁 Redo executed!")

        case "swap" =>
          println("Select a card index to swap from attacker's hand:")
          val index = readLine().toIntOption.getOrElse(-1)
          if (index >= 0) {
            controller.swapAttackerCard(index)
            println(s"🔄 Swapped attacker card at index: $index")
          } else {
            println("❌ Invalid card index.")
          }

        case "exit" =>
          println("👋 Exiting TUI...")
          running = false

        case _ =>
          println("❌ Invalid command. Type 'attack', 'undo', 'redo', 'swap', or 'exit'.")
      }
    }
  }

  /** ✅ Observer Pattern: Refresh the game state whenever notified */
  override def update: Unit = {
    println("================================")
    println(displayGameState())
  }

  /** Displays the current game state */
  def displayGameState(): String = {
    val pf = controller.getPlayingField
    val attacker = pf.getAttacker
    val defender = pf.getDefender
    val attackingCard = pf.getAttackingCard
    val attackerHand = pf.getHand(attacker).mkString(", ")
    val defenderHand = pf.getHand(defender).mkString(", ")
    val defenderField = pf.fieldState.playerDefenders(defender).mkString(", ")
    val defenderGoalkeeper = pf.fieldState.getGoalkeeper(defender)

    s"""🏆 **Current Game State**
       |--------------------------------
       |⚔️  Attacker: ${attacker.name}
       |   🎴 Hand: $attackerHand
       |   🃏 Attacking Card: $attackingCard
       |
       |🛡️  Defender: ${defender.name}
       |   🎴 Hand: $defenderHand
       |   🏟️ Field: $defenderField
       |   🥅 Goalkeeper: $defenderGoalkeeper
       |--------------------------------
       |""".stripMargin
  }
}