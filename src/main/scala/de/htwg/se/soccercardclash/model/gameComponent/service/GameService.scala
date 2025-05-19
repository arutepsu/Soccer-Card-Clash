package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.service.{IGameInitializer, IGamePersistence}
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.model.playerComponent.strategyAI.{MetaAIStrategy, SimpleAIStrategy, SmartAIStrategy}
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.{Random, Try}

class GameService @Inject()(
                             initializer: IGameInitializer,
                             persistence: IGamePersistence
                           ) extends IGameService{
  def createNewGame(player1: String, player2: String): IGameState =
    initializer.createGameState(player1, player2)

  def createNewGameWithAI(humanPlayerName: String): IGameState =
    val seed = 42L
    val seededRandom = new Random(seed)
    initializer.createGameStateWithAI(humanPlayerName, new MetaAIStrategy(seededRandom))

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
  def createNewGameWithAI(humanPlayerName: String): IGameState
  def loadGame(file: String): Try[IGameState]
  def saveGame(state: IGameState): Try[Unit]
}
