package model.gameComponent.base

import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.HandSwapActionCommand
import model.cardComponent.factory.DeckFactory
import model.gameComponent.IGame
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.cardComponent.factory.IDeckFactory
import util.UndoManager
import play.api.libs.json.*
import model.gameComponent.factory.{GameStateFactory, IGameState}

import scala.xml.*
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.Try
import com.google.inject.{Inject, Singleton}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import play.api.libs.json.{JsObject, Json}
import model.playerComponent.factory.*
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.base.ActionManager
import com.google.inject.{Inject, Singleton}
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.factory.IPlayingFieldFactory
import model.cardComponent.factory.IDeckFactory
import model.playingFiledComponent.manager.IActionManager
import model.fileIOComponent.IFileIO
import model.playingFiledComponent.dataStructure.HandCardsQueue

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import play.api.libs.json.Json
import model.gameComponent.factory.IGameStateFactory
@Singleton
class Game @Inject()(
                      playerFactory: IPlayerFactory,
                      playingFieldFactory: IPlayingFieldFactory,
                      deckFactory: IDeckFactory,
                      fileIO: IFileIO,
                      gameStateFactory: IGameStateFactory  // ✅ Inject GameStateFactory
                    ) extends IGame {

  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: IPlayingField = _
  private var gameState: IGameState = _  // ✅ Game state variable to store/load game data

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
    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2

    playingField = playingFieldFactory.createPlayingField(player1, player2)
    val dataManager = playingField.getDataManager // ✅ Store DataManager reference for reuse

    // ✅ Initialize player hands
    dataManager.initializePlayerHands(player1.getCards.toList, player2.getCards.toList)
    playingField.setPlayingField()

    // ✅ Retrieve necessary game state information
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

    gameState = gameStateFactory.create(
      playingField,
      player1,
      player2,
      new HandCardsQueue(player1.getCards.toList),
      new HandCardsQueue(player2.getCards.toList),
      dataManager.getPlayerField(player1),
      dataManager.getPlayerField(player2),
      dataManager.getPlayerGoalkeeper(player1),
      dataManager.getPlayerGoalkeeper(player2),
      playingField.getScores.getScorePlayer1,
      playingField.getScores.getScorePlayer2
    )
  }
  override def selectDefenderPosition(): Int = {
    if (playingField.getDataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2
  }

  override def saveGame(): Unit = {
    if (gameState != null) {
      try {
        fileIO.saveGame(gameState)
        println("✅ Game saved successfully using FileIO.")
      } catch {
        case e: Exception => println(s"❌ Error saving game: ${e.getMessage}")
      }
    } else {
      println("❌ Cannot save: Game state is not initialized.")
    }
  }

  override def loadGame(fileName: String): Unit = {
    println(s"📂 Attempting to load game: $fileName")

    try {
      val loadedState = fileIO.loadGame(fileName) // 🔥 Check if this triggers recursion
      println(s"🔍 Loaded game state: ${if (loadedState != null) "Success" else "Failed"}")

      if (loadedState != null) {
        gameState = loadedState
        println(s"🎮 Updating player references...")

        player1 = gameState.player1
        player2 = gameState.player2
        playingField = gameState.playingField

        println(s"🛠 Setting up playing field...")
//        playingField.setPlayingField() // 🔥 Check if recursion happens here

        println(s"✅ Game '$fileName' loaded successfully using FileIO.")


      } else {
        println(s"❌ Error: No valid game state found in '$fileName'.")
      }
    } catch {
      case e: Exception =>
        println(s"❌ ERROR loading game '$fileName': ${e.getMessage}")
        e.printStackTrace()
    }
  }


  override def exit(): Unit = {
    System.exit(0)
  }
}