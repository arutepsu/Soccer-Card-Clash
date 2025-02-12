package controller

import util.Command
import model.playingFiledComponent.PlayingField
import model.cardComponent.Card

class BoostGoalkeeperCommand(pf: PlayingField) extends Command {
  private var memento: Option[Memento] = None
  private var boostValue: Int = 0 // ✅ Track boost value separately for undo

  override def doStep(): Unit = {
    // ✅ Save the current state before making changes
    memento = Some(createMemento())

    // ✅ Get the goalkeeper card
    val goalkeeperOpt = pf.getGoalkeeper(pf.getAttacker)

    goalkeeperOpt.foreach { goalkeeper =>
      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
      pf.chooseBoostCardGoalkeeper() // ✅ Apply the boost
    }
  }

  override def undoStep(): Unit = {
    // ✅ Restore the previous state
    memento.foreach(restoreMemento)
  }

  override def redoStep(): Unit = {
    doStep() // ✅ Reapply the boost effect
  }

  private def createMemento(): Memento = {
    Memento(
      attacker = pf.getAttacker,
      defender = pf.getDefender,
      player1Defenders = pf.playerDefenders(pf.getAttacker),
      player2Defenders = pf.playerDefenders(pf.getDefender),
      player1Goalkeeper = pf.playerGoalkeeper(pf.getAttacker),
      player2Goalkeeper = pf.playerGoalkeeper(pf.getDefender),
      player1Hand = pf.getHand(pf.getAttacker).clone(),
      player2Hand = pf.getHand(pf.getDefender).clone(),
      player1Score = pf.getScorePlayer1,
      player2Score = pf.getScorePlayer2,
      boostValues = Map(-1 -> boostValue) // ✅ Store boost value for the goalkeeper (-1 is a special index)
    )
  }

  private def restoreMemento(memento: Memento): Unit = {
    pf.setRoles(memento.attacker, memento.defender)

    // ✅ Restore player-specific states
    pf.setPlayerDefenders(memento.attacker, memento.player1Defenders)
    pf.setPlayerDefenders(memento.defender, memento.player2Defenders)
    pf.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper)
    pf.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper)

    // ✅ Restore player hands
    val attackerHand = pf.getHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand)

    val defenderHand = pf.getHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand)

    // ✅ Restore scores
    pf.setScorePlayer1(memento.player1Score)
    pf.setScorePlayer2(memento.player2Score)

    // ✅ Reset goalkeeper boost
    memento.boostValues.get(-1).foreach { boost =>
      pf.getGoalkeeper(memento.attacker).foreach { goalkeeper =>
        println(s"Undoing boost: Resetting goalkeeper ${goalkeeper} by -$boost")

        // ✅ Revert the boost effect
        val revertedGoalkeeper = goalkeeper.copy(
          additionalValue = goalkeeper.additionalValue - boost, // ✅ Subtract boost to revert
          lastBoostValue = 0 // ✅ Reset lastBoostValue
        )

        // ✅ Restore the goalkeeper in the playing field
        pf.setGoalkeeperForAttacker(revertedGoalkeeper)

        println(s"After Undo: $revertedGoalkeeper")
      }
    }

    // ✅ Notify observers of the restored state
    pf.notifyObservers()
  }
}
