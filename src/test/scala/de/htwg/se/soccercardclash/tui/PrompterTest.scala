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
import de.htwg.se.soccercardclash.model.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.view.tui.Prompter

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
    val mockField = mock(classOf[PlayingField])
    val mockAttacker = mock(classOf[Player])

    // Mock the getCurrentGame and getPlayingField methods
    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)

    // Mock the getAttacker method
    when(mockField.getAttacker).thenReturn(mockAttacker)
    when(mockAttacker.name).thenReturn("Attacker")

    // Mock getDataManager to return a mock IDataManager
    val mockDataManager = mock(classOf[IDataManager])
    when(mockField.getDataManager).thenReturn(mockDataManager)

    // Mock the getPlayerField method to return a List (not a String)
    when(mockDataManager.getPlayerField(mockAttacker)).thenReturn(List("⚽", "🔥"))

    // Capture the output of promptShowAttackersField
    val output = captureOutput(new Prompter(mockController).promptShowAttackersField())

    // Check if the expected output is present
    output should include("Attacker field cards after boost")
    output should include("⚽")
    output should include("🔥")
  }

  it should "print defender's field after attack" in {
    val mockController = mock(classOf[IController])
    val mockGame = mock(classOf[IGame])
    val mockField = mock(classOf[PlayingField])
    val mockDefender = mock(classOf[Player])

    // Mocking controller, game, and playing field
    when(mockController.getCurrentGame).thenReturn(mockGame)
    when(mockGame.getPlayingField).thenReturn(mockField)

    // Mock the defender and its name
    when(mockField.getDefender).thenReturn(mockDefender)
    when(mockDefender.name).thenReturn("Defender")

    // Mock getDataManager and the getPlayerField method to return a List
    val mockDataManager = mock(classOf[IDataManager])
    when(mockField.getDataManager).thenReturn(mockDataManager)
    when(mockDataManager.getPlayerField(mockDefender)).thenReturn(List("🛡️", "⚔️")) // Return a List of cards

    // Capture the output of promptShowDefendersField
    val output = captureOutput(new Prompter(mockController).promptShowDefendersField())

    // Check if the expected output is present
    output should include("Defender field cards after attack")
    output should include("🛡️")
    output should include("⚔️")
  }

//  it should "print current game state" in {
//    val mockController = mock(classOf[IController])
//    val mockGame = mock(classOf[IGame])
//    val mockField = mock(classOf[PlayingField])
//    val mockAttacker = mock(classOf[Player])
//    val mockDefender = mock(classOf[Player])
//
//    // Mock controller, game, and playing field
//    when(mockController.getCurrentGame).thenReturn(mockGame)
//    when(mockGame.getPlayingField).thenReturn(mockField)
//
//    // Mock the attacker and defender
//    when(mockField.getAttacker).thenReturn(mockAttacker)
//    when(mockField.getDefender).thenReturn(mockDefender)
//
//    // Mock attacker and defender names
//    when(mockAttacker.name).thenReturn("Attacker")
//    when(mockDefender.name).thenReturn("Defender")
//
//    // Mock getDataManager and its methods to return valid mocks
//    val mockDataManager = mock(classOf[IDataManager])
//    when(mockField.getDataManager).thenReturn(mockDataManager)
//
//    // Mock IHandCardsQueue to return an iterable collection (e.g., Seq)
//    val mockHandQueue = mock(classOf[IHandCardsQueue])
//
//    // Ensure getCards returns a valid Iterable (e.g., Seq)
//    val mockCards: Seq[String] = Seq("Card1", "Card2") // Immutable sequence, iterable
//    when(mockHandQueue.getCards).thenReturn(mockCards)
//
//    // Mock getPlayerHand to return the mocked hand queue
//    when(mockDataManager.getPlayerHand(mockAttacker)).thenReturn(mockHandQueue)
//
//    // Mock getPlayerDefenders to return a valid list
//    when(mockDataManager.getPlayerDefenders(mockDefender)).thenReturn(List("Def1"))
//
//    // Capture the output of printGameState
//    val output = captureOutput(new Prompter(mockController).printGameState())
//
//    // Check if the expected output is present
//    output should include("🏆 **CURRENT GAME STATE**")
//    output should include("⚔️ Attacker: Attacker")
//    output should include("🛡️ Defender: Defender")
//    output should include("Card1")
//    output should include("Card2")
//    output should include("Def1")
//  }



  it should "print prompt for exit" in {
    val mockController = mock(classOf[IController])
    val prompter = new Prompter(mockController)
    val output = captureOutput(prompter.promptExit())
    output should include("👋 Goodbye!")
  }
}