package de.htwg.se.soccercardclash.model.gameComponent.base

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.util.{GameStateMemento, Memento}

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

  override def createMemento(): Memento =
    GameStateMemento(gameCards, roles, scores)

  override def restoreFromMemento(m: Memento): IGameState = m match {
    case GameStateMemento(cards, r, s) =>
      copy(gameCards = cards, roles = r, scores = s)

    case _ =>
      throw new IllegalArgumentException("Invalid memento for GameState")
  }

}

