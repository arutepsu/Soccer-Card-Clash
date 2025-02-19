package model.gameComponent

import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.Player
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
  override def setPlayerName(playerIndex: Int, name: String): Unit = {
    if (playingField == null) startGame()

    if (playerIndex == 1) {
      getPlayingField.getAttacker.setName(name)
    } else if (playerIndex == 2) {
      getPlayingField.getDefender.setName(name)
    } else {
    }
  }

  override def startGame(): Unit = {
    val (p1, p2) = dealCards()
    player1 = p1
    player2 = p2
    playingField = new PlayingField(player1, player2)
    playingField.fieldState.initializePlayerHands(player1.getCards, player2.getCards)
    playingField.setPlayingField()
    gameManager = new GameManager(playingField)
    val logger = new GameLogger()
    gameManager.addObserver(logger)
  }

  private def dealCards(): (Player, Player) = {
    val deck = DeckFactory.createDeck()
    DeckFactory.shuffleDeck(deck)
    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()
    (Player("Player 1", hand1.toList), Player("Player 2", hand2.toList))
  }

  override def selectDefenderPosition(): Int =
    if (playingField.fieldState.allDefendersBeaten(playingField.getDefender)) -1 else -2

  override def saveGame(filePath: String): Try[Unit] =
    Try {
      val oos = new ObjectOutputStream(new FileOutputStream(filePath))
      oos.writeObject(player1)
      oos.writeObject(player2)
      oos.writeObject(playingField)
      oos.close()
    }

  override def loadGame(filePath: String): Try[IGame] =
    Try {
      val ois = new ObjectInputStream(new FileInputStream(filePath))
      val loadedPlayer1 = ois.readObject().asInstanceOf[Player]
      val loadedPlayer2 = ois.readObject().asInstanceOf[Player]
      val loadedPf = ois.readObject().asInstanceOf[PlayingField]
      ois.close()
      val game = new Game()
      game.player1 = loadedPlayer1
      game.player2 = loadedPlayer2
      game.playingField = loadedPf
      game
    }
}