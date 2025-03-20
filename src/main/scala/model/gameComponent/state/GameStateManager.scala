package model.gameComponent.state

import com.google.inject.{Inject, Singleton}
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.RegularSwapActionCommand
import model.cardComponent.ICard
import model.cardComponent.factory.{DeckFactory, IDeckFactory}
import model.fileIOComponent.IFileIO
import model.gameComponent.IGame
import model.gameComponent.state.IGameStateManager
import model.gameComponent.factory.*
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

trait IGameStateManager {
  def updateGameState(gameInitializer: IGameInitializer): Unit
  def getGameState: IGameState
  def reset(playingField: IPlayingField): Boolean
}

class GameStateManager @Inject()(
                                  gameStateFactory: IGameStateFactory,
                                  handCardsQueueFactory: IHandCardsQueueFactory
                                ) extends IGameStateManager {

  private var gameState: IGameState = _

  override def updateGameState(gameInitializer: IGameInitializer): Unit = {
    val playingField = gameInitializer.getPlayingField
    val player1 = gameInitializer.getPlayer1
    val player2 = gameInitializer.getPlayer2

    val dataManager = playingField.getDataManager

    gameState = gameStateFactory.create(
      playingField,
      player1,
      player2,
      handCardsQueueFactory.create(dataManager.getPlayerHand(player1).toList),
      handCardsQueueFactory.create(dataManager.getPlayerHand(player2).toList),
      dataManager.getPlayerDefenders(player1),
      dataManager.getPlayerDefenders(player2),
      dataManager.getPlayerGoalkeeper(player1),
      dataManager.getPlayerGoalkeeper(player2),
      playingField.getScores.getScorePlayer1,
      playingField.getScores.getScorePlayer2
    )
  }

  override def getGameState: IGameState = gameState

  override def reset(playingField: IPlayingField): Boolean = {
    if (playingField != null) {
      playingField.reset()
      true
    } else {
      false
    }
  }
}
