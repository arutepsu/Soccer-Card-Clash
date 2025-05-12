package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.ObservableEvent


enum Events extends ObservableEvent:

  case MainMenu, CreatePlayer, LoadGame, Exit
  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, RegularSwap, ReverseSwap
  case BoostDefender, BoostGoalkeeper, Reverted
  case AttackerHandCards, AttackerDefenderCards, PlayingField
  case Init, Quit, PauseGame, ResetGame
  case Undo, Redo, SaveGame
  case TieComparison, DoubleTieComparison

  case CardReverted(card: ICard, owner: IPlayer)
  case CardBoosted(card: ICard, owner: IPlayer)
  case GameOver(winner: IPlayer)
  case ComparedCardsEvent(attackingCard: ICard, defendingCard: ICard)
  case DoubleComparedCardsEvent(attackingCard1: ICard, attackingCard2: ICard, defendingCard: ICard)
  case AttackResultEvent(attacker: IPlayer, defender: IPlayer, attackSuccess: Boolean)
  case TieComparisonEvent(attackingCard: ICard, defenderCard: ICard, extraAttackerCard: ICard, extraDefenderCard: ICard)
  case DoubleTieComparisonEvent(attackingCard1: ICard, attackingCard2: ICard, defendingCard: ICard, extraAttackerCard: ICard, extraDefenderCard: ICard)
  case NoBoostsEvent(player: IPlayer)
  case NoSwapsEvent(player: IPlayer)
  case NoDoubleAttacksEvent(player: IPlayer)
  case ScoreEvent(player: IPlayer)

  override def toString: String = this match
    case e @ (RegularAttack | DoubleAttack | Undo | Redo | BoostDefender | BoostGoalkeeper | RegularSwap | ReverseSwap) =>
      e.productPrefix
    case ScoreEvent(player) => s"ScoreEvent(${player.name})"
    case GameOver(winner) => s"GameOver(${winner.name})"
    case ComparedCardsEvent(a, d) => s"ComparedCards(${a.value} of ${a.suit}, ${d.value} of ${d.suit})"
    case DoubleComparedCardsEvent(a1, a2, d) =>
      s"DoubleComparedCards(${a1.value} of ${a1.suit}, ${a2.value} of ${a2.suit}, ${d.value} of ${d.suit})"
    case AttackResultEvent(a, d, success) => s"AttackResult(${a.name} vs ${d.name}, success=$success)"
    case TieComparisonEvent(a, d, ea, ed) =>
      s"TieComparison(${a.value} of ${a.suit}, ${d.value} of ${d.suit}, extra: ${ea.value} of ${ea.suit}, ${ed.value} of ${ed.suit})"
    case DoubleTieComparisonEvent(a1, a2, d, ea, ed) =>
      s"DoubleTieComparison(${a1.value} of ${a1.suit}, ${a2.value} of ${a2.suit}, ${d.value} of ${d.suit}, extra: ${ea.value} of ${ea.suit}, ${ed.value} of ${ed.suit})"
    case CardReverted(c, o) => s"CardReverted(${c.value} of ${c.suit}, owner=${o.name})"
    case CardBoosted(c, o) => s"CardBoosted(${c.value} of ${c.suit}, owner=${o.name})"
    case NoBoostsEvent(p) => s"NoBoosts(${p.name})"
    case NoSwapsEvent(p) => s"NoSwaps(${p.name})"
    case NoDoubleAttacksEvent(p) => s"NoDoubleAttacks(${p.name})"
    case _ => productPrefix

