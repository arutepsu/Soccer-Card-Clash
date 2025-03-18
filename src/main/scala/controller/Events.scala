package controller
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import util.ObservableEvent

enum Events extends ObservableEvent {

  case MainMenu, CreatePlayer, LoadGame, Exit

  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, RegularSwap, CircularSwap
  case BoostDefender, BoostGoalkeeper, Reverted

  case AttackerHandCards, AttackerDefenderCards, PlayingField

  case Quit
  
  case PauseGame, ResetGame
  
  case Undo, Redo, SaveGame
  case TieComparison, DoubleTieComparison
}
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
