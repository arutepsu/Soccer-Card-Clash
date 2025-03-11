package model.gameComponent.base

import com.google.inject.{Inject, Singleton}
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.HandSwapActionCommand
import model.cardComponent.factory.{DeckFactory, IDeckFactory}
import model.fileIOComponent.IFileIO
import model.gameComponent.IGame
import model.gameComponent.factory.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import model.playerComponent.IPlayer
import model.playerComponent.factory.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.dataStructure.HandCardsQueue
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.manager.base.ActionManager
import util.UndoManager
import play.api.libs.json.*

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.Try
import scala.xml.*
import model.cardComponent.ICard
@Singleton
class Game @Inject()(
                      playerFactory: IPlayerFactory,
                      playingFieldFactory: IPlayingFieldFactory,
                      deckFactory: IDeckFactory,
                      fileIO: IFileIO,
                      gameStateFactory: IGameStateFactory,
                    ) extends IGame {

  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: IPlayingField = _
  private var gameState: IGameState = _

  override def getPlayingField: IPlayingField = playingField

  override def getPlayer1: IPlayer = player1

  override def getPlayer2: IPlayer = player2

  override def getActionManager: IActionManager = playingField.getActionManager

  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) = {
    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val p1 = playerFactory.createPlayer(playerName1, hand1)
    val p2 = playerFactory.createPlayer(playerName2, hand2)

    (p1, p2)
  }

  override def startGame(playerName1: String, playerName2: String): Unit = {
    resetPlayingField()
    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2
    println("players created")
    playingField = playingFieldFactory.createPlayingField(player1, player2)
    val dataManager = playingField.getDataManager

    dataManager.initializePlayerHands(player1.getCards.toList, player2.getCards.toList)
    playingField.setPlayingField()

    val player1Hand = new HandCardsQueue(player1.getCards.toList)
    val player2Hand = new HandCardsQueue(player2.getCards.toList)

    val player1Field = dataManager.getPlayerField(player1)
    val player2Field = dataManager.getPlayerField(player2)

    val player1Goalkeeper = dataManager.getPlayerGoalkeeper(player1)
    val player2Goalkeeper = dataManager.getPlayerGoalkeeper(player2)

    val player1Score = playingField.getScores.getScorePlayer1
    val player2Score = playingField.getScores.getScorePlayer2

    gameState = gameStateFactory.create(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Score,
      player2Score
    )

  }

  override def updateGameState(): Unit = {
    val dataManager = playingField.getDataManager

    val player1Field = dataManager.getPlayerDefenders(player1)
    val player2Field = dataManager.getPlayerDefenders(player2)

    val player1Hand = new HandCardsQueue(dataManager.getPlayerHand(player1).toList)
    val player2Hand = new HandCardsQueue(dataManager.getPlayerHand(player2).toList)

    val player1Goalkeeper = dataManager.getPlayerGoalkeeper(player1)
    val player2Goalkeeper = dataManager.getPlayerGoalkeeper(player2)

    val player1Score = playingField.getScores.getScorePlayer1
    val player2Score = playingField.getScores.getScorePlayer2

    gameState = gameStateFactory.create(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Score,
      player2Score
    )
  }

  override def selectDefenderPosition(): Int = {
    if (playingField.getDataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2
  }

  override def saveGame(): Unit = {
    if (gameState != null) {
      try {
        fileIO.saveGame(gameState)
      } catch {
        case e: Exception => throw new RuntimeException("Failed to save the game", e)
      }
    } else {
    }
  }

  override def loadGame(fileName: String): Unit = {

    try {
      val loadedState = fileIO.loadGame(fileName)

      if (loadedState != null) {
        gameState = loadedState

        player1 = gameState.player1
        player2 = gameState.player2
        playingField = gameState.playingField

        val dataManager = playingField.getDataManager

        dataManager.initializePlayerHands(gameState.player1Hand.getCards.toList, gameState.player2Hand.getCards.toList)

        dataManager.setPlayerDefenders(player1, gameState.player1Defenders)
        dataManager.setPlayerDefenders(player2, gameState.player2Defenders)

        dataManager.setPlayerGoalkeeper(player1, gameState.player1Goalkeeper)
        dataManager.setPlayerGoalkeeper(player2, gameState.player2Goalkeeper)

        if (gameState.player1Goalkeeper.isEmpty || gameState.player2Goalkeeper.isEmpty) {
          throw new IllegalStateException("Goalkeeper is missing! The game logic must always have one.")
        }
        playingField.getScores.setScorePlayer1(gameState.player1Score)
        playingField.getScores.setScorePlayer2(gameState.player2Score)

        playingField.setPlayingField()

      } else {
        throw new RuntimeException(s"Failed to load game: No valid game state found in '$fileName'")
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException(s"Failed to load game '$fileName'", e)
    }
  }

  def resetPlayingField(): Unit = {
    playingField = null
    gameState = null
    player1 = null
    player2 = null
  }

  override def exit(): Unit = {
    System.exit(0)
  }
}