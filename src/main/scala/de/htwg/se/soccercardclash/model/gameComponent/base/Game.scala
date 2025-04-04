package de.htwg.se.soccercardclash.model.gameComponent.base

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.factory.IGameInitializer
import de.htwg.se.soccercardclash.model.gameComponent.io.IGamePersistence
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory, IGameStateManager}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{ActionManager, IActionManager}
import de.htwg.se.soccercardclash.util.UndoManager

import play.api.libs.json.*

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scala.xml.*

@Singleton
class Game @Inject()(
                      gameInitializer: IGameInitializer,
                      gameStateManager: IGameStateManager,
                      gamePersistence: IGamePersistence,
                    ) extends IGame {

  override def getPlayingField: IPlayingField = gameInitializer.getPlayingField

  override def getPlayer1: IPlayer = gameInitializer.getPlayer1

  override def getPlayer2: IPlayer = gameInitializer.getPlayer2

  override def getActionManager: IActionManager = gameInitializer.getActionManager

  override def createGame(playerName1: String, playerName2: String): Unit = {
    gameInitializer.createGame(playerName1, playerName2)
    gameStateManager.updateGameState(gameInitializer)
  }

  override def updateGameState(): Unit = gameStateManager.updateGameState(gameInitializer)

  override def saveGame(): Try[Unit] = gamePersistence.saveGame(gameStateManager.getGameState)
  
  override def loadGame(fileName: String): Try[Unit] =
    gamePersistence.loadGame(fileName) match {
      case Success(state) =>
        gameInitializer.initializeFromState(state)
        gameStateManager.updateGameState(gameInitializer)
        Success(())

      case Failure(exception) =>
        Failure(new RuntimeException(s"❌ Game: Failed to load game: ${exception.getMessage}", exception))
    }
  
  override def reset(): Boolean = gameStateManager.reset(gameInitializer.getPlayingField)

  override def exit(): Unit = System.exit(0)
}
