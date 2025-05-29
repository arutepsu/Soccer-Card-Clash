package de.htwg.se.soccercardclash.tui

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.tui
import de.htwg.se.soccercardclash.view.tui.{IPrompter, PromptState, Prompter, Tui}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.{ITuiCommandFactory, TuiCommandFactory}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{CreatePlayersNameTuiCommand, CreatePlayersNameWithAITuiCommand}
import de.htwg.se.soccercardclash.util.*

import java.io.File
import scala.io.StdIn
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.{spy, verify, verifyNoInteractions, when}

class TuiSpec extends AnyWordSpec with Matchers with MockitoSugar {

  class DummyController extends Observable with IController {

    override def createGame(player1: String, player2: String): Unit = ()

    override def createGameWithAI(humanPlayerName: String, aiName: String): Unit = ()

    override def quit(): Unit = ()

    override def undo(ctx: GameContext): GameContext = ctx

    override def redo(ctx: GameContext): GameContext = ctx

    override def saveGame(ctx: GameContext): Boolean = true

    override def loadGame(fileName: String): Boolean = true

    override def singleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def doubleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def boostDefender(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def boostGoalkeeper(ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def regularSwap(index: Int, ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def reverseSwap(ctx: GameContext): (GameContext, Boolean) = (ctx, true)

    override def executeAIAction(action: AIAction, ctx: GameContext): (GameContext, Boolean) = (ctx, true)
  }
  class DummySaveCommand extends ITuiCommand {
    var executed: Boolean = false
    override def execute(input: Option[String] = None): Unit = {
      executed = true
      println("Save command executed")
    }
  }

  class TestPrompter extends IPrompter {
    var regularAttackCalled = false
    var showDefendersFieldCalled = false
    var doubleAttackCalled = false
    var boostCalled = false
    var swapCalled = false
    var showGoalkeeperCalled = false
    var showAttackerHandCalled = false

    override def promptShowGoalkeeper(player: IPlayer): Unit = showGoalkeeperCalled = true
    override def promptShowAttackersHand(): Unit = showAttackerHandCalled = true
    override def promptRegularAttack(): Unit = regularAttackCalled = true
    override def promptDoubleAttack(): Unit = doubleAttackCalled = true
    override def promptBoost(): Unit = boostCalled = true
    override def promptSwap(): Unit = swapCalled= true
    override def promptShowDefendersField(player: IPlayer): Unit = showDefendersFieldCalled = true

    // All other methods do nothing
    def promptMainMenu(): Unit = ()

    def promptCreatePlayers(): Unit = ()

    def promptNewGame(): Unit = ()

    def promptExit(): Unit = ()

    def promptPlayingField(): Unit = ()

    def showAvailableGames(): Unit = ()

    def promptSaveGame(): Unit = ()

    def promptUndo(): Unit = ()

    def promptRedo(): Unit = ()

    def printGameState(): Unit = ()

    def promptPlayersName(): Unit = ()
  }

  "A Tui" should {

    "initialize with mocked dependencies" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]

      val tui = new Tui(controller, contextHolder)

      // Verify the Tui instance is created (not null) and promptState starts as None
      tui should not be null
    }
  }
  "processInputLine" should {
    "print unknown command message for invalid input" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val tui = new Tui(controller, contextHolder)

      // Capture console output
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.processInputLine("invalidCommand")
      }

