package de.htwg.se.soccercardclash.view.tui

import de.htwg.se.soccercardclash.controller.{Events, IController}
import scalafx.application.Platform
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.tui
import de.htwg.se.soccercardclash.view.tui.PromptState
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand

import java.io.File
import scala.io.StdIn

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

//class Tui(controller: IController) extends Observer {
//  controller.add(this)
//
//  private var promptState: PromptState = PromptState.None
//  val prompter: IPrompter = new Prompter(controller)
//  private val tuiCommandFactory: ITuiCommandFactory = new TuiCommandFactory(controller, prompter)
//  private val createPlayersNameTuiCommand: CreatePlayersNameTuiCommand = tuiCommandFactory.createCreatePlayersNameTuiCommand()
//  private var waitingForNames: Boolean = false
//
//  private val commands: Map[String, ITuiCommand] = Map(
//    TuiKeys.Attack.key -> tuiCommandFactory.createAttackTuiCommand(),
//    TuiKeys.BoostDefender.key -> tuiCommandFactory.createBoostDefenderTuiCommand(),
//    TuiKeys.RegularSwap.key -> tuiCommandFactory.createRegularSwapTuiCommand(),
//    TuiKeys.CreatePlayers.key -> createPlayersNameTuiCommand,
//    TuiKeys.Undo.key -> tuiCommandFactory.createUndoTuiCommand(),
//    TuiKeys.Redo.key -> tuiCommandFactory.createRedoTuiCommand(),
//    TuiKeys.Save.key -> tuiCommandFactory.createSaveGameTuiCommand(),
//    TuiKeys.Exit.key -> tuiCommandFactory.createExitTuiCommand(),
//    TuiKeys.ShowGames.key -> tuiCommandFactory.createShowGamesTuiCommand(),
//
//  )
//
//  def processInputLine(input: String): Unit = {
//    println(s"🛠 Received input: '$input'")
//
//    val parts = input.split(" ").map(_.trim)
//    val commandKey = parts.head
//    val commandArg = if (parts.length > 1) Some(parts(1)) else None
//
//    if (waitingForNames && createPlayersNameTuiCommand.handlePlayerNames(input)) {
//      waitingForNames = false
//      return
//    }
//
//    if (commandKey == TuiKeys.StartGame.key) {
//      createPlayersNameTuiCommand.execute()
//      waitingForNames = true
//      return
//    }
//
//
//    commandKey match {
//      case TuiKeys.Attack.key =>
//        prompter.promptRegularAttack()
//
//      case TuiKeys.BoostDefender.key =>
//        prompter.promptBoost()
//
//      case TuiKeys.DoubleAttack.key =>
//        prompter.promptDoubleAttack()
//
//      case TuiKeys.RegularSwap.key =>
//        prompter.promptSwap()
//
//      case TuiKeys.CreatePlayers.key =>
//        prompter.promptCreatePlayers()
//
//      case TuiKeys.Save.key =>
//        prompter.promptSaveGame()
//
//      case TuiKeys.ShowGames.key =>
//        controller.notifyObservers(Events.LoadGame)
//        prompter.showAvailableGames()
//        val pattern = """select\s+(\d+)""".r
//        commandArg match {
//          case Some(pattern(numStr)) =>
//            val index = numStr.toIntOption.getOrElse(-1)
//            if (index >= 0)
//              prompter.loadSelectedGame(index, tuiCommandFactory)
//            else
//              println("❌ Please enter a valid number after 'select'.")
//          case _ =>
//            println("❌ Usage: select <number>")
//        }
//
//      case _ =>
//        commands.get(commandKey) match {
//          case Some(command) =>
//            command.execute(commandArg)
//          case None =>
//            println("❌ Unknown command. Try again.")
//        }
//    }
//  }
//
//  private def handlePrimaryCommand(input: String): Unit = {
//    val parts = input.split(" ").map(_.trim)
//    val commandKey = parts.head
//    val commandArg = if (parts.length > 1) Some(parts(1)) else None
//
//    if (commandKey == TuiKeys.StartGame.key) {
//      createPlayersNameTuiCommand.execute()
//      waitingForNames = true
//      return
//    }
//
//    commandKey match {
//      case TuiKeys.Attack.key =>
//        controller.notifyObservers(Events.RegularAttack)
//
//      case TuiKeys.DoubleAttack.key =>
//        controller.notifyObservers(Events.DoubleAttack)
//
//      case TuiKeys.BoostDefender.key =>
//        controller.notifyObservers(Events.BoostDefender)
//
//      case TuiKeys.RegularSwap.key =>
//        controller.notifyObservers(Events.RegularSwap)
//
//      case TuiKeys.CreatePlayers.key =>
//        controller.notifyObservers(Events.CreatePlayers)
//
//      case TuiKeys.Save.key =>
//        controller.notifyObservers(Events.SaveGame)
//
//      case TuiKeys.ShowGames.key =>
//        controller.notifyObservers(Events.LoadGame)
//
//      case _ =>
//        commands.get(commandKey) match {
//          case Some(command) =>
//            command.execute(commandArg)
//          case None =>
//            println("❌ Unknown command. Try again.")
//        }
//    }
//  }
//
//  override def update(e: ObservableEvent): Unit = {
//    e match {
//      case Events.MainMenu =>
//        promptState = PromptState.MainMenu
//        prompter.promptMainMenu()
//
//      case Events.CreatePlayers =>
//        promptState = PromptState.CreatePlayers
//        prompter.promptCreatePlayers()
//
//      case Events.StartGame =>
//        promptState = PromptState.STARTGAME
//        prompter.promptNewGame()
//
//      case Events.Quit =>
//        promptState = PromptState.Exit
//        prompter.promptExit()
//
//      case Events.PlayingField =>
//        promptState = PromptState.PlayingField
//        prompter.promptPlayingField()
//
//      case Events.RegularAttack =>
//        promptState = PromptState.SingleAttack
//        prompter.promptShowDefendersField()
//        prompter.promptShowAttackersHand()
//
//      case Events.DoubleAttack =>
//        promptState = PromptState.DoubleAttack
//        prompter.promptShowDefendersField()
//        prompter.promptShowAttackersHand()
//
//      case Events.BoostDefender =>
//        promptState = PromptState.Boost
//        prompter.promptShowDefendersField()
//
//      case Events.RegularSwap =>
//        promptState = PromptState.RegularSwap
//        prompter.promptShowAttackersHand()
//
//      case Events.LoadGame =>
//        promptState = PromptState.LoadGame
//        prompter.showAvailableGames()
//
//      case Events.SaveGame =>
//        promptState = PromptState.SaveGame
//        prompter.promptSaveGame()
//
//      case Events.Undo =>
//        promptState = PromptState.Undo
//        println("Undo")
//
//      case Events.Redo =>
//        promptState = PromptState.Redo
//        println("Redo")
//
//      case _ =>
//    }
//  }
//}

