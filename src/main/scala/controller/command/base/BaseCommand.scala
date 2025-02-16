package controller.command.base

import controller.gameBase.GameController
import model.playingFiledComponent.PlayingField
import util.ICommand
import model.playerComponent.PlayerAction._

abstract class BaseCommand(val gc: GameController) extends ICommand {

  protected var memento: Option[Memento] = None

  override def doStep(): Unit = {
    // ✅ Save the current game state
    memento = Some(createMemento())

    // ✅ Execute subclass-specific action
    executeAction()
  }

  override def undoStep(): Unit = {
    memento.foreach(restoreGameState) // ✅ Restore everything except boosts
  }

  override def redoStep(): Unit = {
    // ✅ Reapply the last executed action
    doStep()
  }

  /** ✅ Subclasses implement their unique action */
  protected def executeAction(): Unit

  /** ✅ Creates a snapshot of the game state */
  private def createMemento(): Memento = {
    val pf = gc.getPlayingField // ✅ Now accessing PlayingField via GameController

    val boostValues = pf.fieldState.getPlayerDefenders(pf.getAttacker).zipWithIndex.collect {
      case (card, index) if card.additionalValue > 0 =>
        index -> (card.additionalValue, card.lastBoostValue, card.wasBoosted)
    }.toMap

    val goalkeeperBoost = pf.fieldState.getPlayerGoalkeeper(pf.getAttacker).map { gk =>
      (gk.additionalValue, gk.lastBoostValue, gk.wasBoosted)
    }

    Memento(
      attacker = pf.getAttacker,
      defender = pf.getDefender,
      player1Defenders = pf.fieldState.getPlayerDefenders(pf.getAttacker).map(_.copy()),
      player2Defenders = pf.fieldState.getPlayerDefenders(pf.getDefender).map(_.copy()),
      player1Goalkeeper = pf.fieldState.getPlayerGoalkeeper(pf.getAttacker),
      player2Goalkeeper = pf.fieldState.getPlayerGoalkeeper(pf.getDefender),
      player1Hand = pf.fieldState.getPlayerHand(pf.getAttacker).toList,
      player2Hand = pf.fieldState.getPlayerHand(pf.getDefender).toList,
      player1Score = pf.scores.getScorePlayer1,
      player2Score = pf.scores.getScorePlayer2,
      boostValues = boostValues,
      goalkeeperBoost = goalkeeperBoost,

      player1Actions = pf.getAttacker.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions()) => action -> 0
      },

      player2Actions = pf.getDefender.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions()) => action -> 0
      }
    )
  }

  protected def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit = {
    val pf = gc.getPlayingField // ✅ Access PlayingField via GameController
    if (lastBoostedIndex != -1) {
      println(s"🔄 Undoing boost at index $lastBoostedIndex")
      pf.fieldState.getPlayerDefenders(memento.attacker).lift(lastBoostedIndex).foreach { card =>
        println(s"✅ Reverting Boost: ${card} → Original Value Restored")
        card.revertAdditionalValue()
      }
    } else {
      println(s"🔄 Skipping boost restoration (General Undo)")
    }
  }

  protected def restoreGameState(memento: Memento): Unit = {
    val pf = gc.getPlayingField // ✅ Access PlayingField via GameController

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
