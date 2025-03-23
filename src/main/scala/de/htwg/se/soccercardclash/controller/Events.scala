package de.htwg.se.soccercardclash.controller

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.ObservableEvent


enum Events extends ObservableEvent {

  case MainMenu, CreatePlayer, LoadGame, Exit

  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, RegularSwap, ReverseSwap
  case BoostDefender, BoostGoalkeeper, Reverted

  case AttackerHandCards, AttackerDefenderCards, PlayingField
  case Init
  case Quit
  
  case PauseGame, ResetGame
  
  case Undo, Redo, SaveGame
  case TieComparison, DoubleTieComparison

}
case class GameOver(winner: IPlayer) extends ObservableEvent

case class ComparedCardsEvent(attackingCard: ICard,
                              defendingCard: ICard
                             ) extends ObservableEvent
case class DoubleComparedCardsEvent(attackingCard1: ICard,
                                    attackingCard2: ICard,
                                    defendingCard: ICard
                                   ) extends ObservableEvent
case class AttackResultEvent(attacker: IPlayer,
                             defender: IPlayer,
                             attackSuccess: Boolean
                            ) extends ObservableEvent
case class TieComparisonEvent(attackingCard: ICard,
                              defenderCard: ICard,
                              extraAttackerCard: ICard,
                              extraDefenderCard: ICard
                             ) extends ObservableEvent
case class DoubleTieComparisonEvent(attackingCard1: ICard,
                                     attackingCard2: ICard,
                                     defendingCard: ICard,
                                     extraAttackerCard: ICard,
                                     extraDefenderCard: ICard
                                   ) extends ObservableEvent
case class NoBoostsEvent(player: IPlayer) extends ObservableEvent
case class NoSwapsEvent(player: IPlayer) extends ObservableEvent
case class NoDoubleAttacksEvent(player: IPlayer) extends ObservableEvent
case class GoalScoredEvent(player: IPlayer) extends ObservableEvent