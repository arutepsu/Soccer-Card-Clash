package controller.command.memento

import model.cardComponent.base.BoostedCard
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerAction.*
import model.playerComponent.base.Player
import model.playingFiledComponent.base.{ActionHandler, PlayingField}

class MementoManager(private var gameManager: ActionHandler) {

  def createMemento(): Memento = {
    val playingField = gameManager.getPlayingField

    Memento(
      attacker = playingField.getAttacker,
      defender = playingField.getDefender,
      player1Defenders = playingField.dataManager.getPlayerDefenders(playingField.getAttacker).map(_.copy()),
      player2Defenders = playingField.dataManager.getPlayerDefenders(playingField.getDefender).map(_.copy()),
      player1Goalkeeper = playingField.dataManager.getPlayerGoalkeeper(playingField.getAttacker).map(_.copy()),
      player2Goalkeeper = playingField.dataManager.getPlayerGoalkeeper(playingField.getDefender).map(_.copy()),
      player1Hand = playingField.dataManager.getPlayerHand(playingField.getAttacker).map(_.copy()).toList,
      player2Hand = playingField.dataManager.getPlayerHand(playingField.getDefender).map(_.copy()).toList,
      player1Score = playingField.scores.getScorePlayer1,
      player2Score = playingField.scores.getScorePlayer2,

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
    val playingField = gameManager.getPlayingField

    if (lastBoostedIndex != -1) {

      playingField.dataManager.getPlayerDefenders(memento.attacker).lift(lastBoostedIndex).foreach {
        case boosted: BoostedCard =>
          println(s"✅ Reverting Boost: ${boosted} → Original Value Restored")
          val revertedCard = boosted.revertBoost()
          
          val updatedDefenders = playingField.dataManager.getPlayerDefenders(memento.attacker).updated(lastBoostedIndex, revertedCard)
          playingField.dataManager.setPlayerDefenders(memento.attacker, updatedDefenders)

        case regular =>
          println(s"ℹ️ Card at index $lastBoostedIndex is not boosted: $regular")
      }
    }
  }


  def restoreGameState(memento: Memento): Unit = {
    val pf = gameManager.getPlayingField

    pf.roles.setRoles(memento.attacker, memento.defender)
    pf.dataManager.setPlayerDefenders(memento.attacker, memento.player1Defenders.map(_.copy()))
    pf.dataManager.setPlayerDefenders(memento.defender, memento.player2Defenders.map(_.copy()))
    pf.dataManager.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper.map(_.copy()))
    pf.dataManager.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper.map(_.copy()))
    
    val attackerHand = pf.dataManager.getPlayerHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.dataManager.getPlayerHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))
    
    pf.scores.setScorePlayer1(memento.player1Score)
    pf.scores.setScorePlayer2(memento.player2Score)

    val restoredPlayer1 = memento.attacker.setActionStates(
      memento.player1Actions.map {
        case (action, remainingUses) =>
          action -> (if (remainingUses > 0) CanPerformAction(remainingUses) else OutOfActions())
      }
    )

    val restoredPlayer2 = memento.defender.setActionStates(
      memento.player2Actions.map {
        case (action, remainingUses) =>
          action -> (if (remainingUses > 0) CanPerformAction(remainingUses) else OutOfActions())
      }
    )

    pf.roles.setRoles(restoredPlayer1, restoredPlayer2)
    
    pf.notifyObservers()
  }
}
