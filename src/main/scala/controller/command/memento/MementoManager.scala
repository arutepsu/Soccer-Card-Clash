package controller.command.memento

import model.playingFiledComponent.PlayingField
import model.cardComponent.base.Card
import model.playerComponent.Player
import model.playerComponent.PlayerAction.PlayerAction
import controller.gameBase.GameManager
import model.playerComponent.PlayerAction._

class MementoManager(private var gameController: GameManager) {

  def createMemento(): Memento = {
    val playingField = gameController.getPlayingField // ✅ Now accessing PlayingField via GameController

    val boostValues = playingField.fieldState.getPlayerDefenders(playingField.getAttacker).zipWithIndex.collect {
      case (card, index) if card.additionalValue > 0 =>
        index -> (card.additionalValue, card.lastBoostValue, card.wasBoosted)
    }.toMap

    val goalkeeperBoost = playingField.fieldState.getPlayerGoalkeeper(playingField.getAttacker).map { gk =>
      (gk.additionalValue, gk.lastBoostValue, gk.wasBoosted)
    }

    Memento(
      attacker = playingField.getAttacker,
      defender = playingField.getDefender,
      player1Defenders = playingField.fieldState.getPlayerDefenders(playingField.getAttacker).map(_.copy()),
      player2Defenders = playingField.fieldState.getPlayerDefenders(playingField.getDefender).map(_.copy()),
      player1Goalkeeper = playingField.fieldState.getPlayerGoalkeeper(playingField.getAttacker),
      player2Goalkeeper = playingField.fieldState.getPlayerGoalkeeper(playingField.getDefender),
      player1Hand = playingField.fieldState.getPlayerHand(playingField.getAttacker).toList,
      player2Hand = playingField.fieldState.getPlayerHand(playingField.getDefender).toList,
      player1Score = playingField.scores.getScorePlayer1,
      player2Score = playingField.scores.getScorePlayer2,
      boostValues = boostValues,
      goalkeeperBoost = goalkeeperBoost,

      player1Actions = playingField.getAttacker.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions()) => action -> 0
        case (action, unknown) =>
          println(s"⚠️ Warning: Unhandled action state for $action: $unknown")
          action -> 0
      },

      player2Actions = playingField.getDefender.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions()) => action -> 0
        case (action, unknown) =>
          println(s"⚠️ Warning: Unhandled action state for $action: $unknown")
          action -> 0
      }

    )
  }

  def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit = {
    val playingField = gameController.getPlayingField // ✅ Access PlayingField via GameController
    if (lastBoostedIndex != -1) {
      println(s"🔄 Undoing boost at index $lastBoostedIndex")
      playingField.fieldState.getPlayerDefenders(memento.attacker).lift(lastBoostedIndex).foreach { card =>
        println(s"✅ Reverting Boost: ${card} → Original Value Restored")
        card.revertAdditionalValue()
      }
    } else {
      println(s"🔄 Skipping boost restoration (General Undo)")
    }
  }

  def restoreGameState(memento: Memento): Unit = {
    val pf = gameController.getPlayingField // ✅ Access PlayingField via GameController

    pf.roles.setRoles(memento.attacker, memento.defender)
    pf.fieldState.setPlayerDefenders(memento.attacker, memento.player1Defenders.map(_.copy()))
    pf.fieldState.setPlayerDefenders(memento.defender, memento.player2Defenders.map(_.copy()))
    pf.fieldState.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper.map(_.copy()))
    pf.fieldState.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper.map(_.copy()))

    // ✅ Restore player hands
    val attackerHand = pf.fieldState.getPlayerHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.fieldState.getPlayerHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))

    // ✅ Restore scores
    pf.scores.setScorePlayer1(memento.player1Score)
    pf.scores.setScorePlayer2(memento.player2Score)

    // ✅ Restore goalkeeper boost if needed
    if (memento.goalkeeperBoost.isDefined) {
      val (additional, lastBoost, wasBoosted) = memento.goalkeeperBoost.get
      pf.fieldState.getPlayerGoalkeeper(memento.attacker).foreach { goalkeeper =>
        println(s"🔄 Restoring goalkeeper boost...")
        goalkeeper.revertAdditionalValue()
        pf.fieldState.setPlayerGoalkeeper(memento.attacker, Some(goalkeeper))
      }
    }
    // ✅ Restore action limits
    val restoredPlayer1 = memento.attacker.copy(
      actionStates = memento.player1Actions.map { case (action, remainingUses) =>
        action -> (if (remainingUses > 0) CanPerformAction(remainingUses) else OutOfActions())
      }
    )

    val restoredPlayer2 = memento.defender.copy(
      actionStates = memento.player2Actions.map { case (action, remainingUses) =>
        action -> (if (remainingUses > 0) CanPerformAction(remainingUses) else OutOfActions())

      }
    )
    pf.roles.setRoles(restoredPlayer1, restoredPlayer2)

    // ✅ Notify observers of restored state
    pf.notifyObservers()
  }
}