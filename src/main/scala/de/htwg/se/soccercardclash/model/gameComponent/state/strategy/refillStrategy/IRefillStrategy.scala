package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IDataManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable


trait IRefillStrategy {
  def refillDefenderField(fieldState: IDataManager, defender: IPlayer): IDataManager

  def refillField(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): IDataManager
}