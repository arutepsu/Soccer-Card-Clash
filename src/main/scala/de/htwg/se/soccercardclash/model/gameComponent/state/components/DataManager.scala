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
  def create(player1: IPlayer, player2: IPlayer): IDataManager

  def createFromData(
                      player1: IPlayer,
                      player1Hand: List[ICard],
                      player2: IPlayer,
                      player2Hand: List[ICard],
                      player1Defenders: List[Option[ICard]],
                      player2Defenders: List[Option[ICard]],
                      player1Goalkeeper: Option[ICard],
                      player2Goalkeeper: Option[ICard]
                    ): IDataManager
}

class DataManagerFactory @Inject()(
                                    handManagerFactory: IHandCardsFactory,
                                    fieldManagerFactory: IFieldCardsFactory
                                  ) extends IDataManagerFactory {

  override def create(player1: IPlayer, player2: IPlayer): IDataManager = {
    val handManager = handManagerFactory.empty
    val fieldManager = fieldManagerFactory.create(
      Map(player1 -> List.empty, player2 -> List.empty).withDefaultValue(List.empty),
      Map(player1 -> None, player2 -> None).withDefaultValue(None),
      Map(player1 -> List.empty, player2 -> List.empty).withDefaultValue(List.empty)
    )
    DataManager(handManager, fieldManager)
  }

  override def createFromData(
                               player1: IPlayer,
                               player1Hand: List[ICard],
                               player2: IPlayer,
                               player2Hand: List[ICard],
                               player1Defenders: List[Option[ICard]],
                               player2Defenders: List[Option[ICard]],
                               player1Goalkeeper: Option[ICard],
                               player2Goalkeeper: Option[ICard]
                             ): IDataManager = {

    val handManager = handManagerFactory.create(
      player1,
      player1Hand,
      player2,
      player2Hand
    )

    val fieldManager = fieldManagerFactory.create(
      playerFields = Map.empty, // If not used
      goalkeepers = Map(
        player1 -> player1Goalkeeper,
        player2 -> player2Goalkeeper
      ),
      defenders = Map(
        player1 -> player1Defenders,
        player2 -> player2Defenders
      )
    )

    val rawDataManager = DataManager(handManager, fieldManager)
    rawDataManager.initializeFields(player1, player2)
  }
}

case class DataManager(
                        handCards: IHandCards,
                        fieldCards: IFieldCards,
                        refillStrategy: IRefillStrategy = new StandardRefillStrategy()
                      ) extends IDataManager {

  override def initializePlayerHands(
                                      player1: IPlayer,
                                      cards1: List[ICard],
                                      player2: IPlayer,
                                      cards2: List[ICard]
                                    ): IDataManager = {
    val updatedHandCards = handCards.initializePlayerHands(player1, cards1, player2, cards2)
    copy(handCards = updatedHandCards)
  }

  override def initializeFields(player1: IPlayer, player2: IPlayer): IDataManager = {
    val updated1 = refillStrategy.refillField(this, player1, handCards.getPlayerHand(player1))
    val updated2 = refillStrategy.refillField(updated1, player2, handCards.getPlayerHand(player2))
    updated2
  }

  override def getPlayerHand(player: IPlayer): IHandCardsQueue =
    handCards.getPlayerHand(player)

  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IDataManager =
    copy(handCards = handCards.setPlayerHand(player, newHand))

  override def getAttackingCard(attacker: IPlayer): ICard =
    handCards.getAttackingCard(attacker)

  override def getDefenderCard(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    fieldCards.getPlayerGoalkeeper(player)

  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IDataManager =
    copy(fieldCards = fieldCards.setPlayerGoalkeeper(player, goalkeeper))

  override def getPlayerDefenders(player: IPlayer): List[Option[ICard]] =
    fieldCards.getPlayerDefenders(player)

  override def setPlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IDataManager =
    copy(fieldCards = fieldCards.setPlayerField(player, defenders))

  override def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IDataManager =
    copy(fieldCards = fieldCards.removeDefenderCard(defender, card))

  override def removeDefenderGoalkeeper(defender: IPlayer): IDataManager =
    copy(fieldCards = fieldCards.removeDefenderGoalkeeper(defender))

  override def allDefendersBeaten(defender: IPlayer): Boolean =
    fieldCards.allDefendersBeaten(defender)

  override def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)
  
  override def setPlayerField(player: IPlayer, newField: List[Option[ICard]]): IDataManager =
    copy(fieldCards = fieldCards.setPlayerField(player, newField))

  override def refillDefenderField(defender: IPlayer): IDataManager =
    refillStrategy.refillDefenderField(this, defender)

  override def setRefillStrategy(strategy: IRefillStrategy): IDataManager =
    copy(refillStrategy = strategy)

  override def setGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IDataManager =
    copy(fieldCards = fieldCards.setGoalkeeperForAttacker(attacker, card))

}

trait IDataManager {
  def initializePlayerHands(player1: IPlayer, cards1: List[ICard], player2: IPlayer, cards2: List[ICard]): IDataManager

  def getPlayerHand(player: IPlayer): IHandCardsQueue

  def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IDataManager

  def getAttackingCard(attacker: IPlayer): ICard

  def getDefenderCard(defender: IPlayer, index: Int): Option[ICard]

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IDataManager

  def getPlayerDefenders(player: IPlayer): List[Option[ICard]]

  def setPlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IDataManager

  def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IDataManager

  def removeDefenderGoalkeeper(defender: IPlayer): IDataManager

  def allDefendersBeaten(defender: IPlayer): Boolean

  def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard]

  def setPlayerField(player: IPlayer, newField: List[Option[ICard]]): IDataManager

  def initializeFields(player1: IPlayer, player2: IPlayer): IDataManager

  def refillDefenderField(defender: IPlayer): IDataManager

  def setRefillStrategy(strategy: IRefillStrategy): IDataManager

  def setGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IDataManager

}
