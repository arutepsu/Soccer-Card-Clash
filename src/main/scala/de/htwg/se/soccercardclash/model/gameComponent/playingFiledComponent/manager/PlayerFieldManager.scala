package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy


class PlayerFieldManager extends IPlayerFieldManager {
  private var playerFields: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List())
  private var goalkeepers: Map[IPlayer, Option[ICard]] = Map().withDefaultValue(None)
  private var defenders: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List())

  override def getPlayerField(player: IPlayer): List[ICard] = playerFields(player)

  override def setPlayerField(player: IPlayer, newField: List[ICard]): Unit = {
    defenders = defenders.updated(player, newField)
  }

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] = {
    goalkeepers.getOrElse(player, None)
  }


  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit = {
    goalkeepers = goalkeepers.updated(player, goalkeeper)
  }

  override def setGoalkeeperForAttacker(playingField: IPlayingField, card: ICard): Unit = {
    val attacker = playingField.getRoles.attacker
    goalkeepers = goalkeepers.updated(attacker, Some(card))
    playingField.notifyObservers()
  }

  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit = {
    defenders = defenders.updated(currentDefender, defenders(currentDefender).filterNot(_ == defenderCard))
  }

  override def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit = {
    goalkeepers = goalkeepers.updated(currentDefender, None)
  }

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  override def getPlayerDefenders(player: IPlayer): List[ICard] = {
    val playerDefenders = defenders(player)
    playerDefenders
  }

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    val playerDefenders = defenders(player)
    if (index < 0 || index >= playerDefenders.size)
      throw new IndexOutOfBoundsException("Invalid defender index")
    playerDefenders(index)
  }

  override def clearAll(): Unit = {
    playerFields = Map().withDefaultValue(List())
    goalkeepers = Map().withDefaultValue(None)
    defenders = Map().withDefaultValue(List())
  }

}

trait IPlayerFieldManager {
  def getPlayerField(player: IPlayer): List[ICard]

  def setPlayerField(player: IPlayer, newField: List[ICard]): Unit

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit

  def getPlayerDefenders(player: IPlayer): List[ICard]

  def setGoalkeeperForAttacker(playingField: IPlayingField, card: ICard): Unit

  def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit

  def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit

  def allDefendersBeaten(currentDefender: IPlayer): Boolean

  def getDefenderCard(player: IPlayer, index: Int): ICard

  def clearAll(): Unit
}