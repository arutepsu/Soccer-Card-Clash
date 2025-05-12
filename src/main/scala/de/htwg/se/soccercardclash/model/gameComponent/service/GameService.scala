package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.service.{IGameInitializer, IGamePersistence}
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.Try

class GameService @Inject()(
                             initializer: IGameInitializer,
                             persistence: IGamePersistence
                           ) extends IGameService{
  def createNewGame(player1: String, player2: String): IGameState =
    initializer.createGameState(player1, player2)

  def loadGame(file: String): Try[IGameState] = {
    persistence.loadGame(file)
      .map(initializer.initializeFromState)
      .recover {
        case ex =>
          throw ex
      }

  }

  def saveGame(state: IGameState): Try[Unit] =
    persistence.saveGame(state)
}
trait IGameService {
  def createNewGame(player1: String, player2: String): IGameState
  def loadGame(file: String): Try[IGameState]
  def saveGame(state: IGameState): Try[Unit]
}
