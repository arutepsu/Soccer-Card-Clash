package de.htwg.se.soccercardclash.tui

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import scala.collection.mutable.Queue
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.view.tui.Prompter
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.nio.file.{Files, Path}
import scala.Console
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles}
import scala.collection.mutable
class PrompterTest extends AnyFlatSpec with Matchers {

  // Helper to capture println output
  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString
  }

  "Prompter" should "print prompt for players name" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptPlayersName())
    output should include("👥 Enter player names")
  }

  it should "print prompt for regular attack" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptRegularAttack())
    output should include("⚔️ Select a defender to attack")
  }

  it should "print prompt for double attack" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptDoubleAttack())
    output should include("⚔️ Select a defender to attack")
  }

  it should "print prompt for boost" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptBoost())
    output should include("🔥 Choose a defender to boost")
  }

  it should "print prompt for swap" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptSwap())
    output should include("🔄 Choose a card to swap")
  }

  it should "print prompt for new game" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptNewGame())
    output should include("Creating a new game!")
  }

  it should "print prompt for playing field" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptPlayingField())
    output should include("Game started!")
  }

  it should "print attacker's field after boost" in {
    val mockController = mock(classOf[IController])
    val mockGame = mock(classOf[IGame])
    val mockField = mock(classOf[GameState])
    val mockAttacker = mock(classOf[Player])

    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)

    val mockRoles = mock(classOf[IRoles])
    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockField.getRoles).thenReturn(mockRoles)
    when(mockAttacker.name).thenReturn("Attacker")

    val mockDataManager = mock(classOf[IGameCards])
    when(mockField.getGameCards).thenReturn(mockDataManager)

    val mockHandQueue = mock(classOf[IHandCardsQueue])
    val mockCard1 = mock(classOf[ICard])
    val mockCard2 = mock(classOf[ICard])

    when(mockCard1.toString).thenReturn("⚽")
    when(mockCard2.toString).thenReturn("🔥")
    when(mockHandQueue.toList).thenReturn(List(mockCard1, mockCard2)) // ✅ FIXED here
    when(mockDataManager.getPlayerHand(mockAttacker)).thenReturn(mockHandQueue)

    val output = captureOutput(new Prompter(mockController).promptShowAttackersHand())

    output should include("Attacker's hand cards")
    output should include("⚽")
    output should include("🔥")
  }

  it should "print defender's field after attack" in {
    val mockController = mock(classOf[IController])
    val mockGame = mock(classOf[IGame])
    val mockField = mock(classOf[GameState])
    val mockDefender = mock(classOf[Player])
    val mockDataManager = mock(classOf[IGameCards])
    val mockCard1 = mock(classOf[ICard])
    val mockCard2 = mock(classOf[ICard])

    val mockRolesManager = mock(classOf[IRoles])
    when(mockRolesManager.defender).thenReturn(mockDefender)
    when(mockField.getRoles).thenReturn(mockRolesManager)

    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)
    when(mockDefender.name).thenReturn("Defender")
    when(mockField.getGameCards).thenReturn(mockDataManager)

    // ✅ Fix here: use getPlayerDefenders
    when(mockCard1.toString).thenReturn("🛡️")
    when(mockCard2.toString).thenReturn("⚔️")
    when(mockDataManager.getPlayerDefenders(mockDefender)).thenReturn(List(mockCard1, mockCard2))

    val output = captureOutput(new Prompter(mockController).promptShowDefendersField(mockDefender))

    output should include("Defender's defender cards")
    output should include("🛡️")
    output should include("⚔️")
  }

  it should "print current game state with attacker hand and defender defenders" in {
    val mockController = mock(classOf[IController])
    val mockGame = mock(classOf[IGame])
    val mockField = mock(classOf[GameState])
    val mockAttacker = mock(classOf[Player])
    val mockDefender = mock(classOf[Player])
    val mockDataManager = mock(classOf[IGameCards])
    val mockHandQueue = mock(classOf[IHandCardsQueue])
    val mockCard1 = mock(classOf[ICard])
    val mockCard2 = mock(classOf[ICard])
    val mockDefenderCard = mock(classOf[ICard])

    // Roles manager
    val mockRoles = mock(classOf[IRoles])
    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockField.getRoles).thenReturn(mockRoles)

    // Game & field
    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)
    when(mockField.getGameCards).thenReturn(mockDataManager)

    // Player names
    when(mockAttacker.name).thenReturn("Attacker")
    when(mockDefender.name).thenReturn("Defender")

    // Cards and mocks
    when(mockCard1.toString).thenReturn("⚽")
    when(mockCard2.toString).thenReturn("🔥")
    when(mockDefenderCard.toString).thenReturn("🛡️")

    when(mockHandQueue.toList).thenReturn(List(mockCard1, mockCard2))
    when(mockDataManager.getPlayerHand(mockAttacker)).thenReturn(mockHandQueue)
    when(mockDataManager.getPlayerDefenders(mockDefender)).thenReturn(List(mockDefenderCard))

    // Capture and assert
    val output = captureOutput {
      new Prompter(mockController).printGameState()
    }

    output should include("🏆 **CURRENT GAME STATE**")
    output should include("⚔️ Attacker: Attacker")
    output should include("🛡️ Defender: Defender")
    output should include("🎴 Attacker's Hand: ⚽, 🔥")
    output should include("🏟️ Defender's Defenders: 🛡️")
  }
  it should "print the goalkeeper card of the given player" in {
    val mockController = mock(classOf[IController])
    val mockGame = mock(classOf[IGame])
    val mockField = mock(classOf[IGameState])
    val mockDataManager = mock(classOf[IGameCards])
    val mockPlayer = mock(classOf[IPlayer])
    val mockCard = mock(classOf[ICard])

    when(mockPlayer.name).thenReturn("Alice")
    when(mockCard.toString).thenReturn("🧤 GoalkeeperCard")

    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)
    when(mockField.getGameCards).thenReturn(mockDataManager)
    when(mockDataManager.getPlayerGoalkeeper(mockPlayer)).thenReturn(Some(mockCard))

    val prompter = new Prompter(mockController)
    val output = captureOutput {
      prompter.promptShowGoalkeeper(mockPlayer)
    }

    output should include("Alice's goalkeeper Card")
    output should include("🧤 GoalkeeperCard")
  }
  it should "print the main menu" in {
    val prompter = new Prompter(mock(classOf[IController]))
    val output = captureOutput(prompter.promptMainMenu())

    output should include("Welcome to Soccer Card Clash")
    output should include(":start")
    output should include(":load")
    output should include(":exit")
  }

  it should "print player creation message" in {
    val prompter = new Prompter(mock(classOf[IController]))
    val output = captureOutput(prompter.promptCreatePlayers())

    output should include("Creating Players")
  }
  it should "print game save confirmation" in {
    val prompter = new Prompter(mock(classOf[IController]))
    val output = captureOutput(prompter.promptSaveGame())

    output should include("Game saved successfully")
  }

  it should "print undo message" in {
    val prompter = new Prompter(mock(classOf[IController]))
    val output = captureOutput(prompter.promptUndo())

    output should include("Undo")
  }
  it should "print redo message" in {
    val prompter = new Prompter(mock(classOf[IController]))
    val output = captureOutput(prompter.promptRedo())

    output should include("Redo")
  }

  it should "print prompt for exit" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptExit())
    output should include("👋 Goodbye!")
  }

}