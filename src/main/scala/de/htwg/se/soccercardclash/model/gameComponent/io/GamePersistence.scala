package de.htwg.se.soccercardclash.model.gameComponent.io

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

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scala.xml.*

trait IGamePersistence {
  def saveGame(state: IGameState): Try[Unit]
  def loadGame(fileName: String): Try[IGameState]
}

class GamePersistence @Inject()(fileIO: IFileIO) extends IGamePersistence {

  override def saveGame(state: IGameState): Try[Unit] = {
    fileIO.saveGame(state)
  }

  override def loadGame(fileName: String): Try[IGameState] = {
    fileIO.loadGame(fileName)
  }
}


