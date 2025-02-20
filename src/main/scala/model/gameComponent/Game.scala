package model.gameComponent

import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.playerRole.{Attacker, Defender}
import model.playerComponent.base.Player
import util.UndoManager
import model.playerComponent.playerFactory.PlayerFactory

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.Try
import model.playerComponent.IPlayer
import model.playingFiledComponent.base.{ActionHandler, PlayingField}

class Game extends IGame {
  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: PlayingField = _
  private var gameManager: ActionHandler = _

  override def getPlayingField: PlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2
  def getGameManager: ActionHandler = gameManager


  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) = {
    println(s"🃏 Creating players: $playerName1 (Attacker) and $playerName2 (Defender)")

    val deck = DeckFactory.createDeck()

    DeckFactory.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    println(s"🎴 $playerName1's Hand: ${hand1.mkString(", ")}")
    println(s"🎴 $playerName2's Hand: ${hand2.mkString(", ")}")

    // ✅ Use PlayerFactory to create players
    val player1 = PlayerFactory.createPlayer(playerName1, hand1.toList)
    val player2 = PlayerFactory.createPlayer(playerName2, hand2.toList)

    (player1, player2)
  }


  override def startGame(playerName1: String, playerName2: String): Unit = {

    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2

    playingField = new PlayingField(player1, player2)

    playingField.dataManager.initializePlayerHands(player1.getCards, player2.getCards)

    playingField.setPlayingField()

    gameManager = new ActionHandler(playingField)
  }


  override def selectDefenderPosition(): Int =
    if (playingField.dataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2

  override def saveGame(): Unit =
    Try {

    }

  override def loadGame(): Unit =
    Try {
    }
}