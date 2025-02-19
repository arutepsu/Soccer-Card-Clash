package controller.command.memento

import model.cardComponent.base.BoostedCard
import model.playingFiledComponent.PlayingField
import model.gameComponent.GameManager
import model.playerComponent.Player
import model.playerComponent.PlayerAction.PlayerAction
import model.playerComponent.PlayerAction.*

class MementoManager(private var gameController: GameManager) {

  def createMemento(): Memento = {
    val playingField = gameController.getPlayingField // ✅ Access PlayingField via GameController

    Memento(
      attacker = playingField.getAttacker,
      defender = playingField.getDefender,
      player1Defenders = playingField.fieldState.getPlayerDefenders(playingField.getAttacker).map(_.copy()),
      player2Defenders = playingField.fieldState.getPlayerDefenders(playingField.getDefender).map(_.copy()),
      player1Goalkeeper = playingField.fieldState.getPlayerGoalkeeper(playingField.getAttacker).map(_.copy()),
      player2Goalkeeper = playingField.fieldState.getPlayerGoalkeeper(playingField.getDefender).map(_.copy()),
      player1Hand = playingField.fieldState.getPlayerHand(playingField.getAttacker).map(_.copy()).toList,
      player2Hand = playingField.fieldState.getPlayerHand(playingField.getDefender).map(_.copy()).toList,
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
    val playingField = gameController.getPlayingField // ✅ Access PlayingField via GameController

    if (lastBoostedIndex != -1) {
      println(s"🔄 Undoing boost at index $lastBoostedIndex")

      playingField.fieldState.getPlayerDefenders(memento.attacker).lift(lastBoostedIndex).foreach {
        case boosted: BoostedCard =>
          println(s"✅ Reverting Boost: ${boosted} → Original Value Restored")
          val revertedCard = boosted.revertBoost()

          // 🔥🔥🔥 FIX: Store the reverted card back in the game state
          val updatedDefenders = playingField.fieldState.getPlayerDefenders(memento.attacker).updated(lastBoostedIndex, revertedCard)
          playingField.fieldState.setPlayerDefenders(memento.attacker, updatedDefenders)

        case regular =>
          println(s"ℹ️ Card at index $lastBoostedIndex is not boosted: $regular")
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
