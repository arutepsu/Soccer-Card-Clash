package controller.command

import model.playingFiledComponent.PlayingField
import util.Command

abstract class BaseCommand(val pf: PlayingField) extends Command {

  protected var memento: Option[Memento] = None

  override def doStep(): Unit = {
    // ✅ Save the current game state
    memento = Some(createMemento())

    // ✅ Execute subclass-specific action
    executeAction()
  }

  override def undoStep(): Unit = {
    // ✅ Restore the state from the memento
    memento.foreach(restoreMemento)
  }

  override def redoStep(): Unit = {
    // ✅ Reapply the last executed action
    doStep()
  }

  /** ✅ Subclasses implement their unique action */
  protected def executeAction(): Unit

  /** ✅ Creates a snapshot of the game state */
  private def createMemento(): Memento = {
    val boostValues = pf.fieldState.playerDefenders(pf.getAttacker).zipWithIndex.collect {
      case (card, index) if card.additionalValue > 0 =>
        index -> (card.additionalValue, card.lastBoostValue, card.wasBoosted)
    }.toMap

    val goalkeeperBoost = pf.fieldState.getGoalkeeper(pf.getAttacker).map { gk =>
      (gk.additionalValue, gk.lastBoostValue, gk.wasBoosted)
    }

    Memento(
      attacker = pf.getAttacker,
      defender = pf.getDefender,
      player1Defenders = pf.fieldState.playerDefenders(pf.getAttacker).map(_.copy()),
      player2Defenders = pf.fieldState.playerDefenders(pf.getDefender).map(_.copy()),
      player1Goalkeeper = pf.fieldState.getGoalkeeper(pf.getAttacker),
      player2Goalkeeper = pf.fieldState.getGoalkeeper(pf.getDefender),
      player1Hand = pf.getHand(pf.getAttacker).toList,
      player2Hand = pf.getHand(pf.getDefender).toList,
      player1Score = pf.scores.getScorePlayer1,
      player2Score = pf.scores.getScorePlayer2,
      boostValues = boostValues,
      goalkeeperBoost = goalkeeperBoost
    )
  }

  private def restoreMemento(memento: Memento): Unit = {
    pf.roles.setRoles(memento.attacker, memento.defender)
    pf.fieldState.setPlayerDefenders(memento.attacker, memento.player1Defenders.map(_.copy()))
    pf.fieldState.setPlayerDefenders(memento.defender, memento.player2Defenders.map(_.copy()))
    pf.fieldState.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper.map(_.copy()))
    pf.fieldState.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper.map(_.copy()))

    // ✅ Restore player hands
    val attackerHand = pf.getHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.getHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))

    // ✅ Restore scores
    pf.scores.setScorePlayer1(memento.player1Score)
    pf.scores.setScorePlayer2(memento.player2Score)

    // ✅ Restore boosted cards
    memento.boostValues.foreach { case (index, (additional, lastBoost, wasBoosted)) =>
      pf.fieldState.playerDefenders(memento.attacker).lift(index).foreach { card =>
        card.additionalValue = additional
        card.lastBoostValue = lastBoost
        card.wasBoosted = wasBoosted
        card.updateValue()
      }
    }

    // ✅ Restore goalkeeper boost
    memento.goalkeeperBoost.foreach { case (additional, lastBoost, wasBoosted) =>
      pf.fieldState.getGoalkeeper(memento.attacker).foreach { goalkeeper =>
        goalkeeper.additionalValue = additional
        goalkeeper.lastBoostValue = lastBoost
        goalkeeper.wasBoosted = wasBoosted
        goalkeeper.updateValue()
        pf.fieldState.setPlayerGoalkeeper(memento.attacker, Some(goalkeeper))
      }
    }

    // ✅ Notify observers of restored state
    pf.notifyObservers()
  }
}
