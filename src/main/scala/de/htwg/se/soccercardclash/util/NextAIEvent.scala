package de.htwg.se.soccercardclash.util

object AIEvent {
  case object NextAIEvent extends ObservableEvent
}

case object NextAIEvent extends ObservableEvent

sealed trait AIAction

case class SingleAttackAIAction(defenderIndex: Int) extends AIAction

case class DoubleAttackAIAction(defenderIndex: Int) extends AIAction

case class RegularSwapAIAction(index: Int) extends AIAction

case object ReverseSwapAIAction extends AIAction

case object UndoAIAction extends AIAction

case object RedoAIAction extends AIAction

case object NoOpAIAction extends AIAction

sealed trait Zone

case object DefenderZone extends Zone

case object GoalkeeperZone extends Zone

case class BoostAIAction(cardIndex: Int, zone: Zone) extends AIAction