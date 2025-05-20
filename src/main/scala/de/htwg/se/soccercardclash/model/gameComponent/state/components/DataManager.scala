package de.htwg.se.soccercardclash.model.gameComponent.state.components

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory

import scala.collection.mutable

trait IDataManagerFactory {
  def create(attacker: IPlayer, defender: IPlayer): IDataManager

  def createFromData(
                      attacker: IPlayer,
                      attackerHand: List[ICard],
                      defender: IPlayer,
                      defenderHand: List[ICard],
                      attackerDefenders: List[Option[ICard]],
                      defenderDefenders: List[Option[ICard]],
                      attackerGoalkeeper: Option[ICard],
                      defenderGoalkeeper: Option[ICard]
                    ): IDataManager
}

class DataManagerFactory @Inject()(
                                    handManagerFactory: IHandCardsFactory,
                                    fieldManagerFactory: IFieldCardsFactory
                                  ) extends IDataManagerFactory {

  override def create(attacker: IPlayer, defender: IPlayer): IDataManager = {
    val handManager = handManagerFactory.empty
    val fieldManager = fieldManagerFactory.create(
      Map(attacker -> List.empty, defender -> List.empty).withDefaultValue(List.empty),
      Map(attacker -> None, defender -> None).withDefaultValue(None),
      Map(attacker -> List.empty, defender -> List.empty).withDefaultValue(List.empty)
    )
    DataManager(handManager, fieldManager)
  }

  override def createFromData(
                               attacker: IPlayer,
                               attackerHand: List[ICard],
                               defender: IPlayer,
                               defenderHand: List[ICard],
                               attackerDefenders: List[Option[ICard]],
                               defenderDefenders: List[Option[ICard]],
                               attackerGoalkeeper: Option[ICard],
                               defenderGoalkeeper: Option[ICard]
                             ): IDataManager = {

    val handManager = handManagerFactory.create(
      attacker,
      attackerHand,
      defender,
      defenderHand
    )

    val fieldManager = fieldManagerFactory.create(
      playerFields = Map.empty, // If not used
      goalkeepers = Map(
        attacker -> attackerGoalkeeper,
        defender -> defenderGoalkeeper
      ),
      defenders = Map(
        attacker -> attackerDefenders,
        defender -> defenderDefenders
      )
    )

    val rawDataManager = DataManager(handManager, fieldManager)
    rawDataManager.initializeFields(attacker, defender)
  }
}

case class DataManager(
                        handCards: IHandCards,
                        fieldCards: IFieldCards,
                        refillStrategy: IRefillStrategy = new StandardRefillStrategy()
                      ) extends IDataManager {
  override def getPlayerHand(player: IPlayer): IHandCardsQueue =
    handCards.getPlayerHand(player)

  override def updatePlayerHand(player: IPlayer, newHand: IHandCardsQueue): IDataManager =
    copy(handCards = handCards.updatePlayerHand(player, newHand))

  override def getAttackingCard(attacker: IPlayer): ICard =
    handCards.getAttackingCard(attacker)

  override def getDefenderCard(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    fieldCards.getPlayerGoalkeeper(player)

  override def updatePlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IDataManager =
    copy(fieldCards = fieldCards.updatePlayerGoalkeeper(player, goalkeeper))

  override def getPlayerDefenders(player: IPlayer): List[Option[ICard]] =
    fieldCards.getPlayerDefenders(player)

  override def updatePlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IDataManager =
    copy(fieldCards = fieldCards.updatePlayerDefenders(player, defenders))

  override def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IDataManager =
    copy(fieldCards = fieldCards.removeDefenderCard(defender, card))

  override def removeDefenderGoalkeeper(defender: IPlayer): IDataManager =
    copy(fieldCards = fieldCards.removeDefenderGoalkeeper(defender))

  override def allDefendersBeaten(defender: IPlayer): Boolean =
    fieldCards.allDefendersBeaten(defender)

  override def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)

  override def initializeFields(attacker: IPlayer, defender: IPlayer): IDataManager = {
    val updated1 = refillStrategy.refillField(this, attacker, handCards.getPlayerHand(attacker))
    val updated2 = refillStrategy.refillField(updated1, defender, handCards.getPlayerHand(defender))
    updated2
  }
  override def refillDefenderField(defender: IPlayer): IDataManager =
    refillStrategy.refillDefenderField(this, defender)

  override def updateRefillStrategy(strategy: IRefillStrategy): IDataManager =
    copy(refillStrategy = strategy)

  override def updateGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IDataManager =
    copy(fieldCards = fieldCards.updateGoalkeeperForAttacker(attacker, card))

}

trait IDataManager {
  def getPlayerHand(player: IPlayer): IHandCardsQueue

  def updatePlayerHand(player: IPlayer, newHand: IHandCardsQueue): IDataManager

  def getAttackingCard(attacker: IPlayer): ICard

  def getDefenderCard(defender: IPlayer, index: Int): Option[ICard]

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def updatePlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IDataManager

  def getPlayerDefenders(player: IPlayer): List[Option[ICard]]

  def updatePlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IDataManager

  def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IDataManager

  def removeDefenderGoalkeeper(defender: IPlayer): IDataManager

  def allDefendersBeaten(defender: IPlayer): Boolean

  def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard]

  def initializeFields(attacker: IPlayer, defender: IPlayer): IDataManager

  def refillDefenderField(defender: IPlayer): IDataManager

  def updateRefillStrategy(strategy: IRefillStrategy): IDataManager

  def updateGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IDataManager

}
