package de.htwg.se.soccercardclash.tui
import java.io.File
import scala.io.StdIn
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.{IGameContextHolder, UndoManager}
import de.htwg.se.soccercardclash.view.tui.Prompter
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadGameTuiCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import org.scalatest.wordspec.AnyWordSpec

class PrompterSpec extends AnyWordSpec with Matchers {

  "Prompter" should {

    "print attacker's hand correctly" in {
      val attacker = mock(classOf[IPlayer])
      when(attacker.name).thenReturn("Alice")

      val card1 = mock(classOf[ICard])
      val card2 = mock(classOf[ICard])
      when(card1.toString).thenReturn("card1")
      when(card2.toString).thenReturn("card2")

      val hand = List(card1, card2)

      val gameCards = mock(classOf[IGameCards])
      val handQueue = mock(classOf[IHandCardsQueue])
      when(handQueue.toList).thenReturn(List(card1, card2))
      when(gameCards.getPlayerHand(attacker)).thenReturn(handQueue) // ✅ correct


      val roles = mock(classOf[IRoles])
      when(roles.attacker).thenReturn(attacker)

      val state = mock(classOf[IGameState])
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(gameCards)

      val context = mock(classOf[IGameContextHolder])
      val undoManager = mock(classOf[UndoManager])
      when(context.get).thenReturn(GameContext(state, undoManager))


      val prompter = new Prompter(mock(classOf[IController]), context)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        prompter.promptShowAttackersHand()
      }

      val result = output.toString
      result should include("Alice's hand cards")
      result should include("card1")
      result should include("card2")
    }
  }
  "Prompter basic prompts" should {
    "print player name prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptPlayersName()
      }
      out.toString should include("Enter player names")
    }

    "print regular attack prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptRegularAttack()
      }
      out.toString should include("Select a defender")
    }

    "print double attack prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptDoubleAttack()
      }
      out.toString should include("Select a defender")
    }

    "print boost prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptBoost()
      }
      out.toString should include("Choose a defender")
    }

    "print swap prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptSwap()
      }
      out.toString should include("Choose a card to swap")
    }

    "print new game prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptNewGame()
      }
      out.toString should include("Creating a new game")
    }

    "print exit prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptExit()
      }
      out.toString should include("Goodbye")
    }

    "print undo prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptUndo()
      }
      out.toString should include("Undo")
    }

    "print redo prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptRedo()
      }
      out.toString should include("Redo")
    }

    "print create players prompt" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptCreatePlayers()
      }
      out.toString should include("Creating Players")
    }

    "print save game message" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptSaveGame()
      }
      out.toString should include("Game saved successfully")
    }
  }
  "Prompter main menu" should {
    "print main menu banner" in {
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        new Prompter(null, null).promptMainMenu()
      }
      val output = out.toString
      output should include("Welcome to Soccer Card Clash")
      output should include(":startAI")
      output should include(":exit")
    }
  }

}
