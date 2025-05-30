package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable


trait IRefillStrategy {
  def refillDefenderField(gameCards: IGameCards, defender: IPlayer): IGameCards

  def refillField(gameCards: IGameCards, player: IPlayer, hand: IHandCardsQueue): IGameCards
}