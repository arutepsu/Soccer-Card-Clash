package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory

class FieldCardsFactory extends IFieldCardsFactory {
  override def create(
                       playerFields: Map[IPlayer, List[ICard]],
                       goalkeepers: Map[IPlayer, Option[ICard]],
                       defenders: Map[IPlayer, List[ICard]]
                     ): IFieldCards = FieldCards(playerFields, goalkeepers, defenders)
}

trait IFieldCardsFactory {
  def create(
              playerFields: Map[IPlayer, List[ICard]],
              goalkeepers: Map[IPlayer, Option[ICard]],
              defenders: Map[IPlayer, List[ICard]]
            ): IFieldCards
}

case class FieldCards(
                               playerFields: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List()),
                               goalkeepers: Map[IPlayer, Option[ICard]] = Map().withDefaultValue(None),
                               defenders: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List())
                             ) extends IFieldCards {

  override def getPlayerField(player: IPlayer): List[ICard] = playerFields(player)

  override def setPlayerField(player: IPlayer, newField: List[ICard]): FieldCards = {
    copy(defenders = defenders.updated(player, newField))
  }

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    goalkeepers.getOrElse(player, None)

  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): FieldCards = {
    copy(goalkeepers = goalkeepers.updated(player, goalkeeper))
  }

  override def setGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IFieldCards =
    copy(goalkeepers = goalkeepers.updated(attacker, Some(card)))

  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): FieldCards = {
    val updatedList = defenders(currentDefender).filterNot(_ == defenderCard)
    copy(defenders = defenders.updated(currentDefender, updatedList))
  }

  override def removeDefenderGoalkeeper(currentDefender: IPlayer): FieldCards = {
    copy(goalkeepers = goalkeepers.updated(currentDefender, None))
  }

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  override def getPlayerDefenders(player: IPlayer): List[ICard] = defenders(player)

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    val playerDefenders = defenders(player)
    if (index < 0 || index >= playerDefenders.size)
      throw new IndexOutOfBoundsException("Invalid defender index")
    playerDefenders(index)
  }

}

trait IFieldCards {
  def getPlayerField(player: IPlayer): List[ICard]

  def setPlayerField(player: IPlayer, newField: List[ICard]): IFieldCards

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IFieldCards

  def getPlayerDefenders(player: IPlayer): List[ICard]

  def setGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IFieldCards

  def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): IFieldCards

  def removeDefenderGoalkeeper(currentDefender: IPlayer): IFieldCards

  def allDefendersBeaten(currentDefender: IPlayer): Boolean

  def getDefenderCard(player: IPlayer, index: Int): ICard
}