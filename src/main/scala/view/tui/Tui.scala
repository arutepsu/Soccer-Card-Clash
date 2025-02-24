package view.tui

import controller.{Events, IController}
import scalafx.application.Platform
import util.{Observable, ObservableEvent, Observer}
//import view.gui.scenes.sceneManager.SceneManager.{attackerDefendersScene, attackerHandScene, controller, createPlayerScene, mainMenuScene, playingFieldScene, switchScene}
import view.tui.PromptState
import view.tui.tuiCommand.base.ITuiCommand
import view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand

enum PromptState {
  case None            // No active prompt
  case STARTGAME
  case LoadGame
  case SaveGame
  case SingleAttack
  case DoubleAttack
  case RegularSwap
  case Redo
  case Undo
  case Boost
  case PlayingField
  case MainMenu
  case CreatePlayers
  case Exit
  case Reverted
}
class Tui(controller: IController) extends Observer {
  controller.add(this)

  private var promptState: PromptState = PromptState.None
  private val tuiCommandFactory: ITuiCommandFactory = new TuiCommandFactory(controller)
  private val createPlayersNameTuiCommand: CreatePlayersNameTuiCommand = tuiCommandFactory.createCreatePlayersNameTuiCommand()
  private val prompter = new Prompter(controller)
  private var waitingForNames: Boolean = false

  private val commands: Map[String, ITuiCommand] = Map(
    TuiKeys.Attack.key -> tuiCommandFactory.createAttackTuiCommand(),
    TuiKeys.BoostDefender.key -> tuiCommandFactory.createBoostDefenderTuiCommand(),
    TuiKeys.RegularSwap.key -> tuiCommandFactory.createRegularSwapTuiCommand(),
    TuiKeys.CreatePlayers.key -> createPlayersNameTuiCommand,
    TuiKeys.Undo.key -> tuiCommandFactory.createUndoTuiCommand(),
    TuiKeys.Redo.key -> tuiCommandFactory.createRedoTuiCommand(),
    TuiKeys.Exit.key -> tuiCommandFactory.createExitTuiCommand()
  )

  def processInputLine(input: String): Unit = {
    println(s"🛠 Received input: '$input'") // Debug print

    val parts = input.split(" ").map(_.trim)
    val commandKey = parts.head
    val commandArg = if (parts.length > 1) Some(parts(1)) else None

    // ✅ Step 1: Handle player name input if waiting for names
    if (waitingForNames && createPlayersNameTuiCommand.handlePlayerNames(input)) {
      waitingForNames = false
      return
    }

    // ✅ Step 2: If StartGame is selected, ask for names first
    if (commandKey == TuiKeys.StartGame.key) {
      createPlayersNameTuiCommand.execute() // Trigger name input prompt
      waitingForNames = true
      return
    }
    commandKey match {
      case TuiKeys.Attack.key => prompter.promptRegularAttack()
      case TuiKeys.BoostDefender.key => prompter.promptBoost()
      case TuiKeys.DoubleAttack.key => prompter.promptDoubleAttack()
      case TuiKeys.RegularSwap.key => prompter.promptSwap()
      case TuiKeys.CreatePlayers.key => prompter.promptCreatePlayers()
      case _ => // No prompt for other commands
    }

    // ✅ Step 3: Process Regular Commands Using `TuiKeys`
    commands.get(commandKey) match {
      case Some(command: ITuiCommand) =>
        command.execute(commandArg)

      case None =>
        println("❌ Unknown command. Try again.")
    }
  }

  private def handlePlayerNames(input: String): Boolean = {
    val playerNames = input.split(" ").map(_.trim).filter(_.nonEmpty)

    if (playerNames.length == 2) {
      val player1 = playerNames(0)
      val player2 = playerNames(1)

      println(s"✅ Players set: $player1 & $player2") // Debugging print
      waitingForNames = false // Reset state

      // Create StartGameCommand dynamically and execute it
      val startGameCommand = tuiCommandFactory.createStartGameTuiCommand(player1, player2)
      startGameCommand.execute()

      return true
    } else {
      println("❌ Invalid format! Enter names in the format: `player1 player2`.")
    }
    false
  }
  override def update(e: ObservableEvent): Unit = {
    e match {

      case Events.MainMenu =>
        promptState = PromptState.MainMenu
        prompter.promptMainMenu()

      case Events.CreatePlayers =>
        promptState = PromptState.CreatePlayers
        prompter.promptCreatePlayers()

      case Events.StartGame =>
        promptState = PromptState.STARTGAME
        prompter.promptNewGame()

      case Events.Quit =>
        promptState = PromptState.Exit
        prompter.promptExit()

      case Events.PlayingField =>
        promptState = PromptState.PlayingField
        prompter.promptPlayingField()

      case Events.RegularAttack =>
        promptState = PromptState.SingleAttack
        prompter.promptShowDefendersField()
        prompter.promptShowAttackersHand()

      case Events.DoubleAttack =>
        promptState = PromptState.DoubleAttack
        prompter.promptShowDefendersField()
        prompter.promptShowAttackersHand()

      case Events.BoostDefender =>
        promptState = PromptState.Boost
        prompter.promptShowDefendersField()

      case Events.RegularSwap =>
        promptState = PromptState.RegularSwap
        prompter.promptShowAttackersHand()

      case Events.LoadGame =>
        promptState = PromptState.LoadGame
        prompter.promptLoadGame()

      /** 💾 Save Game */
      case Events.SaveGame =>
        promptState = PromptState.SaveGame
        prompter.promptSaveGame()

      case Events.Undo =>
        promptState = PromptState.Undo
        println("Undo")

      case Events.Redo =>
        promptState = PromptState.Redo
        println("Redo")
      /** 🔄 Default: Refresh Game Status */
      case _ => println("in tui error")
    }
  }

}
