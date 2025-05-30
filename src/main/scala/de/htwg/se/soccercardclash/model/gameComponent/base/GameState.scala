package de.htwg.se.soccercardclash.model.gameComponent.base

import com.google.inject.{Inject, Provider, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.xml.*

case class GameState(
                      gameCards: IGameCards,
                      roles: IRoles,
                      scores: IScores
                    ) extends IGameState {
  override def getGameCards: IGameCards = gameCards

  override def getRoles: IRoles = roles

  override def getScores: IScores = scores

  override def newGameCards(newGameCards: IGameCards): IGameState =
    copy(gameCards = newGameCards)

  override def newRoles(newRoles: IRoles): IGameState =
    copy(roles = newRoles)

  override def newScores(newScores: IScores): IGameState =
    copy(scores = newScores)

}