class Tui(controller: IController) extends Observer {
  controller.add(this)

  private var promptState: PromptState = PromptState.None
  val prompter: IPrompter = new Prompter(controller)
  private val tuiCommandFactory: ITuiCommandFactory = new TuiCommandFactory(controller, prompter)
  private val createPlayersNameTuiCommand: CreatePlayersNameTuiCommand = tuiCommandFactory.createCreatePlayersNameTuiCommand()
  private var waitingForNames: Boolean = false

  private val commands: Map[String, ITuiCommand] = Map(
    TuiKeys.Attack.key -> tuiCommandFactory.createAttackTuiCommand(),
    TuiKeys.DoubleAttack.key -> tuiCommandFactory.createDoubleAttackTuiCommand(),
    TuiKeys.BoostDefender.key -> tuiCommandFactory.createBoostDefenderTuiCommand(),
    TuiKeys.RegularSwap.key -> tuiCommandFactory.createRegularSwapTuiCommand(),
    TuiKeys.CreatePlayers.key -> createPlayersNameTuiCommand,
    TuiKeys.Undo.key -> tuiCommandFactory.createUndoTuiCommand(),
    TuiKeys.Redo.key -> tuiCommandFactory.createRedoTuiCommand(),
    TuiKeys.Save.key -> tuiCommandFactory.createSaveGameTuiCommand(),
    TuiKeys.Exit.key -> tuiCommandFactory.createExitTuiCommand(),
    TuiKeys.ShowGames.key -> tuiCommandFactory.createShowGamesTuiCommand()
  )

