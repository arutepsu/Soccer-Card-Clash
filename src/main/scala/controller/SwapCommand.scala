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
class SwapCommand(cardIndex: Int, pf: PlayingField) extends Command {
  private var memento: Option[Memento] = None

  override def doStep(): Unit = {
    // Save the current state before swapping
    memento = Some(createMemento())

    // Execute the swap
    pf.swapAttacker(cardIndex)
  }

  override def undoStep(): Unit = {
    // Restore the previous state
    memento.foreach(restoreMemento)
  }

  override def redoStep(): Unit = {
    doStep() // Reapply the swap action
  }

  private def createMemento(): Memento = {
    val boostValues = pf.playerDefenders(pf.getAttacker).zipWithIndex.collect {
      case (card, index) if card.additionalValue > 0 => index -> (card.additionalValue, card.lastBoostValue, card.wasBoosted)
    }.toMap

    val goalkeeperBoost = pf.getGoalkeeper(pf.getAttacker).map { gk =>
      (gk.additionalValue, gk.lastBoostValue, gk.wasBoosted)
    }

    Memento(
      attacker = pf.getAttacker,
      defender = pf.getDefender,
      player1Defenders = pf.playerDefenders(pf.getAttacker).map(_.copy()), // ✅ Ensure deep copy
      player2Defenders = pf.playerDefenders(pf.getDefender).map(_.copy()), // ✅ Ensure deep copy
      player1Goalkeeper = pf.getGoalkeeper(pf.getAttacker),
      player2Goalkeeper = pf.getGoalkeeper(pf.getDefender),
      player1Hand = pf.getHand(pf.getAttacker).toList,
      player2Hand = pf.getHand(pf.getDefender).toList,
      player1Score = pf.getScorePlayer1,
      player2Score = pf.getScorePlayer2,
      boostValues = boostValues,
      goalkeeperBoost = goalkeeperBoost
    )
  }


  private def restoreMemento(memento: Memento): Unit = {
    pf.setRoles(memento.attacker, memento.defender)

    // ✅ Restore player-specific states (including ATTACKER's FIELD)
    pf.setPlayerDefenders(memento.attacker, memento.player1Defenders.map(_.copy()))
    pf.setPlayerDefenders(memento.defender, memento.player2Defenders.map(_.copy()))
    pf.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper.map(_.copy()))
    pf.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper.map(_.copy()))

    // ✅ Restore player hands
    val attackerHand = pf.getHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.getHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))

    // ✅ Restore scores
    pf.setScorePlayer1(memento.player1Score)
    pf.setScorePlayer2(memento.player2Score)

    // ✅ Restore BOOSTED CARDS in ATTACKER’S FIELD
    memento.boostValues.foreach { case (index, (additional, lastBoost, wasBoosted)) =>
      val attackerField = pf.playerDefenders(memento.attacker).map(_.copy())

      attackerField.lift(index).foreach { card =>
        println(s"Restoring boost for attacker's field card: $card")
        card.additionalValue = additional // ✅ Ensure `additionalValue` is properly restored
        card.lastBoostValue = lastBoost
        card.wasBoosted = wasBoosted
        card.updateValue()
      }

      pf.setPlayerDefenders(memento.attacker, attackerField)
    }

    // ✅ Restore BOOSTED CARDS in DEFENDER’S FIELD
    memento.boostValues.foreach { case (index, (additional, lastBoost, wasBoosted)) =>
      val defenderField = pf.playerDefenders(memento.defender).map(_.copy())

      defenderField.lift(index).foreach { card =>
        println(s"Restoring boost for defender's field card: $card")
        card.additionalValue = additional // ✅ Ensure `additionalValue` is properly restored
        card.lastBoostValue = lastBoost
        card.wasBoosted = wasBoosted
        card.updateValue()
      }

      pf.setPlayerDefenders(memento.defender, defenderField)
    }

    // ✅ Restore boosted attacker’s GOALKEEPER if applicable
    memento.goalkeeperBoost.foreach { case (additional, lastBoost, wasBoosted) =>
      pf.getGoalkeeper(memento.attacker).foreach { goalkeeper =>
        println(s"Restoring boost for attacker's goalkeeper: ${goalkeeper}")
        goalkeeper.additionalValue = additional // ✅ Ensure `additionalValue` is properly restored
        goalkeeper.lastBoostValue = lastBoost
        goalkeeper.wasBoosted = wasBoosted
        goalkeeper.updateValue()
        pf.setPlayerGoalkeeper(memento.attacker, Some(goalkeeper))
      }
    }

    // ✅ Notify observers of the restored state
    pf.notifyObservers()
  }

}

