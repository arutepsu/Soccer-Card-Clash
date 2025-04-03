package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager

import scala.collection.mutable

trait IRefillStrategy {
  def refillDefenderField(fieldState: IDataManager, defender: IPlayer): Unit
  def refillField(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): Unit
}