      stream.toString should include("Unknown command")
    }
  }
  "processInputLine" should {
    "trigger the save command" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]

      val dummySaveCommand = new DummySaveCommand

      val tui = new Tui(controller, contextHolder) {
        override val commands: Map[String, ITuiCommand] =
          Map("save" -> dummySaveCommand)
      }

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.processInputLine("save")
      }

      stream.toString should include ("Save command executed")
      dummySaveCommand.executed shouldBe true
    }
  }

  "processInputLine" should {
    "trigger the attack prompt state and call appropriate prompts" in {
      val controller = new DummyController

      // Stubbed roles
      val roles = mock[IRoles]
      when(roles.defender).thenReturn(mock[IPlayer])
      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val ctx = mock[GameContext]
      when(ctx.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(ctx)

      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":attack")

      testPrompter.regularAttackCalled shouldBe true
      testPrompter.showDefendersFieldCalled shouldBe true
    }
  }
  "processInputLine" should {
    "trigger the double attack prompt state and call appropriate prompts" in {
      val controller = new DummyController

      // Stubbed roles
      val roles = mock[IRoles]
      when(roles.defender).thenReturn(mock[IPlayer])
      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val ctx = mock[GameContext]
      when(ctx.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(ctx)

      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":doubleattack")

      testPrompter.doubleAttackCalled shouldBe true
      testPrompter.showDefendersFieldCalled shouldBe true
    }
  }
  "processInputLine" should {
    "trigger the boost prompt state and call appropriate prompts" in {
      val controller = new DummyController

      // Stub roles
      val attacker = mock[IPlayer]
      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)
      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val ctx = mock[GameContext]
      when(ctx.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(ctx)

      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":boostdefender")

      testPrompter.boostCalled shouldBe true
      testPrompter.showDefendersFieldCalled shouldBe true
    }
  }
  "processInputLine" should {
    "trigger the regular swap prompt state and call appropriate prompts" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]

      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":regularswap")

      testPrompter.swapCalled shouldBe true
      testPrompter.showAttackerHandCalled shouldBe true
    }
  }
  "processInputLine" should {
    "trigger the reverse swap prompt state and show attackers hand" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":reverseswap")

      testPrompter.showAttackerHandCalled shouldBe true
    }
  }
  "processInputLine" should {
    "trigger the boost goalkeeper prompt state and show goalkeeper" in {
      val controller = new DummyController

      val attacker = mock[IPlayer]
      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)
      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val ctx = mock[GameContext]
      when(ctx.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(ctx)

      val testPrompter = new TestPrompter

      val tui = new Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
      }

      tui.processInputLine(":boostgoalkeeper")

      testPrompter.showGoalkeeperCalled shouldBe true
    }
  }
  "update" should {
    "set prompt state and call promptMainMenu on SceneSwitchEvent.MainMenu" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = mock[IPrompter]

      // Subclass Tui to expose promptState for testing
      class TestableTui extends Tui(controller, contextHolder) {
        override val prompter: IPrompter = testPrompter

        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui

      tui.update(SceneSwitchEvent.MainMenu)

      tui.exposedPromptState shouldBe PromptState.MainMenu
      verify(testPrompter).promptMainMenu()
    }
  }
  "update" should {
    "set prompt state and call promptCreatePlayers on SceneSwitchEvent.CreatePlayer" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(SceneSwitchEvent.Multiplayer)

      tui.exposedPromptState shouldBe PromptState.CreatePlayers
      verify(testPrompter).promptCreatePlayers()
    }
  }
  "update" should {
    "set prompt state and call promptNewGame on SceneSwitchEvent.StartGame" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(SceneSwitchEvent.StartGame)

      tui.exposedPromptState shouldBe PromptState.StartGame
      verify(testPrompter).promptNewGame()
    }
  }
  "update" should {
    "set prompt state and call promptExit on SceneSwitchEvent.Exit" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(SceneSwitchEvent.Exit)

      tui.exposedPromptState shouldBe PromptState.Exit
      verify(testPrompter).promptExit()
    }
  }
  "update" should {
    "set prompt state and call promptPlayingField on SceneSwitchEvent.PlayingField" in {
      val controller = new DummyController
      val contextHolder = mock[IGameContextHolder]
      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(SceneSwitchEvent.PlayingField)

      tui.exposedPromptState shouldBe PromptState.PlayingField
      verify(testPrompter).promptPlayingField()
    }
  }
  "update" should {
    "set prompt state and call appropriate prompts on GameActionEvent.RegularAttack" in {
      val controller = new DummyController
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.defender).thenReturn(defender)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val context = mock[GameContext]
      when(context.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(context)

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.RegularAttack)

      tui.exposedPromptState shouldBe PromptState.SingleAttack
      verify(testPrompter).promptShowDefendersField(defender)
      verify(testPrompter).promptShowAttackersHand()
    }
  }
  "update" should {
    "set prompt state and show defenders field on GameActionEvent.BoostDefender" in {
      val controller = new DummyController
      val attacker = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val context = mock[GameContext]
      when(context.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(context)

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.BoostDefender)

      tui.exposedPromptState shouldBe PromptState.Boost
      verify(testPrompter).promptShowDefendersField(attacker)
    }
  }
  "update" should {
    "set prompt state and show goalkeeper on GameActionEvent.BoostGoalkeeper" in {
      val controller = new DummyController
      val attacker = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val context = mock[GameContext]
      when(context.state).thenReturn(state)

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(context)

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.BoostGoalkeeper)

      tui.exposedPromptState shouldBe PromptState.BoostGoalkeeper
      verify(testPrompter).promptShowGoalkeeper(attacker)
    }
  }
  "update" should {
    "set prompt state and show attacker's hand on GameActionEvent.RegularSwap" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.RegularSwap)

      tui.exposedPromptState shouldBe PromptState.RegularSwap
      verify(testPrompter).promptShowAttackersHand()
    }
  }
  "update" should {
    "set prompt state and show attacker's hand on GameActionEvent.ReverseSwap" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.ReverseSwap)

      tui.exposedPromptState shouldBe PromptState.ReverseSwap
      verify(testPrompter).promptShowAttackersHand()
    }
  }
  "update" should {
    "set prompt state and call prompter methods on GameActionEvent.Undo" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.Undo)

      tui.exposedPromptState shouldBe PromptState.Undo
      verify(testPrompter).promptUndo()
      verify(testPrompter).printGameState()
    }
  }
  "update" should {
    "set prompt state and call prompter methods on GameActionEvent.Redo" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.Redo)

      tui.exposedPromptState shouldBe PromptState.Redo
      verify(testPrompter).promptRedo()
      verify(testPrompter).printGameState()
    }
  }
  "update" should {
    "set prompt state and call promptSaveGame on GameActionEvent.SaveGame" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(GameActionEvent.SaveGame)

      tui.exposedPromptState shouldBe PromptState.SaveGame
      verify(testPrompter).promptSaveGame()
    }
  }
  "update" should {
    "set prompt state and show available games on SceneSwitchEvent.LoadGame" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui
      tui.update(SceneSwitchEvent.LoadGame)

      tui.exposedPromptState shouldBe PromptState.LoadGame
      verify(testPrompter).showAvailableGames()
    }
  }
  "update" should {
    "do nothing on unknown events" in {
      val controller = new DummyController

      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(mock[GameContext])

      val testPrompter = mock[IPrompter]

      class TestableTui extends Tui(controller, contextHolder) {
        override protected val prompter: IPrompter = testPrompter
        def exposedPromptState: PromptState = this.promptState
      }

      val tui = new TestableTui

      case object UnknownEvent extends ObservableEvent

      tui.update(UnknownEvent)

      tui.exposedPromptState shouldBe PromptState.None

      verifyNoInteractions(testPrompter)
    }
  }


}






