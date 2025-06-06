package de.htwg.se.soccercardclash.model.gameComponent.components

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

trait IGameCardsFactory {
  def create(attacker: IPlayer, defender: IPlayer): IGameCards

  def createFromData(
                      attacker: IPlayer,
                      attackerHand: List[ICard],
                      defender: IPlayer,
                      defenderHand: List[ICard],
                      attackerDefenders: List[Option[ICard]],
                      defenderDefenders: List[Option[ICard]],
                      attackerGoalkeeper: Option[ICard],
                      defenderGoalkeeper: Option[ICard]
                    ): IGameCards
}

class GameCardsFactory @Inject()(
                                  handCardsFactory: IHandCardsFactory,
                                  fieldCardsFactory: IFieldCardsFactory,
                                  refillStrategy: IRefillStrategy
                                ) extends IGameCardsFactory {


  override def create(attacker: IPlayer, defender: IPlayer): IGameCards = {
    val handManager = handCardsFactory.empty
    val fieldManager = fieldCardsFactory.create(
      Map(attacker -> List.empty, defender -> List.empty).withDefaultValue(List.empty),
      Map(attacker -> None, defender -> None).withDefaultValue(None),
      Map(attacker -> List.empty, defender -> List.empty).withDefaultValue(List.empty)
    )
    GameCards(handManager, fieldManager, refillStrategy)
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
                             ): IGameCards = {

    val hand = handCardsFactory.create(
      attacker,
      attackerHand,
      defender,
      defenderHand
    )

    val field = fieldCardsFactory.create(
      playerFields = Map.empty,
      goalkeepers = Map(
        attacker -> attackerGoalkeeper,
        defender -> defenderGoalkeeper
      ),
      defenders = Map(
        attacker -> attackerDefenders,
        defender -> defenderDefenders
      )
    )

    val rawGameCards = GameCards(hand, field, refillStrategy)
    rawGameCards.initializeFields(attacker, defender)
  }
}

case class GameCards(
                      handCards: IHandCards,
                      fieldCards: IFieldCards,
                      refillStrategy: IRefillStrategy
                    ) extends IGameCards {
  override def getPlayerHand(player: IPlayer): IHandCardsQueue =
    handCards.getPlayerHand(player)

  override def newPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IGameCards =
    copy(handCards = handCards.newPlayerHand(player, newHand))

  override def getAttackingCard(attacker: IPlayer): ICard =
    handCards.getAttackingCard(attacker)

  override def getDefenderCard(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    fieldCards.getPlayerGoalkeeper(player)

  override def newPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IGameCards =
    copy(fieldCards = fieldCards.newPlayerGoalkeeper(player, goalkeeper))

  override def getPlayerDefenders(player: IPlayer): List[Option[ICard]] =
    fieldCards.getPlayerDefenders(player)

  override def newPlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IGameCards =
    copy(fieldCards = fieldCards.newPlayerDefenders(player, defenders))

  override def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IGameCards =
    copy(fieldCards = fieldCards.removeDefenderCard(defender, card))

  override def removeDefenderGoalkeeper(defender: IPlayer): IGameCards =
    copy(fieldCards = fieldCards.removeDefenderGoalkeeper(defender))

  override def allDefendersBeaten(defender: IPlayer): Boolean =
    fieldCards.allDefendersBeaten(defender)

  override def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard] =
    fieldCards.getDefenderCard(defender, index)

  override def initializeFields(attacker: IPlayer, defender: IPlayer): IGameCards = {
    val updated1 = refillStrategy.refillField(this, attacker, handCards.getPlayerHand(attacker))
    val updated2 = refillStrategy.refillField(updated1, defender, handCards.getPlayerHand(defender))
    updated2
  }

  override def refillDefenderField(defender: IPlayer): IGameCards =
    refillStrategy.refillDefenderField(this, defender)

  override def newRefillStrategy(strategy: IRefillStrategy): IGameCards =
    copy(refillStrategy = strategy)

  override def newGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IGameCards =
    copy(fieldCards = fieldCards.newGoalkeeperForAttacker(attacker, card))

}

trait IGameCards {
  def getPlayerHand(player: IPlayer): IHandCardsQueue

  def newPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IGameCards

  def getAttackingCard(attacker: IPlayer): ICard

  def getDefenderCard(defender: IPlayer, index: Int): Option[ICard]

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def newPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): IGameCards

  def getPlayerDefenders(player: IPlayer): List[Option[ICard]]

  def newPlayerDefenders(player: IPlayer, defenders: List[Option[ICard]]): IGameCards

  def removeDefenderCard(defender: IPlayer, card: Option[ICard]): IGameCards

  def removeDefenderGoalkeeper(defender: IPlayer): IGameCards

  def allDefendersBeaten(defender: IPlayer): Boolean

  def getDefenderCardAt(defender: IPlayer, index: Int): Option[ICard]

  def initializeFields(attacker: IPlayer, defender: IPlayer): IGameCards

  def refillDefenderField(defender: IPlayer): IGameCards

  def newRefillStrategy(strategy: IRefillStrategy): IGameCards

  def newGoalkeeperForAttacker(attacker: IPlayer, card: ICard): IGameCards

}
