package view.tui

import controller.{Events, IController}
import util.{Observable, ObservableEvent, Observer}
import view.tui.PromptState
enum PromptState {
  case None            // No active prompt
  case PlayerName      // Waiting for player names
  case NewGame         // New game setup
  case LoadGame        // Loading game
  case SaveGame        // Saving game
  case Attack          // Selecting an attack
  case DoubleAttack
  case Swap            // Swapping cards
  case Redo        // Undo/Redo operations
  case Undo        // Undo/Redo operations
  case Boost
}
class Tui(controller: IController) extends Observer {
  private var promptState: PromptState = PromptState.None
  private val prompter = new Prompter()
  private val parser = new Parser
  controller.add(this)

  println("🎮 Welcome to Soccer Card Clash!")
  TuiKeys.values.foreach { key =>
    println(s"${key.productPrefix}, Key: ${key.key}")
  }

  /** ✅ Start TUI Loop */
  def start(): Unit = {
    var input: String = ""
    while (input != TuiKeys.Quit.key) {
      input = scala.io.StdIn.readLine().trim
      processInputLine(input)
    }
  }

  def printStatusScreen(): Unit = {
//    val playingField = controller.getPlayingField
//    val attacker = playingField.getAttacker
//    val defender = playingField.getDefender
//    val player1HandCards = playingField.fieldState.getPlayerHand(attacker)
//    val player2HandCards = playingField.fieldState.getPlayerHand(defender)
//    val player1field = playingField.fieldState.getPlayerField(attacker)
//    val player2field = playingField.fieldState.getPlayerField(defender)
//
//    println("\n===================================")
//    println("🏆 **CURRENT GAME STATE**")
//    println("===================================")
//
//    // 🎭 **Attacker & Defender**
//    println(f"⚔️ Attacker: ${attacker.name}")
//    println(f"🛡️ Defender: ${defender.name}")
//    println("-----------------------------------")
//
//    // 🃏 **Player Hands**
//    println(s"🎴 ${attacker.name}'s Hand: " +
//      (if (player1HandCards.nonEmpty) player1HandCards.mkString(", ") else "No cards left!")
//    )
//    println(s"🎴 ${defender.name}'s Hand: " +
//      (if (player2HandCards.nonEmpty) player2HandCards.mkString(", ") else "No cards left!")
//    )
//    println("-----------------------------------")
//
//    // 🏟️ **Player Field Cards**
//    println(s"🏟️ ${attacker.name}'s Field: " +
//      (if (player1field.nonEmpty) player1field.mkString(", ") else "No defenders!")
//    )
//    println(s"🏟️ ${defender.name}'s Field: " +
//      (if (player2field.nonEmpty) player2field.mkString(", ") else "No defenders!")
//    )

    println("===================================")
  }



    /** ✅ Process User Input */
  def processInputLine(input: String): Unit = {
    input match {
      case TuiKeys.Undo.key => controller.undo()
      case TuiKeys.Redo.key => controller.redo()
//      case TuiKeys.CreateGame.key => controller.startGame()
      case TuiKeys.Save.key => controller.saveGame()
      case TuiKeys.Load.key => controller.loadGame()

      // ✅ Process Input Based on Current Prompt State
      case _ => promptState match {

        case PromptState.NewGame =>
          parser.parseStartGame(input) match {
            case Some((player1, player2)) =>
              controller.startGame(player1, player2)
              println(s"🎮 Game started with players: $player1 & $player2!")
            case None =>
              println("❌ Invalid input! Use: ':start player1 player2'")
          }
        /** 🔹 Attack a Defender */
        case PromptState.Attack =>
          parser.parseAttack(input, controller.getPlayingField.fieldState.getPlayerDefenders(controller.getPlayingField.getDefender).size) match {
            case Some(index) => controller.executeSingleAttackCommand(index)
            case None => println("Invalid defender index! Try again.")
          }
        case PromptState.DoubleAttack =>
          parser.parseAttack(input, controller.getPlayingField.fieldState.getPlayerDefenders(controller.getPlayingField.getDefender).size) match {
            case Some(index) => controller.executeDoubleAttackCommand(index)
            case None => println("Invalid defender index! Try again.")
          }

        case PromptState.Boost =>
          parser.parseBoost(input, controller.getPlayingField.fieldState.getPlayerDefenders(controller.getPlayingField.getAttacker).size) match {
            case Some(index) => controller.boostDefender(index)
            case None => println("Invalid defender index! Try again.")
          }

        case PromptState.Swap =>
          parser.parseSwap(input, controller.getPlayingField.fieldState.getPlayerHand(controller.getPlayingField.getAttacker).size) match {
            case Some(index) => controller.regularSwap(index)
            case None => println("❌ Invalid swap index! Try again.")
          }

//        case PromptState.Swap =>
//          parser.parseSwap(input, controller.getPlayingField.fieldState.getPlayerHand(controller.getPlayingField.getAttacker).size) match {
//            case Some(index) => controller.circularSwap(index)
//            case None => println("❌ Invalid swap index! Try again.")
//          }

        /** 🔹 Load Game */
//        case PromptState.LoadGame =>
//          parser.parseFilePath(input) match {
//            case Some() => controller.loadGame()
//            case None => println("❌ Invalid file path! Try again.")
//          }

        /** 🔹 Save Game */
//        case PromptState.SaveGame =>
//          parser.parseFilePath(input) match {
//            case Some() => controller.saveGame()
//            case None => println("❌ Invalid file path! Try again.")
//          }

        /** ❌ Invalid Input */
        case _ =>
          println("❌ Invalid input. Try again!")
      }
    }
  }

  override def update(e: ObservableEvent): Unit = {
    e match {

      case Events.StartGame =>
        promptState = PromptState.NewGame
        prompter.promptNewGame()

      /** 🔴 Quit the game */
      case Events.Quit =>
        println("👋 Goodbye!")
        System.exit(0)

      /** 🛡️ Prompt for Attacking a Defender */
      case Events.RegularAttack =>
        promptState = PromptState.Attack
        prompter.promptAttack()

      /** ⚡ Prompt for Boosting a Defender */
      case Events.BoostDefender =>
        promptState = PromptState.Boost
        prompter.promptBoost()

      /** 🔄 Prompt for Swapping a Card */
      case Events.RegularSwap =>
        promptState = PromptState.Swap
        prompter.promptSwap()

      /** 💾 Load Game */
      case Events.LoadGame =>
        println("✅ Game loaded successfully!")
//        printStatusScreen()

      /** 💾 Save Game */
      case Events.SaveGame =>
        println("✅ Game saved successfully!")

      /** 🔄 Default: Refresh Game Status */
      case _ =>
        printStatusScreen()
    }
  }

}
