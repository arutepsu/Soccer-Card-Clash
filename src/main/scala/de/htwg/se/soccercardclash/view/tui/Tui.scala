package de.htwg.se.soccercardclash.view.tui

import de.htwg.se.soccercardclash.controller.IController
import scalafx.application.Platform
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.tui
import de.htwg.se.soccercardclash.view.tui.PromptState
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand

import java.io.File
import scala.io.StdIn

enum PromptState {
  case None
  case StartGame
  case LoadGame
  case SaveGame
  case SingleAttack
  case DoubleAttack
  case RegularSwap
  case ReverseSwap
  case Redo
  case Undo
  case Boost
  case BoostGoalkeeper
  case PlayingField
  case MainMenu
  case CreatePlayers
  case Exit
  case Reverted
}

class Tui(controller: IController) extends Observer {
  controller.add(this)

  private var promptState: PromptState = PromptState.None
  protected val prompter: IPrompter = new Prompter(controller)
  protected val tuiCommandFactory: ITuiCommandFactory = new TuiCommandFactory(controller, new Prompter(controller))
  private val createPlayersNameTuiCommand: CreatePlayersNameTuiCommand = tuiCommandFactory.createCreatePlayersNameTuiCommand()

  private val commands: Map[String, ITuiCommand] = Map(
    TuiKeys.CreatePlayers.key -> createPlayersNameTuiCommand,
    TuiKeys.Undo.key -> tuiCommandFactory.createUndoTuiCommand(),
    TuiKeys.Redo.key -> tuiCommandFactory.createRedoTuiCommand(),
    TuiKeys.Save.key -> tuiCommandFactory.createSaveGameTuiCommand(),
    TuiKeys.Exit.key -> tuiCommandFactory.createExitTuiCommand(),
    TuiKeys.ShowGames.key -> tuiCommandFactory.createShowGamesTuiCommand()
  )

  private val promptHandlers: Map[PromptState, String => Unit] = Map(
    PromptState.SingleAttack -> (input => runCommand(tuiCommandFactory.createSingleAttackTuiCommand(), input)),
    PromptState.DoubleAttack -> (input => runCommand(tuiCommandFactory.createDoubleAttackTuiCommand(), input)),
    PromptState.Boost -> (input => runCommand(tuiCommandFactory.createBoostDefenderTuiCommand(), input)),
    PromptState.RegularSwap -> (input => runCommand(tuiCommandFactory.createRegularSwapTuiCommand(), input)),
    PromptState.ReverseSwap -> (_ => runCommand(tuiCommandFactory.createReverseSwapTuiCommand(), "")),
    PromptState.BoostGoalkeeper -> (_ => runCommand(tuiCommandFactory.createBoostGoalkeeperTuiCommand(), "")),
    PromptState.LoadGame -> handleLoadGameInput
  )

  def processInputLine(input: String): Unit = {
    println(s"\uD83D\uDEE0 Received input: '$input'")

    if (createPlayersNameTuiCommand.handlePlayerNames(input)) return

      promptHandlers.get(promptState) match {
        case Some(handler) => handler(input)
        case None => handlePrimaryCommand(input)
      }
  }

  private def runCommand(command: ITuiCommand, input: String): Unit = {
    command.execute(Some(input))
    promptState = PromptState.None
  }

  private def handlePrimaryCommand(input: String): Unit = {
    val parts = input.split(" ").map(_.trim)
    val commandKey = parts.head
    val commandArg = if (parts.length > 1) Some(parts(1)) else None

    if (commandKey == TuiKeys.StartGame.key) {
      createPlayersNameTuiCommand.execute()
      return
    }

    commandKey match {
      case TuiKeys.Attack.key =>
        promptState = PromptState.SingleAttack
        prompter.promptRegularAttack()
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.defender)

      case TuiKeys.DoubleAttack.key =>
        promptState = PromptState.DoubleAttack
        prompter.promptDoubleAttack()
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.defender)

      case TuiKeys.BoostDefender.key =>
        promptState = PromptState.Boost
        prompter.promptBoost()
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.attacker)

      case TuiKeys.RegularSwap.key =>
        promptState = PromptState.RegularSwap
        prompter.promptSwap()
        prompter.promptShowAttackersHand()

      case TuiKeys.ReverseSwap.key =>
        promptState = PromptState.ReverseSwap
        prompter.promptShowAttackersHand()

      case TuiKeys.BoostGoalkeeper.key =>
        promptState = PromptState.BoostGoalkeeper
        prompter.promptShowGoalkeeper(controller.getCurrentGame.getPlayingField.getRoles.attacker)

      case TuiKeys.CreatePlayers.key =>
        controller.notifyObservers(Events.CreatePlayers)

      case TuiKeys.Save.key =>
        commands(TuiKeys.Save.key).execute()

      case TuiKeys.ShowGames.key =>
        controller.notifyObservers(Events.LoadGame)

      case _ =>
        commands.get(commandKey) match {
          case Some(command) => command.execute(commandArg)
          case None => println("❌ Unknown command. Try again.")
        }
    }
  }

  private def handleLoadGameInput(input: String): Unit = {
    val pattern = """(?i)select\s+(\d+)""".r
    input match {
      case pattern(numStr) =>
        val index = numStr.toIntOption.getOrElse(-1)
        if (index >= 0) {
          tuiCommandFactory.createLoadSelectedGameTuiCommand(index).execute()
          promptState = PromptState.None
        } else println("❌ Invalid number.")
      case _ => println("❌ Usage: select <number>")
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
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.defender)
        prompter.promptShowAttackersHand()

      case Events.DoubleAttack =>
        promptState = PromptState.DoubleAttack
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.defender)
        prompter.promptShowAttackersHand()

      case Events.BoostDefender =>
        promptState = PromptState.Boost
        prompter.promptShowDefendersField(controller.getCurrentGame.getPlayingField.getRoles.attacker)

      case Events.BoostGoalkeeper =>
        promptState = PromptState.BoostGoalkeeper
        prompter.promptShowGoalkeeper(controller.getCurrentGame.getPlayingField.getRoles.attacker)

      case Events.RegularSwap =>
        promptState = PromptState.RegularSwap
        prompter.promptShowAttackersHand()

      case Events.ReverseSwap =>
        promptState = PromptState.ReverseSwap
        prompter.promptShowAttackersHand()

      case Events.LoadGame =>
        promptState = PromptState.LoadGame
        prompter.showAvailableGames()

      case Events.SaveGame =>
        promptState = PromptState.SaveGame
        prompter.promptSaveGame()

      case Events.Undo =>
        promptState = PromptState.Undo
        prompter.promptUndo()
        prompter.printGameState()

      case Events.Redo =>
        promptState = PromptState.Redo
        prompter.promptRedo()
        prompter.printGameState()

      case _ =>
    }
  }
}
