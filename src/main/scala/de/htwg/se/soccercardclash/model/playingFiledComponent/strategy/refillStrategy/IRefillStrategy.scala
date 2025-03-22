package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IDataManager

import scala.collection.mutable

trait IRefillStrategy {
  def refillDefenderField(fieldState: IDataManager, defender: IPlayer): Unit
  def refillField(fieldState: IDataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit
}