  def processInputLine(input: String): Unit = {
    println(s"🛠 Received input: '$input'")

    if (waitingForNames && createPlayersNameTuiCommand.handlePlayerNames(input)) {
      waitingForNames = false
      return
    }

    promptState match {
      case PromptState.SingleAttack    => handleSingleAttackInput(input)
      case PromptState.DoubleAttack    => handleDoubleAttackInput(input)
      case PromptState.Boost           => handleBoostInput(input)
      case PromptState.RegularSwap     => handleSwapInput(input)
      case PromptState.LoadGame        => handleLoadGameInput(input)
      case _                           => handlePrimaryCommand(input)
    }
  }

  private def handlePrimaryCommand(input: String): Unit = {
    val parts = input.split(" ").map(_.trim)
    val commandKey = parts.head
    val commandArg = if (parts.length > 1) Some(parts(1)) else None

    if (commandKey == TuiKeys.StartGame.key) {
      createPlayersNameTuiCommand.execute()
      waitingForNames = true
      return
    }

    commandKey match {
      case TuiKeys.Attack.key =>
        promptState = PromptState.SingleAttack
        prompter.promptShowDefendersField()

      case TuiKeys.DoubleAttack.key =>
        controller.notifyObservers(Events.DoubleAttack)

      case TuiKeys.BoostDefender.key =>
        controller.notifyObservers(Events.BoostDefender)

      case TuiKeys.RegularSwap.key =>
        controller.notifyObservers(Events.RegularSwap)

      case TuiKeys.CreatePlayers.key =>
        controller.notifyObservers(Events.CreatePlayers)

      case TuiKeys.Save.key =>
        controller.notifyObservers(Events.SaveGame)

      case TuiKeys.ShowGames.key =>
        controller.notifyObservers(Events.LoadGame)

      case _ =>
        commands.get(commandKey) match {
          case Some(command) =>
            command.execute(commandArg)
          case None =>
            println("❌ Unknown command. Try again.")
        }
    }
  }

  private def handleSingleAttackInput(input: String): Unit = {
    input.toIntOption match {
      case Some(index) =>
        controller.executeSingleAttackCommand(index)
      case None =>
        println("❌ Invalid input. Please enter a number.")
    }
    promptState = PromptState.None
  }


  private def handleDoubleAttackInput(input: String): Unit = {
    commands.get(TuiKeys.DoubleAttack.key).foreach(_.execute(Some(input)))
    promptState = PromptState.None
  }

  private def handleBoostInput(input: String): Unit = {
    commands.get(TuiKeys.BoostDefender.key).foreach(_.execute(Some(input)))
    promptState = PromptState.None
  }

  private def handleSwapInput(input: String): Unit = {
    commands.get(TuiKeys.RegularSwap.key).foreach(_.execute(Some(input)))
    promptState = PromptState.None
  }

  private def handleLoadGameInput(input: String): Unit = {
    val pattern = """select\s+(\d+)""".r
    input match {
      case pattern(numStr) =>
        val index = numStr.toIntOption.getOrElse(-1)
        if (index >= 0) {
          prompter.loadSelectedGame(index, tuiCommandFactory)
          promptState = PromptState.None
        } else {
          println("❌ Invalid number.")
        }
      case _ =>
        println("❌ Usage: select <number>")
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
