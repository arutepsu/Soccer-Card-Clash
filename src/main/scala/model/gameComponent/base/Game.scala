package model.gameComponent.base

import com.google.inject.{Inject, Singleton}
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.RegularActionCommand
import model.cardComponent.ICard
import model.cardComponent.factory.{DeckFactory, IDeckFactory}
import model.fileIOComponent.IFileIO
import model.gameComponent.IGame
import model.gameComponent.factory.IGameInitializer
import model.gameComponent.io.IGamePersistence
import model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory, IGameStateManager}
import model.playerComponent.IPlayer
import model.playerComponent.factory.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.dataStructure.{HandCardsQueue, IHandCardsQueueFactory}
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.{ActionManager, IActionManager}
import util.UndoManager
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

  override def saveGame(): Unit = gamePersistence.saveGame(gameStateManager.getGameState)

  override def loadGame(fileName: String): Unit = {
    gamePersistence.loadGame(fileName) match {
      case Some(state) =>
        gameInitializer.initializeFromState(state)
        gameStateManager.updateGameState(gameInitializer)
      case None =>
        throw new RuntimeException(s"Failed to load game: No valid game state found in '$fileName'")
    }
  }

  override def reset(): Boolean = gameStateManager.reset(gameInitializer.getPlayingField)

  override def selectDefenderPosition(): Int = gameInitializer.selectDefenderPosition()

  override def exit(): Unit = System.exit(0)
}
