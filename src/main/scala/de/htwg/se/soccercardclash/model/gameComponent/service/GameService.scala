package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.service.{IGameInitializer, IGamePersistence, IGameService}
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{SimpleAttackAIStrategy, SmartAttackAIStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.MetaAIStrategy
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.{Random, Try}

class GameService @Inject()(
                             initializer: IGameInitializer,
                             persistence: IGamePersistence,
                             aiRoster: Map[String, IPlayer]
                           ) extends IGameService {
  def createNewGame(player1: String, player2: String): IGameState =
    initializer.createGameState(player1, player2)

  override def createNewGameWithAI(humanPlayerName: String, aiName: String): IGameState = {
    val aiPlayer = aiRoster.getOrElse(
      aiName,
      throw new IllegalArgumentException(s"Unknown AI: $aiName")
    )
    initializer.createGameStateWithAI(humanPlayerName, aiPlayer)
  }


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

  def createNewGameWithAI(humanPlayerName: String, aiName: String): IGameState

  def loadGame(file: String): Try[IGameState]

  def saveGame(state: IGameState): Try[Unit]
}
