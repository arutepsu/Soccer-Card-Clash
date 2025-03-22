package de.htwg.se.soccercardclash.view.tui

import de.htwg.se.soccercardclash.controller.{Events, IController}
import scalafx.application.Platform
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.tui
import de.htwg.se.soccercardclash.view.tui.PromptState
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand

enum PromptState {
  case None
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
import java.io.File
import scala.io.StdIn

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
    TuiKeys.Save.key -> tuiCommandFactory.createSaveGameTuiCommand(),
    TuiKeys.Exit.key -> tuiCommandFactory.createExitTuiCommand()
  )

  // Add a new flag
  private var waitingForGameSelection: Boolean = false

  def processInputLine(input: String): Unit = {
    println(s"🛠 Received input: '$input'")

    val parts = input.split(" ").map(_.trim)
    val commandKey = parts.head
    val commandArg = if (parts.length > 1) Some(parts(1)) else None

    if (waitingForNames && createPlayersNameTuiCommand.handlePlayerNames(input)) {
      waitingForNames = false
      return
    }

    if (commandKey == TuiKeys.StartGame.key) {
      createPlayersNameTuiCommand.execute()
      waitingForNames = true
      return
    }

    if (waitingForGameSelection && input.forall(_.isDigit)) {
      val index = input.toInt - 1
      prompter.loadSelectedGame(index, tuiCommandFactory)
      waitingForGameSelection = false
      return
    } else if (input.forall(_.isDigit)) {
      println("❌ You must first type 'load' to see available games before selecting one.")
      return
    }

    commandKey match {
      case TuiKeys.Attack.key =>
        prompter.promptRegularAttack()
        waitingForGameSelection = false

      case TuiKeys.BoostDefender.key =>
        prompter.promptBoost()
        waitingForGameSelection = false

      case TuiKeys.DoubleAttack.key =>
        prompter.promptDoubleAttack()
        waitingForGameSelection = false

      case TuiKeys.RegularSwap.key =>
        prompter.promptSwap()
        waitingForGameSelection = false

      case TuiKeys.CreatePlayers.key =>
        prompter.promptCreatePlayers()
        waitingForGameSelection = false

      case TuiKeys.Save.key =>
        prompter.promptSaveGame()
        waitingForGameSelection = false

      case TuiKeys.Load.key =>
        controller.notifyObservers(Events.LoadGame)
        prompter.showAvailableGames()
        waitingForGameSelection = true

      case "select" if commandArg.isDefined && commandArg.get.forall(_.isDigit) =>
        if (waitingForGameSelection) {
          val index = commandArg.get.toInt - 1
          prompter.loadSelectedGame(index, tuiCommandFactory)
          waitingForGameSelection = false
        } else {
          println("❌ Unknown command. Try again")
        }

      case _ =>
        waitingForGameSelection = false
        commands.get(commandKey) match {
          case Some(command) =>
            command.execute(commandArg)
          case None =>
            println("❌ Unknown command. Try again.")
        }
    }
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
        prompter.showAvailableGames()

      case Events.SaveGame =>
        promptState = PromptState.SaveGame
        prompter.promptSaveGame()

      case Events.Undo =>
        promptState = PromptState.Undo
        println("Undo")

      case Events.Redo =>
        promptState = PromptState.Redo
        println("Redo")

      case _ =>
    }
  }
}
