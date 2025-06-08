package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.ObservableEvent

object GlobalObservable extends Observable

sealed trait SceneSwitchEvent extends ObservableEvent

case class SceneChangedEvent(newScene: SceneSwitchEvent) extends ObservableEvent

object SceneSwitchEvent:
  case object MainMenu extends SceneSwitchEvent

  case object Multiplayer extends SceneSwitchEvent

  case object SinglePlayer extends SceneSwitchEvent

  case object AISelection extends SceneSwitchEvent

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

  case object TieComparison extends GameActionEvent

  case object DoubleTieComparison extends GameActionEvent

  case object Undo extends GameActionEvent

  case object Redo extends GameActionEvent

  case object SaveGame extends GameActionEvent

sealed trait StateEvent extends ObservableEvent

object StateEvent:
  case class CardBoosted(card: Option[ICard], owner: IPlayer) extends StateEvent

  case class GameOver(winner: IPlayer) extends StateEvent

  case class ComparedCardsEvent(attackingCard: Option[ICard], defendingCard: Option[ICard]) extends StateEvent

  case class DoubleComparedCardsEvent(attackingCard1: Option[ICard], attackingCard2: Option[ICard], defendingCard: Option[ICard]) extends StateEvent

  case class AttackResultEvent(attacker: IPlayer, defender: IPlayer, attackSuccess: Boolean) extends StateEvent

  case class TieComparisonEvent(
                                 attackingCard: Option[ICard], defenderCard: Option[ICard],
                                 extraAttackerCard: Option[ICard], extraDefenderCard: Option[ICard]
                               ) extends StateEvent

  case class DoubleTieComparisonEvent(
                                       attackingCard1: Option[ICard], attackingCard2: Option[ICard], defendingCard: Option[ICard],
                                       extraAttackerCard: Option[ICard], extraDefenderCard: Option[ICard]
                                     ) extends StateEvent

  case class NoBoostsEvent(player: IPlayer) extends StateEvent
  case class NoSwapsEvent(player: IPlayer) extends StateEvent
  case class NoDoubleAttacksEvent(player: IPlayer) extends StateEvent
  case class ScoreEvent(player: IPlayer) extends StateEvent
