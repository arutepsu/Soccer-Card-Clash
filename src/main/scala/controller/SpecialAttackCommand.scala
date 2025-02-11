package controller
import util.Command
import model.playingFiledComponent.PlayingField
import scala.collection.mutable
import model.cardComponent.Card
import controller.Memento
//package controller
import util.Command
import model.playingFiledComponent.PlayingField
import controller.Memento
class SpecialAttackCommand(defenderIndex: Int, pf: PlayingField) extends Command {
  private var memento: Option[Memento] = None
  private var attackSuccessful: Boolean = false // Tracks if the attack was successful

  override def doStep(): Unit = {
    // Save the current state before making changes
    memento = Some(createMemento())
    attackSuccessful = pf.doubleAtack(defenderIndex)
  }

  override def undoStep(): Unit = {
    // Restore the state from the memento
    memento.foreach(restoreMemento)
  }

  override def redoStep(): Unit = {
    // Re-execute the attack logic
    doStep()
  }

  private def createMemento(): Memento = {
    // Capture the current state of the game
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
      boostValues = Map.empty[Int, Int] // ✅ Fix: Added boostValues fiel
    )
  }

  private def restoreMemento(memento: Memento): Unit = {
    pf.setRoles(memento.attacker, memento.defender)

    // Restore player-specific states
    pf.setPlayerDefenders(memento.attacker, memento.player1Defenders)
    pf.setPlayerDefenders(memento.defender, memento.player2Defenders)
    pf.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper)
    pf.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper)

    // Restore player hands (clear and refill with memento state)
    val attackerHand = pf.getHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand)

    val defenderHand = pf.getHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand)

    // Restore scores
    pf.setScorePlayer1(memento.player1Score)
    pf.setScorePlayer2(memento.player2Score)
    // ✅ Properly revert the boost effects
    memento.boostValues.foreach { case (index, boost) =>
      pf.getHand(memento.defender).toList.lift(index).foreach { card =>
        println(s"Undoing boost: Resetting ${card} by -$boost")

        // ✅ Reset the card boost
        val revertedCard = card.copy(
          additionalValue = card.additionalValue - boost, // ✅ Subtract boost to revert
          lastBoostValue = 0 // ✅ Reset lastBoostValue
        )

        // ✅ Replace the card in the defender's hand
        defenderHand.update(index, revertedCard)

        println(s"After Undo: $revertedCard")
      }
    }
    // Notify observers of the restored state
    pf.notifyObservers()
  }

}