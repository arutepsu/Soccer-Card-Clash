package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.ObservableEvent

object GlobalObservable extends Observable

sealed trait SceneSwitchEvent extends ObservableEvent
object SceneSwitchEvent:
  case object MainMenu extends SceneSwitchEvent
  case object CreatePlayer extends SceneSwitchEvent
  case object CreatePlayerWithAI extends SceneSwitchEvent
  case object LoadGame extends SceneSwitchEvent
  case object Exit extends SceneSwitchEvent
  case object StartGame extends SceneSwitchEvent
  case object AttackerHandCards extends SceneSwitchEvent
  case object AttackerDefenderCards extends SceneSwitchEvent
  case object PlayingField extends SceneSwitchEvent

sealed trait GameActionEvent extends ObservableEvent
object GameActionEvent:
  case object RegularAttack extends GameActionEvent
  case object DoubleAttack extends GameActionEvent
  case object RegularSwap extends GameActionEvent
  case object ReverseSwap extends GameActionEvent
  case object BoostDefender extends GameActionEvent
  case object BoostGoalkeeper extends GameActionEvent
  case object Reverted extends GameActionEvent
  case object TieComparison extends GameActionEvent
  case object DoubleTieComparison extends GameActionEvent
  case object Undo extends GameActionEvent
  case object Redo extends GameActionEvent
  case object SaveGame extends GameActionEvent

sealed trait StateEvent extends ObservableEvent
object StateEvent:
  case class CardReverted(card: ICard, owner: IPlayer) extends StateEvent
  case class CardBoosted(card: ICard, owner: IPlayer) extends StateEvent
  case class GameOver(winner: IPlayer) extends StateEvent

  case class ComparedCardsEvent(attackingCard: ICard, defendingCard: ICard) extends StateEvent
  case class DoubleComparedCardsEvent(attackingCard1: ICard, attackingCard2: ICard, defendingCard: ICard) extends StateEvent
  case class AttackResultEvent(attacker: IPlayer, defender: IPlayer, attackSuccess: Boolean) extends StateEvent

  case class TieComparisonEvent(
                                 attackingCard: ICard, defenderCard: ICard,
                                 extraAttackerCard: ICard, extraDefenderCard: ICard
                               ) extends StateEvent

  case class DoubleTieComparisonEvent(
                                       attackingCard1: ICard, attackingCard2: ICard, defendingCard: ICard,
                                       extraAttackerCard: ICard, extraDefenderCard: ICard
                                     ) extends StateEvent

  case class NoBoostsEvent(player: IPlayer) extends StateEvent
  case class NoSwapsEvent(player: IPlayer) extends StateEvent
  case class NoDoubleAttacksEvent(player: IPlayer) extends StateEvent
  case class ScoreEvent(player: IPlayer) extends StateEvent


sealed trait PlayerAction

case class SingleAttackAction(defenderIndex: Int) extends PlayerAction
case class DoubleAttackAction(defenderIndex: Int) extends PlayerAction
case class RegularSwapAction(index: Int) extends PlayerAction
case object ReverseSwapAction extends PlayerAction
case class BoostDefenderAction(defenderIndex: Int) extends PlayerAction
case object BoostGoalkeeperAction extends PlayerAction
case object UndoAction extends PlayerAction
case object RedoAction extends PlayerAction
case object NoOpAction extends PlayerAction
