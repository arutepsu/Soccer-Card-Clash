package model.playingFiledComponent.manager

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure._
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

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

  override def getPlayerDefenders(player: IPlayer): List[ICard] = {
    val playerDefenders = defenders(player)
    playerDefenders
  }


  override def setGoalkeeperForAttacker(playingField: IPlayingField, card: ICard): Unit = {
    val attacker = playingField.getAttacker
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
    println("🚨 DEBUG: Current Player Fields: " + playerFields.keys.mkString(", "))
    println("🚨 DEBUG: Current Goalkeepers: " + goalkeepers.keys.mkString(", "))
    println("🚨 DEBUG: Current Defenders: " + defenders.keys.mkString(", "))
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