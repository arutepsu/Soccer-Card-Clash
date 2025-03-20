package model.gameComponent.io

import com.google.inject.{Inject, Singleton}
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.RegularActionCommand
import model.cardComponent.ICard
import model.cardComponent.factory.{DeckFactory, IDeckFactory}
import model.fileIOComponent.IFileIO
import model.gameComponent.IGame
import model.gameComponent.io.IGamePersistence
import model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
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

trait IGamePersistence {
  def saveGame(state: IGameState): Unit
  def loadGame(fileName: String): Option[IGameState]
}

class GamePersistence @Inject()(fileIO: IFileIO) extends IGamePersistence {
  override def saveGame(state: IGameState): Unit = {
    Try(fileIO.saveGame(state)) match {
      case Success(_) =>
      case Failure(exception) =>
        throw new RuntimeException("Failed to save the game", exception)
    }
  }

  override def loadGame(fileName: String): Option[IGameState] = {
    Try(fileIO.loadGame(fileName)).toOption.filter(_ != null)
  }
}

