package de.htwg.se.soccercardclash.view.tui

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import scalafx.application.Platform
import de.htwg.se.soccercardclash.util.{GameActionEvent, GlobalObservable, Observable, ObservableEvent, Observer, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.tui
import de.htwg.se.soccercardclash.view.tui.PromptState
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{CreatePlayersNameTuiCommand, CreatePlayersNameWithAITuiCommand}

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

class Tui(
           controller: IController,
           gameContextHolder: IGameContextHolder,
         ) extends Observer {

  GlobalObservable.add(this)
  controller.add(this)
  protected val prompter: IPrompter = new Prompter(controller, gameContextHolder)
  protected val tuiCommandFactory: ITuiCommandFactory = new TuiCommandFactory(controller, gameContextHolder, prompter)
  private var promptState: PromptState = PromptState.None
//  private val createPlayersNameTuiCommand:
//    CreatePlayersNameTuiCommand = tuiCommandFactory.createCreatePlayersNameTuiCommand()
//  private val createPlayersNameWitAITuiCommand:
//    CreatePlayersNameWithAITuiCommand = tuiCommandFactory.createCreatePlayersNameWithAITuiCommand()
  private val commands: Map[String, ITuiCommand] = Map(
//    TuiKeys.CreatePlayers.key -> createPlayersNameTuiCommand,
//    TuiKeys.CreatePlayersAI.key -> createPlayersNameWitAITuiCommand,
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

//    if (createPlayersNameWitAITuiCommand.handlePlayerNames(input)) return
//    if (createPlayersNameTuiCommand.handlePlayerNames(input)) return

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

//    if (commandKey == TuiKeys.StartGameWithAI.key) {
//      createPlayersNameWitAITuiCommand.execute()
//      return
//    }
//
//    if (commandKey == TuiKeys.StartGameMultiplayer.key) {
//      createPlayersNameTuiCommand.execute()
//      return
//    }

    commandKey match {
      case TuiKeys.Attack.key =>
        promptState = PromptState.SingleAttack
        prompter.promptRegularAttack()
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.defender)

      case TuiKeys.DoubleAttack.key =>
        promptState = PromptState.DoubleAttack
        prompter.promptDoubleAttack()
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.defender)

      case TuiKeys.BoostDefender.key =>
        promptState = PromptState.Boost
        prompter.promptBoost()
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.attacker)

      case TuiKeys.RegularSwap.key =>
        promptState = PromptState.RegularSwap
        prompter.promptSwap()
        prompter.promptShowAttackersHand()

      case TuiKeys.ReverseSwap.key =>
        promptState = PromptState.ReverseSwap
        prompter.promptShowAttackersHand()

      case TuiKeys.BoostGoalkeeper.key =>
        promptState = PromptState.BoostGoalkeeper
        prompter.promptShowGoalkeeper(gameContextHolder.get.state.getRoles.attacker)

      case TuiKeys.CreatePlayers.key =>
        GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayer)

      case TuiKeys.CreatePlayersAI.key =>
        GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)

      case TuiKeys.Save.key =>
        commands(TuiKeys.Save.key).execute()

      case TuiKeys.ShowGames.key =>
        GlobalObservable.notifyObservers(SceneSwitchEvent.LoadGame)

      case _ =>
        commands.get(commandKey) match {
          case Some(command) => command.execute(commandArg)
          case None => println("Unknown command. Try again.")
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
        } else println("Invalid number.")
      case _ => println("Usage: select <number>")
    }
  }

  override def update(e: ObservableEvent): Unit = {
    e match {
      case SceneSwitchEvent.MainMenu =>
        promptState = PromptState.MainMenu
        prompter.promptMainMenu()

      case SceneSwitchEvent.CreatePlayer =>
        promptState = PromptState.CreatePlayers
        prompter.promptCreatePlayers()

      case SceneSwitchEvent.StartGame =>
        promptState = PromptState.StartGame
        prompter.promptNewGame()

      case SceneSwitchEvent.Exit =>
        promptState = PromptState.Exit
        prompter.promptExit()

      case SceneSwitchEvent.PlayingField =>
        promptState = PromptState.PlayingField
        prompter.promptPlayingField()

      case GameActionEvent.RegularAttack =>
        promptState = PromptState.SingleAttack
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.defender)
        prompter.promptShowAttackersHand()

      case GameActionEvent.DoubleAttack =>
        promptState = PromptState.DoubleAttack
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.defender)
        prompter.promptShowAttackersHand()

      case GameActionEvent.BoostDefender =>
        promptState = PromptState.Boost
        prompter.promptShowDefendersField(gameContextHolder.get.state.getRoles.attacker)

      case GameActionEvent.BoostGoalkeeper =>
        promptState = PromptState.BoostGoalkeeper
        prompter.promptShowGoalkeeper(gameContextHolder.get.state.getRoles.attacker)

      case GameActionEvent.RegularSwap =>
        promptState = PromptState.RegularSwap
        prompter.promptShowAttackersHand()

      case GameActionEvent.ReverseSwap =>
        promptState = PromptState.ReverseSwap
        prompter.promptShowAttackersHand()

      case SceneSwitchEvent.LoadGame =>
        promptState = PromptState.LoadGame
        prompter.showAvailableGames()

      case GameActionEvent.SaveGame =>
        promptState = PromptState.SaveGame
        prompter.promptSaveGame()

      case GameActionEvent.Undo =>
        promptState = PromptState.Undo
        prompter.promptUndo()
        prompter.printGameState()

      case GameActionEvent.Redo =>
        promptState = PromptState.Redo
        prompter.promptRedo()
        prompter.printGameState()

      case _ =>
    }
  }
}
