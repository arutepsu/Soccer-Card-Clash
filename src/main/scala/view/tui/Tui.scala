package view.tui

import controller.{Events, IController}
import scalafx.application.Platform
import util.{Observable, ObservableEvent, Observer}
import view.gui.scenes.sceneManager.SceneManager.{attackerDefendersScene, attackerHandScene, controller, createPlayerScene, mainMenuScene, playingFieldScene, switchScene}
import view.tui.PromptState
enum PromptState {
  case None            // No active prompt
  case StartGame        // New game setup
  case LoadGame        // Loading game
  case SaveGame        // Saving game
  case Attack          // Selecting an attack
  case DoubleAttack
  case Swap            // Swapping cards
  case Redo        // Undo/Redo operations
  case Undo        // Undo/Redo operations
  case Boost
  case PlayingField
}
class Tui(controller: IController) extends Observer {
  private var promptState: PromptState = PromptState.None
  private val startGameCommand = new StartGameCommand(controller)
  private val attackCommand = new AttackCommand(controller)
  private val boostCommand = new BoostCommand(controller)
  private val regularSwapCommand = new RegularSwapCommand(controller)
  private val prompter = new Prompter(controller)
  private val commands: Map[String, TuiCommand] = Map(
    TuiKeys.StartGame.key -> startGameCommand,
    TuiKeys.Attack.key -> attackCommand,
    TuiKeys.BoostDefender.key -> boostCommand,
    TuiKeys.RegularSwap.key -> regularSwapCommand
  )

  def processInputLine(input: String): Unit = {
    println(s"🛠 Received input: '$input'") // Debug print

    val parts = input.split(" ").map(_.trim)
    val commandKey = parts.head
    val commandArg = if (parts.length > 1) Some(parts(1)) else None

    // ✅ Step 1: Check if handling player name input
    if (startGameCommand.handlePlayerNames(input)) return

      // ✅ Step 2: Process Regular Commands Using `TuiKeys`
      commands.get(commandKey) match {
        case Some(command: TuiCommand) =>
          command.execute(commandArg)

        case None=> // Handle swap input separately
          ()

        case _ =>
          println("❌ Unknown command. Try again.")
      }
  }

  override def update(e: ObservableEvent): Unit = {
    e match {

      case Events.StartGame =>
        promptState = PromptState.StartGame
        prompter.promptNewGame()

      /** 🔴 Quit the game */
      case Events.Quit =>
        prompter.promptExit()

      case Events.PlayingField =>
        println("⚽ Game Started! Displaying playing field.")
       prompter.promptPlayingField()

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
      case _ => println("error")
    }
  }

}
