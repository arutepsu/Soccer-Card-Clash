package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory

class FieldCardsFactory extends IFieldCardsFactory {
  override def create(
                       playerFields: Map[IPlayer, List[Option[ICard]]],
                       goalkeepers: Map[IPlayer, Option[ICard]],
                       defenders: Map[IPlayer, List[Option[ICard]]]
                     ): IFieldCards = FieldCards(playerFields, goalkeepers, defenders)
}

trait IFieldCardsFactory {
  def create(
              playerFields: Map[IPlayer, List[Option[ICard]]],
              goalkeepers: Map[IPlayer, Option[ICard]],
              defenders: Map[IPlayer, List[Option[ICard]]]
            ): IFieldCards
}

case class FieldCards(
                               playerFields: Map[IPlayer, List[Option[ICard]]] = Map().withDefaultValue(List()),
                               goalkeepers: Map[IPlayer, Option[ICard]] = Map().withDefaultValue(None),
                               defenders: Map[IPlayer, List[Option[ICard]]] = Map().withDefaultValue(List())
                             ) extends IFieldCards {
  
  override def newPlayerDefenders(player: IPlayer, newField: List[Option[ICard]]): FieldCards = {
    copy(defenders = defenders.updated(player, newField))
  }

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    goalkeepers.getOrElse(player, None)

  override def newPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): FieldCards = {
    copy(goalkeepers = goalkeepers.updated(player, goalkeeper))
  }

  override def newGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IFieldCards =
    copy(goalkeepers = goalkeepers.updated(attacker, Some(card)))

  override def removeDefenderCard(currentDefender: IPlayer, defenderCardOpt: Option[ICard]): FieldCards = {
    val original = defenders(currentDefender)
    
    val updated = original.zipWithIndex.map {
      case (Some(c), i) if defenderCardOpt.contains(c) => None
      case (other, _) => other
    }
    copy(defenders = defenders.updated(currentDefender, updated))
  }


  override def removeDefenderGoalkeeper(currentDefender: IPlayer): FieldCards = {
    copy(goalkeepers = goalkeepers.updated(currentDefender, None))
  }

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).forall(_.isEmpty)

  override def getPlayerDefenders(player: IPlayer): List[Option[ICard]] = defenders(player)

  def getDefenderCard(player: IPlayer, index: Int): Option[ICard] = {
    val playerDefenders = defenders(player)
    if (index < 0 || index >= playerDefenders.size)
      None
    else
      playerDefenders(index)
  }


}

trait IFieldCards {

  def newPlayerDefenders(player: IPlayer, newField: List[Option[ICard]]): IFieldCards

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def newPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IFieldCards

  def getPlayerDefenders(player: IPlayer): List[Option[ICard]]

  def newGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IFieldCards

  def removeDefenderCard(currentDefender: IPlayer, defenderCard: Option[ICard]): IFieldCards

  def removeDefenderGoalkeeper(currentDefender: IPlayer): IFieldCards

  def allDefendersBeaten(currentDefender: IPlayer): Boolean

  def getDefenderCard(player: IPlayer, index: Int): Option[ICard]
}