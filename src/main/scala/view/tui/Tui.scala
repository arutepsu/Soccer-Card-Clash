package view.tui

import controller.{Events, IController}
import scalafx.application.Platform
import util.{Observable, ObservableEvent, Observer}
import view.gui.scenes.sceneManager.SceneManager.{attackerDefendersScene, attackerHandScene, controller, createPlayerScene, mainMenuScene, playingFieldScene, switchScene}
import view.tui.PromptState
enum PromptState {
  case None            // No active prompt
  case StartGame
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
    TuiKeys.DoubleAttack.key -> tuiCommandFactory.createDoubleAttackCommand()
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
        promptState = PromptState.StartGame
        prompter.promptNewGame()

      case Events.Quit =>
        promptState = PromptState.Exit
        prompter.promptExit()

      case Events.PlayingField =>
        promptState = PromptState.PlayingField
        prompter.promptPlayingField()

      case Events.RegularAttack =>
        promptState = PromptState.SingleAttack
        prompter.promptRegularAttack()

      case Events.DoubleAttack =>
        promptState = PromptState.DoubleAttack
        prompter.promptDoubleAttack()

      case Events.BoostDefender =>
        promptState = PromptState.Boost
        prompter.promptBoost()

      case Events.RegularSwap =>
        promptState = PromptState.RegularSwap
        prompter.promptSwap()

      case Events.LoadGame =>
        println("✅ Game loaded successfully!")

      /** 💾 Save Game */
      case Events.SaveGame =>
        println("✅ Game saved successfully!")

      /** 🔄 Default: Refresh Game Status */
      case _ => println("error")
    }
  }

}
