package de.htwg.se.soccercardclash.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.mockito.ArgumentCaptor
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.{IPrompter, Tui, TuiKeys}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IRoles
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand

class TestController extends IController {
  override def getCurrentGame: IGame = null
  override def executeCommand(command: ICommand, event: Events): Unit = {}
  override def undo(): Unit = {}
  override def redo(): Unit = {}
  override def executeSingleAttackCommand(defenderPosition: Int): Unit = {}
  override def executeDoubleAttackCommand(defenderPosition: Int): Unit = {}
  override def boostDefender(defenderPosition: Int): Unit = {}
  override def boostGoalkeeper(): Unit = {}
  override def regularSwap(index: Int): Unit = {}
  override def reverseSwap(): Unit = {}
  override def createGame(player1: String, player2: String): Unit = {}
  override def quit(): Unit = {}
  override def saveGame(): Unit = {}
  override def loadGame(fileName: String): Unit = {}
  override def resetGame(): Unit = {}

  // You get add/remove/notifyObservers from IController (which extends Observable)
  override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
}
class TuiSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Tui" should {
    val mockController = spy(new TestController)
    val mockPrompter = mock[IPrompter]
    val mockFactory = mock[ITuiCommandFactory]
    val mockGame = mock[IGame]
    val mockField = mock[IGameState]
    val mockRolesManager = mock[IRoles]
    val mockDefender = mock[IPlayer]

    val mockCreatePlayersCommand = mock[CreatePlayersNameTuiCommand]
    when(mockFactory.createCreatePlayersNameTuiCommand()).thenReturn(mockCreatePlayersCommand)
    when(mockCreatePlayersCommand.handlePlayerNames(any[String])).thenReturn(false)

    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)
    when(mockField.getRoles).thenReturn(mockRolesManager)
    when(mockRolesManager.defender).thenReturn(mockDefender)

    val tui = new Tui(mockController, mockFactory, mockPrompter)


    "execute Save command on input" in {
      val saveCommand = mock[ITuiCommand]
      when(mockFactory.createSaveGameTuiCommand()).thenReturn(saveCommand)
      val mockCreatePlayersCommand = mock[CreatePlayersNameTuiCommand]
      when(mockFactory.createCreatePlayersNameTuiCommand()).thenReturn(mockCreatePlayersCommand)
      when(mockCreatePlayersCommand.handlePlayerNames(any[String])).thenReturn(false)

      val tui = new Tui(mockController, mockFactory, mockPrompter)

      tui.processInputLine(TuiKeys.Save.key)

      verify(saveCommand).execute()

    }

    "prompt for regular attack and update prompt state" in {
      when(mockFactory.createSingleAttackTuiCommand()).thenReturn(mock[ITuiCommand])
      tui.processInputLine(TuiKeys.Attack.key)

      verify(mockPrompter).promptRegularAttack()
      verify(mockPrompter).promptShowDefendersField(mockDefender)
    }

    "execute a known promptHandler command" in {
      val doubleAttackCommand = mock[ITuiCommand]
      when(mockFactory.createDoubleAttackTuiCommand()).thenReturn(doubleAttackCommand)

      val tui = new Tui(mockController, mockFactory, mockPrompter)

      // simulate the prompt state via update
      tui.update(Events.DoubleAttack)
      tui.processInputLine("some_input")

      verify(doubleAttackCommand).execute(Some("some_input"))

    }

//    "handle unknown input gracefully" in {
//      val output = new java.io.ByteArrayOutputStream()
//      Console.withOut(output) {
//        tui.processInputLine("invalidCommand")
//      }
//      output.toString should include("Unknown command") // dropped emoji ✅
//    }


    "respond to Events.Undo and print state" in {
      tui.update(Events.Undo)
      verify(mockPrompter).promptUndo()
      verify(mockPrompter).printGameState()
    }

    "respond to Events.LoadGame and show games" in {
      tui.update(Events.LoadGame)
      verify(mockPrompter).showAvailableGames()
    }

    "handle load game input with index" in {
      val loadSelected = mock[ITuiCommand]
      when(mockFactory.createLoadSelectedGameTuiCommand(1)).thenReturn(loadSelected)
      tui.update(Events.LoadGame)
      tui.processInputLine("select 1")

      verify(loadSelected).execute()
    }

    "handle invalid load game input" in {
      tui.update(Events.LoadGame)
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        tui.processInputLine("select notANumber")
      }
      output.toString should include("❌ Usage: select <number>")
    }
  }
}
