//package view
//
//import model.cardComponent.Deck
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import model.cardComponent.cardBaseImplementation.Card
//import util.Observable
//import util.Observer
//import scala.io.StdIn.readLine
//import scala.collection.mutable
//import scala.util.Try
//import controller.IController
//
//class Tui(controller: IController) extends Observer {
//
//  // ✅ Register as an Observer
//  controller.add(this)
//
//  /** Starts the TUI loop */
//  def start(): Unit = {
//    println("Welcome to the Soccer Card Game (TUI Mode)!")
//
//    // ✅ Ensure the game is initialized
//    controller.startGame()
//
//    println("Type 'attack', 'undo', 'redo', 'swap', or 'exit' to quit.")
//    update // ✅ Show initial game state
//
//    var running = true
//    while (running) {
//      val input = readLine().trim.toLowerCase
//      input match {
//        case "attack" =>
//          val defenderPosition = controller.selectDefenderPosition()
//          if (defenderPosition == -1) {
//            println("Attacking the goalkeeper.")
//          } else if (defenderPosition == -2) {
//            println("Select a defender position to attack:")
//            val pos = readLine().toIntOption.getOrElse(-1)
//            if (pos >= 1) controller.executeAttackCommand(pos - 1)
//            else println("❌ Invalid input.")
//          }
//
//        case "undo" =>
//          controller.undo()
//          println("🔄 Undo executed!")
//
//        case "redo" =>
//          controller.redo()
//          println("🔁 Redo executed!")
//
//        case "swap" =>
//          println("Select a card index to swap from attacker's hand:")
//          val index = readLine().toIntOption.getOrElse(-1)
//          if (index >= 0) {
//            controller.swapAttackerCard(index)
//            println(s"🔄 Swapped attacker card at index: $index")
//          } else {
//            println("❌ Invalid card index.")
//          }
//
//        case "exit" =>
//          println("👋 Exiting TUI...")
//          running = false
//
//        case _ =>
//          println("❌ Invalid command. Type 'attack', 'undo', 'redo', 'swap', or 'exit'.")
//      }
//    }
//  }
//
//  /** ✅ Observer Pattern: Refresh the game state whenever notified */
//  override def update: Unit = {
//    println("================================")
//    println(displayGameState())
//  }
//
//  /** Displays the current game state */
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.fieldState.playerDefenders(defender).mkString(", ")
//    val defenderGoalkeeper = pf.fieldState.getGoalkeeper(defender)
//
//    s"""🏆 **Current Game State**
//       |--------------------------------
//       |⚔️  Attacker: ${attacker.name}
//       |   🎴 Hand: $attackerHand
//       |   🃏 Attacking Card: $attackingCard
//       |
//       |🛡️  Defender: ${defender.name}
//       |   🎴 Hand: $defenderHand
//       |   🏟️ Field: $defenderField
//       |   🥅 Goalkeeper: $defenderGoalkeeper
//       |--------------------------------
//       |""".stripMargin
//  }
//}
package view

import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import util.Observable
import util.Observer
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.IController
import model.cardComponent.base.Card
import model.cardComponent.cardFactory.DeckFactory

class Tui(controller: IController) extends Observer {

  // ✅ Register as an Observer
  controller.add(this)

  // ✅ Define different states for the TUI
  sealed trait TuiState
  case object MainMenu extends TuiState
  case object CreatePlayer extends TuiState
  case object LoadGame extends TuiState
  case object PlayingField extends TuiState
  case object Exiting extends TuiState

  private var currentState: TuiState = MainMenu // ✅ Start with the main menu

  /** Starts the TUI loop */
  def start(): Unit = {
    println("Welcome to the Soccer Card Game (TUI Mode)!")
    var running = true

    while (running) {
      currentState match {
        case MainMenu       => showMainMenu()
        case CreatePlayer   => showCreatePlayer()
        case LoadGame       => showLoadGame()
        case PlayingField   => showPlayingField()
        case Exiting        => running = false
      }
    }
  }

  /** ✅ Show Main Menu */
  def showMainMenu(): Unit = {
    println(
      """|======== Main Menu ========
         |1. Create New Game
         |2. Load Game
         |3. Exit
         |===========================
         |Enter choice:""".stripMargin
    )

    readLine().trim match {
      case "1" => currentState = CreatePlayer
      println("Create Player Menu Selected")
      case "2" => currentState = LoadGame
        println("Load Game Menu Selected")
      case "3" => currentState = Exiting
        println("Exit Selected")
      case _   => println("❌ Invalid choice. Please enter 1, 2, or 3.")
    }
  }

  /** ✅ Handle Player Creation */
  def showCreatePlayer(): Unit = {
    println("\n👥 Enter Player Names (exactly 2 players required):")

    val playerNames = (1 to 2).map { i =>
      print(s"Player $i: ")
      readLine().trim
    }.filter(_.nonEmpty)

    if (playerNames.size != 2) {
      println("❌ Exactly 2 players are required! Returning to Main Menu...")
      currentState = MainMenu
      return
    }

    // ✅ Assign names to players using `setPlayerName`
    controller.setPlayerName(1, playerNames.head)
    controller.setPlayerName(2, playerNames(1))

    println(s"✅ Players set: ${playerNames.head} & ${playerNames(1)}")
    controller.startGame()

    currentState = PlayingField
  }

  /** ✅ Handle Loading a Game */
  def showLoadGame(): Unit = {
    println("\n📂 Enter the saved game file path:")
    val filePath = readLine().trim

    controller.loadGame(filePath) match {
      case scala.util.Success(_) =>
        println("✅ Game loaded successfully!")
        currentState = PlayingField
      case scala.util.Failure(ex) =>
        println(s"❌ Failed to load game: ${ex.getMessage}")
        currentState = MainMenu
    }
  }

  /** ✅ Show Playing Field & Handle In-Game Actions */
  def showPlayingField(): Unit = {
    println("🎮 Game Started! Type 'attack', 'undo', 'redo', 'swap', 'exit'.")

    var inGame = true
    while (inGame) {
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
          println("👋 Exiting to Main Menu...")
          inGame = false
          currentState = MainMenu

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

  /** ✅ Displays the current game state */
  def displayGameState(): String = {

    val pf = controller.getPlayingField
    if(pf == null) {
      println("not init pf")
      "stt"
    } else {
      val attacker = pf.getAttacker
      val defender = pf.getDefender
      val attackingCard = pf.getAttackingCard
      val attackerHand = pf.getHand(attacker).mkString(", ")
      val defenderHand = pf.getHand(defender).mkString(", ")
      val defenderField = pf.fieldState.getPlayerDefenders(defender).mkString(", ")
      val defenderGoalkeeper = pf.fieldState.getPlayerGoalkeeper(defender)

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
}
