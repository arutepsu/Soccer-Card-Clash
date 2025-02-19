package model.gameComponent

import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.playerRole.{Attacker, Defender}
import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField
import util.UndoManager
import view.GameLogger

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.Try

class Game extends IGame {
  private var player1: Player = _
  private var player2: Player = _
  private var playingField: PlayingField = _
  private var gameManager: GameManager = _

  override def getPlayingField: PlayingField = playingField
  override def getPlayer1: Player = player1
  override def getPlayer2: Player = player2
  def getGameManager: GameManager = gameManager
  

  private def createPlayers(playerName1: String, playerName2: String): (Player, Player) = {
    println(s"🃏 Creating players: $playerName1 (Attacker) and $playerName2 (Defender)")

    val deck = DeckFactory.createDeck()
    println(s"📦 Deck Created: ${deck.mkString(", ")}")

    DeckFactory.shuffleDeck(deck)
    println(s"🔀 Deck Shuffled!")

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    println(s"🎴 $playerName1's Hand: ${hand1.mkString(", ")}")
    println(s"🎴 $playerName2's Hand: ${hand2.mkString(", ")}")

    (Player(playerName1, Attacker, hand1.toList), Player(playerName2, Defender, hand2.toList))
  }

  override def startGame(playerName1: String, playerName2: String): Unit = {
    println("🎮 Starting game setup...")

    val (p1, p2) = createPlayers(playerName1, playerName2) // ✅ Create players inside startGame()
    player1 = p1
    player2 = p2

    println(s"✅ Players created successfully: $player1 and $player2")

    playingField = new PlayingField(player1, player2)
    println("🏟️ Playing field initialized!")

    playingField.fieldState.initializePlayerHands(player1.getCards, player2.getCards)
    println("🛠️ Player hands set in field state!")

    playingField.setPlayingField()
    println("⚽ Playing field set!")

    gameManager = new GameManager(playingField)
    println("🎯 Game Manager initialized!")

    val logger = new GameLogger()
    println("📜 Logger added!")

    println("✅ Game successfully started!")
  }


  override def selectDefenderPosition(): Int =
    if (playingField.fieldState.allDefendersBeaten(playingField.getDefender)) -1 else -2

  override def saveGame(): Unit =
    Try {

    }

  override def loadGame(): Unit =
    Try {
    }
}