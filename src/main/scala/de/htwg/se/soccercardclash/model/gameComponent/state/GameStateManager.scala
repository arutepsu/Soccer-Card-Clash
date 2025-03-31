package de.htwg.se.soccercardclash.model.gameComponent.state

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.RegularSwapActionCommand
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.io.IGamePersistence
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{ActionManager, IActionManager}
import de.htwg.se.soccercardclash.util.UndoManager
import play.api.libs.json.*
import de.htwg.se.soccercardclash.model.gameComponent.factory.IGameInitializer
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
